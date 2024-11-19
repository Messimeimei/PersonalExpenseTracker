package com.example.personalexpensetracker.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExpenseRecordDisplayViewModel extends AndroidViewModel {
    private final ExpenseRecordDao expenseRecordDao;
    private final MutableLiveData<List<ExpenseRecordWithCategory>> expenseRecordsLiveData = new MutableLiveData<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ExpenseRecordDisplayViewModel(@NonNull Application application, ExpenseRecordDao expenseRecordDao) {
        super(application);
        this.expenseRecordDao = expenseRecordDao;
    }

    public LiveData<List<ExpenseRecordWithCategory>> getExpenseRecordsLiveData() {
        return expenseRecordsLiveData;
    }

    public void loadExpenseRecords(long userId) {
        executor.execute(() -> {
            List<ExpenseRecordWithCategory> records = expenseRecordDao.getExpenseWithCategoryByUserId(userId);
            expenseRecordsLiveData.postValue(records); // 通知 UI
        });
    }

    // 动态添加记录的方法
    public void addExpenseRecord(ExpenseRecord newRecord, long userId) {
        executor.execute(() -> {
            // 插入数据库
            expenseRecordDao.insertRecord(newRecord);

            // 重新加载数据库中的记录，确保更新同步
            List<ExpenseRecordWithCategory> updatedRecords = expenseRecordDao.getExpenseWithCategoryByUserId(userId);
            expenseRecordsLiveData.postValue(updatedRecords); // 通知 UI
        });
    }
}
