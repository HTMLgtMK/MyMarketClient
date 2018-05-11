package com.gthncz.mymarketclient.grant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 会员授权界面
 * Created by GT on 2018/5/9.
 */

public class GrantActivity extends AppCompatActivity {

    @BindView(R.id.linearLayout_grant_wrapper) protected LinearLayout mWrapper;
    @BindView(R.id.toolbar_grant) protected Toolbar mToolbar;
    @BindView(R.id.viewFlipper_grant_togglePage) protected ViewFlipper mPageToggle;
    @BindView(R.id.textView_grant_msg) protected TextView mGrantMsg;

    private User mUser;
    private String mToken;
    private String mUserToken;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mUser = ClientApplication.getInstance().getUser();
        mUserToken = ClientApplication.getInstance().getToken();
        checkGrantReq();
    }


    /**
     * 检查授权请求状态
     */
    public void checkGrantReq(){
        User user = ClientApplication.getInstance().getUser();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", mToken);
        JSONObject params = new JSONObject(map);
        MyUserJsonObjectRequest jsonObjectRequest = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_SCAN, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    String msg = response.getString("msg");
                    if( code == 1){
                        mPageToggle.showNext();
                    }else{
                        showMsgAndExit(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showMsgAndExit(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMsgAndExit(error.toString());
            }
        });
        mQueue.add(jsonObjectRequest);
        mQueue.start();
    }

    /**
     * 提交授权信息
     */
    @OnClick({R.id.button_grant_confirm})
    protected void grantConfirm(){
        HashMap<String,String> map = new HashMap<>();
        map.put("token", mToken);
        JSONObject jsonObject = new JSONObject(map);
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_GRANT, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    String msg = response.getString("msg");
                    if(code == 1){
                        //授权成功
                        Snackbar snackbar = Snackbar.make(mWrapper, msg, Snackbar.LENGTH_SHORT);
                        snackbar.addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                GrantActivity.this.finish();
                            }
                        });
                        snackbar.show();
                    }else{
                        showMsgAndExit(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMsgAndExit(error.toString());
            }
        });
        mQueue.add(request);
    }

    /*显示信息然后退出*/
    private void showMsgAndExit(String msg){
        MyDialogClickedListener listener = new MyDialogClickedListener();
        AlertDialog.Builder builder = new AlertDialog.Builder(GrantActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("授权");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", listener );
        builder.setCancelable(false);
        builder.create().show();
    }

    private class MyDialogClickedListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener{

        @Override
        public void onCancel(DialogInterface dialog) {
            finishActivity();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finishActivity();
        }

        private void finishActivity(){
            GrantActivity.this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
