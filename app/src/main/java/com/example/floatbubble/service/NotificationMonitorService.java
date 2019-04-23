package com.example.floatbubble.service;

import android.app.Notification;
import android.content.Intent;

import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.floatbubble.InterfaceBin.NotificationComeListener;
import com.example.floatbubble.Util.ClassifyNotiUtil;
import com.example.floatbubble.Util.LogUtil;
import com.example.floatbubble.data.LabelFlags;
import com.example.floatbubble.data.ProgramFlags;
import com.example.floatbubble.entity.InterestedPool;
import com.example.floatbubble.entity.NewNotification;
import com.example.floatbubble.entity.NotificationPool;
import com.example.floatbubble.entity.dbTable.AppNames;
import com.example.floatbubble.entity.dbTable.Keywords;
import com.example.floatbubble.entity.dbTable.NotiTest;
import com.example.floatbubble.entity.dbTable.PkgNames;
import com.example.floatbubble.entity.dbTable.SocialApps;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.example.floatbubble.data.ProgramFlags.COMMONINFO;
import static com.example.floatbubble.data.ProgramFlags.SOCIALINFO;
import static com.example.floatbubble.data.ProgramFlags.INVALIDINFO;
import static com.example.floatbubble.data.ProgramFlags.SYSTEMINFO;


public class NotificationMonitorService extends NotificationListenerService {

    // private volatile static NotificationMonitorService service;


    //第一次运行标志
    private boolean isFirst = true;
    //关键词过滤列表
     private List<Keywords> keyWords;
    //包名过滤列表
    private List<PkgNames> pkgInflateList;
    //APP名过滤列表
    private List<AppNames> appNameList;
    //社交app保护
    private List<SocialApps> socialAppsList;

    private NmBinder nmBinder = new NmBinder();

    //通知带来的回调接口
    public NotificationComeListener notiComelistener;

