package com.kaidongyuan.app.kdydriver.serviceAndReceiver;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import com.kaidongyuan.app.basemodule.utils.nomalutils.DateUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.StringUtils;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.R;
import com.kaidongyuan.app.kdydriver.app.AppContext;
import com.kaidongyuan.app.kdydriver.bean.Tools;
import com.kaidongyuan.app.kdydriver.bean.order.LocationContineTime;
import com.kaidongyuan.app.kdydriver.bean.order.User;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.utils.LocationFileHelper;
import com.kaidongyuan.app.kdydriver.utils.SharedPreferencesUtils;
import com.kaidongyuan.app.kdydriver.utils.baidumapUtils.DataUtil;
import com.kaidongyuan.app.kdydriver.utils.baidumapUtils.UploadCacheLocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台定位服务
 *
 * @author ke
 */
public class TrackingService extends Service {

    private static final String TAG = "upLoad position ------ ";
    private final static String prefName = "configs";// shareprence中的配置名
    private Context mContext = this;
    //  public static final String Action_Tracking_Service = "com.kaidongyuan.app.kdydriver.service.TrackingService";
    public LocationClient mLocationClient;// 百度定位客户端
    public MyLocationListener myListener;// 百度定位监听
    private String tempcoor = "bd09ll";// 百度地图的编码模式
    private double mLat = 0, mLng = 0;
    //高精度模式
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    //仅GPS定位模式
//    private LocationMode tempMode = LocationMode.Device_Sensors;
    boolean isLoop = true;
    private static final double Min_Distance = 0;  // 上传时判断的最小距离
    //    private  static final double Min_Distance=-1;
    private RequestQueue mRequestQueue;
    private final static String Tag_Upload_Position = "Tag_Upload_Position";
    private boolean needClose =false;
    private String mUserId;
    private StopReceiver mReceiver;
    private String mFileName;
    //上传数据线程用到
    private long mNetNotConnetTime = 0;
    private android.os.Handler mHandler;
    private int mScanSpanTime = Constants.scanSpan;
    private Thread mThread;
    private boolean mLocationThreadRunning;
    private String locationaddress;//被定义为百度定位返回code码字段
    private boolean againBoolean=true;
    // private PowerManager.WakeLock wakeLock = null;

    private boolean isUpload = true; // 是否上传，防止20秒内上传多个点

