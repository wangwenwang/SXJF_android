package com.kaidongyuan.app.kdydriver.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import static android.widget.Toast.LENGTH_LONG;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.NetworkUtils;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.R;
import com.kaidongyuan.app.kdydriver.app.AppContext;
import com.kaidongyuan.app.kdydriver.bean.Tools;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.httpclient.OrderAsyncHttpClient;
import com.kaidongyuan.app.kdydriver.serviceAndReceiver.TrackingService;
import com.kaidongyuan.app.kdydriver.ui.base.BaseFragmentActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class hbx extends BaseFragmentActivity implements AsyncHttpCallback {

    public static Context mContext;
    public static WebView mWebView;

    String inputName;
    public String ScanResultP;
    public static final int REQUEST_READ_PHONE_STATE = 8;

    String appName;

    String ifManualUpdate = "更新";

    public static String mAppVersion;

    // 微信开放平台APP_ID
    private static final String APP_ID = Constants.WXLogin_AppID;

    static public IWXAPI mWxApi;

    public final static String DestFileName = "sxjf_tms.apk";
    public final static String ZipFileName = "dist.zip";

    String server_App_Version;

    String server_App_Url;

    //检测版本更新
    private final String TAG_CHECKVERSION = "check_version";
    private OrderAsyncHttpClient mClient;
    private NotificationManager mNotificationManager;
    private Handler mHandler;
    private Notification mUpdataNotification;
    private Snackbar pmSnackbar;
    private RemoteViews remoteView;
    private final int RequestPermission_STATUS_CODE0=8800;
    private AlertDialog mUpdataVersionDialog;

    //5.0以下使用
    private ValueCallback<Uri> uploadMessage;
    // 5.0及以上使用
    private ValueCallback<Uri[]> uploadMessageAboveL;
    //图片
    private final static int FILE_CHOOSER_RESULT_CODE = 128;
    //拍照
    private final static int FILE_CAMERA_RESULT_CODE = 129;
    //拍照图片路径
    private String cameraFielPath;
    private Uri mImageUri, mImageUriFromFile;
    private static final String FILE_PROVIDER_AUTHORITY = "com.cy_scm.tms_android.fileprovider";
//    private String CURR_ZIP_VERSION = "99.9.9";
    private String CURR_ZIP_VERSION = "0.2.5";

    private Intent mLocationIntent;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    RelativeLayout scan_rt;  // 扫码控件布局

    @SuppressLint("JavascriptInterface")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        mContext = this;

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        scan_rt = (RelativeLayout) findViewById(R.id.scan_rt);
        scan_rt.setVisibility(View.INVISIBLE);
        capture.onPause();

//        requestReadPhonePermission();
//        boolOpenCarmer();
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//        } else {
//            //TODO
//        }

        try {
            mAppVersion = getMContext().getPackageManager().getPackageInfo(getMContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("LM", "程序启动");

        // 设置ZIP版本号
        String curr_zip_version = Tools.getAppZipVersion(mContext);

        if(curr_zip_version != null && curr_zip_version.equals("")) {

            Tools.setAppZipVersion(mContext, CURR_ZIP_VERSION);
        }

        curr_zip_version = Tools.getAppZipVersion(mContext);

        LocationClient mLocationClient = new LocationClient(getApplicationContext());

        appName = getResources().getString(R.string.app_name);

        mWebView = (WebView) findViewById((R.id.lmwebview));
        mWebView.getSettings().setTextZoom(100);

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("LM", "当前位置: " + url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            // js拔打电话
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                Log.d("LM", "------------------------: ");

                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }

            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return false;
            }

            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return true;
            }

            // 处理定位权限请求
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                Log.d("LM", "处理定位权限请求：：ddd ");
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            // 设置应用程序的标题title
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        mLocationClient.start();

        // 获取上次启动记录的版本号
        String lastVersion = Tools.getAppLastTimeVersion(mContext);

        mWebView.loadUrl("file:///android_asset/apps/H5A4057B2/www/index.html");

        Tools.setAppLastTimeVersion(mContext);

        lastVersion = Tools.getAppLastTimeVersion(mContext);

        // 启用javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true);

        // 在js中调用本地java方法
        mWebView.addJavascriptInterface(new JsInterface(this), "CallAndroidOrIOS");

        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);


