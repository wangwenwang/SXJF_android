package com.kaidongyuan.app.basemodule.utils.nomalutils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/10.
 */
public class DateUtil {
    public static String formateWithoutTime(Date date){
        if (date!=null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(date);
        }else {
            return "";
        }
    }

    public static String formateWithTime(Date date){
        if(date!=null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format(date);
        }else return "";
    }
    public static Date getDateTime(long time){
        Calendar c=Calendar.getInstance();
        Date date=new Date(time);
        c.setTime(date);
        return date;
    }
}
