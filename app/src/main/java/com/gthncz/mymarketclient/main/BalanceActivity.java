package com.gthncz.mymarketclient.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.ClientDBHelper;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GT on 2018/5/18.
 */

public class BalanceActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_activity_balance)
    protected Toolbar mToolbar;
    @BindView(R.id.textView_activity_balance_balance)
    protected TextView mCurrentBalance;

    private RequestQueue mQueue;
    private int mBalance;
    private User mUser;
    private String mToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCurrentBalance.setText("--");
        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mUser = MyLocalUserHelper.getLocalUser(this);
        mToken = MyLocalUserHelper.getLocalToken(this);
        loadBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.stop();
        mQueue = null;
    }

    @OnClick({R.id.button_activity_balance_inpour})
    protected void onInpour() {
        // TODO 处理充值
    }

    @OnClick({R.id.button_activity_balance_detail})
    protected void showDetail() {
        Intent intent = new Intent(BalanceActivity.this, BalanceDetailActivity.class);
        startActivity(intent);
    }

    protected void loadBalance() {
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_MYBALANCE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");
                            String msg = response.getString("msg");
                            if(code == 1){
                                JSONObject data = response.getJSONObject("data");
                                mBalance = data.getInt("balance");
                                mCurrentBalance.setText(String.format("%.2f", (float)mBalance/100));
                                // 更新数据库
                                mUser.setBalance(mBalance);
                                ClientDBHelper.getInstance(BalanceActivity.this).getDaoSession().getUserDao().update(mUser);
                            }else{
                                Toast.makeText(BalanceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BalanceActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
        }){
            @Override
            public String getUserToken() {
                return mToken;
            }
        };
        mQueue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
