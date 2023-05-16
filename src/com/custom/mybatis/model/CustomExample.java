package com.custom.mybatis.model;

import javax.annotation.Generated;

public class CustomExample {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer rowid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String data;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getRowid() {
        return rowid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setRowid(Integer rowid) {
        this.rowid = rowid;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getData() {
        return data;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }
}