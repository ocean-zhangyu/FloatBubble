package com.example.floatbubble.entity.dbTable;

import org.litepal.crud.DataSupport;

public class SocialApps extends DataSupport {
    private int id;
    private String pkgName;

    public SocialApps() {
    }

    public SocialApps(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
