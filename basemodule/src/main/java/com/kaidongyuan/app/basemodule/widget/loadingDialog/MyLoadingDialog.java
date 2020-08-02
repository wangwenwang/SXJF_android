package com.kaidongyuan.app.basemodule.widget.loadingDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaidongyuan.app.basemodule.R;
import com.kaidongyuan.app.basemodule.network.BaseAsyncHttpClient;


public class MyLoadingDialog extends Dialog {
    Window window;
    TextView tipTextView;
    NewtonCradleLoading loadingBalls;
    ProgressBar loadingProgressBar;
    private Thread mCloseSelfThread;

    public MyLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    protected MyLoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public MyLoadingDialog(Context context) {
//        super(context, R.style.loadingDialog);
        this(context, R.style.loadingDialog);
    }
    public void showDialog(){
        if (window == null
                || window.getDecorView().getVisibility() == View.VISIBLE) {//decorView是window中的最顶层view
            setContentView(R.layout.dialog_loading);
            windowDeploy();
            tipTextView = (TextView) findViewById(R.id.tipTextView);
            loadingBalls = (NewtonCradleLoading) findViewById(R.id.loading_balls);
            loadingProgressBar= (ProgressBar) findViewById(R.id.loading_progressBar);
            tipTextView.setVisibility(View.GONE);
            loadingBalls.setVisibility(View.VISIBLE);
            loadingProgressBar.setVisibility(View.GONE);
        }
        loadingBalls.start();
        try {
            show();
        }catch (Exception e){
            e.printStackTrace();
        }
        //设置默认10秒后取消MyLoadingDialog的显示
        mCloseSelfThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(40*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MyLoadingDialog.this.dismiss();
            }
        });
        mCloseSelfThread.start();
    }
    public  void  showProgressDialog(){
        if (window == null
                || window.getDecorView().getVisibility() == View.VISIBLE) {//decorView是window中的最顶层view
            setContentView(R.layout.dialog_loading);
            windowDeploy();
            tipTextView = (TextView) findViewById(R.id.tipTextView);
            loadingBalls = (NewtonCradleLoading) findViewById(R.id.loading_balls);
            loadingProgressBar= (ProgressBar) findViewById(R.id.loading_progressBar);
            loadingProgressBar.setVisibility(View.VISIBLE);
            loadingProgressBar.setProgress(0);
            tipTextView.setVisibility(View.VISIBLE);
            loadingBalls.setVisibility(View.GONE);
        }
        //设置默认20秒后取消MyLoadingDialog的显示
        mCloseSelfThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MyLoadingDialog.this.dismiss();
            }
        });
        mCloseSelfThread.start();

    }
    public void setLoadingProgressBar(int progress){
        if (progress>0&&progress<=100) {
            loadingProgressBar.setProgress(progress);
        }
    }
    @Override
    public void dismiss() {
        super.dismiss();
        if (mCloseSelfThread!=null){
            mCloseSelfThread.interrupt();
            mCloseSelfThread=null;
        }
    }

    // 设置窗口显示
    public void windowDeploy() {
            // 设置触摸对话框意外的地方取消对话框
            setCanceledOnTouchOutside(false);
            window = getWindow(); // 得到对话框
            // window.setWindowAnimations(R.style.goldDialogWindowAnim); // 设置窗口弹出动画
            // window.setBackgroundDrawableResource(R.drawable.bluebtn); //
            // 设置对话框背景为透明
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.alpha = 0.5f; //设置透明度
            wl.gravity = Gravity.CENTER; // 设置重力
        try {
            window.setAttributes(wl);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
