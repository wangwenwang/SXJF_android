package com.kaidongyuan.app.kdydriver.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.kaidongyuan.app.kdydriver.R;
import com.kaidongyuan.app.kdydriver.bean.Tools;

import static android.widget.Toast.LENGTH_LONG;

public class ScanActivity extends CaptureActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    public static WebView mWebView;
    String inputName;


    //5.0以下使用
    private ValueCallback<Uri> uploadMessage;
    // 5.0及以上使用
    private ValueCallback<Uri[]> uploadMessageAboveL;


    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan);

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);



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
//                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
//                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
//                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
//                openImageChooserActivity();
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

            ScanActivity.this.inputName = inputName;

            if (exceName.equals("登录页面已加载")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String  url = "javascript:VersionShow('" + Tools.getVerName(mContext) + "')";
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
                final float lng = readLatLng.getFloat("w_lng",0f);
                final float lat = readLatLng.getFloat("w_lat", 0f);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject lngObject =  new JSONObject();
                        lngObject.put("lng",lng);
                        lngObject.put("lat",lat);

                        String url = "javascript:SetCurrAddress('" + lngObject + "')";

                        ScanActivity.mWebView.loadUrl(url);
                    }
                });
            } else if (exceName.equals("导航")) {

                new Thread() {

                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Tools.ToNavigation(ScanActivity.this.inputName, mContext, getResources().getString(R.string.app_name));
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
                                intent2.putExtra("order_IDX",ScanActivity.this.inputName);
                                mContext.startActivity(intent2);
                            }
                        });
                    }
                }.start();
            }else if(exceName.equals("打印")){

                Intent intentprint=new Intent(mContext,PrintActivity.class);
                intentprint.putExtra("omsNo",ScanActivity.this.inputName);
                mContext.startActivity(intentprint);

            }else if(exceName.equals("shein打印")){

                Log.d("LM", "shein打印");

                Intent intentprint1=new Intent(mContext,PrintActivityshein.class);
                intentprint1.putExtra("omsNo",ScanActivity.this.inputName);
                mContext.startActivity(intentprint1);
            }
        }

        //    扫码
        @JavascriptInterface
        public void  VueSCAN() {

//            new Thread() {
//                public void run() {
//                    Looper.prepare();//增加部分
//                    IntentIntegrator integator = new IntentIntegrator(ScanActivity.this);
//                    integator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//                    integator.setPrompt("请扫描");
//                    integator.setCameraId(0);
//                    integator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
//                    integator.setBarcodeImageEnabled(false);
//                    integator.setCaptureActivity(ScanActivity.class);
//                    integator.initiateScan();
//
//                }
//            }.start();
        }

        @JavascriptInterface
        public  void updateAPP(){

//            initHandler();
//            checkVersion();
//            ifManualUpdate = "手动更新";
        }
    }


    public void showDialog(String result) {
        // 弹出dialog的代码略...

        Log.d("LM", result);

        String url = "javascript:QRScanAjax('" + "234455" + "')";
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
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
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
}
