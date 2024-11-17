package com.example.personalexpensetracker.data.model;

import androidx.room.*;

public class ExpenseRecordWithCategory {
    @Embedded
    public ExpenseRecord expenseRecord;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "categoryId"
    )
    public Category category;
}
