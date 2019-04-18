package com.example.floatbubble.service;

import android.app.Notification;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.floatbubble.InterfaceBin.NotificationComeListener;
import com.example.floatbubble.Util.AppUtil;
import com.example.floatbubble.Util.ClassifyNotiUtil;
import com.example.floatbubble.db.InterestedPool;
import com.example.floatbubble.db.NewNotification;
import com.example.floatbubble.db.NotificationPool;


public class NotificationMonitorService extends NotificationListenerService {

   // private volatile static NotificationMonitorService service;



    private NmBinder nmBinder = new NmBinder();

    //通知带来的回调接口
    public NotificationComeListener notiComelistener;

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
        StatusBarNotification[] list = getActiveNotifications();
        String pck = getPackageName();
        if (list != null) {
        for (StatusBarNotification  n : list) {
            Notification notification = n.getNotification();
            //获取发送通知的App名字
            String appName = "知乎";
            //获取通知的标签
            String label = ClassifyNotiUtil.analyseNoti(notification);
            //新建自定义通知
            NewNotification nn = new NewNotification(notification, label);
            //设置发送者名字
            nn.setSendAppName(appName);
            //将该通知加入到通知池
            NotificationPool.getNotiPoolInstance().addNotification(nn);
            InterestedPool.getNotiPoolInstance().addNotification(nn);
        }
        }

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
        Notification notification = sbn.getNotification();
        //获取发送通知的App名字
        //String appName = AppUtil.getAppName(this,notificationPkg);
        String appName = "知乎";
        //获取通知的标签
        String label = ClassifyNotiUtil.analyseNoti(notification);
        //新建自定义通知
        NewNotification nn = new NewNotification(notification,label);
        //设置发送者名字
        nn.setSendAppName(appName);
        //将该通知加入到通知池
        NotificationPool.getNotiPoolInstance().addNotification(nn);
        InterestedPool.getNotiPoolInstance().addNotification(nn);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //isBound = true;
        String action = intent.getAction();
        Log.d("NOTIF", "onBind: " + action);

        if (SERVICE_INTERFACE.equals(action)) {
            Log.d("NOTIF", "Bound by system");
            return super.onBind(intent);
        } else {
            Log.d("NOTIF", "Bound by application");
            return nmBinder;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //isBound = false;
        return super.onUnbind(intent);
    }

    public class NmBinder extends Binder {
        //TODO 一些操控服务的方法
        /**
         * 获取当前服务的实例
         * * @return
        */
       public  NotificationMonitorService getNmService() {
           return NotificationMonitorService.this;
        }

    }
}