//        minefragment = new MineFragment();
        mClient = new OrderAsyncHttpClient(this, this);
        mNotificationManager = (NotificationManager) getMContext().getSystemService(Context.NOTIFICATION_SERVICE);
        initPermission();

        //开启后台定位服务
        if (mLocationIntent == null) {
            mLocationIntent = new Intent(this, TrackingService.class);
        }
        getApplicationContext().startService(mLocationIntent);

        initHandler();

        capture.setResultCallBack(new CaptureManager.ResultCallBack() {
            @Override
            public void callBack(int requestCode, int resultCode, Intent intent) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (null != result && null != result.getContents()) {
                    showDialog(result.getContents());
                    new Thread() {
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }.start();
                }
            }
        });
        capture.decode();
    }

    public void showDialog(String result) {
        // 弹出dialog的代码略...

        Log.d("LM", result);

        String url = "javascript:QRScanAjax('" + result + "')";
        hbx.mWebView.loadUrl(url);

        new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // 重新拉起扫描
                        capture.onResume();
                        capture.decode();
                    }
                });
            }
        }.start();
    }

    private void requestReadPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //在这里面处理需要权限的代码
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_READ_PHONE_STATE);
        }
    }
    private void boolOpenCarmer(){
//READ_PHONE_STATE   CALL_PHONE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)  //打开相机权限
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)   //可读
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  //可写
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,Manifest.permission.CALL_PHONE},
                    REQUEST_READ_PHONE_STATE);
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {

        Log.d("LM", "标签" + request_tag + "请求成功：" + msg);

        if (msg.equals("error")){
            //下载安装包失败
            if (request_tag.equals(DestFileName)){
                Message message=mHandler.obtainMessage();
                message.arg1=-1;
                message.sendToTarget();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(getMContext())){
                NetworkUtils.setContactNetDialog(getApplication());
                return;
            }
        }else if (request_tag.equals(TAG_CHECKVERSION)){
            JSONObject jo= JSON.parseObject(msg);

            String status = jo.getString("status");

            String apkDownloadUrl = null;
            String server_apkVersion = null;
            if(status.equals("1")) {

                JSONObject dict = jo.getJSONObject("data");
                apkDownloadUrl = dict.getString("downloadUrl");
                server_apkVersion = dict.getString("versionNo");
            }

            if (server_apkVersion!=null && apkDownloadUrl!=null) {
                try {
                    String current_apkVersion = mAppVersion;
                    MLog.w( "server_apkVersion:"+server_apkVersion+"\tcurrent_apkVersion:"+current_apkVersion);

                    int compareVersion = Tools.compareVersion(server_apkVersion, current_apkVersion);

                    if (compareVersion == 1) {

                        createUpdateDialog(current_apkVersion, server_apkVersion, apkDownloadUrl);
                    } else {

                        if(ifManualUpdate.equals("手动更新")){

                            AlertDialog.Builder builderw = new AlertDialog.Builder(mContext);
//                            builderw.setTitle("更新版本");
                            builderw.setMessage("当前版本为最新版本");
                            builderw.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUpdataVersionDialog.cancel();
                                }
                            });
                            builderw.setCancelable(false);

                            mUpdataVersionDialog = builderw.create();
                            mUpdataVersionDialog.show();

                            ifManualUpdate = "更新";
                        }else{
                            Log.d("LM", "apk为最新版本");
                            checkGpsState();
                        }
                    }
                } catch (Exception e) {
                    Log.d("LM","NameNotFoundException" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 版本更新对话框
     * @param currentVersion 当前版本versionName
     * @param version 最新版本versionName
     * @param downUrl 最新版本安装包下载url
     */
    public void createUpdateDialog(String currentVersion, String version, final String downUrl) {

        mClient = new OrderAsyncHttpClient(this, this);

        AlertDialog.Builder builderw = new AlertDialog.Builder(mContext);
        builderw.setTitle("更新版本");
        builderw.setMessage("当前版本：" + currentVersion + "\n最新版本：" + version);
        builderw.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUpdataVersionDialog.cancel();
            }
        });
        builderw.setNegativeButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mUpdataVersionDialog.cancel();
                MLog.w("update.url:" + downUrl);
                //以存储文件名为Tag名
                mClient.sendFileRequest(downUrl, DestFileName);
            }
        });
        builderw.setCancelable(false);
