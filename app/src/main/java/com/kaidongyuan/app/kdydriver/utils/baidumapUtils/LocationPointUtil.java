package com.kaidongyuan.app.kdydriver.utils.baidumapUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaidongyuan.app.kdydriver.bean.order.LocationContineTime;

import java.util.HashMap;
import java.util.List;


/**
 * 对位置点处理的工具类
 */
public class LocationPointUtil {
    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;//坐标方向偏移用常量
    /**
     * 将位置点的集合信息转换为 JSONArray
     * @param locationList 位置点集合
     * @return 点位集合的 JSONArray
     */
    public static JSONArray changeLocationListToString(List<LocationContineTime> locationList){
                JSONArray arr = new JSONArray();
                for (int i = 0; i < locationList.size(); i++) {
                    LocationContineTime locationContineTime = locationList.get(i);
                    JSONObject obj = new JSONObject();
                    obj.put("id", i + "");
                    obj.put("userIDX", locationContineTime.userIdx);
                    obj.put("ADDRESS", locationContineTime.ADDRESS);
                    obj.put("CORDINATEX", locationContineTime.CORDINATEY);
                    obj.put("CORDINATEY", locationContineTime.CORDINATEX);
                    obj.put("TIME", locationContineTime.TIME);
                    arr.add(i, obj);
                }
        return arr;
    }

    /**
     * 百度坐标转换成火星坐标
     * @param bd_lat 百度坐标lat
     * @param bd_lon 百度坐标lon
     * @return HashMap 以strlat、strlon为标志的火星坐标参数
     */
    public static HashMap bd_decrypt(double bd_lat, double bd_lon){
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        HashMap<String,Double> latlonmap=new HashMap<>();
        latlonmap.put("gcjlat",z*Math.sin(theta));
        latlonmap.put("gcjlon",z*Math.cos(theta));
        return latlonmap;
    }
    /**
     * 火星坐标转换成百度坐标
     * @param gcj_lat 火星坐标lat
     * @param gcj_lon 火星坐标lon
     * @return HashMap 以bdlat、bdlon为标志的百度坐标参数
     */
   public static HashMap bd_encrypt(double gcj_lat, double gcj_lon){

        double x = gcj_lon, y = gcj_lat;

        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);

        double theta = Math.atan2(y, x) + 0.000003 *Math.cos(x * x_pi);
       HashMap<String,Double> latlonmap=new HashMap<>();
       latlonmap.put("bdlat",z*Math.sin(theta)+ 0.006);
       latlonmap.put("bdlon",z*Math.cos(theta)+ 0.0065);
        return latlonmap;
    }
}






















