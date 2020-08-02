package com.kaidongyuan.app.kdydriver.constants;

import android.os.Environment;

import com.kaidongyuan.app.basemodule.utils.nomalutils.Base64;

/**
 * 存储一些关于服务器地址的常量信息
 *
 */
public class Constants {
    /**
     *  发起定位请求的间隔时间
     */
    public static int scanSpan = 1000 * 60 * 6;
    public static final String BasicInfo="dataBases.BasicInfo";
    public static final String IsUsedApp="isusedapp";
    public static final String WXLogin_AppID="";
    public static final String WXLogin_AppSecret="";

    public class URL {
        public static final String LoadVersion_Url = "";
        public static final String Base_Url = "";
        public static final String register="";
        public static final String Login = Base_Url;
        public static final String Information = Base_Url;
        //获取用户推送消息列表和内容
        public static final String CurrentLocaltion = Base_Url;
        public static final String CurrentLocationList = Base_Url;
        /**
         * 获取最新版本 app 信息
         */
        public static final String CheckVersion = Base_Url;
        /**
         * 推送功能，上传CID UserID
         */
        public static final String SavaPushToken=Base_Url;


        public static  final String SAAS_API_BASE = "http://113.105.119.25:8099/tmsApp/";
//        public static  final String SAAS_API_BASE = "http://sfkc.cy-scm.com:9998/tmsApp/";
//        public static  final String SAAS_API_BASE = "http://sfkc.sf-express.com/tmsApp/";
    }
}
