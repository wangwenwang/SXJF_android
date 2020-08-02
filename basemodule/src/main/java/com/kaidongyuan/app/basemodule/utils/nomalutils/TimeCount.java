package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.os.CountDownTimer;
import android.widget.TextView;



public class TimeCount extends CountDownTimer {

	public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	private TextView timerBtn;

	private String tips;

	public TimeCount(TextView btn, String tips, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		this.timerBtn = btn;
		this.tips = tips;
	}

	@Override
	public void onFinish() {
		// timerBtn.setBackgroundResource(R.drawable.btn_green_select);
		switch (timerBtn.getId()) {
			//注释用于给指定textview设置计时显示
//		case R.id.sent_verify_code:
//			// 计时完毕时触发
//			timerBtn.setText(tips);
//			timerBtn.setEnabled(true);
//			break;
		default:
			break;
		}

	}

	@Override
	public void onTick(long millisUntilFinished) {
		// 计时过程显示
		timerBtn.setEnabled(false);
		// timerBtn.setBackgroundResource(R.drawable.gray_rect_coner_bg);
		switch (timerBtn.getId()) {
			//注释用于给指定textview设置计时显示
//		case R.id.sent_verify_code:
//			timerBtn.setText(millisUntilFinished / 1000 + " s");
//			break;
		}
	}

}
