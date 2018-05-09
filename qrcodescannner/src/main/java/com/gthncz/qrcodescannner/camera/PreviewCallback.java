package com.gthncz.qrcodescannner.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 摄像机预览帧回调接口
 * Created by GT on 2018/5/7.
 */

public class PreviewCallback implements Camera.PreviewCallback {

    private Handler mPreviewHandler;
    private int mPreviewMessgae;
    private CameraConfigManager mCameraConfigManager;

    public PreviewCallback(CameraConfigManager configManager){
        this.mCameraConfigManager = configManager;
    }


    public void setHandler(Handler handler, int message){
        this.mPreviewHandler = handler;
        this.mPreviewMessgae = message;
    }

    /**
     * 相机预览帧， 通过传入的PreiewHandler发送到外部
     * @param data 帧数据
     * @param camera 相机对象
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = mCameraConfigManager.getCameraResolution();
        if(mPreviewHandler != null && cameraResolution != null){
            Message message = mPreviewHandler.obtainMessage(mPreviewMessgae, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
            mPreviewHandler = null;
        }else {
            Log.d(getClass().getSimpleName(), "** 信息 >> PreviewCallback 已经获取到相机数据，但是没有可用Handler处理!");
        }
    }
}
