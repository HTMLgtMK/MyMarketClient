package com.gthncz.mymarketclient;

import android.app.Application;

import com.gthncz.mymarketclient.greendao.User;

/**
 * 存储部分全局变量
 * 注: 在这里存储变换的全局变量不明智，
 * 因为当应用由于内存低或者崩溃灯情况，
 * 会重新create Application，导致
 * 存储的全局变量为null.
 * 对应解决方法是利用持久化存储。
 *
 * Created by GT on 2018/5/5.
 */

public class ClientApplication extends Application {

    public static final boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}