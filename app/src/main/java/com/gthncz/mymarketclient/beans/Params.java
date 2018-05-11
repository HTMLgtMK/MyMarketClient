package com.gthncz.mymarketclient.beans;

/**
 * Created by GT on 2018/5/5.
 */

public class Params {
    /*配置名称*/
    public static final String INI_NAME = "client_ini";
    /*数据库名称*/
    public static final String DB_NAME = "db_market";
    /*当前会员的ID键名*/
    public static final String KEY_CURRENT_USER_ID = "CURRENT_USER_ID";

    /**服务器地址
     * 1,如果你使用的模拟器是AndroidStudio自带的,那么你如果想要访问本地的服务器,那你必须把IP地址改成: 10.0.2.2或者你的本机实际ip地址(cmd里ipconfig就可以看到)
     * 2,如果你使用的模拟器是Genymotion的话,那你就需要把你的IP地址改成 10.0.3.2才可以!!!
     * 3.如果使用的是手机真机做测试，则服务器地址为电脑热点的网关地址 http://192.168.137.1:8888/
     */
    public static final String URL_SERVER_BASE = "http://192.168.137.1:8888/";
    /*会员登陆*/
    public static final String URL_USER_LOGIN =  URL_SERVER_BASE + "api/user/public/login";
    /*会员注册获取验证码地址*/
    public static final String URL_REGISTER_GET_VERIFICATION_CODE = URL_SERVER_BASE + "api/user/public/send_verify_code";
    /*会员注册地址*/
    public static final String URL_USRT_REGISTER = URL_SERVER_BASE + "api/user/public/register";
    /*会员授权请求地址*/
    public static final String URL_USR_GRANT_REQUEST = URL_SERVER_BASE + "api/user/User_Grant/grantReq";
    /*检查会员授权状态地址*/
    public static final String URL_USER_QUERY_GRANT_STATUS = URL_SERVER_BASE + "api/user/User_Grant/queryGrantStatus";
    /*会员授权地址*/
    public static final String URL_USER_GRANT = URL_SERVER_BASE + "api/user/User_Grant/grant";
    /*会员授权用户扫描检查授权地址*/
    public static final String URL_USER_SCAN = URL_SERVER_BASE + "api/user/User_Grant/scan";
    /*会员登出地址*/
    public static final String URL_USER_LOGOUT = URL_SERVER_BASE + "api/user/public/logout";
}
