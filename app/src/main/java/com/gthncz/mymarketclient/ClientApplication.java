package com.gthncz.mymarketclient;

import android.app.Application;

import com.gthncz.mymarketclient.greendao.User;

/**
 * 存储部分全局变量
 * Created by GT on 2018/5/5.
 */

public class ClientApplication extends Application {

    private static ClientApplication mInstance;

    public static final boolean DEBUG = true;

    private User mUser;
    private String mToken;

    public synchronized void setUser(User user){
        this.mUser = user;
    }

    public synchronized User getUser(){
        return this.mUser;
    }

    public synchronized void setToken(String token){
        this.mToken = token;
    }

    public synchronized String getToken(){
        return mToken;
    }

    public synchronized static final ClientApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onTerminate() {
        mInstance = null;
        super.onTerminate();
    }
}