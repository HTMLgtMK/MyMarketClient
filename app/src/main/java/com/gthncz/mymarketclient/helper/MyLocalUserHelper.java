package com.gthncz.mymarketclient.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.gthncz.mymarketclient.beans.Params;
import com.gthncz.mymarketclient.greendao.ClientDBHelper;
import com.gthncz.mymarketclient.greendao.User;
import com.gthncz.mymarketclient.greendao.UserDao;
import com.gthncz.mymarketclient.greendao.UserToken;
import com.gthncz.mymarketclient.greendao.UserTokenDao;

import java.util.List;

/**
 * 本地用户操作帮助类
 * Created by GT on 2018/5/21.
 */

public class MyLocalUserHelper {

    public MyLocalUserHelper(){}

    /**
     * 获取当前已经登陆的用户信息
     * @param context
     * @return
     */
    public static synchronized User getLocalUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Params.INI_NAME, Context.MODE_PRIVATE);
        long userId  = preferences.getLong(Params.KEY_CURRENT_USER_ID, 0L);
        if(userId == 0) return null; // 目前没有会员登陆
        List<User> users = ClientDBHelper.getInstance(context).getDaoSession().getUserDao()
                .queryBuilder().where(UserDao.Properties.Id.eq(userId)).limit(1).list();
        User user = null;
        if(users != null && users.size() > 0){
            user = users.get(0);
        }
        return user;
    }

    /**
     * 获取当前已经登陆用户的token
     * @param context
     * @return
     */
    public static synchronized String getLocalToken(Context context){
        UserToken userToken = getUserToken(context);
        String token = userToken == null ? null : userToken.getToken();
        return token;
    }

    /**
     * 判断本地用户是否过期
     * @param context
     * @return
     */
    public static boolean isLocalUserExpired(Context context){
        UserToken userToken = getUserToken(context);
        if(userToken == null) return true;
        if(System.currentTimeMillis()/1000 > userToken.getExpire_time()) return true;
        else return false;
    }

    private static UserToken getUserToken(Context context){
        SharedPreferences  preferences = context.getSharedPreferences(Params.INI_NAME, Context.MODE_PRIVATE);
        long userId = preferences.getLong(Params.KEY_CURRENT_USER_ID, 0L);
        if(userId == 0) return null;
        List<UserToken> tokens = ClientDBHelper.getInstance(context).getDaoSession().getUserTokenDao()
                .queryBuilder().where(UserTokenDao.Properties.User_id.eq(userId)).limit(1).list();
        UserToken token = null;
        if(tokens != null && tokens.size() > 0){
            token = tokens.get(0);
        }
        return token;
    }

}
