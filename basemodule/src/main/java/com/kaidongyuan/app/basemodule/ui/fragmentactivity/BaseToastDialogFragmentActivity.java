
package com.kaidongyuan.app.basemodule.ui.fragmentactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kaidongyuan.app.basemodule.R;
import com.kaidongyuan.app.basemodule.interfaces.BaseActivityListener;
import com.kaidongyuan.app.basemodule.widget.loadingDialog.MyLoadingDialog;


/**
 * 继承自LifecyclePrintActivity的类，集成Toast,Dialog
 */
public class BaseToastDialogFragmentActivity extends BaseLifecyclePrintFragmentActivity implements BaseActivityListener {
    protected MyLoadingDialog myLoadingDialog;
    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        myLoadingDialog = new MyLoadingDialog(this, R.style.loadingDialog);
    }

    @Override
    public void mStartActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    @Override
    public void mStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastMsg(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToastMsg(String msg, int duration) {
        Toast.makeText(this, msg, duration).show();
    }

    @Override
    public void showToastMsg(int msgId, int duration) {
        Toast.makeText(this, msgId, duration).show();
    }

    @Override
    public void showLoadingDialog() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(mContext, R.style.loadingDialog);
        }
        cancelLoadingDialog();
        myLoadingDialog.showDialog();
    }

    @Override
    public void showProgressBarLoadingDialog() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new MyLoadingDialog(mContext);
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
        return this;
    }



}
