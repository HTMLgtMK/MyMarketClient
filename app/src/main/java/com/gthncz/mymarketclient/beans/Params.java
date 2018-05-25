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
    /*最近一周的账单获取地址*/
    public static final String URL_WEEK_ACCOUNT = URL_SERVER_BASE + "api/client/Account/index";
    /*账单地址*/
    public static final String URL_ACCOUNT_LIST = URL_SERVER_BASE + "api/client/Account/account";
    /*会员领取积分地址*/
    public static final String URL_USER_OBTAIN_POINT = URL_SERVER_BASE + "api/user/User_Point/obtain";
    /*会员积分地址*/
    public static final String URL_USER_MYPOINT = URL_SERVER_BASE + "api/user/User_Point/mypoint";
    /*会员积分转余额地址*/
    public static final String URL_USER_POINT2BALANCE = URL_SERVER_BASE + "api/user/User_Point/point2balance";
    /*会员余额地址*/
    public static final String URL_USER_MYBALANCE = URL_SERVER_BASE + "api/user/User_Balance/mybalance";
    /*会员余额明细地址*/
    public static final String URL_USER_BALANCE_DETAIL = URL_SERVER_BASE +  "api/user/User_Balance/balanceIndex";
    /*会员积分明细地址*/
    public static final String URL_USER_POINT_DETAIL = URL_SERVER_BASE + "api/user/User_Point/pointIndex";
    /*优惠广场地址*/
    public static final String URL_DISCOUNT_STORE_INDEX = URL_SERVER_BASE + "api/market/Discount/index";
    /*领取优惠地址*/
    public static final String URL_DISCOUNT_OBTAIN = URL_SERVER_BASE + "api/market/Discount/obtain";
    /*会员优惠列表地址*/
    public static final String URL_USER_DISCOUNS = URL_SERVER_BASE + "api/market/Discount/mydiscount";
    /*会员头像上传地址*/
    public static final String URL_USER_AVATAR_UPLOAD = URL_SERVER_BASE + "api/user/Upload/uploadAvatar";
    /*会员头像地址*/
    public static final String URL_USER_AVATAR = URL_SERVER_BASE + "api/user/User_Info/avatar";
    /*检查余额支付的状态地址*/
    public static final String URL_CHECK_BALANCE_PAY_STATUS = URL_SERVER_BASE + "api/user/User_Payment/checkBalancePayStatus";
    /*用户余额支付地址*/
    public static final String URL_BALANCE_PAY = URL_SERVER_BASE + "api/user/User_Payment/balance_pay";
    /*用户修改支付密码地址*/
    public static final String URL_MODIFY_PAY_PASSWORD = URL_SERVER_BASE + "api/user/User_Payment/modify_pay_password";
}
