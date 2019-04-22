package com.example.floatbubble.entity;

import android.app.Notification;
import android.app.PendingIntent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewNotification{
    private String time;
    private String title;
    private String content;
    private int label;
    private String sendAppName;
    private int priority;
    private PendingIntent intent;
    private Notification notification;
    public NewNotification(String time, String title, String content, int label, int priority, PendingIntent intent) {
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
        this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(notification.when));
        this.title = notification.extras.getString(Notification.EXTRA_TITLE);
        this.content = notification.extras.getString(Notification.EXTRA_TEXT);
        intent = notification.contentIntent;
        this.priority = notification.priority;
    }
    public NewNotification(Notification notification1, int label) {
        this.notification = notification1;
        this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(notification.when));
        this.title = notification.extras.getString(Notification.EXTRA_TITLE);
        this.content = notification.extras.getString(Notification.EXTRA_TEXT);
        intent = notification.contentIntent;
        this.priority = notification.priority;
        this.label = label;
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
    public String getSendAppName() {
        return sendAppName;
    }

    public void setSendAppName(String sendAppName) {
        this.sendAppName = sendAppName;
    }


    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
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
