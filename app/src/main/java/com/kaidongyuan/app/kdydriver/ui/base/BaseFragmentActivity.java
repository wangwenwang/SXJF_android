package com.kaidongyuan.app.kdydriver.ui.base;

import android.os.Bundle;


import com.kaidongyuan.app.basemodule.ui.fragmentactivity.BaseInputFragmentActivity;
import com.kaidongyuan.app.kdydriver.R;
import com.kaidongyuan.app.kdydriver.app.AppManager;


/**
 * baseActivity 封装一些通用方法 所有的activity继承自该Activity
 * 
 */
public abstract class BaseFragmentActivity extends BaseInputFragmentActivity {
	private final String prefName = "account";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}
}
