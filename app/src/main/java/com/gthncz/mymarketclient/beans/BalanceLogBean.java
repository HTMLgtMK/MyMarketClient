package com.gthncz.mymarketclient.beans;

/**
 * 余额Log数据结构
 * Created by GT on 2018/5/18.
 */

public class BalanceLogBean {

    protected long user_id;
    protected long create_time;
    protected int change; // 加减
    protected int balance;
    protected String description;
    protected String remark;

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

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BalanceLogBean{" +
                "user_id=" + user_id +
                ", create_time=" + create_time +
                ", change=" + change +
                ", balance=" + balance +
                ", description='" + description + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
