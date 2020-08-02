package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 用于下载文件的类，在版本更新中用到
 */
public class Download {

    /** 连接url */
    private String urlstr;
    /** sd卡目录路径 */
    private String sdcard;
    /** http连接管理类 */
    private HttpURLConnection urlcon;



    public Download(String url)
    {
        this.urlstr = url;
        //获取设备sd卡目录
        this.sdcard = Environment.getExternalStorageDirectory() + "/";
        urlcon = getConnection();
    }


    /**
     * 获取http连接处理类HttpURLConnection
     */
    private HttpURLConnection getConnection()
    {
        URL url;
        HttpURLConnection urlcon = null;
        try {
            url = new URL(urlstr);
            urlcon = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlcon;
    }

    /**
     * 获取连接文件长度。
     */
    public int getLength()
    {
        return urlcon.getContentLength();
    }

    /**
     * 写文件到sd卡 demo
     * 前提需要设置模拟器sd卡容量，否则会引发EACCES异常
     * 先创建文件夹，在创建文件
     */
    public int down2sd(String dir, String filename, downhandler handler)
    {
        try {
            int responseCode = urlcon.getResponseCode();
            if (responseCode!= HttpURLConnection.HTTP_OK){
                handler.setSize(-1);
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder(sdcard)
                .append(dir);
        File file = new File(sb.toString());
        if (!file.exists()) {
            //创建文件夹
            file.mkdirs();
            Log.d("time", sb.toString());
        }
        //获取文件全名
        sb.append(filename);
        file = new File(sb.toString());

        FileOutputStream fos = null;
        try {
            InputStream is = urlcon.getInputStream();
            //创建文件
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024*10];
            int length;
            while ((length=is.read(buf)) != -1) {
                fos.write(buf, 0, length);
                //同步更新数据
                handler.setSize(length);
            }
            is.close();
            handler.setSize(-2);
        } catch (Exception e) {
            handler.setSize(-1);
            return 0;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    /*
     * 内部回调接口类
     */
    public abstract class downhandler
    {
        public abstract void setSize(int size);
    }



}






