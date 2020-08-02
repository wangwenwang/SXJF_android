package com.kaidongyuan.app.basemodule.widget;

import android.util.Log;

/**
 * Log工具，打印信息
 * 上线时候应关闭设置debug = false
 * Created by changwei on 2015/8/29.
 */
public class MLog {
  private static final String LOG_TAG = "ke_test";
  private static boolean debug = false;

  public static void v(String msg){
      if (debug) {
        Log.v(LOG_TAG, msg == null ? "null" : msg);
      }
  }

  public static void d(String msg){
    if (debug) {
      Log.d(LOG_TAG, msg == null ? "null" : msg);
    }
  }

  public static void i(String msg){
    if (debug) {
      Log.i(LOG_TAG, msg == null ? "null" : msg);
    }
  }

  public static void w(String msg){
    if (debug) {
      Log.i(LOG_TAG, msg == null ? "null" : msg);
    }
  }

  public static void e(String msg){
    if (debug) {
      Log.e(LOG_TAG, msg == null ? "null" : msg);
    }
  }
}

