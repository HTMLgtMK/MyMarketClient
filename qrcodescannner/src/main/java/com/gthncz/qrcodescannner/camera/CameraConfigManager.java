package com.gthncz.qrcodescannner.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.WindowManager;

/**
 * 相机属性管理类
 * Created by GT on 2018/5/7.
 */

public class CameraConfigManager {

    private Context context;
    /*屏幕分辨率*/
    private Point mScreenResolution;
    /*相机分辨率*/
    private Point mCameraResolution;

    public CameraConfigManager(Context context){
        this.context = context;
    }

    /**
     * 获取到相机时获取相机属性，初始化参数
     * @param camera 已经获取到的相机
     */
    public void initConfigFromCamera(Camera camera){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenResolution = new Point();
        windowManager.getDefaultDisplay().getSize(mScreenResolution);

        Camera.Parameters parameters = camera.getParameters();
        Camera.Size defaultPreviewSize = parameters.getPreviewSize();
        mCameraResolution = new Point(defaultPreviewSize.width, defaultPreviewSize.height);

        //设置摄像机的方向为竖屏
        camera.setDisplayOrientation(90);
    }

    public Point getCameraResolution(){
        return mCameraResolution;
    }

    public Point getScreenResolution(){
        return mScreenResolution;
    }


}
