package com.gthncz.mymarketclient.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 用户等级表
 * Created by GT on 2018/5/5.
 */
@Entity
public class UserLevel {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    @Unique
    private long _id;
    @NotNull
    private String name;
    @NotNull
    private int count;
    @NotNull
    private int status;

    @Generated(hash = 826131989)
    public UserLevel() {
    }

    @Generated(hash = 1995930281)
    public UserLevel(long _id, @NotNull String name, int count, int status) {
        this._id = _id;
        this.name = name;
        this.count = count;
        this.status = status;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserLevel{" +
                "id=" + _id +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", status=" + status +
                '}';
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
