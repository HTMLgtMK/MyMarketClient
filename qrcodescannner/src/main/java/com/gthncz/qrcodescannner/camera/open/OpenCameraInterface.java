package com.gthncz.qrcodescannner.camera.open;

import android.hardware.Camera;
import android.util.Log;

/**
 * 打开相机的接口
 * Created by GT on 2018/5/7.
 * Copy from zxing android project. 感谢!
 */

public final class OpenCameraInterface {

    /*不可实例化*/
    private OpenCameraInterface(){}

    /**
     * 打开手机背面相机(如果有)，否则打开其它相机
     * @return
     */
    public static Camera open(){
        int numberOfCameras= Camera.getNumberOfCameras();
        if(numberOfCameras == 0){
            Log.w(OpenCameraInterface.class.getSimpleName(), "** 警告 ** >> 没有找到相机! ");
            return null;
        }
        int index = 0;
        while(index < numberOfCameras){
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                break;
            }
            ++index;
        }
        Camera camera;
        if(index < numberOfCameras){
            camera = Camera.open(index);
            Log.w(OpenCameraInterface.class.getSimpleName(), ">> 打开相机: " + index);
        }else{
            Log.i(OpenCameraInterface.class.getSimpleName(), "** 信息 >> 没有背面相机!");
            camera = Camera.open(0);//随便开一个摄像头。。
            Log.w(OpenCameraInterface.class.getSimpleName(), ">> 打开默认相机 : " + 0);
        }
        return camera;
    }
}
