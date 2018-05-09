package com.gthncz.qrcodescannner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.gthncz.qrcodescannner.camera.CameraManager;
import com.gthncz.qrcodescannner.camera.DecodeFormatManager;
import com.gthncz.qrcodescannner.camera.DecodeHandler;
import com.gthncz.qrcodescannner.camera.DecodeHintManager;
import com.gthncz.qrcodescannner.camera.DecodeThread;
import com.gthncz.qrcodescannner.camera.Intents;
import com.gthncz.qrcodescannner.ui.ScannerView;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

/**
 * 扫描二维码的Activity
 * Create By GT 2018/05/07
 * <p>
 * 内部大致逻辑:
 * 1. 初始化UI控件， 获取外部传入的解码信息
 * 2. 初始化Surface, 初始化相机
 * 3. 开始预览，开启解码线程
 * 4. 开始业务逻辑，将相机预览帧回调数据送给解码Handler处理
 * 5. 解码Handler将解码后数据传给UIHandler展示
 * 6. 关闭相机，关闭解码线程
 * 7. 将解码数据传回调用Activity
 * <p>
 * 返回的键值：Activity.RESULT_OK | Activity.RESULT_CANCELED
 * 返回的数据: intent
 * ----------key----------------------------||------value-----------------
 * 1. Intents.Scan.RESULT (String)          ||  Result rawResult
 * 2. Intents.Scan.RESULT_FORMAT (String)   ||  BarcodeFormat decodeFormat
 * 3. Intents.Scan.RESULT_BYTES (byte[])    ||  byte[] rawBytes
 * 4.  其它属性                              ||    详见@handleResultExternally
 * <p>
 * 传入的参数(详见@parseDecodeInfoFromIntent):
 * intent.action           ||  Intents.Scan.ACTION (**必须**)
 * ---------key---------------------------||-------value------------------
 * 1. Intents.Scan.FORMATS(StringExtra)   || Intents.Scan.FORMATS
 * 2. Intents.Scan.MODE(StringExtra)      || Intents.Scan.PRODUCT_MODE | QR_CODE_MODE | DATA_MATRIX_MODE | ONE_D_MODE
 * 3. Intents.Scan.WIDTH(IntExtra)        || Integer
 * 4. Intents.Scan.HEIGHT(IntExtra)       || Integer
 * 5. Intents.Scan.PROMPT_MESSAGE(StringExtra) || String
 * 6. Intents.Scan.CHARACTER_SET(StringExtra)  || String
 * </p>
 */