//        builder.show();
        mUpdataVersionDialog = builderw.create();
        mUpdataVersionDialog.show();
    }


    /**
     * 判断 GPS是否开启
     */
    private void checkGpsState() {
        LocationManager alm= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER )&&!alm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
            MLog.w("MainActivity.checkGpsState:GpsisOff");
            createCheckGpsDialog();
        } else {
            MLog.w("MainActivity.checkGpsState:GpsisOn");
        }
    }

    private void createCheckGpsDialog() {
        showSnackbar("请开启GPS服务", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent;
                myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                hbx.this.startActivity(myIntent);
            }
        }, Snackbar.LENGTH_INDEFINITE);
    }

    private void showSnackbar(String strSnackbar, View.OnClickListener listener,int duration) {
        pmSnackbar = Snackbar.make(findViewById(R.id.acitvity_mainAcitivity),strSnackbar,duration);
        View v= pmSnackbar.getView();
        v.setBackgroundColor(getResources().getColor(R.color.details_text));
        final TextView tv_snackbar= (TextView) v.findViewById(R.id.snackbar_text);
        tv_snackbar.setGravity(Gravity.CENTER);
        tv_snackbar.setTextColor(getResources().getColor(R.color.white));
        pmSnackbar.setAction("设置",listener).show();
    }

    // js调用java
    private class JsInterface extends Activity {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void callAndroidTrack(String u, String p) {

            // 当前时间
            String curDate = Tools.getCurrDate();

            if (u != null) {

                Log.d("LM", "保存轨迹的手机号" + u);
                SharedPreferences crearPre = mContext.getSharedPreferences("w_UserInfo", MODE_PRIVATE);
                crearPre.edit().putString("UserName", u).commit();
                crearPre.edit().putString("Password", p).commit();
                crearPre.edit().putString("Set_User_Pass_Time", curDate).commit();
            }
        }

        @JavascriptInterface
        public void callAndroid( String exceName, String inputName) {

            hbx.this.inputName = inputName;

            if (exceName.equals("登录页面已加载")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String  url = "javascript:VersionShow('" + Tools.getVerName(mContext) + "')";
                        hbx.mWebView.loadUrl(url);
                        Log.d("LM", url);

                        url = "javascript:Device_Ajax('android')";
                        hbx.mWebView.loadUrl(url);
                        Log.d("LM", url);
                    }
                });
            } else if (exceName.equals("获取当前位置页面已加载")) {

                Log.d("LM", "获取当前位置页面已加载");

                new Thread() {

                    public void run() {
                        //开启后台定位服务

                        if (mLocationIntent == null) {

                            mLocationIntent = new Intent(mContext, TrackingService.class);
                        }

                        mContext.getApplicationContext().startService(mLocationIntent);

                    }
                }.start();

                SharedPreferences readLatLng = mContext.getSharedPreferences("CurrLatLng", MODE_MULTI_PROCESS);

                final String address = readLatLng.getString("w_address", "");
                final float lng = readLatLng.getFloat("w_lng",0f);
                final float lat = readLatLng.getFloat("w_lat", 0f);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject lngObject =  new JSONObject();
                        lngObject.put("lng",lng);
                        lngObject.put("lat",lat);

                        String url = "javascript:SetCurrAddress('" + lngObject + "')";

                        hbx.mWebView.loadUrl(url);
                    }
                });
            } else if (exceName.equals("导航")) {

                new Thread() {

                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Tools.ToNavigation(hbx.this.inputName, mContext, appName);
                            }
                        });
                    }
                }.start();

            } else if (exceName.equals("查看路线")) {

                Log.d("LM", "查看路线");

                new Thread() {

                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent2=new Intent(mContext,OrderTrackActivity.class);
                                intent2.putExtra("order_IDX",hbx.this.inputName);
                                mContext.startActivity(intent2);
                            }
                        });
                    }
                }.start();
            }else if(exceName.equals("打印")){

                Intent intentprint=new Intent(mContext,PrintActivity.class);
                intentprint.putExtra("omsNo",hbx.this.inputName);
                mContext.startActivity(intentprint);

            }else if(exceName.equals("shein打印")){

                Log.d("LM", "shein打印");

                Intent intentprint1=new Intent(mContext,PrintActivityshein.class);
                intentprint1.putExtra("omsNo",hbx.this.inputName);
                mContext.startActivity(intentprint1);
            }
        }

        //    扫码
        @JavascriptInterface
        public void  VueSCAN() {

            new Thread() {
                public void run() {
                    Looper.prepare();//增加部分
                IntentIntegrator integator = new IntentIntegrator(hbx.this);
                integator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integator.setPrompt("请扫描");
                integator.setCameraId(0);
                integator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
                integator.setBarcodeImageEnabled(false);
                integator.set_is_Continuous_Scan(false);
                integator.setCaptureActivity(Scan_Single_Activity.class);
                integator.initiateScan();

                }
            }.start();
        }

        // 连续扫码
        @JavascriptInterface
        public void ContinuousScan() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    capture.is_Continuous_Scan = true;
                    capture.onResume();
                    scan_rt.setVisibility(View.VISIBLE);
                }
            });
        }

        // 取消连续扫码
        @JavascriptInterface
        public void CancelScan() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                      android.view.ViewGroup.LayoutParams pp = scan_rt.getLayoutParams();
