package com.example.floatbubble.entity.dbTable;

import org.litepal.crud.DataSupport;

public class Keywords extends DataSupport {
    private int id;
    private String keyWrod;

    public Keywords() {
    }

    public Keywords(String keyWrod) {
        this.keyWrod = keyWrod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyWrod() {
        return keyWrod;
    }

    public void setKeyWrod(String keyWrod) {
        this.keyWrod = keyWrod;
    }
}
