package com.kaidongyuan.app.kdydriver.bean.order;

import com.alibaba.fastjson.JSONObject;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * 用户保存没有网络情况下的位置点的实体类
 */
public class LocationContineTime implements Serializable {

    public String id;
    public String userIdx;
    public Double CORDINATEX;
    public Double CORDINATEY;
    public String ADDRESS;
    public String TIME;

    @Override
    public String toString() {
        return "CORDINATEX:"+CORDINATEX+"\t,CORDINATEY"+CORDINATEY;
    }

}

