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
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的积分
 * Created by GT on 2018/5/18.
 */

public class PointActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_activity_point) protected Toolbar mToolbar;
    @BindView(R.id.textView_activity_point_point) protected TextView mPoints;

    private RequestQueue mQueue;
    private int mPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar  = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPoints.setText("--"); // 加载前让其积分显示为未知
        mQueue = Volley.newRequestQueue(this);
        mQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPoint(); // 选择从服务器加载
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.stop();
        mQueue = null;
    }

    @OnClick({R.id.button_activity_point_detail})
    protected void showDetail(){
        // TODO 处理积分详情
    }

    @OnClick({R.id.button_activity_point_obtain})
    protected void showObtain(){
        Intent intent = new Intent(this, PointStoreActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_activity_point_point2balance})
    protected void showPoint2Balance(){
        Intent intent = new Intent(this, Point2BalanceActivity.class);
        startActivity(intent);
    }

    protected void loadPoint(){
        MyUserJsonObjectRequest request = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_USER_MYPOINT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if(code == 1){
                        JSONObject data = response.getJSONObject("data");
                        mPoint = data.getInt("point");
                        mPoints.setText(String.valueOf(mPoint));
                        // 更新数据库
                        User user = ClientApplication.getInstance().getUser();
                        user.setPoint(mPoint);
                        ClientDBHelper.getInstance(PointActivity.this).getDaoSession().getUserDao().update(user);
                        ClientApplication.getInstance().setUser(user);
                    }else{
                        String msg = response.getString("msg");
                        Toast.makeText(PointActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PointActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
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
}
