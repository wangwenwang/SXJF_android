package com.kaidongyuan.app.basemodule.widget;

import android.util.Log;

public class LMLog {
  private static final String LOG_TAG_LM = "LM";
  private static boolean debug = false;

  public static void v(String msg){
    if (debug) {
      Log.v(LOG_TAG_LM, msg == null ? "null" : msg);
    }
  }

  public static void d(String msg){
    if (debug) {
      Log.d(LOG_TAG_LM, msg == null ? "null" : msg);
    }
  }

  public static void i(String msg){
    if (debug) {
      Log.i(LOG_TAG_LM, msg == null ? "null" : msg);
    }
  }

  public static void w(String msg){
    if (debug) {
      Log.i(LOG_TAG_LM, msg == null ? "null" : msg);
    }
  }

  public static void e(String msg){
    if (debug) {
      Log.e(LOG_TAG_LM, msg == null ? "null" : msg);
    }
  }
}