//                      pp.height = 80;
//                      scan_rt.setLayoutParams(pp);
                    scan_rt.setVisibility(View.INVISIBLE);
                    capture.onPause();
                }
            });
        }

        @JavascriptInterface
        public  void updateAPP(){

            initHandler();
            checkVersion();
            ifManualUpdate = "手动更新";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initPermission() {

        Log.d("LM", "申请存储权限");

        try {

            if (Build.VERSION.SDK_INT>=23){
                if (MPermissionsUtil.checkAndRequestPermissions(hbx.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,RequestPermission_STATUS_CODE0)){
                    checkVersion();
                }
            }else {

                checkVersion();
            }
        }catch (Exception e) {

            Log.d("LM", "initPermission: " + e.getMessage());
        }
    }

    public void checkVersion() {
        Log.d("LM", "检查apk及zip版本");

        Map<String, String> params = new HashMap<>();
        params.put("params", "{\"tenantCode\":\"SFKC\"}");

        mClient.sendRequest(Constants.URL.SAAS_API_BASE + "queryAppVersion.do", params, TAG_CHECKVERSION);
    }

    private void initHandler() {

        mHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                int percent=message.arg1;
                if (percent==100){
                    createNotifaction(percent);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),DestFileName);
                    if (!file.exists()) {

                        Toast.makeText(mContext, "升级包不存在", Toast.LENGTH_SHORT).show();
                    } else {

//                        Uri fileUri = null;
//
//                        if (Build.VERSION.SDK_INT >= 24) {
//                            fileUri = FileProvider.getUriForFile(mContext, "com.cy_scm.tms_android.fileprovider", file);
//
//                        } else {
//                            fileUri = Uri.fromFile(file);
//                        }
//
//                        String type = "application/vnd.android.package-archive";//.apk 的 mime 名
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(fileUri, type);
//                        mContext.startActivity(intent);
//                        mContext.startActivity(intent);


                        Uri uri = Uri.fromFile(file);
                        String type = "application/vnd.android.package-archive";//.apk 的 mime 名
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, type);
                        mContext.startActivity(intent);
                    }
                    mNotificationManager.cancel(0);
                } else if (percent==-1) {

                    Toast.makeText(hbx.this, "更新失败，服务器异常", LENGTH_LONG).show();
                    mNotificationManager.cancel(0);
                } else{
                    createNotifaction(percent);
                }
                return false;
            }
        });
    }

    /**
     * 返回上一页
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // 登录页时不允许返回上一页
            String curURL = mWebView.getUrl();
            String orgURL = mWebView.getOriginalUrl();
            if (curURL.equals("file:///android_asset/apps/H5A4057B2/www/index.html#/")) {

                Log.d("LM", "禁止返回上一页1：" + curURL);
                return true;
            }

            // 首页
            String Index = "file:///android_asset/apps/H5A4057B2/www/index.html#/Index";
            // 任务
            String Waybill = "file:///android_asset/apps/H5A4057B2/www/index.html#/Waybill";
            // 我的
            String HomeIndex = "file:///android_asset/apps/H5A4057B2/www/index.html#/HomeIndex";

            // 主菜单时不允许返回上一页
            if (
                    curURL.indexOf(Index + "?") != -1 || curURL.equals(Index) ||
                            curURL.indexOf(Waybill + "?") != -1 || curURL.equals(Waybill) ||
                            curURL.indexOf(HomeIndex + "?") != -1 || curURL.equals(HomeIndex)
            ) {

                Log.d("LM", "禁止返回上一页2：" + curURL);
                return true;
            }
            mWebView.goBack();
            return true;
        }
        return false;
    }


    /**
     * 创建下载进度的 notification
     * @param percent 下载进度
     */
    private void createNotifaction(int percent){
        //自定义 Notification 布局
        if (mUpdataNotification==null) {
            mUpdataNotification = new Notification();
            mUpdataNotification.icon =R.mipmap.ic_launcher;
            mUpdataNotification.tickerText =appName;
        }
        if (remoteView==null) {
            remoteView = new RemoteViews(mContext.getPackageName(), R.layout.dialog_download);
        }
        remoteView.setTextViewText(R.id.textView_dialog_download, percent+"%");
        remoteView.setProgressBar(R.id.progressBar_dialog_download, 100, percent, false);
        mUpdataNotification.contentView = remoteView;
        mNotificationManager.notify(0, mUpdataNotification);
    }

    @Override
    public void setProgressBarLoading(int progress) {
        // super.setProgressBarLoading(progress);
        //改为更新通知栏进度条
        Message message=mHandler.obtainMessage();
        message.arg1=progress;
        message.sendToTarget();
    }
    // android 7.0以上手机存储授权后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        Log.d("LM", "拍照5.8: ");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("LM", "拍照5.9: ");

        if (requestCode == 101) {


            Log.d("LM", "拍照5.9.1: ");
            takeCameraM();
        }
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
//
//        switch (requestCode) {
//            case 1://对应requestPermissions的requestCode
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//如果一次申请多个权限，就按顺序依次grantResults[1]、grantResults[2]判断
////                    Toasts.showShort("再次点击即可拍照");
//                } else {
//                    // Permission Denied
//                }
//                break;
//        }

