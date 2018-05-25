package com.gthncz.mymarketclient.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.main.ClientActivity;

/**
 * Created by GT on 2018/5/25.
 */

public class StartActivity extends AppCompatActivity {

    private LinearLayout mWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);//要放到加载布局文件代码之前

        mWrapper = (LinearLayout) findViewById(R.id.linearLayout_activity_start_wrapper);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // check current user status
                checkCurrentUserStatus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation.setDuration(2*1000);// 2s
        mWrapper.startAnimation(alphaAnimation);
    }

    /**
     * 检查当前用户的登陆状态
     */
    protected void checkCurrentUserStatus() {
        if(!MyLocalUserHelper.isLocalUserExpired(this)){
            if(ClientApplication.DEBUG){
                Log.i(getClass().getSimpleName(), "user in her duration, jump to next page directly !!!");
            }
            showClientMainPage();
        }else{
            showLoginPage();
        }
    }

    private void showClientMainPage(){
        Intent intent = new Intent(this, ClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showLoginPage(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
