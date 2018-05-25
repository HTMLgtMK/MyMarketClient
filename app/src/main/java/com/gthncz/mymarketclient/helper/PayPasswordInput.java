package com.gthncz.mymarketclient.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gthncz.mymarketclient.R;

/**
 * 支付密码输入控件
 *
 * Created by GT on 2018/5/24.
 */

public class PayPasswordInput extends FrameLayout implements TextWatcher {
    private static final String TAG = PayPasswordInput.class.getSimpleName();

    private EditText mEditText; // 输入控件
    private LinearLayout mLinearLayout;
    private TextView[] mTextViews; //显示控件
    private int mPasswordLength; // 密码长度
    private final int DEFAULT_PASSWORD_LENGTH = 6;
    private int mPasswordTextSize;
    private final int DEFAULT_PASSWORD_TEXT_SIZE = 20;
    private int mSplitColor = Color.GRAY; // 密码显示中间的分割的颜色

    private OnFinishInputListener listener;

    public void setOnFinishInputListener(OnFinishInputListener listener){
        this.listener = listener;
    }

    public PayPasswordInput(@NonNull Context context) {
        this(context, null);
    }

    public PayPasswordInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayPasswordInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPasswordLength = DEFAULT_PASSWORD_LENGTH;
        mPasswordTextSize = DEFAULT_PASSWORD_TEXT_SIZE;
        initViews();
    }

    private void initViews() {
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        frameLayoutParams.gravity = Gravity.CENTER;
        // -----------------------------------
        // ----------编辑部分------------------
        mEditText = new EditText(getContext());
        mEditText.setLayoutParams(frameLayoutParams);
        mEditText.setCursorVisible(false); // 设置光标不可见
        mEditText.setTextSize(mPasswordTextSize);
        mEditText.setTextColor(Color.TRANSPARENT); // 设置EditText的文字颜色为透明
        mEditText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_pay_password_input));
        mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mPasswordLength) });
        mEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD
                | InputType.TYPE_CLASS_NUMBER);
        addView(mEditText); // 添加到FrameLayout中
        // -----------------------------------
        // -----------显示部分------------------
        mLinearLayout = new LinearLayout(getContext());
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLinearLayout.setLayoutParams(frameLayoutParams);
        mLinearLayout.setGravity(Gravity.CENTER);
        addView(mLinearLayout);

        mTextViews = new TextView[mPasswordLength];
        LinearLayout.LayoutParams textViewLayoutParams  = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.weight = 1;
        LinearLayout.LayoutParams splitLayoutParams = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        for(int i = 0; i<mPasswordLength; ++i){
            mTextViews[i] = new TextView(getContext());
            mTextViews[i].setLayoutParams(textViewLayoutParams);
            mTextViews[i].setTextSize(mPasswordTextSize);
            mTextViews[i].setGravity(Gravity.CENTER);
            mLinearLayout.addView(mTextViews[i]);
            if(i < mPasswordLength-1){
                View view = new View(getContext());
                view.setBackgroundColor(mSplitColor);
                view.setLayoutParams(splitLayoutParams);
                mLinearLayout.addView(view);
            }
        }
        // 放到最后添加, 防止mTextViews为空
        mEditText.addTextChangedListener(this);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Editable edit = mEditText.getText();
        Selection.setSelection(edit, edit.length());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            int length = s.length();
            for (int i = 0; i < mPasswordLength; i++) {
                if (i < length) {
                    for (int j = 0; j < length; j++) {
//                        char ch = s.charAt(j);
                        char ch = '*';
                        mTextViews[j].setText(String.valueOf(ch));
                    }
                } else {
                    mTextViews[i].setText("");
                }
            }
        } else {
            for (int i = 0; i < mPasswordLength; i++) {
                mTextViews[i].setText("");
            }
        }
        if (s.length() == mPasswordLength) {
            if(listener!= null){
                listener.oFinishInput(s.toString());
            }
        }
    }

    public void clearPassword(){
        mEditText.setText("");
    }


    /** 密码输入完毕监听器 */
    public interface OnFinishInputListener {
        void oFinishInput(String password);
    }
}
