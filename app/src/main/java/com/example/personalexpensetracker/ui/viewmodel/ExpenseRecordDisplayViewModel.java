package com.example.personalexpensetracker.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.database.AppDatabase;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;

import java.util.List;

public class ExpenseRecordDisplayViewModel extends ViewModel {
    private final LiveData<List<ExpenseRecordWithCategory>> expenseRecords;

    public ExpenseRecordDisplayViewModel(AppDatabase database, long userId) {
        // 从 DAO 获取与用户 ID 关联的数据
        ExpenseRecordDao expenseRecordDao = database.expenseRecordDao();
        expenseRecords = expenseRecordDao.getExpenseWithCategoryByUserId(userId);
    }

    public ExpenseRecordDisplayViewModel(LiveData<List<ExpenseRecordWithCategory>> expenseRecords) {
        this.expenseRecords = expenseRecords;
    }

    // 暴露一个方法提供 LiveData
    public LiveData<List<ExpenseRecordWithCategory>> getExpenseRecords() {
        return expenseRecords;
    }
}


