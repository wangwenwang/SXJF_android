package com.kaidongyuan.app.basemodule.ui.fragment;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.kaidongyuan.app.basemodule.interfaces.BaseActivityListener;
import com.kaidongyuan.app.basemodule.widget.loadingDialog.MyLoadingDialog;

/**
 * Created by Administrator on 2015/9/24.
 */
public class BaseImplFragment extends Fragment implements BaseActivityListener {
    protected MyLoadingDialog myLoadingDialog;

    @Override
    public void mStartActivity(Class<? extends Activity> cls) {
        if (getActivity() != null)
        startActivity(new Intent(getActivity(), cls));
    }

    @Override
    public void mStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showToastMsg(String msg) {
        showToastMsg(msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showToastMsg(int msgId) {
        showToastMsg(getString(msgId), Toast.LENGTH_SHORT);
    }

    @Override
    public void showToastMsg(int msgId, int duration) {
        showToastMsg(getString(msgId), duration);
    }

    //在设置了
    //<item name="android:fitsSystemWindows">true</item> Toast信息会错位，需要用getActivity.getApplicationContext()避免
    Toast toast;
    @Override
    public void showToastMsg(String msg, int duration) {
        if (getActivity()==null)return;
        //取消之前的Toast信息
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(getActivity().getApplicationContext(), msg, duration);
//        toast.setText(msg);
//        toast.setDuration(duration);
        toast.show();
    }


    @Override
    public void showLoadingDialog() {
        if (myLoadingDialog == null) {
            if (getActivity() != null)
            myLoadingDialog = new MyLoadingDialog(getActivity());
        }
        cancelLoadingDialog();
        myLoadingDialog.showDialog();
    }

    @Override
    public void showProgressBarLoadingDialog() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(getActivity());
        }
        cancelLoadingDialog();
        myLoadingDialog.showProgressDialog();
    }

    @Override
    public void setProgressBarLoading(int progress) {
        if (myLoadingDialog!=null&&myLoadingDialog.isShowing()){
            myLoadingDialog.setLoadingProgressBar(progress);
        }
    }

    @Override
    public void cancelLoadingDialog() {
        if (myLoadingDialog != null && myLoadingDialog.isShowing()) {
            myLoadingDialog.dismiss();
        }
    }

    @Override
    public Context getMContext() {
        return getActivity();
    }
}
