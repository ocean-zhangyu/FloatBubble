package com.example.floatbubble.Util;

import android.app.Notification;

import com.example.floatbubble.db.NewNotification;

public class ClassifyNotiUtil {
    //TODO 状态量
    //

    //处理方法
    public static NewNotification analyseNoti(Notification notification) {
        //判断逻辑
        NewNotification newNotification = new NewNotification(notification);

        newNotification.setLabel("提醒");

        return newNotification;
    }
}