//        if (requestCode==RequestPermission_STATUS_CODE0){
//            for (int i=0;i<permissions.length;i++){
//                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
//                    switch (permissions[i]){
//                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
//                            showToastMsg("请允许应用使用SD卡存储",3000);
//                            showSnackbar("请允许应用使用SD卡存储~", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
////                                    Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
////                                    startActivity(intent3);
//                                    AppContext.IS_LOGIN=false;
//                                    finish();
//                                }
//                            },Snackbar.LENGTH_INDEFINITE);
//                            break;
//                        case Manifest.permission.CAMERA:
//                            showToastMsg("请授权应用调用摄像头权限~",3000);
//                            showSnackbar("请授权应用调用摄像头权限~", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent2=new Intent(Settings.ACTION_SETTINGS);
//                                    startActivity(intent2);
//                                }
//                            },Snackbar.LENGTH_INDEFINITE);
//                            break;
//                        case Manifest.permission.ACCESS_COARSE_LOCATION:
//                            showToastMsg("请授权应用网络定位和GPS定位权限ACCESS_COARSE_LOCATION",3000);
//                            break;
//
//                        case Manifest.permission.ACCESS_FINE_LOCATION:
//                            showToastMsg("请授权应用网络定位和GPS定位权限ACCESS_FINE_LOCATION",3000);
//                            break;
//
//                        default:
//                            showSnackbar("请授权应用网络定位和GPS定位权限", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent1=new Intent(Settings.ACTION_SETTINGS);
//                                    startActivity(intent1);
//                                }
//                            },Snackbar.LENGTH_INDEFINITE);
//                            break;
//                    }
//                    return;
//                }
//            }
//
//        }
    }

    private void openImageChooserActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(hbx.this);
        builder.setTitle("拍照/相册");
        builder.setPositiveButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takePhoto();
            }
        });
        builder.setNegativeButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takeCamera();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    //选择图片
    private void takePhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    //拍照
    private void takeCamera() {
       /* String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile;
        try {

            imageFile = File.createTempFile(imageFileName,".jpg",storageDir);
            cameraFielPath = imageFile.getPath();
        } catch (IOException e) {

            e.printStackTrace();
        }

        File cameraPhoto = new File(cameraFielPath);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = FileProvider.getUriForFile(
                this,
                "com.cy_scm.tms_android.fileprovider",
                cameraPhoto);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePhotoIntent, FILE_CAMERA_RESULT_CODE);


*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (hasSDCard()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_"+timeStamp+"_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile;
            try {

                imageFile = File.createTempFile(imageFileName,".jpg",storageDir);
                cameraFielPath = imageFile.getPath();
            } catch (IOException e) {

                e.printStackTrace();
            }
            File outputImage = new File(cameraFielPath);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
            startActivityForResult(intent, FILE_CAMERA_RESULT_CODE);
        }
    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public boolean hasSDCard() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void takeCameraM() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//打开相机的Intent
        if(takePhotoIntent.resolveActivity(getPackageManager())!=null){//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
            File imageFile = createImageFile();//创建用来保存照片的文件
            if(imageFile!=null){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
//                    7.0以上要通过FileProvider将File转化为Uri
                    mImageUri = FileProvider.getUriForFile(this,FILE_PROVIDER_AUTHORITY,imageFile);
                }else {
//                    7.0以下则直接使用Uri的fromFile方法将File转化为Uri
                    mImageUri = Uri.fromFile(imageFile);
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);//将用于输出的文件Uri传递给相机
                startActivityForResult(takePhotoIntent, FILE_CAMERA_RESULT_CODE);//打开相机
            }
        }else {
        }
    }

    /**
     * 创建用来存储图片的文件，以时间来命名就不会产生命名冲突
     * @return 创建的图片文件
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName,".jpg",storageDir);
            cameraFielPath = imageFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean hasFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(Intent intent) {
        Uri[] results = null;
        if (intent != null) {
            String dataString = intent.getDataString();
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        capture.onPause();
        capture.onResume();

        Log.d("LM", "onActivityResult: ----");

        IntentResult intentresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentresult != null) {

            if (intentresult.getContents() == null) {

                Toast.makeText(this, "已返回", LENGTH_LONG).show();
            } else {

//                Toast.makeText(this, intentresult.getContents(), LENGTH_LONG).show();

                String url = "javascript:QRScanAjax('" + intentresult.getContents() + "')";
                hbx.mWebView.loadUrl(url);

                Log.d("LM", url);
                Log.d("LM", hbx.this.inputName);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (null == uploadMessage && null == uploadMessageAboveL) return;

        if (resultCode != RESULT_OK) {//同上所说需要回调onReceiveValue方法防止下次无法响应js方法

            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            return;
        }

        Uri result = null;
        if (requestCode == FILE_CAMERA_RESULT_CODE) {

            if (result == null && hasFile(cameraFielPath)) {

                result = Uri.fromFile(new File(cameraFielPath));
            }
            if (uploadMessageAboveL != null) {

                uploadMessageAboveL.onReceiveValue(new Uri[]{result});

                uploadMessageAboveL = null;
            } else if (uploadMessage != null) {

                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {

            if (data != null) {
                result = data.getData();
            }
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }
}
