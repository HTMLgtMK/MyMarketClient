package com.gthncz.mymarketclient.main;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserImageRequest;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.helper.SquareImageView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 会员信息
 *
 * Created by GT on 2018/5/10.
 */

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();

    private User mUser;
    private String mUserToken;

    @BindView(R.id.toolbar_user) protected Toolbar mToolbar;
    @BindView(R.id.imageView_user_avatar) protected SquareImageView mAvatar;
    @BindView(R.id.textView_user_name) protected TextView mName;
    @BindView(R.id.textView_user_nickname) protected TextView mNickname;
    @BindView(R.id.textView_user_login) protected TextView mUserLogin;
    @BindView(R.id.textView_user_mobile) protected TextView mMobile;
    @BindView(R.id.textView_user_email) protected TextView mEmail;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUser = MyLocalUserHelper.getLocalUser(this);
        mUserToken = MyLocalUserHelper.getLocalToken(this);

        mName.setText(mUser.getName());
        mNickname.setText(mUser.getUser_nickname());
        mUserLogin.setText(mUser.getUser_login());
        if(!TextUtils.isEmpty(mUser.getMobile())){
            mMobile.setText(mUser.getMobile());
        }else{
            mMobile.setText(getText(R.string.go_bind));
            Drawable right = ActivityCompat.getDrawable(this, R.drawable.right_16);
            mMobile.setCompoundDrawables(null, null, right, null);
        }
        if(!TextUtils.isEmpty(mUser.getUser_email())){
            mEmail.setText(mUser.getUser_email());
        }else{
            mEmail.setText(getText(R.string.go_bind));
            Drawable right = ActivityCompat.getDrawable(this, R.drawable.right_16);
            mEmail.setCompoundDrawables(null, null, right, null);
        }
        initUserAvatar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(TAG);
        mRequestQueue.stop();
        mRequestQueue = null;
    }

    private void initUserAvatar() {
        int maxWidth = mAvatar.getWidth();
        int maxHeight = mAvatar.getHeight();
        MyUserImageRequest request = new MyUserImageRequest(Params.URL_USER_AVATAR, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mAvatar.setImageBitmap(response);
            }
        }, maxWidth, maxHeight, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAvatar.setImageResource(R.drawable.temp_headerbackground);
            }
        }) {
            @Override
            public String getUserToken() {
                return mUserToken;
            }
        };
        request.addMarker(TAG);
        mRequestQueue.add(request);
    }

    @OnClick({R.id.imageView_user_avatar})
    protected void showAvatarActivity(){
        Intent intent = new Intent(UserActivity.this, AvatarActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.textView_user_pay_password})
    protected void modifyPayPassword(){
        Intent intent = new Intent(UserActivity.this, ModifyPayPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_user_exitlogin})
    protected void exitLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage(getText(R.string.user_exit));
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setPadding(20, 20,20,20);
        builder.setView(progressBar);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_LOGOUT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(UserActivity.class.getSimpleName(), "** 信息 >> " + response.toString());
                alertDialog.cancel();
                setCurrentUserId0();
                exitApplication();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(UserActivity.class.getSimpleName(), "** 信息 >> " + error.toString());
                alertDialog.cancel();
                setCurrentUserId0();
                exitApplication();
            }
        }) {
            @Override
            public String getUserToken() {
                return mUserToken;
            }
        };
        try {
            Log.e(getClass().getSimpleName(), "** 信息 >> user logout resquest : "+ request.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        requestQueue.add(request);
        requestQueue.start();
    }

    private void setCurrentUserId0(){
        SharedPreferences sharedPreferences = getSharedPreferences(Params.INI_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        /*一定要是long类型, 否则LoginActivity读取会出错:java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Long */
        editor.putLong(Params.KEY_CURRENT_USER_ID, 0);
        editor.commit();
    }


    private void exitApplication(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getPackageName());// 杀死后台进程
        finish();// 终止当前Activity
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
