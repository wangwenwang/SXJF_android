package com.kaidongyuan.app.kdydriver.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.kaidongyuan.app.basemodule.interfaces.AsyncHttpCallback;
import com.kaidongyuan.app.basemodule.utils.nomalutils.MPermissionsUtil;
import com.kaidongyuan.app.basemodule.utils.nomalutils.NetworkUtils;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.R;

import com.kaidongyuan.app.kdydriver.adapter.MainViewpageAdapter;
import com.kaidongyuan.app.kdydriver.app.AppContext;
import com.kaidongyuan.app.kdydriver.constants.Constants;
import com.kaidongyuan.app.kdydriver.httpclient.OrderAsyncHttpClient;
import com.kaidongyuan.app.kdydriver.serviceAndReceiver.GetuiIntentService;
import com.kaidongyuan.app.kdydriver.serviceAndReceiver.GetuiPushService;
import com.kaidongyuan.app.kdydriver.serviceAndReceiver.TrackingService;
import com.kaidongyuan.app.kdydriver.ui.base.BaseFragmentActivity;
//import com.kaidongyuan.app.kdydriver.ui.fragment.IndexFragment;
//import com.kaidongyuan.app.kdydriver.ui.fragment.OrderFragment;
import com.kaidongyuan.app.kdydriver.ui.widget.IsScrollableViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseFragmentActivity implements AsyncHttpCallback {
    private TabLayout tabLayout;
    private IsScrollableViewPager viewPager;
    private OrderAsyncHttpClient mClient;
    private List<String> tablist;
    private List<Integer>imagesrcs;
    private FragmentManager mfragmentManager;
    private List<Fragment>fragments;
//    private MineFragment minefragment;
    //退出应用
    private long firstTime;
    private long secondTime;
    private long spaceTime;
    //检测版本更新
    private final String TAG_CHECKVERSION = "check_version";
    private final String DestFileName="kdydriver.apk";
    private AlertDialog mUpdataVersionDialog;
    private NotificationManager mNotificationManager;
    private Notification mUpdataNotification;
    private Handler mHandler;
    private Intent mLocationIntent;
    private RemoteViews remoteView;
    private final int RequestPermission_STATUS_CODE0=8800;
    private Snackbar pmSnackbar;
    private PushManager pushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initPermission();
        //开启后台定位服务
        if (mLocationIntent==null){
            mLocationIntent=new Intent(this,TrackingService.class);
        }
        getApplicationContext().startService(mLocationIntent);
        initHandler();
        PushManager.getInstance().initialize(getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GetuiIntentService.class);
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT>=23){
           if (MPermissionsUtil.checkAndRequestPermissions(MainActivity.this,
                   new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                           Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}
                   ,RequestPermission_STATUS_CODE0)){
                   checkVersion();
           }
        }else {
            checkVersion();
        }

    }

    public void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("strLicense", "");
        mClient.sendRequest(Constants.URL.CheckVersion, params, TAG_CHECKVERSION);
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
                        Toast.makeText(getMContext(), "升级包不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.fromFile(file);
                        String type = "application/vnd.android.package-archive";//.apk 的 mime 名
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, type);
                        startActivity(intent);
                    }
                    mNotificationManager.cancel(0);
                } else if (percent==-1) {
                    Toast.makeText(MainActivity.this, "下载失败，请查看是否授权App存储权限", Toast.LENGTH_LONG).show();
                    mNotificationManager.cancel(0);
                } else{
                    createNotifaction(percent);
                }
                return false;
            }
        });
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
            mUpdataNotification.tickerText =getResources().getText(R.string.app_name);
        }
        if (remoteView==null) {
            remoteView = new RemoteViews(getMContext().getPackageName(), R.layout.dialog_download);
        }
        remoteView.setTextViewText(R.id.textView_dialog_download, percent+"%");
        remoteView.setProgressBar(R.id.progressBar_dialog_download, 100, percent, false);
        mUpdataNotification.contentView = remoteView;
        mNotificationManager.notify(0, mUpdataNotification);
    }
    private void initview() {
     /*   viewPager= (IsScrollableViewPager) findViewById(R.id.viewPager_mainActivity);
        tablist = new ArrayList<>();
        fragments=new ArrayList<>();
        tablist.add(getString(R.string.index_page));
        tablist.add(getString(R.string.order));
        tablist.add(getString(R.string.mine));
        //模版分块开发

        minefragment=new MineFragment();
        fragments.add(minefragment);
        tabLayout= (TabLayout) findViewById(R.id.tablayout_mainAcitivity);
        for (int i=0;i<tablist.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(tablist.get(i)));
        }
        imagesrcs=new ArrayList<>();
        imagesrcs.add(R.drawable.item_info_hall_bg);
        imagesrcs.add(R.drawable.item_assisant_bg);
        imagesrcs.add(R.drawable.item_mine_bg);
        mfragmentManager=getSupportFragmentManager();
        MainViewpageAdapter mainViewpageAdapter=new MainViewpageAdapter(mfragmentManager,fragments,tablist,imagesrcs,MainActivity.this);
        viewPager.setAdapter(mainViewpageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int j=0;j<tabLayout.getTabCount();j++){
            TabLayout.Tab tab=tabLayout.getTabAt(j);
            tab.setCustomView(mainViewpageAdapter.getTabView(tablist.get(j),imagesrcs.get(j)));
        }
        mClient=new OrderAsyncHttpClient(this,this);
        mNotificationManager=(NotificationManager) getMContext().getSystemService(Context.NOTIFICATION_SERVICE);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.cancleRequest(TAG_CHECKVERSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==RequestPermission_STATUS_CODE0){
            for (int i=0;i<permissions.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_DENIED) {
                    switch (permissions[i]){
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            showToastMsg("请允许应用使用SD卡存储",3000);
                            showSnackbar("请允许应用使用SD卡存储~", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
//                                    startActivity(intent3);
                                    AppContext.IS_LOGIN=false;
                                    finish();
                                }
                            },Snackbar.LENGTH_INDEFINITE);
                            break;
                        case Manifest.permission.CAMERA:
                            showToastMsg("请授权应用调用摄像头权限~",3000);
                            showSnackbar("请授权应用调用摄像头权限~", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent2=new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(intent2);
                                }
                            },Snackbar.LENGTH_INDEFINITE);
                            break;
                        case Manifest.permission.ACCESS_COARSE_LOCATION:
                            showToastMsg("请授权应用网络定位和GPS定位权限ACCESS_COARSE_LOCATION",3000);
                            break;

                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            showToastMsg("请授权应用网络定位和GPS定位权限ACCESS_FINE_LOCATION",3000);
                            break;

                        default:
                            showSnackbar("请授权应用网络定位和GPS定位权限", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent1);
                            }
                        },Snackbar.LENGTH_INDEFINITE);
                            break;
                    }
                    return;
                }
            }

        }
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

    /**
     * 两次点击返回按钮小于两秒退出程序到桌面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (spaceTime > 2000&&Build.BRAND!="Meizu") {
                Toast.makeText(this, "再按一次退出" + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                goHomeActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 版本更新对话框
     * @param currentVersion 当前版本versionName
     * @param version 最新版本versionName
     * @param downUrl 最新版本安装包下载url
     */
    public void createUpdateDialog(String currentVersion, String version, final String downUrl) {
        if (mUpdataVersionDialog==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getMContext());
            builder.setMessage("当前版本：" + currentVersion + "\n最新版本：" + version);
            builder.setCancelable(false);
            final int current = (int) Float.parseFloat(currentVersion);
            final int net = (int) Float.parseFloat(version);
            if (current==net) {
                builder.setTitle("更新版本");
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdataVersionDialog.cancel();
                    }
                });
            } else {
                builder.setTitle("当前版本过低，需下载新版本");
            }
            builder.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mUpdataVersionDialog.cancel();
                    MLog.w("update.url:" + downUrl);
                    //以存储文件名为Tag名
                    mClient.sendFileRequest(downUrl, DestFileName);
                    if (current != net) {
                        goHomeActivity();
                    }
                }
            });
            mUpdataVersionDialog = builder.create();
        }
        mUpdataVersionDialog.show();
    }
    @Override
    public void setProgressBarLoading(int progress) {
        // super.setProgressBarLoading(progress);
        //改为更新通知栏进度条
        Message message=mHandler.obtainMessage();
        message.arg1=progress;
        message.sendToTarget();
    }

    /**
     * 返回桌面
     */
    private void goHomeActivity(){
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public void postSuccessMsg(String msg, String request_tag) {
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
            JSONArray ja=jo.getJSONArray("result");
            int size=ja.size();
            String version=null;
            String downUrl=null;
            JSONObject tempobj;
            String tempdownUrl;
            int startIndex;
            for (int i=0;i<size;i++){
                tempobj=ja.getJSONObject(i);
                tempdownUrl=tempobj.getString("DownLoadAddress");
                startIndex=tempdownUrl.indexOf('/')+1;
                String appname=tempdownUrl.substring(startIndex,tempdownUrl.length());
                if (DestFileName.equals(appname)){
                    version = tempobj.getString("VersionCode");
                    downUrl = Constants.URL.LoadVersion_Url + tempdownUrl;
                    break;
                }
            }
            if (version!=null && downUrl!=null) {
                try {
                    String currentVersion = getMContext().getPackageManager().getPackageInfo(getMContext().getPackageName(), 0).versionName;
                    MLog.w( "version:"+version+"\tcurrentVersion:"+currentVersion);
                    if (!currentVersion.equals(version)) {
                        createUpdateDialog(currentVersion, version, downUrl);
//                        minefragment.isupdate=true;
                    } else {
                        //  Toast.makeText(getMContext(), "当前版本是最新的版本", Toast.LENGTH_SHORT).show();
                        checkGpsState();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    /**
     * 判断 GPS是否开启
     */
    private void checkGpsState() {
        LocationManager alm= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER )&&!alm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
        {
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
                MainActivity.this.startActivity(myIntent);
            }
        },Snackbar.LENGTH_INDEFINITE);
    }
}
