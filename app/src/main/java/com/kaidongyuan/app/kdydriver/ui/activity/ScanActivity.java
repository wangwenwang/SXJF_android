package com.kaidongyuan.app.kdydriver.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;
import com.kaidongyuan.app.kdydriver.R;
import com.kaidongyuan.app.kdydriver.bean.Tools;
import com.kaidongyuan.app.kdydriver.constants.Constants;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;

public class ScanActivity extends CaptureActivity {

    public static Context mContext;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    public static WebView mWebView;
    String inputName;
    RelativeLayout scan_rt;  // 扫码控件布局

    String appName;   // App名称
    public static String local_Version;   // 本地版本号
    public final static String ZipFileName = "dist.zip";

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
    private final int RequestPermission_STATUS_CODE0=8800;
    private static final String FILE_PROVIDER_AUTHORITY = "com.kaidongyuan.qh_orders_android.fileprovider";
    private Uri mImageUri;
    public static final int REQUEST_READ_PHONE_STATE = 8;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan);

        mContext = this;

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        scan_rt = (RelativeLayout) findViewById(R.id.scan_rt);
        scan_rt.setVisibility(View.INVISIBLE);
        capture.onPause();

        appName = getResources().getString(R.string.app_name);
        try {
            local_Version = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("LM", "程序启动");

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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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

        mWebView.loadUrl("file:///android_asset/apps/H5A4057B2/www/index.html");
        // 启用javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true);

        // 在js中调用本地java方法
        mWebView.addJavascriptInterface(new ScanActivity.JsInterface(this), "CallAndroidOrIOS");

        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);


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
//        initPermission();
    }

    private void openImageChooserActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
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

    private void initPermission() {

        Log.d("LM", "申请存储权限");

        try {

            if (Build.VERSION.SDK_INT>=23){
                if (MPermissionsUtil.checkAndRequestPermissions(ScanActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,RequestPermission_STATUS_CODE0)){


                    new Thread() {
                        public void run() {

                            checkVersion();
                        }
                    }.start();
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

        String params = "{\"tenantCode\":\"SFKC\"}";
        String paramsEncoding = URLEncoder.encode(params);
        String Strurl = Constants.URL.SAAS_API_BASE + "queryAppVersion.do?params=" + paramsEncoding;
        HttpURLConnection conn = null;
        try {

            URL url = new URL(Strurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {

                InputStream in = conn.getInputStream();
                String resultStr = Tools.inputStream2String(in);
                resultStr = URLDecoder.decode(resultStr, "UTF-8");

                try {
                    org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) (new JSONParser().parse(resultStr));
                    Log.i("LM", jsonObj.toJSONString() + "\n" + jsonObj.getClass());
                    String status = (String) jsonObj.get("status");
                    String Msg = (String) jsonObj.get("Msg");

                    String apkDownloadUrl = null;
                    String server_apkVersion = null;
                    if (status.equals("1")) {

                        org.json.simple.JSONObject dict = (org.json.simple.JSONObject) jsonObj.get("data");
                        apkDownloadUrl = (String) dict.get("downloadUrl");
                        server_apkVersion = (String) dict.get("versionNo");
                    }
                    if (server_apkVersion != null && apkDownloadUrl != null) {
                        try {
                            String current_apkVersion = local_Version;
                            Log.d("LM", "server_apkVersion:" + server_apkVersion + "\tcurrent_apkVersion:" + current_apkVersion);
                            int compareVersion = Tools.compareVersion(server_apkVersion, current_apkVersion);
                            if (compareVersion == 1) {
                                createUpdateDialog(current_apkVersion, server_apkVersion, apkDownloadUrl);
                            }
                        } catch (Exception e) {
                            Log.d("LM", "NameNotFoundException" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                in.close();
            } else {
                Log.d("LM", "检查版本接口|queryAppVersion.do|请求失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        Log.d("LM", "checkVersion: ");
    }


    /**
     * 版本更新对话框
     *
     * @param currentVersion 当前版本versionName
     * @param version        最新版本versionName
     * @param downUrl        最新版本安装包下载url
     */
    public void createUpdateDialog(String currentVersion, String version, final String downUrl) {


        String fff = "http://120.77.206.44/CYSCMAPP/mhy/mhy.apk";
        Log.d("LM", "createUpdateDialog: ");

        AllenVersionChecker
                .getInstance()
                .downloadOnly(
                        UIData.create()
                                .setDownloadUrl(downUrl)
                                .setTitle("更新版本")
                                .setContent("当前版本：" + currentVersion + "\n最新版本：" + version)
                )
                .executeMission(mContext);

//        DownloadManager manager = DownloadManager.getInstance(this);
//        manager.setApkName("appupdate.apk")
//                .setApkUrl(downUrl)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .download();

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
        public void callAndroid(String exceName, String inputName) {

            ScanActivity.this.inputName = inputName;

            if (exceName.equals("登录页面已加载")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String url = "javascript:VersionShow('" + Tools.getVerName(mContext) + "')";
                        ScanActivity.mWebView.loadUrl(url);
                        Log.d("LM", url);

                        url = "javascript:Device_Ajax('android')";
                        ScanActivity.mWebView.loadUrl(url);
                        Log.d("LM", url);
                    }
                });
            } else if (exceName.equals("获取当前位置页面已加载")) {

                Log.d("LM", "获取当前位置页面已加载");

                SharedPreferences readLatLng = mContext.getSharedPreferences("CurrLatLng", MODE_MULTI_PROCESS);

                final String address = readLatLng.getString("w_address", "");
                final float lng = readLatLng.getFloat("w_lng", 0f);
                final float lat = readLatLng.getFloat("w_lat", 0f);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject lngObject = new JSONObject();
                        lngObject.put("lng", 114.2);
                        lngObject.put("lat", 22.44);

                        String url = "javascript:SetCurrAddress('" + lngObject + "')";

                        ScanActivity.mWebView.loadUrl(url);
                    }
                });
            } else if (exceName.equals("导航")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Tools.ToNavigation(ScanActivity.this.inputName, mContext, appName);
                            }
                        });

            } else if (exceName.equals("查看路线")) {

                Log.d("LM", "查看路线");

                new Thread() {

                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent2 = new Intent(mContext, OrderTrackActivity.class);
                                intent2.putExtra("order_IDX", ScanActivity.this.inputName);
                                mContext.startActivity(intent2);
                            }
                        });
                    }
                }.start();
            } else if (exceName.equals("打印")) {

                Intent intentprint = new Intent(mContext, PrintActivity.class);
                intentprint.putExtra("omsNo", ScanActivity.this.inputName);
                mContext.startActivity(intentprint);

            } else if (exceName.equals("shein打印")) {

                Log.d("LM", "shein打印");

                Intent intentprint1 = new Intent(mContext, PrintActivityshein.class);
                intentprint1.putExtra("omsNo", ScanActivity.this.inputName);
                mContext.startActivity(intentprint1);
            }
        }

        //    扫码
        @JavascriptInterface
        public void VueSCAN() {

            new Thread() {
                public void run() {
                    Looper.prepare();//增加部分
                    IntentIntegrator integator = new IntentIntegrator(ScanActivity.this);
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
        public void updateAPP() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    checkVersion();
                }
            });

        }
    }


    public void showDialog(String result) {
        // 弹出dialog的代码略...

        Log.d("LM", result);

        String url = "javascript:QRScanAjax('" + result + "')";
        ScanActivity.mWebView.loadUrl(url);

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

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("LM", "onActivityResult: ----");

        IntentResult intentresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentresult != null) {

            if (intentresult.getContents() == null) {

                Toast.makeText(this, "已返回", LENGTH_LONG).show();
            } else {

//                Toast.makeText(this, intentresult.getContents(), LENGTH_LONG).show();

                String url = "javascript:QRScanAjax('" + intentresult.getContents() + "')";
                ScanActivity.mWebView.loadUrl(url);

                Log.d("LM", url);
                Log.d("LM", ScanActivity.this.inputName);
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {

            String curURL = mWebView.getUrl();
            String orgURL = mWebView.getOriginalUrl();
            if (curURL.equals("file:///android_asset/apps/H5A4057B2/www/index.html#/")) {

                Log.d("LM", "禁止返回上一页1：" + curURL);
                moveTaskToBack(true);
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
                moveTaskToBack(true);
                return true;
            }
            mWebView.goBack();
            return true;
        }
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}
