package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.os.Environment;

import java.io.File;

public class FileUtil {

    /**
     * 获取缓存文件夹
     * @param fileName 缓存文件名
     * @return 缓存文件夹
     */
    public static File getCacheDirFile(String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return null;
        }
        File dirFile = new File(Environment.getExternalStorageDirectory(), fileName);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }


}
