package com.kaidongyuan.app.basemodule.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaidongyuan.app.basemodule.R;
import com.kaidongyuan.app.basemodule.widget.slidingmenu.SlidingMenu;


public class SlidingTitleView extends RelativeLayout {

	private TextView title,managment;
	private EditText ed_center;
	private ImageView goback;
	private ImageView iv_show_menu;
	private Animation animation;
	private SlidingMenu slidingMenu;
	public static final int MODE_BACK = 1;//标记headview的状态，back表示返回，sliding表示打开菜单
	public static final int MODE_SLIDING = 2;
	public static final int MODE_NULL = 3;
	public SlidingTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SlidingTitleView(Context context) {
		super(context);
	}

	public SlidingTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(final Context context) {
		LayoutInflater.from(context).inflate(R.layout.head_layout, this, true);
		findView();
		goback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((Activity) context).finish();
			}
		});
	}
	private void findView() {
		title = (TextView) findViewById(R.id.tv_headview_content);
		goback = (ImageView) findViewById(R.id.iv_headview_goback);
		iv_show_menu = (ImageView) findViewById(R.id.iv_show_menu);
		iv_show_menu.setVisibility(GONE);
		goback.setVisibility(VISIBLE);
		animation = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(500);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
//                btn_back.setAlpha(1.0f);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		iv_show_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(animation);
				if (slidingMenu != null){
					slidingMenu.toggle();
				}
			}
		});
		ed_center= (EditText) findViewById(R.id.ed_headview_center);
		if (isInEditMode()) { return; }
		managment= (TextView) findViewById(R.id.managment);
	}

	public void setSlideMenu(SlidingMenu slideMenu){
		this.slidingMenu = slideMenu;
	}

	public void setMode(int mode){
		if (mode == MODE_BACK){
			iv_show_menu.setVisibility(GONE);
			goback.setVisibility(VISIBLE);
		}else if (mode == MODE_SLIDING){
			iv_show_menu.setVisibility(VISIBLE);
			goback.setVisibility(GONE);
		}else if (mode == MODE_NULL){
			iv_show_menu.setVisibility(GONE);
			goback.setVisibility(GONE);
		}
	}
	public EditText getEd_center(){
		title.setVisibility(GONE);
		ed_center.setVisibility(VISIBLE);
		return ed_center;
	}
	public TextView getManagment(){
		iv_show_menu.setVisibility(GONE);
		managment.setVisibility(VISIBLE);
		return managment;
	}
	public void setText(CharSequence text) {
		title.setText(text);
	}
	public String getText(){
		return (String) title.getText();
	}
}
