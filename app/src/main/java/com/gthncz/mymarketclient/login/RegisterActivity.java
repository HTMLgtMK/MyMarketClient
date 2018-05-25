package com.gthncz.mymarketclient.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GT on 2018/5/6.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolBar_register) Toolbar mToolBar;
    @BindView(R.id.textView_register_title) TextView mTitile;
    @BindView(R.id.frameLayout_register_container) FrameLayout mContainer;
    // step1 page
    @BindView(R.id.scrollView_register_step1_wrapper) ScrollView mStep1Wrapper;
    @BindView(R.id.editText_register_username) EditText mUsername;
    @BindView(R.id.editText_register_user_login) EditText mUserLogin;
    @BindView(R.id.editText_register_name) EditText mName;
    @BindView(R.id.editText_register_user_pass) EditText mUserPass;
    @BindView(R.id.editText_register_user_pass_confirm) EditText mUserPassConfirm;
    @BindView(R.id.editText_register_verification_code) EditText mVerificationCode;
    @BindView(R.id.button_register_get_verification_code) Button mGetVerficationCodeButton;
    @BindView(R.id.button_register_register) Button mRegisterButton;

    private ActionBar mActionBar;
    private int step;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        step1();

        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    protected void step1(){
        step = 1;
        mTitile.setText(getResources().getString(R.string.register));

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    mGetVerficationCodeButton.setEnabled(true);
                }else{
                    mGetVerficationCodeButton.setEnabled(false);
                }
            }
        });

        mGetVerficationCodeButton.setEnabled(false);
        mGetVerficationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = mUsername.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                JSONObject jsonObject = new JSONObject(map);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Params.URL_REGISTER_GET_VERIFICATION_CODE, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(ClientApplication.DEBUG){
                            Log.i(getClass().getSimpleName(), response.toString());
                        }
                        try {
                            String msg = response.getString("msg");
                            String code = "";
                            if(ClientApplication.DEBUG){
                                if(response.has("data")){
                                    JSONObject data = response.getJSONObject("data");
                                    code = data.getString("token");
                                }
                            }
                            Snackbar.make(mContainer, msg + code, Snackbar.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(mContainer, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(mContainer, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
                mQueue.add(jsonObjectRequest);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm()) return;

                String username = mUsername.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String user_login = mName.getText().toString().trim();
                String user_pass = mUserPass.getText().toString();
                String verification_code = mVerificationCode.getText().toString().trim();

                final Dialog dialog = getLoadingDialog();
                dialog.show();

                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("name", name);
                map.put("user_login", user_login);
                map.put("user_pass", user_pass);
                map.put("verification_code", verification_code);
                JSONObject jsonObject = new JSONObject(map);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Params.URL_USRT_REGISTER, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.cancel();
                        if(ClientApplication.DEBUG){
                            Log.i(getClass().getSimpleName(), response.toString());
                        }
                        try{
                            int code = response.getInt("code");
                            String msg = response.getString("msg");
                            Snackbar snackbar = Snackbar.make(mContainer, msg, Snackbar.LENGTH_LONG);
                            snackbar.addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    if(code == 1){
                                        finish();//结束当前Activity
                                    }
                                }
                            });
                            snackbar.show();
                        }catch (JSONException e){
                            e.printStackTrace();
                            Snackbar.make(mContainer, e.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.cancel();
                        Snackbar.make(mContainer, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(jsonObjectRequest);
            }
        });
    }

    protected Dialog getLoadingDialog(){
        Dialog dialog = new Dialog(this);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setPadding(40,40,40,40);
        dialog.setContentView(progressBar);
        return dialog;
    }

    /** 验证表单完整性 */
    private boolean validateForm(){
        boolean hasError = false;
        View focusView = null;
        if(TextUtils.isEmpty(mUsername.getText())){
            mUsername.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mUsername;
        }
        if(TextUtils.isEmpty(mUserLogin.getText())){
            mUserLogin.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mUserLogin;
        }
        if(TextUtils.isEmpty(mUserLogin.getText())){
            mUserLogin.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mUserLogin;
        }
        if(TextUtils.isEmpty(mUserPassConfirm.getText())){
            mUserPassConfirm.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mUserPassConfirm;
        }
        if(!TextUtils.equals(mUserPass.getText(), mUserPassConfirm.getText())){
            mUserPassConfirm.setError("两次输入密码不一致!");
            hasError = true;
            focusView = mUserPassConfirm;
        }
        if(TextUtils.isEmpty(mName.getText())){
            mName.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mName;
        }
        if(TextUtils.isEmpty(mVerificationCode.getText())){
            mVerificationCode.setError(getResources().getString(R.string.error_field_required));
            hasError = true;
            focusView = mVerificationCode;
        }
        if(hasError){
            focusView.requestFocus();
        }
        return !hasError;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();//触发返回
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
