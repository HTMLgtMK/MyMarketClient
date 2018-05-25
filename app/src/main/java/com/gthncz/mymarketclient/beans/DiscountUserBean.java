package com.gthncz.mymarketclient.beans;

/**
 * 会员优惠数据结构
 * Created by GT on 2018/5/19.
 */

public class DiscountUserBean {
    // discount_user info
    protected int id;
    private int discount_id;
    private int user_id;
    protected int count;
    protected int rest;
    // discount info
    protected String name;
    protected float extent;
    protected int coin;
    protected long create_time;
    protected long expire_time;
    protected String remark;
    protected int open;/*是否开放: 1. ALL, 2. VIP Only*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(int discount_id) {
        this.discount_id = discount_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getExtent() {
        return extent;
    }

    public void setExtent(float extent) {
        this.extent = extent;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(long expire_time) {
        this.expire_time = expire_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "DiscountUserBean{" +
                "id=" + id +
                ", discount_id=" + discount_id +
                ", user_id=" + user_id +
                ", count=" + count +
                ", rest=" + rest +
                ", name='" + name + '\'' +
                ", extent=" + extent +
                ", coin=" + coin +
                ", create_time=" + create_time +
                ", expire_time=" + expire_time +
                ", remark='" + remark + '\'' +
                ", open=" + open +
                '}';
    }
}
