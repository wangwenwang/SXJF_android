package com.kaidongyuan.app.kdydriver.utils;


import com.alibaba.fastjson.JSON;

import com.kaidongyuan.app.basemodule.widget.MLog;
import com.kaidongyuan.app.kdydriver.bean.order.LocationContineTime;
import com.kaidongyuan.app.kdydriver.utils.baidumapUtils.DataUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 缓存位置信息文件处理的工具类
 */
public class LocationFileHelper {

    /**
     * 保存方法，会先查出历史记录，后添加新纪录
     * @return 返回是否成功
     */
    public static boolean saveInFile(LocationContineTime location, String fileName){
        if (location.TIME  == null){
            location.TIME = DataUtil.getStringTime(System.currentTimeMillis());
        }
        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //从文件中读取原有的列表数据，再将新的添加进去
        List<LocationContineTime> locationList = readFromFile2(fileName);
        if (locationList == null) {
            locationList = new ArrayList<>();
        }else if (locationList.size()>0){
           //判断新加入的点与最末的存储点是否经纬度差距绝对值大约10，如果大于则视为异常点，放弃存入
            LocationContineTime lastlocation=locationList.get(locationList.size()-1);
            double l= Math.abs(lastlocation.CORDINATEX-location.CORDINATEX)+Math.abs(lastlocation.CORDINATEY-location.CORDINATEY);
            MLog.e("lastlocation-location经纬度差值="+l);
           if (l>10){
               return false;
           }
        }
        if (locationList.size()>100){
            locationList.remove(0);
        }
        locationList.add(location);
        return writeToFile2(locationList, fileName);
    }


    /**
     * 从缓存文件中将点位置信息读取出来
     * @param fileName
     * @return
     */
    public static List<LocationContineTime> readFromFile2(String fileName) {
        File file = new File(fileName);
        if (!file.exists()){
            return null;
        }
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            StringBuffer buffer = new StringBuffer();
            int c = reader.read();
            while (c != -1) {
                buffer.append((char) c);
                c = reader.read();
            }
            if(buffer==null || buffer.length()==0){
                return null;
            }
            return JSON.parseArray(buffer.toString(), LocationContineTime.class);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将点位置信息写入到缓存文件
     * @param locationList
     * @param fileName
     * @return
     */
    private static boolean writeToFile2(List<LocationContineTime> locationList, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }else {
            file.getParentFile().mkdirs();
        }
        if (locationList == null) {
            //如果传入的是空值，则删除文件后返回
            return true;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(JSON.toJSONString(locationList));
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将缓存文件中的位置点信息按要删除的数量删除
     * @param size 要删除的位置点的个数
     * @param fileName 要删除点位置信息的文件
     * @return 删除成功的 Boolean 值
     */
    public static boolean deleteLocation(int size, String fileName){
        //从文件中读取原有的列表数据，删除数据后再将新的添加进去
        List<LocationContineTime> locationList = readFromFile2(fileName);
        if (locationList == null) {
            locationList = new ArrayList<>();
        }
        List<LocationContineTime> locationListCache = locationList.subList(size, locationList.size());
        return writeToFile2(locationListCache, fileName);
    }
}





















