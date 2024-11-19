package com.example.personalexpensetracker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.data.model.Category;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseRecordAdapter extends RecyclerView.Adapter<ExpenseRecordAdapter.ViewHolder> {
    private final Map<String, List<ExpenseRecordWithCategory>> groupedRecords;
    private final List<String> dates; // 用于记录分组的日期顺序
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public ExpenseRecordAdapter(Context context, List<ExpenseRecordWithCategory> recordList) {
        this.context = context;

        // 按日期分组记录
        groupedRecords = new HashMap<>();
        for (ExpenseRecordWithCategory record : recordList) {
            String date = record.expenseRecord.getDate();
            if (!groupedRecords.containsKey(date)) {
                groupedRecords.put(date, new ArrayList<>());
            }
            groupedRecords.get(date).add(record);
        }

        // 获取所有日期的列表并按降序排序
        dates = new ArrayList<>(groupedRecords.keySet());
        Collections.sort(dates, (d1, d2) -> {
            try {
                Date date1 = dateFormat.parse(d1);
                Date date2 = dateFormat.parse(d2);
                return date2.compareTo(date1); // 最新的日期排在前面
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, outAmountLabelTextView, outAmountTextView, inAmountLabelTextView, inAmountTextView;
        LinearLayout recordDetailContainer;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            outAmountLabelTextView = view.findViewById(R.id.outAmountLabelTextView);
            outAmountTextView = view.findViewById(R.id.outAmountTextView);
            inAmountLabelTextView = view.findViewById(R.id.inAmountLabelTextView);
            inAmountTextView = view.findViewById(R.id.inAmountTextView);
            recordDetailContainer = view.findViewById(R.id.recordDetailContainer);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record_daily, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String date = dates.get(position); // 获取当前分组的日期
        List<ExpenseRecordWithCategory> recordsForDate = groupedRecords.get(date);

        if (recordsForDate == null) return;

        // 设置日期
        holder.dateTextView.setText(date);

        // 计算当天的收入和支出
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (ExpenseRecordWithCategory recordWithCategory : recordsForDate) {
            ExpenseRecord record = recordWithCategory.expenseRecord;

            // 根据记录的类型区分收入和支出
            if ("入账".equals(record.getType())) {
                totalIncome += record.getAmount(); // 入账金额累加
            } else if ("支出".equals(record.getType())) {
                totalExpense += Math.abs(record.getAmount()); // 支出金额累加，取绝对值
            }
        }


        // 设置支出和收入标签及金额
        holder.outAmountLabelTextView.setText("出");
        holder.outAmountTextView.setText(String.valueOf(totalExpense));
        holder.inAmountLabelTextView.setText("入");
        holder.inAmountTextView.setText(String.valueOf(totalIncome));

        // 设置支出和收入的文字颜色
        holder.outAmountLabelTextView.setTextColor(ContextCompat.getColor(context, R.color.text_deep_grey));
        holder.outAmountLabelTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.background_grey));
        holder.inAmountLabelTextView.setTextColor(ContextCompat.getColor(context, R.color.text_deep_grey));
        holder.inAmountLabelTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.background_grey));

        // 设置支出和收入金额的颜色
        holder.outAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        holder.inAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.black));

        // 清除之前的子视图
        holder.recordDetailContainer.removeAllViews();

        // 动态加载每一条记录
        // 动态加载每一条记录
        for (ExpenseRecordWithCategory recordWithCategory : recordsForDate) {
            View recordDetailView = LayoutInflater.from(context).inflate(R.layout.item_record_single, holder.recordDetailContainer, false);

            ShapeableImageView categoryLogo = recordDetailView.findViewById(R.id.categoryLogo);
            TextView timeTextView = recordDetailView.findViewById(R.id.timeTextView);
            TextView categoryTextView = recordDetailView.findViewById(R.id.categoryTextView);
            TextView remarksTextView = recordDetailView.findViewById(R.id.remarksTextView);
            TextView amountTextView = recordDetailView.findViewById(R.id.amountTextView);

            ExpenseRecord record = recordWithCategory.expenseRecord;
            Category category = recordWithCategory.category;

            // 设置记录详情
            timeTextView.setText(record.getTime());
            categoryTextView.setText(category != null ? category.getCategoryName() : "未知");
            remarksTextView.setText(record.getRemarks());

            // 根据记录类型设置金额前缀和颜色
            String amountPrefix = ""; // 默认没有符号
            switch (record.getType()) {
                case "支出":
                    amountPrefix = "-";
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // 支出颜色
                    break;
                case "入账":
                    amountPrefix = "+";
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.Argentina_yellow)); // 入账颜色
                    break;
                case "不计入收支":
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // 不计入收支颜色
                    break;
                default:
                    amountTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // 默认颜色
                    break;
            }

            // 设置金额文本（带上符号）
            amountTextView.setText(amountPrefix + record.getAmount());

            categoryLogo.setImageResource(category != null ? category.getCategoryIcon() : R.drawable.app_logo);  // 默认图标

            // 确保金额在右侧显示
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) amountTextView.getLayoutParams();
            params.gravity = Gravity.END;  // 确保金额在最右侧显示
            amountTextView.setLayoutParams(params);

            // 将当前记录添加到容器中
            holder.recordDetailContainer.addView(recordDetailView);
        }

    }


    @Override
    public int getItemCount() {
        return dates.size(); // 返回分组的日期数量
    }
}
