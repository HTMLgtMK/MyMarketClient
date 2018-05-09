package com.gthncz.qrcodescannner.camera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 解码线程
 * This thread does all the heavy lifting of decoding the images.
 * Created by GT on 2018/5/8.
 * copy from zxing android project (DecodeThread.java) 感谢!
 */

public class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private Map<DecodeHintType, Object> hints;

    private DecodeHandler decodeHandler; // 解码Hanlder, 用于处理解码图片
    private CountDownLatch countDownLatch; // 同步工具， 用于解码Handler初始化标识
    private CameraManager cameraManager; // 相机管理器，主要用于获取预览框
    private Handler mUIHandler; // UI处理Hanlder, DecodeHandler用到

    public DecodeThread(CameraManager cameraManager,
                        Handler uiHandler,
                        Collection<BarcodeFormat> decodeFormats,
                        Map<DecodeHintType,?> baseHints,
                        String characterSet,
                        ResultPointCallback resultPointCallback){
        this.cameraManager = cameraManager;
        this.mUIHandler = uiHandler;
        countDownLatch = new CountDownLatch(1);
        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        if(baseHints != null){ // 添加外部传入的 hints
            hints.putAll(baseHints);
        }
        decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        //这里的信息本来是要存入SharedPreference中的
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if(characterSet != null){
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK , resultPointCallback);

        Log.i(getClass().getSimpleName(), " ** 信息 >> Decode Hints: " + hints.toString());
    }

    @Override
    public void run() {
        Log.e(getClass().getSimpleName(), "** 信息 >> DecodeThread running!");
        Looper.prepare();
        decodeHandler = new DecodeHandler(cameraManager, mUIHandler, hints);
        countDownLatch.countDown();
        Looper.loop();
    }

    /**
     * 获取解码Handler
     * @return
     */
    public DecodeHandler getDecodeHandler(){
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // continue
        }
        return decodeHandler;
    }
}
