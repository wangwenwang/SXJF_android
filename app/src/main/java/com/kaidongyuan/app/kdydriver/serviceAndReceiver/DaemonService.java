package com.kaidongyuan.app.kdydriver.serviceAndReceiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class DaemonService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  START_NOT_STICKY改为START_STICKY
        return Service.START_STICKY;
    }
}
