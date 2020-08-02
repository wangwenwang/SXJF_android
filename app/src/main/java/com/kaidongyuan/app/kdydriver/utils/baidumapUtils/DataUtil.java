package com.kaidongyuan.app.kdydriver.utils.baidumapUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataUtil {
	
	public static String getStringTime(long time){

		Calendar c = Calendar.getInstance();
		Date date = new Date(time);
		c.setTime(date);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
	}
	
}






















