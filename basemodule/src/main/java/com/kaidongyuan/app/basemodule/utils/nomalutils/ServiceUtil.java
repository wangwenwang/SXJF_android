package com.kaidongyuan.app.basemodule.utils.nomalutils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtil {
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        /*
         * 获得正在运行的100个服务,如果运行服务个数大于等于100,则只返回100个,如果不足100个,则把所有运行的服务都返回
         * RunningServiceInfo对象封装了服务的信息  例如运行时间等
         */
        List<ActivityManager.RunningServiceInfo> infos = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : infos) {
            //获得正在运行服务的全类名
            String name = runningServiceInfo.service.getClassName();
            if(serviceName.equals(name)){//如果和传进来的服务全类名一致,则表示此服务正在运行
                return true;
            }
        }
        return false;
    }
}
