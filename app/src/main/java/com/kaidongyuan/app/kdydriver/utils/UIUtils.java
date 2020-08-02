package com.kaidongyuan.app.kdydriver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.kaidongyuan.app.kdydriver.app.AppContext;


/**
 */
public class UIUtils {
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    private static Handler handler = new Handler();

    /**
     * 上下文的获取
     */
    public static Context getContext() {
        return AppContext.getInstance();
    }

    /**
     * 获取资源
     */
    public static Resources getResources() {
        return getContext().getResources();
    }


    /**
	 *像素和dp直接的转换方法
     * @param dip
     * @return
     */
	  //   像素转换的使用事例：
	  // final Button btn_login = (Button) findViewById(R.id.btn_login);
      //  LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) btn_login.getLayoutParams();
      //  layoutParams.width=UIUtils.dip2px(60);
      //  btn_login.setLayoutParams(layoutParams);
    public static int dip2px(int dip) {
        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (dip * density + 0.5f);
    }

    public static int px2dip(int px) {
        // 公式 1: px = dp * (dpi / 160)
        // 公式 2: dp = px / denistity;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        // metrics.densityDpi
        return (int) (px / density + 0.5f);
    }

    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    public static String getPackageName() {
        return getContext().getPackageName();
    }

    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    public static int getScreentWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;// 获取屏幕分辨率宽度
    }


    /**
     * 开启activity
     */
    public static void startActivity(Intent intent) {
        getContext().startActivity(intent);
    }

    //拼接商品详情Url
    public static String urlSpan(String id, int type) {
        StringBuffer sb = new StringBuffer("http://www.smdyshop.com/Web/App/ProductDetail?id=");
        sb.append(id);
        sb.append("&type=");
        sb.append(type);
        return sb.toString();
    }

    //获取屏幕高度
    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    //获取屏幕宽度
    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    /**
     * 运气一个任务在主线程
     *
     * @param runnable
     */
    public static final void post(Runnable runnable) {
        post(runnable, 0);
    }

    /**
     * 运气一个任务在主线程
     *
     * @param runnable
     */
    public static final void post(Runnable runnable, int delayMillis) {
        long tid = Looper.getMainLooper().getThread().getId();
        if (tid == Thread.currentThread().getId()) {
            runnable.run();
            return;
        }
        handler.postDelayed(runnable, delayMillis);
    }

    //检验版本
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
