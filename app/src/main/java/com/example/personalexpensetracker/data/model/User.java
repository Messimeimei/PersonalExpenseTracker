package com.example.personalexpensetracker.data.model;

import androidx.room.*;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nickname")
    public String nickname;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "password")
    public String password;
}

