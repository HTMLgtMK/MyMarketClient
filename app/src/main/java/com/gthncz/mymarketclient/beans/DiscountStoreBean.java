package com.gthncz.mymarketclient.beans;

/**
 * 优惠广场数据
 * Created by GT on 2018/5/19.
 */

public class DiscountStoreBean {

    protected int id;
    protected String name;
    protected float extent;
    protected int coin;
    protected long create_time;
    protected long expire_time;
    protected int count;
    protected int rest;
    protected String remark;
    protected int open;/*是否开放: 1. ALL, 2. VIP Only*/
    protected boolean possess;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPossess() {
        return possess;
    }

    public void setPossess(boolean possess) {
        this.possess = possess;
    }

    @Override
    public String toString() {
        return "DiscountStoreBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extend=" + extent +
                ", coin=" + coin +
                ", create_time=" + create_time +
                ", expire_time=" + expire_time +
                ", count=" + count +
                ", rest=" + rest +
                ", remark='" + remark + '\'' +
                ", open=" + open +
                ", possess=" + possess +
                '}';
    }
}
