package com.gthncz.qrcodescannner.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.gthncz.qrcodescannner.camera.open.OpenCameraInterface;
import com.gthncz.qrcodescannner.ui.ScannerView;

import java.io.IOException;

/**
 * 摄像机管理类
 * Created by GT on 2018/5/7.
 */

public class CameraManager {
    /*相机*/
    private Camera mCamera;
    /*相机属性是否初始化*/
    private boolean mInitialized;
    /*是否正在预览*/
    private boolean mIsPreviewing;
    /*预览回调接口*/
    private PreviewCallback mPreviewCallback;

    private AutoFocusManager mAutoFocusManager;

    private CameraConfigManager mCameraConfigManager;
    /*保存用于获取预览框大小*/
    private ScannerView mScannerView;

    public CameraManager(Context context){
        /*相机相关*/
        mInitialized = false;
        mIsPreviewing = false;
        mCameraConfigManager = new CameraConfigManager(context);
        mPreviewCallback = new PreviewCallback(mCameraConfigManager);
        /*解码相关*/
    }

    /**
     * 打开相机
     * @param surfaceHolder
     */
    public synchronized void openDriver(SurfaceHolder surfaceHolder) throws IOException {
        Camera camera = mCamera;
        if(camera == null){
            camera = OpenCameraInterface.open();
            if(camera == null){
                throw new IOException();
            }
            mCamera = camera;
        }

        camera.setPreviewDisplay(surfaceHolder);
        if(!mInitialized){
            mCameraConfigManager.initConfigFromCamera(camera);
            mInitialized = true;
        }
    }

    /**
     * 是否已经打开相机
     * @return
     */
    public synchronized boolean isOpen(){
        return mCamera != null;
    }

    /**
     * 关闭相机
     */
    public synchronized void closeDriver(){
        if(mCamera!=null){
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 开始预览相机， 同时开始解码
     */
    public synchronized void startPreview(){
        Camera camera = mCamera;
        if(camera != null && !mIsPreviewing){
            camera.startPreview();
            mIsPreviewing = true;
            mAutoFocusManager = new AutoFocusManager(camera);
        }
    }

    /**
     * 停止相机预览
     */
    public  synchronized void stopPreview(){
        if (mAutoFocusManager != null) {
            mAutoFocusManager.stop();
            mAutoFocusManager = null;
        }
        Camera camera = mCamera;
        if(camera != null && mIsPreviewing){
            camera.stopPreview();
            mIsPreviewing = false;
            mPreviewCallback.setHandler(null ,0);
        }
    }

    /**
     * 请求相机单帧, 由传进的Handler 返回数据
     * data 是 byte[]类型
     * message.arg1 是 相机分辨率宽度
     * message.arg2 是 相机分辨率高度
     * message.what 是 传入的message
     * @param handler 用于传出帧数据
     * @param message Message的what
     */
    public synchronized void requestPreviewFrame(Handler handler, int message){
        Camera camera = mCamera;
        if(camera != null && mIsPreviewing){
            mPreviewCallback.setHandler(handler, message);// 给相机预览回调接口设置Handler, 用于传出数据
            camera.setOneShotPreviewCallback(mPreviewCallback);
        }
    }

    /**
     * 设置预览框控件
     * @param scannerView
     */
    public void setScannerView(ScannerView scannerView){
        this.mScannerView = scannerView;
    }

    /**
     * 获取预览框
     * @return
     */
    public Rect getPreviewFrameRect(){
        if(mScannerView!=null){
            return mScannerView.getPreviewFrameRect();
        }
        return null;
    }
}
