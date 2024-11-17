package com.example.personalexpensetracker.data.model;


import androidx.room.*;

@Entity(tableName = "category", foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "userId",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE))
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int categoryId;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "categoryName")
    private String categoryName;

    @ColumnInfo(name = "categoryIcon")
    private int categoryIcon;

    @ColumnInfo(name = "type")
    private String type;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}
}

