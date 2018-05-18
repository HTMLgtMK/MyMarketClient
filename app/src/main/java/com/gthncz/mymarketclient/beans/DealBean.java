package com.gthncz.mymarketclient.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 交易的数据结构
 * Created by GT on 2018/5/12.
 */

public class DealBean {

    private String id;
    private long user_id;
    private int store_id;
    private int terminal_id;
    private String goods_detail;
    private int pay_amount;
    private int discount_amount;
    private int total_amount;
    private String pay_detail;
    private String remark;
    private long create_time;
    private long modify_time;
    private int status;

    private String store_name;

    public DealBean() {
    }

    public DealBean(String id, long user_id, int store_id, int terminal_id, String goods_detail, int pay_amount, int discount_amount, int total_amount, String pay_detail, String remark, long create_time, long modify_time, int status, String store_name) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.terminal_id = terminal_id;
        this.goods_detail = goods_detail;
        this.pay_amount = pay_amount;
        this.discount_amount = discount_amount;
        this.total_amount = total_amount;
        this.pay_detail = pay_detail;
        this.remark = remark;
        this.create_time = create_time;
        this.modify_time = modify_time;
        this.status = status;
        this.store_name = store_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(int terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getGoods_detail() {
        return goods_detail;
    }

    public void setGoods_detail(String goods_detail) {
        this.goods_detail = goods_detail;
    }

    public int getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(int pay_amount) {
        this.pay_amount = pay_amount;
    }

    public int getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(int discount_amount) {
        this.discount_amount = discount_amount;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getPay_detail() {
        return pay_detail;
    }

    public void setPay_detail(String pay_detail) {
        this.pay_detail = pay_detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getModify_time() {
        return modify_time;
    }

    public void setModify_time(long modify_time) {
        this.modify_time = modify_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    @Override
    public String toString() {
        return "DealBean{" +
                "id='" + id + '\'' +
                ", user_id=" + user_id +
                ", store_id=" + store_id +
                ", terminal_id=" + terminal_id +
                ", goods_detail='" + goods_detail + '\'' +
                ", pay_amount=" + pay_amount +
                ", discount_amount=" + discount_amount +
                ", total_amount=" + total_amount +
                ", pay_detail='" + pay_detail + '\'' +
                ", remark='" + remark + '\'' +
                ", create_time=" + create_time +
                ", modify_time=" + modify_time +
                ", status=" + status +
                ", store_name='" + store_name + '\'' +
                '}';
    }

    public static ArrayList<DealBean> getDealListFromJSONObject(JSONArray jsonArray) throws JSONException {
        if(jsonArray == null) return null;
        int size = jsonArray.length();
        ArrayList<DealBean> dealList = new ArrayList<>();
        for(int i=0;i<size;++i){
            JSONObject dealObj = jsonArray.getJSONObject(i);
            DealBean dealBean = new DealBean();
            dealBean.setId(dealObj.getString("id"));
            dealBean.setUser_id(dealObj.getLong("user_id"));
            dealBean.setStore_id(dealObj.getInt("store_id"));
            dealBean.setTerminal_id(dealObj.getInt("terminal_id"));
            dealBean.setGoods_detail(dealObj.getString("goods_detail"));
            dealBean.setPay_amount(dealObj.getInt("pay_amount"));
            dealBean.setDiscount_amount(dealObj.getInt("discount_amount"));
            dealBean.setTotal_amount(dealObj.getInt("total_amount"));
            dealBean.setPay_detail(dealObj.getString("pay_detail"));
            dealBean.setStatus(dealObj.getInt("status"));
            dealBean.setCreate_time(dealObj.getLong("create_time")*1000);
            dealBean.setModify_time(dealObj.getLong("modify_time")*1000);
            dealBean.setRemark(dealObj.getString("remark"));
            dealBean.setStore_name(dealObj.getString("store_name"));
            dealList.add(dealBean);
        }
        return dealList;
    }

}
