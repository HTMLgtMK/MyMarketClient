package com.gthncz.mymarketclient.beans;

/**
 * 积分商城Item数据结构
 * Created by GT on 2018/5/18.
 */

public class PointStoreBean {

    private int point; // 积分数量

    public PointStoreBean() {}

    public PointStoreBean(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
