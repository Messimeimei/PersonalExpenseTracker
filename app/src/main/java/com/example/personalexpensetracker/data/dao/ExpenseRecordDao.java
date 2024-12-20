package com.example.personalexpensetracker.data.dao;

import androidx.room.*;

import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;

import java.util.List;

@Dao
public interface ExpenseRecordDao {
    @Insert
    void insertRecord(ExpenseRecord record);

    @Update
    void updateRecord(ExpenseRecord record);

    @Query("DELETE FROM expense_record")
    void deleteAllExpenseRecords();

    // 修改后的查询，使用 date 字段代替 timestamp
    @Query("SELECT * FROM expense_record WHERE date LIKE :date || '%' ORDER BY date DESC")
    List<ExpenseRecord> getRecordsByDate(String date);

    // 获取所有记录，按日期排序
    @Query("SELECT * FROM expense_record ORDER BY date DESC")
    List<ExpenseRecord> getAllRecords();

    @Query("SELECT * FROM expense_record WHERE userId = :userId ORDER BY date DESC")
    List<ExpenseRecord> getRecordsByUserId(long userId);

    // 在expenseRecord中获取类别的名称和图标
    @Transaction
    @Query("SELECT record.*, category.categoryName AS categoryName FROM expense_record AS record " +
            "LEFT JOIN category ON record.categoryId = category.categoryId " +
            "WHERE record.userId = :userId")
    List<ExpenseRecord> getCategoryName(long userId);

    @Transaction
    @Query("SELECT record.*, category.categoryIcon AS categoryIcon FROM expense_record AS record " +
            "LEFT JOIN category ON record.categoryId = category.categoryId " +
            "WHERE record.userId = :userId")
    List<ExpenseRecord> getCategoryIcon(long userId);

    @Transaction
    @Query("SELECT expense_record.*, category.categoryName AS categoryName, category.categoryIcon AS categoryIcon " +
            "FROM expense_record " +
            "JOIN category ON expense_record.categoryId = category.categoryId " +
            "WHERE expense_record.userId = :userId")
    List<ExpenseRecordWithCategory> getExpenseWithCategoryByUserId(long userId);



}
