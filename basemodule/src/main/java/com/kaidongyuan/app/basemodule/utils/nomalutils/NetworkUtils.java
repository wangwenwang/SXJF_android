package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.kaidongyuan.app.basemodule.widget.MLog;

/**
 * 网络工具类
 * 
 * @author gdpancheng@gmail.com 2013-10-22 下午1:08:35
 */
public class NetworkUtils {

	private static Dialog mNetDialog;
	/**
	 * 显示 Dialog 提示用户连接网络
	 */
	public static void setContactNetDialog(final Application application){
		MLog.w("TrackingService.setContactNetDialog");
		if(mNetDialog==null){
			AlertDialog.Builder builder = new AlertDialog.Builder(application);
			builder.setTitle("友情提示：\n点击确定开启网络");
			builder.setCancelable(false);
			builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent=new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					application.startActivity(intent);
					mNetDialog.cancel();
				}
			});
			builder.setNegativeButton("取消",  new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mNetDialog.cancel();
				}
			});
			mNetDialog = builder.create();
			mNetDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		}
		mNetDialog.show();
	}
	/**
	 * 检测手机是否开启GPRS网络,需要调用ConnectivityManager,TelephonyManager 服务.
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean checkGprsNetwork(Context context) {
		boolean has = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming()) {
			has = info.isConnected();
		}
		return has;

	}

	/**
	 * 检测当前手机是否联网
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}


}
