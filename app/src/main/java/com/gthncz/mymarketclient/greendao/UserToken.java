package com.gthncz.mymarketclient.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 用户token表
 * Created by GT on 2018/5/5.
 */
@Entity
public class UserToken {
    @Id(autoincrement = true)
    private long id;
    @Unique
    private int user_id;
    @NotNull
    private int expire_time;
    @NotNull
    private int create_time;
    @NotNull
    private String token;
    @NotNull
    private String device_type;

    @Generated(hash = 2113443620)
    public UserToken() {
    }

    @Generated(hash = 392565934)
    public UserToken(long id, int user_id, int expire_time, int create_time, @NotNull String token,
            @NotNull String device_type) {
        this.id = id;
        this.user_id = user_id;
        this.expire_time = expire_time;
        this.create_time = create_time;
        this.token = token;
        this.device_type = device_type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(int expire_time) {
        this.expire_time = expire_time;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", expire_time=" + expire_time +
                ", create_time=" + create_time +
                ", token='" + token + '\'' +
                ", device_type='" + device_type + '\'' +
                '}';
    }
}
