package com.gthncz.mymarketclient.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GT on 2018/5/18.
 */

public class Point2BalanceActivity extends AppCompatActivity {

    @BindView(R.id.linearLayout_activity_point2balance_wrapper) protected LinearLayout mWrapper;
    @BindView(R.id.toolbar_activity_point2balance) protected Toolbar mToolbar;
    @BindView(R.id.textView_activity_point2balance_current_point) protected TextView mCurrentPoint;
    @BindView(R.id.editText_activity_point2balance_exchange_num) protected TextInputEditText mExchangeNum;
    @BindView(R.id.textView_activity_point2balance_obtain_balance) protected TextView mBalance;
    @BindView(R.id.button_activity_point2balance_submit) protected Button mSubmit;

    private int mPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point2balance);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mSubmit.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = ClientApplication.getInstance().getUser();
        if(user == null){
            Snackbar snackbar = Snackbar.make(mWrapper, "会员未登陆!" , Snackbar.LENGTH_LONG);
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    Intent intent = new Intent(Point2BalanceActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Point2BalanceActivity.this.startActivity(intent);
                }
            });
            snackbar.show();
        }
        mPoint = user.getPoint();
        mCurrentPoint.setText(String.valueOf(mPoint));
        mExchangeNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mExchangeNum.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if(TextUtils.isEmpty(str)){
                    mBalance.setText(String.format("%.2f", 0.00f));
                    mSubmit.setEnabled(false);
                    return;
                }
                int point = Integer.valueOf(str);
                Log.e(getClass().getSimpleName(), "** 信息 >> exchande num:" + point);
                if(point > 0 && point <= mPoint){
                    mSubmit.setEnabled(true);
                    // 更新转换得的余额
                    mBalance.setText(String.format("%.2f", ((float)point)/100));
                }else{
                    mExchangeNum.setError("超过本身拥有的积分!");
                    mSubmit.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.button_activity_point2balance_submit})
    protected void onSubmit(){
        hideKeyboard(); // 先隐藏软键盘
        // 提交转换积分
        int point = Integer.valueOf( mExchangeNum.getText().toString().trim());
        if(point < 0 || point > mPoint){
            Snackbar.make(mWrapper, "转换积分数量不符合要求!", Snackbar.LENGTH_LONG).show();
            return;
        }
        AlertDialog dialog = getDialog();
        dialog.show();
        HashMap<String, String> map = new HashMap<>();
        map.put("point", String.valueOf(point));
        JSONObject params = new JSONObject(map);
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_POINT2BALANCE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        try {
                            int code = response.getInt("code");
                            String msg = response.getString("msg");
                            Snackbar snackbar = Snackbar.make(mWrapper, msg, Snackbar.LENGTH_LONG);
                            if(code == 1){
                                snackbar.addCallback(new Snackbar.Callback(){
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                        Point2BalanceActivity.this.finish();// 请求成功则结束当前Activity
                                    }
                                });
                            }
                            snackbar.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                Snackbar.make(mWrapper, error.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
        requestQueue.start();
    }


    private AlertDialog getDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setPadding(10,10, 10, 10);
        builder.setView(progressBar);
        builder.setCancelable(false);
        return builder.create();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(mExchangeNum.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return  true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
