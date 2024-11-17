package com.example.personalexpensetracker.data.model;

import androidx.room.*;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private long userId;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "registerDate")
    public String registerDate;

    // 展示时格式化为 8 位数的字符串
    public String getFormattedId() {
        return String.format("%08d", userId);
    }

    public long getUserId() {
        return userId;  // 存储时使用 long 类型
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }
}
