package com.example.personalexpensetracker.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalexpensetracker.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Query("DELETE FROM category")
    void deleteAllCategories();

    @Query("SELECT categoryName FROM category WHERE categoryId = :categoryId")
    String getCategoryName(String categoryId);

    @Query("SELECT categoryIcon FROM category WHERE categoryId = :categoryId")
    int getCategoryIcon(int categoryId);

    // 查询所有类别，按名称排序
    @Query("SELECT * FROM category ORDER BY categoryName ASC")
    List<Category> getAllCategories();

    @Query("SELECT * FROM category WHERE userId = :userId")
    List<Category> getCategoriesByUserId(long userId);

    // 根据用户id和所属类型返回对应type下所有的category
    @Query("SELECT * FROM category WHERE type = :type AND userId = :userId")
    List<Category> getCategoriesByUserIdAndType(String type, long userId);

}
