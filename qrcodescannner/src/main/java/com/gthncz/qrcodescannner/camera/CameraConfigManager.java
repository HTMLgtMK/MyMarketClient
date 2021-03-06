package com.gthncz.qrcodescannner.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 相机属性管理类
 * Created by GT on 2018/5/7.
 */

public class CameraConfigManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private Context context;
    /*屏幕分辨率*/
    private Point mScreenResolution;
    /*相机分辨率*/
    private Point mCameraResolution;

    // This is bigger than the size of a small screen, which is still supported. The routine
    // below will still select the default (presumably 320x240) size for these. This prevents
    // accidental selection of very low resolution on some devices.
    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    //private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    //private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final double MAX_ASPECT_DISTORTION = 0.15;

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
        mCameraResolution = findBestPreviewSizeValue(parameters, mScreenResolution);

        setDesiredCameraParameters(camera);
    }

    private void setDesiredCameraParameters(Camera camera){
        Camera.Parameters parameters = camera.getParameters();

        parameters.setPreviewSize(mCameraResolution.x, mCameraResolution.y);
        camera.setParameters(parameters);
        //设置摄像机的方向为竖屏
        camera.setDisplayOrientation(90);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize!= null && (mCameraResolution.x != afterSize.width || mCameraResolution.y != afterSize.height)) {
            Log.w(TAG, "Camera said it supported preview size " + mCameraResolution.x + 'x' + mCameraResolution.y +
                    ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            mCameraResolution.x = afterSize.width;
            mCameraResolution.y = afterSize.height;
        }
    }

    public Point getCameraResolution(){
        return mCameraResolution;
    }

    public Point getScreenResolution(){
        return mScreenResolution;
    }


    /**
     * 找到最佳的分辨率  !very important
     * @param parameters
     * @param screenResolution
     * @return
     */
    private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {

        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                previewSizesString.append(supportedPreviewSize.width).append('x')
                        .append(supportedPreviewSize.height).append(' ');
            }
            Log.i(TAG, "Supported preview sizes: " + previewSizesString);
        }

        double screenAspectRatio = (double) screenResolution.x / (double) screenResolution.y;

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewSizes.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }


}
