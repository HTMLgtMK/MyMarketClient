package com.gthncz.mymarketclient.beans;

/**
 * 积分Log数据结构
 * Created by GT on 2018/5/18.
 */

public class PointLogBean {

    protected long user_id;
    protected long create_time;
    protected String action;
    protected int point;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "PointLogBean{" +
                "user_id=" + user_id +
                ", create_time=" + create_time +
                ", action='" + action + '\'' +
                ", point=" + point +
                '}';
    }
}
