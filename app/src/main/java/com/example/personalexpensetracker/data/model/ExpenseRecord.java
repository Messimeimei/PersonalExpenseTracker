package com.example.personalexpensetracker.data.model;

import com.example.personalexpensetracker.R;
import androidx.room.*;

import java.util.Date;


@Entity(
        tableName = "expense_record",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class, parentColumns = "categoryId", childColumns = "categoryId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("userId"), @Index("categoryId")}
)

public class ExpenseRecord {
    @PrimaryKey(autoGenerate = true)
    private long recordId;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "date")
    private String date; // 记录日期，如 "2024-11-07"

    @ColumnInfo(name = "time")
    private String time; // 记录时间，如 "22:32"

    @ColumnInfo(name = "type")
    private String type; // "收入" 或 "支出"

    @ColumnInfo(name = "categoryId")
    private int categoryId; // 类别，如 "教育" 或 "娱乐"

    @ColumnInfo(name = "categoryName")
    private String categoryName;

    @ColumnInfo(name = "categoryIcon")
    private int categoryIcon;

    @ColumnInfo(name = "remarks")
    private String remarks; // 备注信息

    @ColumnInfo(name = "amount")
    private double amount; // 金额

    // Getter 和 Setter 方法
    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
