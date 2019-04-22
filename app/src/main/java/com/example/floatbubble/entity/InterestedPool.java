package com.example.floatbubble.entity;

import java.util.ArrayList;
import java.util.List;

public class InterestedPool {
    private  int count = 0;
    private static InterestedPool interestedPool;
    private List<NewNotification> notificationList = new ArrayList<>();

    private InterestedPool(){

    }

    public static InterestedPool getNotiPoolInstance() {
        if (interestedPool == null) {
            interestedPool =  new InterestedPool();
        }
        return interestedPool;
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
