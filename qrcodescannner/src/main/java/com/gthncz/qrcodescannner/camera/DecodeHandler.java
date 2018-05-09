package com.gthncz.qrcodescannner.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * 预览解码处理
 * Created by GT on 2018/5/8.
 */

public class DecodeHandler extends Handler {

    private final static String TAG = DecodeHandler.class.getSimpleName();

    // 本Handler传入的消息状态码
    public final static int MESSAGE_DECODE = 1;
    public final static int MESSAGE_QUIT = 2;
    // UIHandler传出的状态码
    public final static int MESSAGE_DECODE_SUCCESSED = 3;
    public final static int MESSAGE_DECODE_FAILED = 4;

    private CameraManager cameraManager;
    private Handler uiHandler;// UI 更新处理，用于传递解码后数据

    private MultiFormatReader multiFormatReader;
    private boolean running;

    public DecodeHandler(CameraManager manager, Handler uiHandler,Map<DecodeHintType, Object> hints){
        this.cameraManager = manager;
        this.uiHandler = uiHandler;

        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);

        running = true;
    }

    @Override
    public void handleMessage(Message msg) {
        if(!running){
            return;
        }
        switch (msg.what){
            case MESSAGE_DECODE:{
                decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                break;
            }
            case MESSAGE_QUIT:{
                running = false;
                Looper.myLooper().quit();
            }
        }
        super.handleMessage(msg);
    }

    /**
     * 解码
     * @param data 图像数据
     * @param width 相机分辨率宽
     * @param height 相机分辨率高
     */
    private void decode(byte[] data, int width, int height){
        long start = System.currentTimeMillis();
        Result rawResult = null;
        Rect previewFrameRect = cameraManager.getPreviewFrameRect(); // 预览框
        if(previewFrameRect == null){
            Log.w(TAG, "** 警告 >> 获取得到的预览框为null! 可能没有为CameraManager设置ScannerView ?");
            return;
        }
        PlanarYUVLuminanceSource planarYUVLuminanceSource  = new PlanarYUVLuminanceSource(data, width, height,
                previewFrameRect.left, previewFrameRect.top, previewFrameRect.width(), previewFrameRect.height(), false);
        if(planarYUVLuminanceSource != null){
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (NotFoundException e) {
                e.printStackTrace();
            } finally {
                multiFormatReader.reset();
            }
        }

        // 发送数据给UI Handler
        if(rawResult != null){
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");

            if(uiHandler != null){
                Message message = uiHandler.obtainMessage(MESSAGE_DECODE_SUCCESSED, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(planarYUVLuminanceSource, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        }else{
            if(uiHandler != null){
                Message message = uiHandler.obtainMessage(MESSAGE_DECODE_FAILED);
                message.sendToTarget();
            }
        }
    }

    /**
     * 生成缩略图，并将其放入Bundle中
     * copy from zxing android project (DecodeHandler : bundleThumbnail)
     * @param source 预览图
     * @param bundle
     */
    private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
        bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
    }
}
