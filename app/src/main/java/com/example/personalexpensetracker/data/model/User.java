package com.example.personalexpensetracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")    // 表明是数据库，同时User对应数据库中的表名是users表
public class User {
    @PrimaryKey(autoGenerate = true)    // id字段为主键，同时自增
    private int id;
    private String username;
    private String password;

    // 构造函数吗，初始化用户的用户名和密码并赋值给字段
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
