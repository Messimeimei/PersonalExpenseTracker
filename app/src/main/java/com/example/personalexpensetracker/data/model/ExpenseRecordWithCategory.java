// 该数据模型用户保存联表查询的结果

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
