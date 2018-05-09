package com.gthncz.mymarketclient.greendao;

import android.content.Context;
import android.content.SharedPreferences;

import com.gthncz.mymarketclient.beans.Params;

import java.lang.ref.WeakReference;

/**
 * 客户端使用Database的帮助类
 * Created by GT on 2018/5/5.
 */

public class ClientDBHelper {

    private static ClientDBHelper mInstance;

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static DaoMaster.DevOpenHelper devOpenHelper;

    private WeakReference<Context> weakContext;

    /**
     * 获取Helper的实例
     * @return
     */
    public static ClientDBHelper getInstance(Context context){
        if(mInstance == null) {
            mInstance = new ClientDBHelper();
            mInstance.weakContext = new WeakReference<Context>(context);
        }
        return mInstance;
    }

    /*构造器不可见*/
    private ClientDBHelper(){}

    public DaoMaster getDaoMaster() {
        if(daoMaster == null && weakContext.get() != null){
            devOpenHelper = new DaoMaster.DevOpenHelper(weakContext.get(), Params.DB_NAME, null);
            daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        }
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        if(daoSession == null){
            if(daoMaster == null){
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public DaoMaster.DevOpenHelper getDevOpenHelper() {
        return devOpenHelper;
    }

    public void closeDevOpenHelper(){
        if(devOpenHelper != null){
            devOpenHelper.close();
            devOpenHelper = null;
        }
    }

    public void closeDaoSession(){
        if(daoSession != null){
            daoSession.clear();
            daoSession = null;
        }
    }

    public void closeConnection(){
        closeDevOpenHelper();
        closeDaoSession();
    }

    /**
     * 数据库是否已经被初始化
     * @return
     */
    public boolean isDBInited(){
        boolean inited = false;
        if(weakContext.get() != null){
            SharedPreferences sharedPreferences = weakContext.get().getSharedPreferences(Params.INI_NAME, Context.MODE_PRIVATE);
            inited = sharedPreferences.getBoolean("db_inited", false);
        }
        return inited;
    }

    /**
     * 第一次使用初始化数据
     * 重新初始化前要卸载app或者删除数据表中的数据
     */
    public void init(){
        /*插入用户等级*/
        getDaoSession().getUserLevelDao().deleteAll();//删除全部

        // construct user_level entity
        UserLevel userLevel11 = new UserLevel(1, "Level 1", 1, 1);
        UserLevel userLevel12 = new UserLevel(2, "Level 2", 0, 1);
        UserLevel userLevel13 = new UserLevel(3, "Level 3", 0, 1);
        UserLevel userLevel14 = new UserLevel(4, "Level 4", 0, 1);
        UserLevel userLevel15 = new UserLevel(5, "Level 5", 0, 1);
        UserLevel userLevel16 = new UserLevel(6, "Level 6", 0, 1);
        UserLevel userLevel17 = new UserLevel(7, "Level 7", 0, 1);
        UserLevel userLevel18 = new UserLevel(8, "Level 8", 0, 1);
        UserLevel userLevel19 = new UserLevel(9, "Level 9", 0, 1);
        UserLevel userLevel110 = new UserLevel(10, "Level 10", 0, 1);

        // insert
        getDaoSession().getUserLevelDao().insert(userLevel11);
        getDaoSession().getUserLevelDao().insert(userLevel12);
        getDaoSession().getUserLevelDao().insert(userLevel13);
        getDaoSession().getUserLevelDao().insert(userLevel14);
        getDaoSession().getUserLevelDao().insert(userLevel15);
        getDaoSession().getUserLevelDao().insert(userLevel16);
        getDaoSession().getUserLevelDao().insert(userLevel17);
        getDaoSession().getUserLevelDao().insert(userLevel18);
        getDaoSession().getUserLevelDao().insert(userLevel19);
        getDaoSession().getUserLevelDao().insert(userLevel110);

        // update ini file
        SharedPreferences sharedPreferences = weakContext.get().getSharedPreferences(Params.INI_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("db_inited", true);
        editor.commit();
    }
}
