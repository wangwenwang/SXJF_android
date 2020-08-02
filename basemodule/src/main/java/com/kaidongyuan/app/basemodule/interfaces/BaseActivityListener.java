package com.kaidongyuan.app.basemodule.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 *此接口既是各activity需要实现的方法，又是作为回调接口，控制Activity的动作
 */
public interface BaseActivityListener {

  void mStartActivity(Class<? extends Activity> cls);

  void mStartActivity(Intent intent);

  /**
   * 显示Toast信息
   * @param msg 信息内容
   */
  void showToastMsg(String msg);

  /**
   *
   * @param msgId 信息资源Id
   */
  void showToastMsg(int msgId);
  /**
   *
   * @param msg 信息内容
   * @param duration  持续时间
   */
  void showToastMsg(String msg, int duration);

  void showToastMsg(int msgId, int duration);
  /**
   * 显示加载中的dialog
   */
  void showLoadingDialog();
  /**
   *显示下载进度的dialog
   */
  void showProgressBarLoadingDialog();
  /**
   *更新下载进度情况
   */
  void setProgressBarLoading(int progress);

  /**
   * 取消显示加载中的dialog
   */
  void cancelLoadingDialog();

  Context getMContext();

}
