package com.kaidongyuan.app.kdydriver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.kaidongyuan.app.kdydriver.app.AppContext;

import java.util.HashMap;


public class SharedPreferencesUtils {
	public static final int STRING = 0;
	public static final int INT = 1;
	public static final int BOOLEAN = 2;
	public static final int LONG = 3;
	public static final String SHARED_NAME = "base_driver";
	public static void saveUserId(String id){
		SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("userId", id);
		editor.apply();
	}

	public static String getUserId(){
		SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		String sid=sharedPreferences.getString("userId", null);
		return sid;
	}


	public static void ClearSharedPreferences(String dataBasesName) {
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, 0);
		Editor editor = user.edit();
		editor.clear();
		editor.commit();
	}

	public static void removeSharedPreferences(String dataBasesName, String key) {
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, 0);
		Editor editor = user.edit();
		editor.remove(key);
		editor.commit();
	}

	public static SharedPreferences ReadSharedPreferences(String dataBasesName) {
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, 0);
		return user;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> getAllByBasesName(String dataBasesName) {
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, 0);
		HashMap<String, Object> map = (HashMap<String, Object>) user.getAll();
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValueByName(String dataBasesName, String key,
									   int type) {
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, Context.MODE_PRIVATE);
		Object value = null;
		switch (type) {
			case STRING:
				value = user.getString(key, "");
				break;
			case INT:
				value = user.getInt(key, 0);
				break;
			case BOOLEAN:
				value = user.getBoolean(key, false);
				break;
			case LONG:
				value = user.getLong(key, 0);
				break;
		}
		return (T) value;
	}

	public static void WriteSharedPreferences(String dataBasesName,
											  String name, Object value) {
		if (name == null || value == null) {
			return;
		}
		SharedPreferences user = AppContext.getInstance().getSharedPreferences(
				dataBasesName, 0);
		Editor editor = user.edit();
		if (value instanceof Integer) {
			editor.putInt(name, Integer.parseInt(value.toString()));
		} else if (value instanceof Long) {
			editor.putLong(name, Long.parseLong(value.toString()));
		} else if (value instanceof Boolean) {
			editor.putBoolean(name, Boolean.parseBoolean(value.toString()));
		} else if (value instanceof String) {
			editor.putString(name, value.toString());
		} else if (value instanceof Float) {
			editor.putFloat(name, Float.parseFloat(value.toString()));
		}
		editor.apply();
	}
}
