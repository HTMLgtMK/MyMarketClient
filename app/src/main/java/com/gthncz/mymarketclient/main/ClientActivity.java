package com.gthncz.mymarketclient.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gthncz.mymarketclient.ClientApplication;
import com.gthncz.mymarketclient.R;
import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.ClientDBHelper;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.greendao.UserDao;
import com.gthncz.qrcodescannner.QrCodeScannerActivity;
import com.gthncz.qrcodescannner.camera.Intents;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GT on 2018/5/6.
 */

public class ClientActivity extends AppCompatActivity {

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
    // left menu resource
    @BindView(R.id.navigation_header_container) protected NavigationView mNavigationView;
    protected ImageView mAvatar;
    protected TextView mNickname;

    private User mUser;
    /*请求码*/
    private final int REQUEST_CODE_SCAN = 0x01;

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
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // TODO to do click item event
                return false;
            }
        });
        View headerView = mNavigationView.getHeaderView(0);
        mNickname = headerView.findViewById(R.id.textView_header_nickname);
        mAvatar = headerView.findViewById(R.id.imageView_header_avatar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*获取当前登陆用户*/
        mUser = ClientApplication.getInstance().getUser();
        /*设置UI控件文字*/
        mNickname.setText(mUser.getUser_nickname());
    }

    @OnClick({R.id.button_client_main_scanecode})
    protected void scanQrCodeAction(){
        Intent intent = new Intent(this, QrCodeScannerActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
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
}
