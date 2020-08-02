package com.kaidongyuan.app.kdydriver.app;

import android.app.Activity;
import android.content.Context;


import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.ui.activity.MainActivity;

import java.util.Iterator;
import java.util.Stack;

/**
 ************************************************************************** 
 * @Version 1.0
 * @ClassName: AppManager
 * @Description: 应用程序Activity管理类：用于Activity管理和应用程序退出
 *************************************************************************** 
 */
public class AppManager {

	private static Stack<Activity> activityStack;
	private static Stack<MainActivity> mainActivityStack;
	private static AppManager instance;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	/**
	 * 管理MainActivity
	 */
	public void addMainActivity(MainActivity mainActivity) {

		if (mainActivityStack == null) {
			mainActivityStack = new Stack<MainActivity>();
		}
		mainActivityStack.add(mainActivity);
	}

	public Activity getMainActivity(Class<?> cls) {
		for (Activity activity : mainActivityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	public Activity getActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
//			activity.finish();
			activity = null;
		}

	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Iterator iterator=activityStack.iterator();iterator.hasNext();){
			Activity activity= (Activity) iterator.next();
			if (activity.getClass().equals(cls)&&!activity.isFinishing()){
				iterator.remove();
				activity.finish();
				break;
			}
		}
	}

	/**
	 * 结束所有MainActivity
	 */

	public void finishAllMainActivity() {
		for (int i = 0, size = mainActivityStack.size(); i < size; i++) {
			if (null != mainActivityStack.get(i)) {
				try {
					mainActivityStack.get(i).finish();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		mainActivityStack.clear();
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
			for (int i = 0, size = activityStack.size(); i < size; i++) {
				if (null != activityStack.get(i)) {
					activityStack.get(i).finish();
				}
			}
			activityStack.clear();
	}

	/**
	 * 结束所有的Activity（除了首页）
	 */
	public void finishToHome() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (MainActivity.class != activityStack.get(i).getClass()) {
				activityStack.get(i).finish();
			}
		}
	}

	/**
	 * 退出所有Activity
	 * 
	 * @return 是否完全退出
	 */
	public boolean exitAllActivity() {
		MLog.e("AppManager " + "应用程序退出exitAllActivity()");
		boolean isFinished = true;
		for (Activity act : activityStack) {
			if (act != null) {
				act.finish();
			}
		}
		for (Activity act : activityStack) {
			if (!act.isFinishing()) {
				isFinished = false;
			}
		}
		return isFinished;
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllMainActivity();
			finishAllActivity();
//			ActivityManager activityMgr = (ActivityManager) context
//					.getSystemService(Context.ACTIVITY_SERVICE);
			if (exitAllActivity()) {
				// activityMgr.killBackgroundProcesses(context.getPackageName());
				System.exit(0);
			}
		} catch (Exception e) {
			MLog.e("AppManager " + "退出应用程序异常!");
			StackTraceElement[] stacks = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			for (StackTraceElement stack : stacks) {
				sb.append(stack.toString() + "\n");
			}
			MLog.e("AppManager " + sb.toString());
		}
	}
}