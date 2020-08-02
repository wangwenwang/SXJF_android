package com.kaidongyuan.app.basemodule.ui.fragmentactivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.kaidongyuan.app.basemodule.R;
import com.kaidongyuan.app.basemodule.widget.MLog;
import com.readystatesoftware.systembartint.SystemBarTintManager;


/**
 * 打印生命周期方法
 */
public class BaseLifecyclePrintFragmentActivity extends FragmentActivity {
  String className;
  private SystemBarTintManager tintManager;
  private View mDecorView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    className = getClass().getSimpleName();
//    initWindow();
    MLog.i(className + " onCreat()");
  }

  /**
   * 设置沉浸式体验
   */
  @TargetApi(19)
  public void initWindow(){
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
      Log.d("LM", "设置沉浸式体验1");
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
     // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      tintManager = new SystemBarTintManager(this);
      tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.theme_color_app));
     // tintManager.setStatusBarTintColor(getResources().getColor(R.color.yb_green));
      tintManager.setStatusBarTintEnabled(true);
    }
    mDecorView = getWindow().getDecorView();
  }

  @TargetApi(19)
  public void hideSystemUI() {
   // hasHideSystemUi = true;
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (mDecorView != null){
      mDecorView.setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                      | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    if (tintManager != null){
      tintManager.setStatusBarTintEnabled(false);
    }
  }

  @TargetApi(16)
  public void showSystemUI() {
    //hasHideSystemUi = false;
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (mDecorView != null){
      mDecorView.setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    if (tintManager != null){
      tintManager.setStatusBarTintEnabled(true);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    MLog.i(className + " onStart()");
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    MLog.i(className + " onRestart()");
  }

  @Override
  protected void onResume() {
    super.onResume();
    MLog.i(className + " onResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    MLog.i(className + " onPause()");
  }

  @Override
  protected void onStop() {
    super.onStop();
    MLog.i(className + " onStop()");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    MLog.i(className + " onDestroy()");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    MLog.i(className + " onSaveInstanceState()");
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    MLog.i(className + " onRestoreInstanceState()");
  }
}
