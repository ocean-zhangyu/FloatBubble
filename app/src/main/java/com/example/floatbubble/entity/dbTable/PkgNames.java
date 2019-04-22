package com.example.floatbubble.entity.dbTable;

import org.litepal.crud.DataSupport;

public class PkgNames extends DataSupport {
    private int id;
    private String pkgName;

    public PkgNames() {
    }

    public PkgNames(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
