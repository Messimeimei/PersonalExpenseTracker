package com.example.personalexpensetracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense_record")
public class ExpenseRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date; // 记录日期，如 "2024-11-07"
    private String time; // 记录时间，如 "22:32"
    private String type; // "收入" 或 "支出"
    private String category; // 类别，如 "教育" 或 "娱乐"
    private String remarks; // 备注信息
    private double amount; // 金额

    // 构造函数
    public ExpenseRecord(String date, String time, String type, String category, String remarks, double amount) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.category = category;
        this.remarks = remarks;
        this.amount = amount;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