public class QrCodeScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = QrCodeScannerActivity.class.getSimpleName();

    protected Toolbar mToolbar;
    protected TextView mTitle;
    protected SurfaceView mSurfaceView;
    protected ScannerView mScannerView;

    private boolean hasSurface;// surfaceView创建标识
    private CameraManager mCameraManager; // 自定义相机管理器
    private MyUIHandler mUIHandler; // UI线程更新处理
    private DecodeThread mDecodeThread; // 解码线程，包含解码Handler

    /******一些解码用变量******/
    private Collection<BarcodeFormat> barcodeFormats;
    private Map<DecodeHintType, Object> decodeHints;
    private String characterSet;

    /*相机权限请求*/
    private final int REQUEST_CODE_CAMERA = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "lifecircle --> onCreate");
        //保持屏幕常亮
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qr_code_scanner);
        // 库工程不能使用Butterknife注解找到R.id，会出现非常量问题
        mToolbar = (Toolbar) findViewById(R.id.toolbar_qr_code);
        mTitle = (TextView) findViewById(R.id.textView_qr_code_title);
        mScannerView = (ScannerView) findViewById(R.id.scannerView_qr_code_scan);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView_qrcodeScanner_camera);
        hasSurface = false;
        mUIHandler = new MyUIHandler();
        // 解析外部传入的解码提示信息, 可以没有这个!!为DecodeHandler传入null
        parseDecodeInfoFromIntent();
        // 设置界面
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "lifecircle --> onResume");
        mCameraManager = new CameraManager(getApplicationContext());
        mCameraManager.setScannerView(mScannerView);// 需要设置这个，解码线程需要获取预览框大小
        mDecodeThread = new DecodeThread(mCameraManager, mUIHandler, barcodeFormats, decodeHints, characterSet, new ResultPointCallback() {
            @Override
            public void foundPossibleResultPoint(ResultPoint point) {
                // TODO to handler Result Point
                Log.e(TAG, "** 信息 >> decodeThread 回调信息: foundPossibleResultPoint -- " + point.toString());
            }
        });
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (hasSurface) {
            Log.e(TAG, "** 信息 >> surfaceView已经在onResume前初始化!");
            // 直接初始化相机，可能是由于Paused但是未stop， surface仍然存在
            initCamera(surfaceHolder);
        } else {
            Log.e(TAG, "** 信息 >> surfaceView在onResume未被初始化!");
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "lifecircle --> onStart");
    }

    @Override
    protected void onPause() {
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        mDecodeThread = null;
        super.onPause();
        Log.i(TAG, "lifecircle --> onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "lifecircle --> onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "lifecircle --> onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "lifecircle --> onStop");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surface --> surfaceCreated");
        if (holder == null) {
            Log.w(TAG, "** 警告 ** surface已经创建但是返回SurfaceHolder为空!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surface --> surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surface --> surfaceDestroyed");
        hasSurface = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{//返回键按钮
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();//结束当前Activity
    }

    /**
     * 解析外部传入的解码提示信息
     * 必须是 Intent.Action == Intents.Scan.ACTION
     */
    protected void parseDecodeInfoFromIntent() {
        Intent intent = getIntent();

        barcodeFormats = null;
        characterSet = null;
        decodeHints = null;

        if (intent != null) {

            String action = intent.getAction();
            String dataString = intent.getDataString();

            if (Intents.Scan.ACTION.equals(action)) {

                // Scan the formats the intent requested, and return the result to the calling activity.
                barcodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        // TODO 修改ScannerView的预览框大小
                    }
                }
                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    // TODO 为预览框下添加提示文字
                }
            }
            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
        }
    }

    /*初始化相机，并开始预览和解码*/
    protected void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("** 错误 >> 提供的SurfaceHolder为空!");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "** 信息 >> 相机以及被打开! 可能是 SurfaceView Callback 时机太晚?");
            return;
        }
        // 检查相机权限
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            requestCameraPermission();
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            Log.e(TAG, "** 信息 >> 开启相机 : " + mCameraManager.isOpen());
            // 开始预览， 开启解码任务
            mCameraManager.startPreview();
            Log.e(TAG, "** 信息 >> 开始预览!");
            mDecodeThread.start();
            Log.e(TAG, "** 信息 >> 开始解码!");
            // 开始业务逻辑
            start();
        } catch (IOException e) {
            e.printStackTrace();
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 开始业务逻辑
     */
    public void start() {
        // 请求一个预览帧
        mCameraManager.requestPreviewFrame(mDecodeThread.getDecodeHandler(), DecodeHandler.MESSAGE_DECODE);
    }

    /*处理回调给Activity的数据*/
    private void handleResultExternally(Result rawResult, float scaleFactor, Bitmap barcode) {
        Log.e(TAG, "rawResult : " + String.valueOf(rawResult));
        {// 给调用的Activity返回结果
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null) {
                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                            metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {
                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {
                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                @SuppressWarnings("unchecked")
                Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {
                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {
                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    /**
     * UI处理
     */
    private class MyUIHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "** 信息 >> UIHandler 获得信息: " + msg.toString());
            switch (msg.what) {
                case DecodeHandler.MESSAGE_DECODE_SUCCESSED: {
                    Log.d(TAG, "Got decode succeeded message");
                    Bundle bundle = msg.getData();
                    Bitmap barcode = null;
                    float scaleFactor = 1.0f;
                    if (bundle != null) {
                        byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                        if (compressedBitmap != null) {
                            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                            // Mutable copy:
                            barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                        }
                        scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                    }
                    handleResultExternally((Result) msg.obj, scaleFactor, barcode);
                    break;
                }
                case DecodeHandler.MESSAGE_DECODE_FAILED: {
                    Log.d(TAG, "Got decode failed message");
                    // running继续请求预览帧, 也可能是已经onPause, 此时应该扔掉这个，并且不再请求
                    if(mDecodeThread != null){
                        mCameraManager.requestPreviewFrame(mDecodeThread.getDecodeHandler(), DecodeHandler.MESSAGE_DECODE);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 动态申请相机权限
     */
    protected void requestCameraPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            // 如果访问了，但是没有被授予权限，则需要告诉用户，使用此权限的好处
            LinearLayout wrapper = (LinearLayout) findViewById(R.id.linearLayout_qr_code_wrapper);
            Snackbar.make(wrapper, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.button_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 这里重新申请权限
                            ActivityCompat.requestPermissions(QrCodeScannerActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CODE_CAMERA);
                        }
                    })
                    .show();
        }else{
            // 第一次请求相机权限，直接显示请求对话框
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 ){
            int grant = grantResults[0];
            if(grant == PackageManager.PERMISSION_GRANTED){
                SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
                initCamera(surfaceHolder);
            }else{//不给权限就退出啊
                displayFrameworkBugMessageAndExit();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 显示出错对话框并且退出程序
     */
    private void displayFrameworkBugMessageAndExit() {
        class ClickedListener implements AlertDialog.OnClickListener, DialogInterface.OnCancelListener {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishActivity();
            }

            @Override
            public void onCancel(DialogInterface dialog) {
                finishActivity();
            }

            protected void finishActivity() {
                QrCodeScannerActivity.this.finish();
            }
        }
        ClickedListener clickedListener = new ClickedListener();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, clickedListener);
        builder.setOnCancelListener(clickedListener);
        builder.show();
    }

}
