package com.example.personalexpensetracker.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;

public class ExpenseRecordDisplayViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final ExpenseRecordDao expenseRecordDao;

    public ExpenseRecordDisplayViewModelFactory(Application application, ExpenseRecordDao expenseRecordDao) {
        this.application = application;
        this.expenseRecordDao = expenseRecordDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExpenseRecordDisplayViewModel.class)) {
            return (T) new ExpenseRecordDisplayViewModel(application, expenseRecordDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