    public void setNotificationComeListener(NotificationComeListener listener) {
        this.notiComelistener = listener;

    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("通知","服务启动");
        cancelAllNotifications();
        //初始化数据等等
        keyWords = DataSupport.findAll(Keywords.class);
        pkgInflateList = DataSupport.findAll(PkgNames.class);
        socialAppsList = DataSupport.findAll(SocialApps.class);
        if (getActiveNotifications() != null) {
            LogUtil.d("通知计数create", "NotificationService onCreate count:" + getActiveNotifications().length);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化数据等等
        LogUtil.d("通知","服务重启");
        keyWords = DataSupport.findAll(Keywords.class);
        pkgInflateList = DataSupport.findAll(PkgNames.class);
        socialAppsList = DataSupport.findAll(SocialApps.class);
        if (getActiveNotifications() != null) {
            LogUtil.d("通知计数startcommand", "NotificationService onCreate count:" + getActiveNotifications().length);
        }
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
        isFirst = false;
        /**
         * 获取通知消息的文本内容,以加以判断
         */
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String pkgName = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        //bigText
        //String bigText = extras.getString(Notification.)
        //TODO 获取发送通知的App名字
        //String appName = AppUtil.getAppName(this,notificationPkg);
        LogUtil.d("通知来源post",pkgName + " 标题 " + notificationTitle + " 内容 " + notificationText);



        //处理通知
        if (notificationTitle != null && notificationText != null & sbn.getNotification().contentIntent != null) {
            //是否过滤该通知
            int flag = inflater(notificationTitle, notificationText, pkgName);
            LogUtil.d("通知post", "过滤: " + String.valueOf(flag));
            Notification notification = sbn.getNotification();
            int label;//标签
            //通信标签单独过滤
            if (flag == SOCIALINFO) {
                label = LabelFlags.COMMUNICATION;
                //新建自定义通知
                NewNotification nn = new NewNotification(notification,label);
                //将该通知加入到通知池,通信消息不存入数据库,不消除
                NotificationPool.getNotiPoolInstance().addNotification(nn);
                InterestedPool.getNotiPoolInstance().addNotification(nn);
            } else if (flag == COMMONINFO){
                //获取通知的标签,默认为提醒
                label = ClassifyNotiUtil.analyseNoti(notificationTitle+";"+notificationText);
                //新建自定义通知
                NewNotification nn = new NewNotification(notification,label);
                //将该通知加入到通知池
                NotificationPool.getNotiPoolInstance().addNotification(nn);
                InterestedPool.getNotiPoolInstance().addNotification(nn);
                //将该通知存入数据库
                NotiTest notiTest = new NotiTest();
                notiTest.setTitle(notificationTitle);
                notiTest.setContent(notificationText);
                notiTest.save();
                // 是否清除该通知
                if (sbn.isClearable()){
                    //不清楚通信消息
                    cancelNotification(sbn.getKey());

                }
            }
//        if (notiComelistener != null) {
//            notiComelistener.onNotificationCome();
//        }

        }
    }

    /**
     * 过滤无用的系统产生信息,如输入法,正在运行提示等
     * 同时可以过滤自定义关键词,自定义包名信息
     * @param strings
     * @return int 处理标志
     */
    private int inflater(String... strings) {
        for (Keywords keywords : keyWords) {
            if (strings[0].contains(keywords.getKeyWrod())){
                return INVALIDINFO;
            }
            if (strings[1].contains(keywords.getKeyWrod())){
                return INVALIDINFO;
            }
        }
        for (PkgNames pkgName : pkgInflateList) {
            if (strings[2].equals(pkgName.getPkgName())){
                return SYSTEMINFO;
            }
        }
        for (SocialApps app : socialAppsList) {
            if (strings[2].equals(app.getPkgName())) {
                return SOCIALINFO;
            }
        }
        return COMMONINFO;
    }
    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (isFirst) {
            Bundle extras = sbn.getNotification().extras;
            // 获取接收消息的抬头
            String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
            // 获取接收消息的内容
            String notificationText = extras.getString(Notification.EXTRA_TEXT);
            String pkgName = sbn.getPackageName();
            Log.d("通知来源remove",pkgName + "标题 " + notificationTitle + " 内容 " + notificationText);
            if (notificationTitle != null && notificationText != null & sbn.getNotification().contentIntent != null) {
                //是否过滤该通知
                int flag = inflater(notificationTitle, notificationText, pkgName);
                Log.d("通知remove", "过滤: " + String.valueOf(flag));
                Notification notification = sbn.getNotification();
                int label;//标签
                //通信标签单独过滤
                if (flag == SOCIALINFO) {
                    label = LabelFlags.COMMUNICATION;
                    //新建自定义通知
                    NewNotification nn = new NewNotification(notification,label);
                    //将该通知加入到通知池,通信消息不存入数据库,不消除
                    NotificationPool.getNotiPoolInstance().addNotification(nn);
                    InterestedPool.getNotiPoolInstance().addNotification(nn);
                } else if (flag == COMMONINFO){
                    //获取通知的标签,默认为提醒
                    label = ClassifyNotiUtil.analyseNoti(notificationTitle+";"+notificationText);
                    //新建自定义通知
                    NewNotification nn = new NewNotification(notification,label);
                    //将该通知加入到通知池
                    NotificationPool.getNotiPoolInstance().addNotification(nn);
                    InterestedPool.getNotiPoolInstance().addNotification(nn);
                    //将该通知存入数据库
                    NotiTest notiTest = new NotiTest();
                    notiTest.setTitle(notificationTitle);
                    notiTest.setContent(notificationText);
                    notiTest.save();
                    // 是否清除该通知
                    if (sbn.isClearable()){
                        //不清除通信消息
                        if (label != LabelFlags.COMMUNICATION ) {
                            cancelNotification(sbn.getKey());
                        }
                    }
                }
            }
        }
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
