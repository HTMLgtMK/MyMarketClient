package com.gthncz.qrcodescannner.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.gthncz.qrcodescannner.R;

/**
 * 自定义用于扫描二维码的控件
 * Created by GT on 2018/5/7.
 */

public class ScannerView extends View {
    private static final String TAG = ScannerView.class.getSimpleName();

    private int mCornerColor;
    private int[] mScannerLineColor;
    public float[] mColorPosition = new float[]{0f, 0.5f, 1f};
    private int mMaskOutSideColor;
    private int mHintTextColor;
    private Paint mPaint;

    private int laserLinePosition; // 扫描线的位置
    private final int SCANNER_POS_FACTOR = 2;// 0-5， 控制扫描框的位置

    private Rect mFrame; // 扫描框
    private String mHintText; // 提示信息
    private float mHintTextSize; //提示信息字体大小

    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    /*构造器，在XML资源文件中会调用*/
    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources resources = getResources();
        mCornerColor = resources.getColor(R.color.colorCorner);
        int mScannerLineStartColor =  resources.getColor(R.color.colorScannerLineStart);
        int  mScannerLineEndColor =  resources.getColor(R.color.colorScannerLineEnd);
        int  mScannerLineCenterColor =  resources.getColor(R.color.colorScannerLineCenter);
        mScannerLineColor = new int[]{mScannerLineStartColor, mScannerLineCenterColor, mScannerLineEndColor};
        mMaskOutSideColor  = resources.getColor(R.color.colorMaskOutSide);
        mHintTextColor = resources.getColor(R.color.colorHintText);
        mHintText = resources.getString(R.string.scanner_hint_text);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        laserLinePosition = 0;
        mHintTextSize = 14;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 控件的宽高
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // 预览框的宽高
        Rect frame = getPreviewFrame();
        mFrame = frame;
        //绘制四个角
        mPaint.setColor(mCornerColor);
        mPaint.setStrokeWidth(5f);//线宽度

        int lineLong = (frame.right-frame.left)*2/10; // 单个线长度

        canvas.drawLine(frame.left, frame.top,  frame.left+lineLong, frame.top, mPaint);
        canvas.drawLine(frame.left, frame.top, frame.left, frame.top+lineLong,mPaint);

        canvas.drawLine(frame.right-lineLong, frame.top, frame.right, frame.top, mPaint);
        canvas.drawLine(frame.right, frame.top, frame.right, frame.top+lineLong, mPaint);

        canvas.drawLine(frame.left, frame.bottom, frame.left, frame.bottom-lineLong,mPaint);
        canvas.drawLine(frame.left, frame.bottom, frame.left+lineLong, frame.bottom, mPaint);

        canvas.drawLine(frame.right, frame.bottom, frame.right, frame.bottom-lineLong,mPaint);
        canvas.drawLine(frame.right, frame.bottom, frame.right-lineLong, frame.bottom, mPaint);

        // 绘制外部的Mask
        mPaint.setColor(mMaskOutSideColor);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, height, mPaint);
        canvas.drawRect(frame.right, frame.top, width, height, mPaint);
        canvas.drawRect(frame.left, frame.bottom, frame.right, height, mPaint);

        // 绘制扫描线
        laserLinePosition += 5; // 动态改变的激光线的位置
        if(laserLinePosition > frame.height()){
            laserLinePosition = 0;
        }
        LinearGradient linearGradient = new LinearGradient(frame.left+1, frame.top + laserLinePosition,
                frame.right-1, frame.top+laserLinePosition, mScannerLineColor, mColorPosition, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        canvas.drawLine(frame.left+1, frame.top + laserLinePosition,
                frame.right-1, frame.top+laserLinePosition, mPaint);
        mPaint.setShader(null);

        // 绘制提示文字
        mPaint.setColor(mHintTextColor);
        mPaint.setTextSize(dip2px(getContext(), mHintTextSize));
        Rect hintTextRect = getHintTextRect();
        canvas.drawText(mHintText, hintTextRect.left, hintTextRect.right, mPaint);

        //刷新控制 只刷新扫描框内内容
        postInvalidateDelayed(30, frame.left, frame.top, frame.right, frame.bottom);
    }

    /**
     * 获取预览框的位置信息
     * @return
     */
    protected Rect getPreviewFrame(){
        Point size= new Point();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);// getWidth() is deprecated of API 16(JellyBean)

        int width = size.x;
        int height = size.y;

        int frameLong = width>height?height:width;

        int left = frameLong*SCANNER_POS_FACTOR/10;
        int right = frameLong*(10-SCANNER_POS_FACTOR)/10;

        Rect rect = new Rect(left, left, right, right);
        return rect;
    }

    /*获取提示信息框*/
    protected Rect getHintTextRect(){
        Rect frame = getPreviewFrame();
        if(frame == null){// 此时尚未绘制，直接返回null
            return null;
        }
        int middle = (frame.left + frame.right)/2;
        int length = mHintText.length();
        length *= dip2px(getContext(), mHintTextSize);
        Rect rect = new Rect(middle-length/2, frame.top+10, middle+length/2, frame.bottom+30);
        return rect;
    }

    /**
     * 获取预览框
     * @return Rect
     */
    public Rect getPreviewFrameRect(){
        return this.mFrame;
    }

    public void setHintText(String hintText){
        this.mHintText = hintText;
        if(getPreviewFrame() != null){
            // 已经绘制， 更新文字
            Rect hintTextRect = getHintTextRect();
            if(hintTextRect != null){
                postInvalidateDelayed(10, hintTextRect.left, hintTextRect.top, hintTextRect.right, hintTextRect.bottom);
            }
        }
    }

    public void setHintTextSize(float hintTextSize){
        this.mHintTextSize = hintTextSize;
        if(getPreviewFrame() != null){
            // 已经绘制， 更新文字
            Rect hintTextRect = getHintTextRect();
            if(hintTextRect != null){
                postInvalidateDelayed(10, hintTextRect.left, hintTextRect.top, hintTextRect.right, hintTextRect.bottom);
            }
        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
