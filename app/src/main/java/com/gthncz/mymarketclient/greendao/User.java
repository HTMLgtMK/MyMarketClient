package com.gthncz.mymarketclient.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.DaoException;

/**
 * 用户表
 * Created by GT on 2018/5/5.
 */

@Entity
public class User {
    @Id(autoincrement = true)
    private long id;
    @NotNull
    private String name;
    @Unique
    private String mobile;
    @NotNull
    private String user_pass;
    @NotNull
    private int user_status;
    @Unique
    private String user_login;
    @Unique
    private String user_email;
    @NotNull
    private String last_login_ip;
    @NotNull
    private int last_login_time;
    @NotNull
    private String user_activation_key;
    @NotNull
    private int create_time;
    @NotNull
    private int point;
    @NotNull
    private int balance;

    private String user_nickname;
    @NotNull
    private String avatar;
    @NotNull
    private int sex;
    @NotNull
    private int birthday;
    @NotNull
    private long user_level ;

    private String more;
    @ToOne(joinProperty = "user_level")
    private UserLevel userLevelClazz;//外键
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;
    @Generated(hash = 447719540)
    private transient Long userLevelClazz__resolvedKey;

    @Generated(hash = 586692638)
    public User() {
    }

    @Generated(hash = 1160950032)
    public User(long id, @NotNull String name, String mobile, @NotNull String user_pass, int user_status, String user_login, String user_email, @NotNull String last_login_ip, int last_login_time, @NotNull String user_activation_key, int create_time, int point, int balance, String user_nickname, @NotNull String avatar, int sex, int birthday,
            long user_level, String more) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.user_pass = user_pass;
        this.user_status = user_status;
        this.user_login = user_login;
        this.user_email = user_email;
        this.last_login_ip = last_login_ip;
        this.last_login_time = last_login_time;
        this.user_activation_key = user_activation_key;
        this.create_time = create_time;
        this.point = point;
        this.balance = balance;
        this.user_nickname = user_nickname;
        this.avatar = avatar;
        this.sex = sex;
        this.birthday = birthday;
        this.user_level = user_level;
        this.more = more;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public int getUser_status() {
        return user_status;
    }

    public void setUser_status(int user_status) {
        this.user_status = user_status;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public int getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(int last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUser_activation_key() {
        return user_activation_key;
    }

    public void setUser_activation_key(String user_activation_key) {
        this.user_activation_key = user_activation_key;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public long getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", user_pass='" + user_pass + '\'' +
                ", user_status=" + user_status +
                ", user_login='" + user_login + '\'' +
                ", user_email='" + user_email + '\'' +
                ", last_login_ip='" + last_login_ip + '\'' +
                ", last_login_time=" + last_login_time +
                ", user_activation_key='" + user_activation_key + '\'' +
                ", create_time=" + create_time +
                ", point=" + point +
                ", balance=" + balance +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", user_level=" + user_level +
                ", more='" + more + '\'' +
                ", userLevelClazz=" + userLevelClazz +
                '}';
    }

    public void setUser_level(long user_level) {
        this.user_level = user_level;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 35416207)
    public UserLevel getUserLevelClazz() {
        long __key = this.user_level;
        if (userLevelClazz__resolvedKey == null || !userLevelClazz__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserLevelDao targetDao = daoSession.getUserLevelDao();
            UserLevel userLevelClazzNew = targetDao.load(__key);
            synchronized (this) {
                userLevelClazz = userLevelClazzNew;
                userLevelClazz__resolvedKey = __key;
            }
        }
        return userLevelClazz;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1549695543)
    public void setUserLevelClazz(@NotNull UserLevel userLevelClazz) {
        if (userLevelClazz == null) {
            throw new DaoException("To-one property 'user_level' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.userLevelClazz = userLevelClazz;
            user_level = userLevelClazz.get_id();
            userLevelClazz__resolvedKey = user_level;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

}
