package com.yzc.comicreader.model;

import java.io.Serializable;

/**
 * Created by YuZhicong on 2017/5/4.
 */

public class Password implements Serializable{
    private int id;
    private String srcName;
    private String password;

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
