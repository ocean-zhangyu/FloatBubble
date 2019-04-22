package com.example.floatbubble.entity.dbTable;

import org.litepal.crud.DataSupport;

public class AppNames extends DataSupport {
    private int id;
    private String appName;

    public AppNames() {
    }

    public AppNames(String appName) {
        this.appName = appName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