    @Override
    public IBinder onBind(Intent intent) {
        MLog.i("\tTrackinSerice.onstart");
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        MLog.i("\tTrackinSerice.onstart");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("time", "TrackingService.onCreate");
        locationaddress="BDCode";
        mContext = this;
        mLocationClient = new LocationClient(this);
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        initLocationClient();
        showNotification();
        try {
            mUserId=AppContext.getInstance().getUser().getIDX();
            if (mUserId==null||mUserId.isEmpty()){
                mUserId = SharedPreferencesUtils.getUserId();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        mFileName = getApplicationContext().getFilesDir().getAbsolutePath()
                + File.separator + "Location" + File.separator + mUserId + ".log";


        //开启子线程，定时上传数据
        mLocationThreadRunning = true;
        if (mThread==null) {
            mThread = new LocationThread();
            mThread.setName("TrackingServiceThread.stander");
            mThread.setPriority(Thread.MAX_PRIORITY);
        }
        alamManagersend();

        Log.d("LM", "MM");
        mHandler = new android.os.Handler(new android.os.Handler.Callback() {

//              Log.d("LM", "MM1");

            @Override
            public boolean handleMessage(Message msg) {

                if (msg.what==1){
                    setContactNetDialog();
                }

                Log.d("LM", "MM2");
                if (msg.what==2){
                    mNetDialog.cancel();
                }
                return false;
            }
        });
        mThread.start();
    }
    private void alamManagersend(){
        if (Build.VERSION.SDK_INT<19){
            return;
        }else {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), TrackingService.class);
            intent.putExtra("AM", "alarmManager");
            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            Long triggerAtime = System.currentTimeMillis() + 1000 * 60 * 19;
            //针对不同版本的AndroidSDK,采用不同方法的闹钟唤醒定位服务
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtime , pendingIntent);
            } else  {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtime , pendingIntent);
            }

        }
    }
    /**
     * 定时上传点位置信息的线程类
     */
    private class LocationThread extends Thread {
        @Override
        public void run() {
            while (mLocationThreadRunning){
                try {
                    startLocate();
//                    stopLocate();
                    if (!isNetworkAvailable()){
                        mNetNotConnetTime += mScanSpanTime;
                        //超过 28 分钟为联网就提示用户，弹出系统 Dialog
                        if(mNetNotConnetTime>=28*60*1000 ) {
                            mHandler.sendEmptyMessage(1);
                            mNetNotConnetTime = 0;
                        }
                    } else {
                        mNetNotConnetTime = 0;
                        if (mNetDialog!=null){
                            mHandler.sendEmptyMessage(2);
                        }
                    }

                    Thread.currentThread().sleep(mScanSpanTime);

                    // Thread.currentThread().wait(mScanSpanTime);

                    MLog.i("mLocationClient.isStarted():\t"+mLocationClient.isStarted());
                    //   Thread.sleep(1*60*1000);
                    // SharedPreferencesUtils.WriteSharedPreferences("TestDatabase",DataUtil.getStringTime(System.currentTimeMillis()),"定位线程启动时间");
                    againBoolean=true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 服务开始启动
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // acquireWakeLock();
        try {
            if (intent.hasExtra("AM")) {
                MLog.e("AlarmManager唤醒定位服务：\tTrackingService.onStartCommand()");
                //  SharedPreferencesUtils.WriteSharedPreferences("TestDatabase",DataUtil.getStringTime(System.currentTimeMillis()),"AlarmManager唤醒定位服务");
                alamManagersend();
                if (mUserId==null||mFileName==null){
                    mUserId = SharedPreferencesUtils.getUserId();
                    mFileName = getApplicationContext().getFilesDir().getAbsolutePath()
                            + File.separator + "Location" + File.separator + mUserId + ".log";
                }
//            if (mThread!=null){
//                mThread.notify();
//            }
                startLocate();
            }else {
                MLog.e("TrackingService.onStartCommand()");
                mUserId = SharedPreferencesUtils.getUserId();
                mFileName = getApplicationContext().getFilesDir().getAbsolutePath()
                        + File.separator + "Location" + File.separator + mUserId + ".log";
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return START_STICKY;
    }


    /**
     * 定位SDK监听函数
     */
    private class MyLocationListener implements BDLocationListener {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @Override
        public void onReceiveLocation(BDLocation location) {

            Log.d("LM", "进入定位函数");

            SharedPreferences readLatLng = getSharedPreferences("w_UserInfo", MODE_MULTI_PROCESS);
            final String u = readLatLng.getString("UserName", "");
            final String a = location.getAddrStr();
            final String lo = location.getLongitude() + "";
            final String la = location.getLatitude() + "";
            final String c = location.getLocType() + "";
            final String display = Tools.GetDisplayStatus(mContext);
            final String charging = Tools.GetChargingStatus(mContext);
            final String os = Build.VERSION.RELEASE + "|" + android.os.Build.MODEL + "|" + StringUtils.getVersionName(mContext);

            // 地址为null时不上传
            if(a == null) {

                return;
            }

            Log.d("LM", "准备上传: " );
            // 上传标识为false时不上传
            if(isUpload == false) {

                Log.d("LM", "通道被关闭，请等待");
                return;
            }
            Log.d("LM", "允许上传: ");

            // 防止60秒内上传多个点
            new Thread() {
                public void run() {
                    try {
                        Log.d("LM", "睡眠20秒，防止重复上传");
                        sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("LM", "解除睡眠，允许上传");
                    isUpload = true;
                }
            }.start();
            Log.d("LM", "通过一个坐标，锁住通道");
            isUpload = false;


            if (mLat == 0 || mLng == 0) {
                mLat = location.getLatitude();
                mLng = location.getLongitude();
                MLog.w( "首次定位"+"mLat:"+mLat+"\tmLng:"+mLng);
                getlocationReturnCode(location.getLocType(),location.getAddrStr());
                Log.d("LM", "地址: " + location.getAddrStr());

                new Thread() {

                    public void run() {

                        String re = Tools.timingTracking(u, a, lo, la, c, display, charging, os);
                        Log.d("LM", "timingTracking结果: " + re);
                    }
                }.start();

                Log.d("LM", "cellphone: " + u);
                Log.d("LM", "userName: " + "");
                Log.d("LM", "vehicleLocation: " + location.getAddrStr());
                Log.d("LM", "lon: " + location.getLongitude() + "");
                Log.d("LM", "lat: " + location.getLatitude() + "");
                Log.d("LM", "uuid: " + "android");
                Log.d("LM", "code: " + location.getLocType() + "");
                Log.d("LM", "brightscreen: " + display);
                Log.d("LM", "charging: " + charging);
                Log.d("LM", "os: " + os);

                SharedPreferences p = mContext.getSharedPreferences("CurrLatLng", Context.MODE_PRIVATE);
                p.edit().putFloat("w_lng", (float) location.getLongitude()).commit();
                p.edit().putFloat("w_lat", (float) location.getLatitude()).commit();
                p.edit().putString("w_address", location.getAddrStr()).commit();

                uploadLocation(mLat, mLng, location.getTime());
            } else {


                double lat = location.getLatitude();
                double lng = location.getLongitude();
                MLog.w("百度返回码："+location.getLocType()+"||location.lat:"+lat+"\t,lng:"+lng);
                double distance = DistanceUtil.getDistance(new LatLng(mLat, mLng), new LatLng(lat, lng));
                //  添加两定位点距离超过1000公里视为异常定位放弃distance<1000*300&&
                if (distance >= Min_Distance &&distance<1000*500&&!needClose) {
                    mLat = lat;
                    mLng = lng;
                    MLog.w(TAG + " :" + mUserId + "--" + lat + "--" + lng + "--" + mContext);
                    getlocationReturnCode(location.getLocType(),location.getAddrStr());
                    uploadLocation(mLat, mLng,location.getTime());
                } else {

                    MLog.w( "移动距离小于最小上传距离，不上传数据");
                }
            }
        }
    }
    /**
     *调试是否成功定位的方法
     */
    private String getlocationReturnCode(int i,String address){
        switch (i){
            case  -1:locationaddress="定位返回值为空|BD0";
                break;
            case  1: locationaddress="重定失败service没有启动|BD1";
                break;
            case   2:locationaddress="重定失败无监听函数|BD2";
                break;
            case   6:locationaddress="重定失败请求太频繁|BD6";
                break;
            case   7:locationaddress="请求百度定位连接失败|BD9";
                break;
            default:locationaddress=address+"|BD"+i;
                break;
        }
        return locationaddress;
    }
    private void closeService() {
        stopLocate();
        stopSelf();
        stopForeground(true);
        if (mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void uploadLocation(final double lat, final double lng, final String locationtime) {
        stopLocate();
        /**
         * 在网络不可用时 缓存到本地文件中
         */
        if (!isNetworkAvailable()) {
            saveCacheLocation(lat, lng,locationtime);
            MLog.w("TrackingService.uploadLocation.无网络，lat:"+lat+"lng:"+lng+"缓存到本地");
            return;
        }

        /**
         * 上传缓存数据
         */
        List<LocationContineTime> locationList = LocationFileHelper.readFromFile2(mFileName);
        if (locationList != null && locationList.size() > 0) {
            // 修改，将缓存位置信息以一定数量上传
            saveCacheLocation(lat, lng,locationtime);
            locationList = LocationFileHelper.readFromFile2(mFileName);
            MLog.w("有缓存数据，将点位置信息保存到缓存文件张再上传TrackingService.locationList.size():" + locationList.size());
            UploadCacheLocationUtil.uploadCacheLocation(mContext, mRequestQueue, mFileName, mUserId, locationList);
        } else {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext);

            }
            String url = Constants.URL.CurrentLocaltion;
            MLog.w("上传位置信息URL：" + url + "?" + "strUserIdx=" + mUserId + "&cordinateX=" + lng + "&cordinateY=" + lat + "&strLicense=");
            StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                    url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    com.alibaba.fastjson.JSONObject jo = JSON.parseObject(response);
                    int type = Integer.parseInt(jo.getString("type"));
                    if(type>=1) {
                        MLog.w("上传位置信息成功，response:" + response +"\t,locationtime:"+locationtime);

                        if (type>1 && type!=mScanSpanTime/60000) {
                            //
//                            if (false){
                            try {

                                Log.d(TAG, "上传位置信息成功，respo: " + mScanSpanTime);
                                mScanSpanTime =type * 60 * 1000;
                                mLocationThreadRunning = false;
                                mThread.interrupt();
                                Thread.sleep(15*1000);
                                //设置间隔时间后，从新开启定位功能
                                mLocationClient = new LocationClient(TrackingService.this);
                                myListener = new MyLocationListener();
                                mLocationClient.registerLocationListener(myListener);
                                initLocationClient();
                                mLocationThreadRunning = true;
                                mThread = new LocationThread();
                                mThread.setName("TrackingService.LocationThread,ScanSpanTime:"+type+"分钟");
                                mThread.start();
                                MLog.w("TrackingService.onResonse:Success" + "改变上传点位置信息时间，上传间隔时间分钟：" + mScanSpanTime / 60000 );
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        MLog.w("上传位置信息失败，将点位置信息保存到缓存文件，response" + response + "\t,time" + DataUtil.getStringTime(System.currentTimeMillis()));
                        saveCacheLocation(lat, lng,locationtime);
                    }
                    if (needClose) {
                        closeService();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MLog.w("上传位置信息失败，将点位置信息保存到缓存文件，error" + "\t,time" + DataUtil.getStringTime(System.currentTimeMillis()));
                    saveCacheLocation(lat, lng,locationtime);
                    /**
                     * 之所以在error中postMsg 是为了在activity中取消listView的刷新状态
                     */
                    if (needClose) {
                        closeService();
                    }
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    if (mUserId == null || mUserId.equals("")) {
                        mUserId = SharedPreferencesUtils.getUserId();
                    }
                    params.put("strUserIdx", mUserId);
                    params.put("cordinateX", lng + "");
                    params.put("cordinateY", lat + "");
                    params.put("address", locationaddress);
                    params.put("strLicense", "");
                    params.put("date", DataUtil.getStringTime(System.currentTimeMillis()) + "");
                    //2016.3.25
                    locationaddress="BDCode";
                    MLog.i("params:"+params.toString());
                    return params;
                }
            };
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(30*1000, 1, 1.0f));  // 设置超时
            mStringRequest.setTag(Tag_Upload_Position);
            mRequestQueue.add(mStringRequest);
        }
    }

    /**
     * 用于传递给服务端是排序用的id
     */
    private int saveCacheLocationId = 0;
    private void saveCacheLocation(double lat, double lng,String locationtime) {
        LocationContineTime location = new LocationContineTime();
        location.id = saveCacheLocationId+"";
        saveCacheLocationId++;
        if (mUserId == null) {
            mUserId = SharedPreferencesUtils.getUserId();
        }
        location.userIdx = (mUserId);
        location.ADDRESS = locationaddress;
        location.CORDINATEX = lat;
        location.CORDINATEY = lng;
        if (locationtime!=null){
            location.TIME=locationtime;
        }else {
            location.TIME = DataUtil.getStringTime(System.currentTimeMillis());
        }
        MLog.w("locationaddress"+locationaddress);
        locationaddress="BDCode";
        if (mFileName == null){
            mFileName = getApplicationContext().getFilesDir().getAbsolutePath()
                    + File.separator + "Location" + File.separator + mUserId + ".log";
        }
        File file = new File(mFileName);
        MLog.w("缓存文件位置.filePath:" + file.getAbsolutePath());
        if(!file.exists()){
            try {
                boolean makeFile = file.createNewFile();
                MLog.w( "TrackingService.saceCacheLocation.本地无缓存文件，创建缓存文件："+makeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!LocationFileHelper.saveInFile(location, mFileName)){
            // 离线存储点返回失败则排序id回退回去
            saveCacheLocationId--;
        }
    }
    /**
     * 用于显示Notification 防止该服务被系统回收
     */
    @SuppressWarnings("deprecation")
//    public void showNotification() {
//        Notification notification = new Notification(R.mipmap.ic_launcher, getText(R.string.app_name), System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_NO_CREATE);
//       notification.setLatestEventInfo(this, "定位服务后台运行中", "", pendingIntent);
//
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        notification.flags |= Notification.FLAG_HIGH_PRIORITY;
//        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//        startForeground(1, notification);
//    }
    public void showNotification(){
        //   NotificationManager manager= (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(mContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getText(R.string.app_name));
        builder.setContentText("正运输物流订单中，请保持App定位服务开启");
        Notification notification=builder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;//标识正在运行的事件
        notification.flags|=Notification.FLAG_NO_CLEAR;//防止通知被点击清除
        notification.flags|=Notification.FLAG_HIGH_PRIORITY;//设置为高优先级的通知
        notification.flags|=Notification.FLAG_FOREGROUND_SERVICE;//表示正在运行的服务
        //   notification.defaults=Notification.DEFAULT_SOUND;//设置通知响铃
        //  notification.defaults|=Notification.DEFAULT_VIBRATE;//设置通知震动（配合相关授权）
        startForeground(R.string.app_name,notification);//以app_name的id为标识来启动和管理通知
        //  manager.notify(R.string.app_name,notification);//以app_name的id为标识来启动和管理通知
        MLog.i("*****showNotification**********");
    }
    /**
     * 初始化定位客户端
     */
    public void initLocationClient() {
        LocationClientOption option = new LocationClientOption();
        option.setProdName(mContext.getPackageName());
        MLog.w("ProdName:" + mContext.getPackageName());
        option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
        option.setLocationMode(tempMode);// 设置定位模式
        option.setScanSpan(mScanSpanTime);//设置上传位置时间间隔
        //  option.setScanSpan(1*60*1000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setTimeOut(10 * 1000); // 网络定位的超时时间
        mLocationClient.setLocOption(option);
    }
    /**
     * 判断是否会定位失败 ，errorCode是百度定位返回的错误代码
     * @param errorCode
     * @return
     */
    public boolean isLocateAvailable(int errorCode) {
        // 注销了65 ： 定位缓存的结果；68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
        //  return (errorCode == 161 ||errorCode == 61||errorCode==66||errorCode==65||errorCode==68);
        return (errorCode == 161 ||errorCode == 61||errorCode==66);
    }

    /**
     * 开始定位
     */
    public void startLocate() {
        //  注释掉导致一次定位请求三次的现象
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
            MLog.i("mLocationThread.sleep:"+mScanSpanTime);
        }else if (mLocationClient!=null){
            mLocationClient.requestLocation();
        }else {
            mLocationClient=new LocationClient(this);
            myListener=new MyLocationListener();
            mLocationClient.registerLocationListener(myListener);
            initLocationClient();
            mLocationClient.start();
        }
//  放开测试锁屏后定位不持续问题
//        if (mLocationClient != null && !mLocationClient.isStarted()) {
//            mLocationClient.start();
//        }
    }

    /**
     * 停止定位
     */
    public void stopLocate() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public void onDestroy() {
        isLoop = false;
        super.onDestroy();
        stopSelf();
        stopForeground(true);
        stopLocate();
        unregisterReceiver(mReceiver);
        // releaseWakeLock();
    }


    private class StopReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            needClose = true;
            mLocationClient.start();
        }
    }
//    public  static  class LocationReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            MLog.e("LocationReceiver.onReceive 广播定位");
//            AlarmManager alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Intent intent1=new Intent(context,LocationReceiver.class);
//            intent.setAction("com.kaidongyuan.app.kdydriver.locationReceiver");
//            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            Long triggerAtime= System.currentTimeMillis()+ 1000 * 60 * 1;
//            Long interval= 1000 * 60 * 2L;
//            //针对不同版本的AndroidSDK,采用不同方法的闹钟唤醒定位广播
//            if (Build.VERSION.SDK_INT>=23){
//                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,triggerAtime, pendingIntent);
//            }else if (Build.VERSION.SDK_INT>=19){
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,triggerAtime,pendingIntent);
//            }else {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtime, interval, pendingIntent);
//            }
//        }
//    }
    /**
     * 检测当前手机是否联网
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    private Dialog mNetDialog;
    /**
     * 显示 Dialog 提示用户连接网络
     */
    private void setContactNetDialog(){
        MLog.w("TrackingService.setContactNetDialog");
        if(mNetDialog==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
            builder.setTitle(getText(R.string.app_name)+"提示：\n点击确定开启网络服务");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
}