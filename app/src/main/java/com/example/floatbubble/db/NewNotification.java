package com.example.floatbubble.db;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewNotification{
    private String time;
    private String title;
    private String content;
    private String label;
    private int priority;
    private PendingIntent intent;
    private Notification notification;
    public NewNotification(String time, String title, String content, String label, int priority, PendingIntent intent) {
        this.time = time;
        this.title = title;
        this.content = content;
        this.label = label;
        this.priority = priority;
        this.intent = intent;
    }


    public NewNotification(){

    }
    public NewNotification(Notification notification) {
        this.notification = notification;
        this.time = new SimpleDateFormat("HH:mm").format(new Date(notification.when));
        this.title = notification.extras.getString(Notification.EXTRA_TITLE);
        this.content = notification.extras.getString(Notification.EXTRA_TEXT);
        intent = notification.contentIntent;
        this.priority = notification.priority;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setIntent(PendingIntent pi) {
        this.intent = pi;
    }

    public PendingIntent getIntent() {
        return intent;
    }

    public Notification getNotification() {
        return notification;
    }
}
