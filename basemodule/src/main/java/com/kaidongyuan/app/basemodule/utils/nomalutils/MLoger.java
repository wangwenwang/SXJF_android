package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.util.Log;

/**
 * Log工具，上线时候应关闭设置debug = false
 *
 * @author kechangwei
 */
public class MLoger {

  private static boolean debug = true;
  public static String tag = "kdy_debug";



  /**
   * The Log Level:i
   *
   * @param str
   */
  public static void i(Object str) {
    if (debug) {
      String name = getFunctionName();
      if (name != null) {
        Log.i(tag,
                name
                        + "\n"
                        + str.toString());
      }
    }
  }

  public static void s(Object str) {
    if (debug) {
      String name = getFunctionName();
      if (name != null) {
        System.out
                .println(name
                        + "\n"
                        + str.toString());
      }
    }

  }

  /**
   * The Log Level:d
   *
   * @param str
   */
  public static void d(Object str) {
    if (debug) {
      String name = getFunctionName();
      if (name != null) {
        Log.d(tag,
                name
                        + "\n"
                        + str.toString());
      }
    }
  }

  /**
   * The Log Level:V
   *
   * @param str
   */
  public static void v(Object str) {
    if (debug) {
      String name = getFunctionName();
      if (name != null) {
        Log.v(tag,
                name
                        + "\n"
                        + str.toString());
      }
    }
  }

  /**
   * The Log Level:w
   *
   * @param str
   */
  public static void w(Object str) {
    if (debug) {
      String name = getFunctionName();
      if (name != null) {

        Log.w(tag,
                name
                        + "\n"
                        + str.toString());
      }
    }
  }

  /**
   * The Log Level:e
   *
   * @param str
   */
  public static void e(Object str) {
    if (debug) {
        String name = getFunctionName();
        if (name != null) {
          Log.e(tag,
                  name
                          + "\n"
                          + str.toString());
      }
    }
  }


  /**
   * Get The Current Function Name
   *
   * @return
   */
  private static String getFunctionName() {
    StackTraceElement[] sts = Thread.currentThread().getStackTrace();
    if (sts == null) {
      return null;
    }
    for (StackTraceElement st : sts) {
      if (st.isNativeMethod()) {
        continue;
      }
      if (st.getClassName().equals(Thread.class.getName())) {
        continue;
      }
      return "[ " + Thread.currentThread().getName() + " ]";
    }
    return null;
  }

}
