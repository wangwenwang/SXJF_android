package com.kaidongyuan.app.kdydriver.bean;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.kaidongyuan.app.basemodule.utils.nomalutils.SystemUtil;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.ui.activity.ScanActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.WINDOW_SERVICE;
import static android.widget.Toast.LENGTH_LONG;

public class Tools{

	private int logo;

	private String name;

	public int getLogo() {
		return logo;
	}

	public void setLogo(int logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tools(int logo, String name) {
		super();
		this.logo = logo;
		this.name = name;
	}

	/**
	 * 通过ping判断是否可用
	 * @return
	 */
	public static boolean ping() {
		try {
			//服务器ip地址
			String ip = "www.baidu.com";
			Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content;
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}
			int status = p.waitFor();
			if (status == 0) {
				return true;
			}
		}
		catch (IOException e) {}
		catch (InterruptedException e) {}
		return false;
	}

	/**
	 * 版本号比较
	 *
	 * @param server 服务器版本号
	 * @param locati 本地版本号
	 * @return
	 */
	public static int compareVersion(String server, String locati){
		if (server.equals(locati)) {
			return 0;
		}
		String[] version1Array = server.split("\\.");
		String[] version2Array = locati.split("\\.");
		Log.d("HomePageActivity", "version1Array==" + version1Array.length);
		Log.d("HomePageActivity", "version2Array==" + version2Array.length);
		int index = 0;
		// 获取最小长度值
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;
		// 循环判断每位的大小
		Log.d("HomePageActivity", "verTag2=2222=" + version1Array[index]);
		while (index < minLen
				&& (diff = Integer.parseInt(version1Array[index])
				- Integer.parseInt(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			// 如果位数不一致，比较多余位数
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}

	/**
	 * 下载进度条
	 */
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
		//如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			//获取到文件的大小
			pd.setMax(conn.getContentLength() / 1000 / 1000);
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ScanActivity.ZipFileName);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len ;
			int total=0;
			while((len =bis.read(buffer))!=-1){
				fos.write(buffer, 0, len);
				total+= len;
				//获取当前下载量
				double progressD = total / 1000 / 1000.0;
				String progressS = doubleToString(progressD);
				pd.setProgress(total / 1000 / 1000);
				pd.setProgressNumberFormat(progressS + "m");
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		}
		else{
			return null;
		}
	}

	/**
	 * double转String,保留小数点后一位
	 * @param num
	 * @return
	 */
	public static String doubleToString(double num){
		//使用0.0不足位补0，#.#仅保留有效位
		return new DecimalFormat("0.0").format(num);
	}


	public static String inputStream2String (InputStream in) throws IOException {

		StringBuffer out = new StringBuffer();
		byte[]  b = new byte[4096];
		int n;
		while ((n = in.read(b))!= -1){
			out.append(new String(b,0,n));
		}
		Log.i("String的长度",new Integer(out.length()).toString());
		return  out.toString();
	}

	/**
	 * 获取版本号名称
	 *
	 * @param context 上下文
	 * @return
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().
					getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	/**
	 * 获取当前时间，格式：yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getCurrDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate = df.format(new java.util.Date());
		return curDate;
	}

	/**
	 * 计算时间差
	 *
	 * @return 分钟
	 */
	public static long getTimeExpend(String startTime, String endTime){
		//传入字串类型
		long longStart = getTimeMillis(startTime); //获取开始时间毫秒数
		long longEnd = getTimeMillis(endTime);  //获取结束时间毫秒数
		long longExpend = longEnd - longStart;  //获取时间差
		long longMinutes = longExpend / (60 * 1000);   //根据时间差来计算分钟数
		return longMinutes;
	}

	private static long getTimeMillis(String strTime) {
		long returnMillis = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;

		try {
			d = sdf.parse(strTime);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			Toast.makeText(ScanActivity.mContext, e.toString(), Toast.LENGTH_SHORT).show();
		}
		returnMillis = d.getTime();
		return returnMillis;
	}


	/**
	 * 跳转手机地图APP
	 *
	 * @param address 地址
	 * @param mContext  上下文
	 * @param appName   APP名字
	 *
	 * @return
	 */
	public static void ToNavigation(final String address, final Context mContext, final String appName) {


        List list = new ArrayList();
        if (SystemUtil.isInstalled(mContext, "com.autonavi.minimap")) {

            list.add("高德地图");
        }
        if (SystemUtil.isInstalled(mContext, "com.baidu.BaiduMap")) {

            list.add("百度地图");
        }

        PromptDialog promptDialog = new PromptDialog((Activity) ScanActivity.mContext);
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);

        PromptButton cancle = new PromptButton("取消", null);
        cancle.setTextColor(Color.parseColor("#0076ff"));
        if (list.size() == 2) {
            promptDialog.showAlertSheet("请选择地图", true, cancle,
                    new PromptButton("高德地图", new PromptButtonListener() {
                        @Override
                        public void onClick(PromptButton button) {

                            Log.d("LM", "调用高德地图");
                            minimap(mContext, address, appName);
                        }
                    }),
                    new PromptButton("百度地图", new PromptButtonListener() {
                        @Override
                        public void onClick(PromptButton button) {

                            Log.d("LM", "调用百度地图");
                            BaiduMap(mContext, address);
                        }
                    })
            );
        } else if (list.size() == 1) {

            if (list.get(0).equals("高德地图")) {

                Log.d("LM", "调用高德地图");
                minimap(mContext, address, appName);
            } else if (list.get(0).equals("百度地图")) {

                Log.d("LM", "调用百度地图");
                BaiduMap(mContext, address);
            }
        } else {

            Toast.makeText(mContext, "未检索到本机已安装‘百度地图’或‘高德地图’App", LENGTH_LONG).show();
        }
    }


