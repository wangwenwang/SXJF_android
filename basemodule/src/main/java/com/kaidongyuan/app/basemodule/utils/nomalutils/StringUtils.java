package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.kaidongyuan.app.basemodule.widget.MLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String C_String_Date_Format = "yyyy-MM-dd";
    private static final String C_String_Date_Time_Format = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormater = new SimpleDateFormat(C_String_Date_Format);
    private static final SimpleDateFormat dateTimeFormater = new SimpleDateFormat(C_String_Date_Time_Format);
    //改为去除第二个？号报错的集合
//    public static final String[] regs = {"！", " ，", "，", "；", "：", "“", "”", "？", "。" + " " + "\"", "! ", ", ", ", ", "; ",
//            ": ", "\"" + " ", " " + "\"", "?", "。" + "\""};
    public static final String[] regs = {"！", " ，", "，", "；", "：", "“", "”", "？", "。" + " " + "\"", "! ", ", ", ", ", "; ",
            ": ", "\"" + " ", " " + "\"", "。" + "\""};
    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    /*
     * 国内电话 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isPlateNum(String number) {
        Pattern p = Pattern.compile("^[A-Za-z_0-9]{5}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    public static String formatDate(Date date, String... format) {
        if (date == null) {
            return "";
        }
        String result = null;
        synchronized (dateFormater) {
            if (format != null && format.length > 0) {
                dateFormater.applyPattern(format[0]);
                result = dateFormater.format(date);
                dateFormater.applyPattern(C_String_Date_Format);
            } else {
                result = dateFormater.format(date);
            }
        }
        return result;
    }

    public static String formatDateTime(Date date, String... format) {
        if (date == null) {
            return "";
        }
        String result = null;
        synchronized (dateTimeFormater) {
            if (format != null && format.length > 0) {
                dateTimeFormater.applyPattern(format[0]);
                result = dateTimeFormater.format(date);
                dateTimeFormater.applyPattern(C_String_Date_Time_Format);
            } else {
                result = dateTimeFormater.format(date);
            }
        }
        return result;
    }
    public static long StringToTime(String strTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (strTime.equals("")){
            strTime=sdf.format(System.currentTimeMillis());
        }
        try {
            return sdf.parse(strTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * @param eTime 较早时间
     * @param Ltime 较晚时间
     * @return 分钟
     */
    public static long calculatorInterval(String eTime, String Ltime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (eTime.equals(""))
            eTime = sdf.format(System.currentTimeMillis());
        try {
            long interval = sdf.parse(eTime).getTime() - sdf.parse(Ltime).getTime();
            interval = interval > 0 ? interval : interval * -1;
            interval /= 1000 * 60; // 分
            long hour = interval / 60;
            long minute = interval % 60;
            return hour * 60 + minute;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * @param startDate
     * @param endDate
     * @return 秒
     */
    public static long intervalCalculator(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            long interval = sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays = interval / daysInMilli;
            interval = interval % daysInMilli;
            long elapsedHours = interval / hoursInMilli;
            interval = interval % hoursInMilli;
            long elapsedMinutes = interval / minutesInMilli;
            interval = interval % minutesInMilli;
            long elapsedSeconds = interval / secondsInMilli;
            System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes,
                    elapsedSeconds);
            return elapsedDays * 24 * 3600 + elapsedHours * 3600 + elapsedMinutes * 60 + elapsedSeconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String decodeStr(String content, String codename) {
        String result = "";
        if (codename == null) {
            codename = "utf-8";
        }

        try {
            result = new String(content.getBytes(), codename);
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return result;
    }



    public static String FormatIndependent2En(String str) {
        for (int i = 0; i < regs.length / 2; i++) {
            str = str.replaceAll(regs[i], regs[i + regs.length / 2]);
        }
        Log.i("FormatIndependent2En", "" + str);
        return str;
    }

    public static LinkedHashMap<String, String> jsonToHashMap(JSONArray array) {
        List<String> temp = new ArrayList<String>();
        LinkedHashMap<String, String> resultMap = null;
        try {
            resultMap = new LinkedHashMap<String, String>();
            resultMap.put("不限", "");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                for (Iterator<String> iter = jsonObject.keys(); iter.hasNext(); ) {
                    temp.add(jsonObject.getString(iter.next()));
                }
                if (isInteger(temp.get(1))) {
                    resultMap.put(temp.get(0), temp.get(1));
                } else
                    resultMap.put(temp.get(1), temp.get(0));
                temp.clear();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static ArrayList<String> getMapKey(HashMap<String, String> map) {
        ArrayList<String> keyList = new ArrayList<String>(map.keySet());
        // for (int i = 0; i < map.size(); i++) {
        // System.out.print("Key list element: " + keyList.get(i));
        // System.out.println();
        // }
        return keyList;
    }

    public static ArrayList<Integer> getMapKeys(HashMap<Integer, Boolean> map) {
        ArrayList<Integer> keyList = new ArrayList<Integer>(map.keySet());
        return keyList;
    }

    public static ArrayList<String> getMapValue(HashMap<String, String> map) {
        ArrayList<String> valueList = new ArrayList<String>(map.values());
        return valueList;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isDouble(String value){
        try {
            Double.parseDouble(value);
            return true;
        }catch (NumberFormatException e){
            return false;
        }


    }
    /**
     * is null or its length is 0 or it is made by space
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return
     * true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static int trin(String str) {
        return Integer.parseInt(str.replaceAll("\\.", ""));
    }

    public static void replaceText(TextView text1, TextView text2) {
        String str2 = text2.getText().toString();
        text2.setText(text1.getText().toString());
        text1.setText(str2);
    }

    public static void replaceId(String id1, String id2) {
        String temp = id1;
        id1 = id2;
        id2 = temp;
    }

    public static String cityNameSubString(String str) {
        return str.replace("市", "");
    }

    public static String replaceHash(String str) {
        if (str.contains("#")) {
            return str.substring(1);
        }
        return str;
    }

    public static int max(ArrayList<Integer> array) {
        int max = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) > array.get(max))
                max = i;
        }
        return array.get(max);
    }

    public static int min(ArrayList<Integer> array) {
        int min = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) < array.get(min))
                min = i;
        }
        return array.get(min);
    }

    public static String paramsToString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String getVersionName(Context mContext) {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    public static String StringToDate(String str) {
        str = str.substring(4, str.length());
        return str.substring(0, str.length() - 2) + "/" + str.substring(2, str.length());
    }

    public static long getRandom() {
        Random random = new Random();
        return Math.abs(random.nextLong() % 100000000l);
    }

    // 截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static boolean isPhoneNumber(String strPhone) {
        Pattern p = Pattern.compile("^(17[0-9]|13[0-9]|15[0-9]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(strPhone);
        return m.find();
    }

    public static boolean checkNull(EditText et) {
        return et.getText().toString().trim().equals("");
    }

    public static boolean checkNull(TextView tv) {
        return tv.getText().toString().trim().equals("");
    }


    public static String subUnit(String orz, String unit) {
        String result = "";
        try {
            int index = orz.indexOf(unit);
            result = orz.substring(0, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String subBySpace(String s, boolean fore) {
        if (fore) {
            return s.split(" ")[0];
        } else {
            return s.split(" ")[1];
        }
    }
    /**
     *按目标sp，切割s后返回第i截字段
     */
    public static String getTargetField(String s,String sp,int i){
        if (s==null||!s.contains("")){
            return null;
        }else try {
            return s.split(sp)[i];
        }catch (Exception ex){
            MLog.e("无法截取指定字段,"+ex.getMessage());
        }
       return null;
    }

    /**
     *
     * @param str 原字符串字段
     * @param s   需要被剔除的字符传
     * @return 返回提出
     */
    public static String subAllTargetCharSequence(String str,String s){

       if (str==null||str.isEmpty()){
           return "";
       }else if (s==null||s.isEmpty()){
           return str.trim();
       } else {
          str=str.replace(s, "");
           return str.trim();
       }
    }

    public static String getProductName(String s) {
        return s.split(",")[0];
    }

    public static String getProductStyle(String s) {
        if (s.contains(",")) {
            s = s.substring(s.split(",")[0].length() + 1, s.length());
        }
        return s;
    }
    //    /**
//     *更改读取产品规格的方法
//     */
//    public static String getProductStyle(String s) {
//        if (s.contains("*")) {
//            s=s.substring(0,s.split("[*]")[0].length());
//
//        }
//        return s;
//    }
    //获取订单状态
    public static String getOrderStatus(String s) {
        if (s.equals("CLOSE")) {
            return "已完成";
        } else if (s.equals("OPEN")) {
            return "在途";
        } else if (s.equals("CANCEL")) {
            return "已取消";
        } else if (s.equals("PENDING")) {
            return "待接收";
        } else {
            return s;
        }
    }

    //获取订单流程
    public static String getOrderState(String s) {
        if (s.equals("新建")) {
            return "已接收";
        } else if (s.equals("已释放")) {
            return "待装货";
        } else if (s.equals("已确认")) {
            return "已拼车";
        } else if (s.equals("已回单")) {
            return "已完结";
        } else {
            return s;
        }
    }

    public static String getRoleName(String s) {
        if (s==null||s.isEmpty()){
            return "";
        } else if (s.equals("PARTY")) {
            return "客户";
        } else if (s.equals("PARGANA")) {
            return "大区";
        } else if (s.equals("BUSINESS")) {
            return "业务员";
        } else if (s.equals("ADMIN")) {
            return "管理员";
        } else {
            return s;
        }
    }


}
