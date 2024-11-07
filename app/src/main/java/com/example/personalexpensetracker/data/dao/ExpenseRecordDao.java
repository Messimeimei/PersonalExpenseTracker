package com.example.personalexpensetracker.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalexpensetracker.data.model.ExpenseRecord;

import java.util.List;

@Dao
public interface ExpenseRecordDao {
    @Insert
    void insertRecord(ExpenseRecord record);

    @Update
    void updateRecord(ExpenseRecord record);

    @Delete
    void deleteRecord(ExpenseRecord record);

    // 修改后的查询，使用 date 字段代替 timestamp
    @Query("SELECT * FROM expense_record WHERE date LIKE :date || '%' ORDER BY date DESC")
    List<ExpenseRecord> getRecordsByDate(String date);

    // 获取所有记录，按日期排序
    @Query("SELECT * FROM expense_record ORDER BY date DESC")
    List<ExpenseRecord> getAllRecords();
}
