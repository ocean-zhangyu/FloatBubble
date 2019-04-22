package com.example.floatbubble.entity;


import java.util.ArrayList;
import java.util.List;

public final class NotificationPool {
    private  int count = 0;
    private static NotificationPool notificationPool;
    private  List<NewNotification> notificationList = new ArrayList<>();

    private NotificationPool(){

    }

    public static NotificationPool getNotiPoolInstance() {
        if (notificationPool == null) {
            notificationPool =  new NotificationPool();
        }
        return notificationPool;
    }

    public void addNotification(NewNotification newNotification) {
        notificationList.add(0,newNotification);
        count++;

    }
    public  List<NewNotification> getNewNotificationList() {
        return notificationList;
    }
    public int getCount() {
        return count;
    }

}
