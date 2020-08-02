package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class DensityUtil {
	private static Context context;
	private static int width;
	private static int height;
	private static int width_dp;
	private static int height_dp;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		DensityUtil.context = context;
	}

	public static int getWidth() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		width = metrics.widthPixels;// 屏幕的宽px
		return width;
	}

	public static int getHeight() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		height = metrics.heightPixels;// 屏幕的高px
		return height;
	}

	public static int getWidth_dp() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		width_dp = metrics.widthPixels;
		return px2dip(width_dp);// 屏幕的宽dp
	}

	public static int getHeight_dp() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		height_dp = metrics.heightPixels;
		return px2dip(height_dp);// 屏幕的高dp
	}

	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}

}
