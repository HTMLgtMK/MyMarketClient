package com.gthncz.mymarketclient.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.DealBean;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.helper.MyLocalUserHelper;
import com.gthncz.mymarketclient.helper.MyUserImageRequest;
import com.gthncz.mymarketclient.helper.MyUserJsonObjectRequest;
import com.gthncz.mymarketclient.payment.BPayActivity;
import com.gthncz.qrcodescannner.QrCodeScannerActivity;
import com.gthncz.qrcodescannner.camera.Intents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 无人超市客户端主界面
 * Created by GT on 2018/5/6.
 */

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = ClientActivity.class.getSimpleName();

    /* bind UI resource */
    // main page ui resource
    @BindView(R.id.toolBar_client_main) protected Toolbar mToolbar;
    @BindView(R.id.button_client_main_scanecode) protected Button mScanCodeButton;
    @BindView(R.id.button_client_main_payment) protected Button mPaymentButton;
    @BindView(R.id.button_client_main_mydeal) protected Button mDealButton;
    @BindView(R.id.button_client_main_mydiscount) protected Button mDiscountButton;
    @BindView(R.id.button_client_main_pointstore) protected Button mPointStoreButton;
    @BindView(R.id.button_client_main_mypoint) protected Button mPointButton;
    @BindView(R.id.button_client_main_mybalance) protected Button mBalanceButton;
    @BindView(R.id.nestedScrollView_client_main_wrapper) protected NestedScrollView mWrapper;
    @BindView(R.id.recyclerView_client_week_account) protected RecyclerView mRecycleView;
    @BindView(R.id.imageView_item_account_header_loading) protected ImageView mRefresh;

    // left menu resource
    @BindView(R.id.navigation_header_container) protected NavigationView mNavigationView;
    protected ImageView mAvatar;
    protected TextView mNickname;

    private User mUser;
    private String mToken;

    // for week deal list
    private ArrayList<DealBean> mWeekDealList;
    private AccountListAdapter mAccountListAdapter;

    private RequestQueue mRequestQueue;

    /*请求码*/
    private final int REQUEST_CODE_SCAN = 0x01;
    private final String KEY_WEEK_DEAL_LIST = "weekDealList";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        mNavigationView.setItemIconTintList(null);//让图片以原来的颜色显示
        mNavigationView.setNavigationItemSelectedListener(new MyNavigationItemSelectedListener());
        View headerView = mNavigationView.getHeaderView(0);
        mNickname = headerView.findViewById(R.id.textView_header_nickname);
        mAvatar = headerView.findViewById(R.id.imageView_header_avatar);

        if(savedInstanceState != null){
            mWeekDealList = savedInstanceState.getParcelableArrayList(KEY_WEEK_DEAL_LIST);
        }else{
            mWeekDealList = new ArrayList<>();
        }
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*获取当前登陆用户*/
        mUser = MyLocalUserHelper.getLocalUser(this);
        if(mUser == null){
            finish();// 可能是由于点了退出登陆，导致User信息为null
        }
        Log.e(getClass().getSimpleName(), "** 信息 >> in ClientMainActivity user: "+ mUser.toString());
        mToken = MyLocalUserHelper.getLocalToken(this);
        /*设置UI控件文字*/
        mNickname.setText(mUser.getUser_nickname());
        /*设置UI控件显示头像*/
        initUserAvatar();
        /*初始化最近一周账单*/
        initWeekAccount();
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
                return mToken;
            }
        };
        request.addMarker(TAG);
        mRequestQueue.add(request);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_WEEK_DEAL_LIST, mWeekDealList);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.button_client_main_scanecode})
    protected void scanQrCodeAction(){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @OnClick({R.id.button_client_main_payment})
    protected void bpayAction(){
//        Intent intent = new Intent(this, BPayActivity.class);
//        intent.setAction(Intents.Scan.ACTION);
//        startActivity(intent);
        scanQrCodeAction();
    }

    @OnClick({R.id.button_client_main_mydeal})
    protected void showAccountActivity(){
        Intent intent = new Intent(ClientActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_client_main_pointstore})
    protected void showPointStoreActivity(){
        Intent intent = new Intent(ClientActivity.this, PointStoreActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_client_main_mypoint})
    protected void showPointActivity(){
        Intent intent = new Intent(ClientActivity.this, PointActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_client_main_mybalance})
    protected void showBalanceActivity(){
        Intent intent = new Intent(ClientActivity.this, BalanceActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_client_main_mydiscount})
    protected void showDiscountActivity(){
        Intent intent = new Intent(ClientActivity.this, DiscountActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.button_client_main_discountstore})
    protected void showDiscountStoreActivity(){
        Intent intent = new Intent(ClientActivity.this, DiscountStoreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SCAN ){
            if(resultCode == Activity.RESULT_OK){
                String rawResult = data.getStringExtra(Intents.Scan.RESULT);
                Log.e(getClass().getSimpleName(), "** 信息 >> 获得Scanner返回数据:" + rawResult);
                ScanResDispatch.dispatch(ClientActivity.this, rawResult);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*侧边栏点击时间处理*/
    private class MyNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.account_item:{
                    Intent intent = new Intent(ClientActivity.this, UserActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.application_item:{
                    Intent intent = new Intent(ClientActivity.this, SettingActivity.class);
//                    intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
//                    intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingFragment.class.getName());
                    startActivity(intent);
                    return true;
                }
                case R.id.about_item:{
                    Intent intent = new Intent(ClientActivity.this, AboutActivity.class);
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        }
    }

    protected void initWeekAccount(){
        mRecycleView.setNestedScrollingEnabled(false);
        mAccountListAdapter = new AccountListAdapter(this);
        mAccountListAdapter.setDealList(mWeekDealList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAccountListAdapter.setFooterViewClickedListener(null);// 在setAdapter前调用
        mRecycleView.setAdapter(mAccountListAdapter);
        mRecycleView.setLayoutManager(layoutManager);
        mAccountListAdapter.setLoadStatus(FooterViewHolder.STATUS_LOAD_FULL);
        if(mWeekDealList.size() == 0) {
            loadWeekAccountlList();
        }
    }

    @OnClick({R.id.imageView_item_account_header_loading})
    protected void onRefreshWeekAccountList(){
        loadWeekAccountlList();
    }

    private void loadWeekAccountlList(){
        mWeekDealList.clear();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.refresh);
        mRefresh.startAnimation(animation);
        MyUserJsonObjectRequest jsonObjectRequest = new MyUserJsonObjectRequest(Request.Method.POST, Params.URL_WEEK_ACCOUNT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(ClientActivity.class.getSimpleName(), response.toString());
                mRefresh.clearAnimation();
                try {
                    int code = response.getInt("code");
                    if (code == 1) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray jsonArray = data.getJSONArray("sales");
                        ArrayList<DealBean> list = DealBean.getDealListFromJSONObject(jsonArray);
                        mWeekDealList.addAll(list);
                        mAccountListAdapter.notifyDataSetChanged();
                    }
                    String msg = response.getString("msg");
                    Log.e(ClientActivity.class.getSimpleName(), " ** 信息 >> getWeekAccount response : " + msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(ClientActivity.class.getSimpleName(), "** 信息 >> " + error.toString());
                mRefresh.clearAnimation();
            }
        }) {
            @Override
            public String getUserToken() {
                return mToken;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }

    /*测试数据*/
    private ArrayList<DealBean> getDealList(){
        ArrayList<DealBean> dealLists = new ArrayList<>();
        DealBean dealBean1 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean2 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean3 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean4 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean5 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean6 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean7 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean8 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean9 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        DealBean dealBean10 = new DealBean("0001", 1, 1, 1, "{}", 2000, 0, 2000, "{}", "", 1525939619, 1525939619, 3, "GT商铺");
        dealLists.add(dealBean1);dealLists.add(dealBean2);
        dealLists.add(dealBean3);dealLists.add(dealBean4);
        dealLists.add(dealBean5);dealLists.add(dealBean6);
        dealLists.add(dealBean7);dealLists.add(dealBean8);
        dealLists.add(dealBean9);dealLists.add(dealBean10);
        return dealLists;
    }
}