    private static void minimap(Context mContext, String address, String appName) {
        //跳转到高德导航
        Intent autoIntent = new Intent();
        try {
            autoIntent.setData(Uri
                    .parse("androidamap://route?" +
                            "sourceApplication=" + appName +
                            "&slat=" + "" +
                            "&slon=" + "" +
                            "&dlat=" + "" +
                            "&dlon=" + "" +
                            "&dname=" + address +
                            "&dev=0" +
                            "&m=2" +
                            "&t=0"
                    ));
        } catch (Exception e) {
            Log.i("LM", "高德地图异常" + e);
        }
        mContext.startActivity(autoIntent);
    }

    private static void BaiduMap(Context mContext, String address) {

        //跳转到百度导航
        try {
            Intent baiduintent = Intent.parseUri("intent://map/direction?" +
                    "origin=" + "" +
                    "&destination=" + address +
                    "&mode=driving" +
                    "&src=Name|AppName" +
                    "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
            mContext.startActivity(baiduintent);
        } catch (URISyntaxException e) {
            MLog.d("URISyntaxException : " + e.getMessage());
            e.printStackTrace();
        }
    }



	/**
	 * 使用get方式与服务器通信
	 * @return
	 */
	public static String timingTracking(String cellphone, String vehicleLocation, String lon, String lat, String code, String brightscreen, String charging, String os) {

		Log.d("LM", "上传定位点，网络请求");

		String params1 =
				"{" +
						"\"cellphone\":\"" + cellphone + "\"," +
						"\"userName\":\"" + "" + "\"," +
						"\"vehicleLocation\":\"" + vehicleLocation + "\"," +
						"\"lon\":\"" + lon + "\"," +
						"\"lat\":\"" + lat + "\"," +
						"\"uuid\":\"" + "android" + "\"," +
						"\"code\":\"" + code + "\"," +
						"\"brightscreen\":\"" + brightscreen + "\"," +
						"\"charging\":\"" + charging + "\"," +
						"\"os\":\"" + os + "\"" +
				"}";

		Log.d("LM", "params1: " + params1);

		Map<String, String> params = new HashMap<>();
		params.put("params", params1);
		byte[] data = getRequestData(params, "utf-8").toString().getBytes();//获得请求体
		try {

			Log.d("LM", "上传位置");
			URL url = new URL(Constants.URL.SAAS_API_BASE + "timingTracking.do");

			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setConnectTimeout(5000);     //设置连接超时时间
			httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
			httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
			httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
			httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
			//设置请求体的类型是文本类型
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//设置请求体的长度
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
			//获得输出流，向服务器写入数据
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);

			int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
			if(response == HttpURLConnection.HTTP_OK) {
				Log.d("LM", "上传位置成功");
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream);                     //处理服务器的响应结果
			}
		} catch (IOException e) {
			//e.printStackTrace();
			return "err: " + e.getMessage().toString();
		}
		return "timingTracking请求结束";
	}

	/*
	 * Function  :   封装请求体信息
	 * Param     :   params请求体内容，encode编码格式
	 */
	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
		try {
			for(Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/*
	 * Function  :   处理服务器的响应结果（将输入流转化成字符串）
	 * Param     :   inputStream服务器的响应输入流
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null;      //存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		return resultData;
	}

	/**
	 *  解压assets的zip压缩文件到指定目录
	 *  @param  context 上下文对象
	 *  @param  assetName 压缩文件名
	 *  @param  outputDirectory 输出目录
	 *  @param  isReWrite 是否覆盖
	 *  @throws IOException
	 */
	public static void unZip(Context context, String assetName, String outputDirectory, boolean isReWrite) throws IOException {
		// 创建解压目标目录
		File file = new File(outputDirectory);
		//如果目标目录不存在，则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		// 打开压缩文件
		InputStream inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		// 读取一个进入点
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		// 使用1Mbuffer
		byte[] buffer = new byte[1024 * 1024];
		//解压时字节计数
		int count = 0;
		// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
		while (zipEntry != null) {
			//如果是一个目录
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator + zipEntry.getName());                // 文件需要覆盖或者是文件不存在
				if (isReWrite || !file.exists()) {
					file.mkdir();
				}
			} else {
				// 如果是文件
				file = new File(outputDirectory + File.separator + zipEntry.getName());
				// 文件需要覆盖或者文件不存在，则解压文件
				if (isReWrite || !file.exists()) {
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
			}
			// 定位到下一个文件入口
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
	}

	public static boolean fileIsExists(String strFile) {
		try {
			File f=new File(strFile);
			if(!f.exists()) {
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 解压zip到指定的路径
	 * @param zipFileString  ZIP的名称
	 * @param outPathString   要解压缩路径
	 * @throws Exception
	 */
	public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
		ZipEntry zipEntry;
		String  szName = "";
		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			if (zipEntry.isDirectory()) {
				//获取部件的文件夹名
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(outPathString + File.separator + szName);
				folder.mkdirs();
			} else {
				Log.d("LM","解压：" + outPathString + "   " + File.separator + "   " + szName);
				File file = new File(outPathString + File.separator + szName);
				if (!file.exists()){
					Log.e("LM", "Create the file:" + outPathString + "   " +  File.separator + "   " + szName);
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				// 获取文件的输出流
				FileOutputStream out = new FileOutputStream(file);
				int len;
				byte[] buffer = new byte[1024];
				// 读取（字节）字节到缓冲区
				while ((len = inZip.read(buffer)) != -1) {
					// 从缓冲区（0）位置写入（字节）字节
					out.write(buffer, 0, len);
					out.flush();
				}
				out.close();
			}
		}
		inZip.close();
	}

	/**
	 * 获取zip版本号
	 * @param mContext 上下文
	 * @return
	 */
	public static String getAppZipVersion(Context mContext) {

		SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
		return pre_appinfo.getString("ZipVersion", "");
	}

	/**
	 * 设置zip版本号
	 * @param mContext 上下文
	 * @param CURR_ZIP_VERSION 版本号
	 * @throws Exception
	 */
	public static void setAppZipVersion(Context mContext, String CURR_ZIP_VERSION) {

		SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
		pre_appinfo.edit().putString("ZipVersion", CURR_ZIP_VERSION).commit();
	}

    /**
     * 获取是否安装使用
     * @param mContext 上下文
     * @return
     */
    public static String getAppInstallationUsed(Context mContext) {

        SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
        return pre_appinfo.getString("InstallationUsed", "");
    }

    /**
     * 设置已安装使用
     * @param mContext 上下文
     * @throws Exception
     */
    public static void setAppInstallationUsed(Context mContext) {

        SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
        pre_appinfo.edit().putString("InstallationUsed", "YES").commit();
    }


    /**
     * 获取上一次启动的版本号
     * @param mContext 上下文
     * @return
     */
    public static String getAppLastTimeVersion(Context mContext) {

        SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
        return pre_appinfo.getString("LastTimeVersion", "");
    }

    /**
     * 设置上一次启动的版本号
     * @param mContext 上下文
     * @throws Exception
     */
    public static void setAppLastTimeVersion(Context mContext) {

        SharedPreferences pre_appinfo = mContext.getSharedPreferences("w_AppInfo", MODE_MULTI_PROCESS);
        pre_appinfo.edit().putString("LastTimeVersion", ScanActivity.local_Version).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
	public static String GetDisplayStatus(Context mContext) {

		WindowManager windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		int screenState = display.getState();
		if(screenState == 1) {
			return "0";
		}else if(screenState == 2) {
			return "1";
		}else{
			return "2";
		}
	}

	public static String GetChargingStatus(Context mContext) {

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = mContext.registerReceiver(null, ifilter);

		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				status == BatteryManager.BATTERY_STATUS_FULL;

		int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		final boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		final boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

		if(isCharging == true) {

			if(usbCharge == true) {

//				Toast.makeText(mContext, "USB", Toast.LENGTH_SHORT).show();
				return "1";
			} else if(acCharge == true) {

//				Toast.makeText(mContext, "AC", Toast.LENGTH_SHORT).show();
				return "2";
			}else {

//				Toast.makeText(mContext, "未知充电方式", Toast.LENGTH_SHORT).show();
				return "3";
			}
		}else {

//			Toast.makeText(mContext, "未充电", Toast.LENGTH_SHORT).show();
			return "0";
		}
	}
}
