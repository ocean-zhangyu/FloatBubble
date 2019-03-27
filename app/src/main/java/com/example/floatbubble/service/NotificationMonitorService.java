package com.example.floatbubble.service;

import android.app.Notification;
import android.content.Intent;

import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.floatbubble.InterfaceBin.NotificationComeListener;
import com.example.floatbubble.Util.ClassifyNotiUtil;
import com.example.floatbubble.db.NewNotification;
import com.example.floatbubble.db.NotificationPool;


public class NotificationMonitorService extends NotificationListenerService {

    //private volatile static NotificationMonitorService service;


    private NmBinder nmBinder = new NmBinder();

    //通知带来的回调接口
    private NotificationComeListener notiComelistener;

    public void setNotificationComeListener(NotificationComeListener listener) {
        this.notiComelistener = listener;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActiveNotifications() != null) {
                    Log.d("通知计数","NotificationService onCreate count:" + getActiveNotifications().length);
                }
            }
        }, 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化数据等等
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActiveNotifications() != null) {
                    Log.d("通知计数","NotificationService onCreate count:" + getActiveNotifications().length);
                }
            }
        }, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // service = null;//断开强引用
        Log.d("destroy","服务停止");
//        notiComelistener = null;
    }

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        NewNotification nn = ClassifyNotiUtil.analyseNoti(sbn.getNotification());
        NotificationPool.getNotiPoolInstance().addNotification(nn);
//        if (notiComelistener != null) {
//            notiComelistener.onNotificationCome();
//        }
        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText);
    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
    }


    public class NmBinder extends Binder {
        /**
         * 获取当前服务的实例
         * * @return
        */
       public  NotificationMonitorService getNmService() {
            return NotificationMonitorService.this;
        }

    }
}
