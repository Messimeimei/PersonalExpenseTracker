package com.example.personalexpensetracker.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import java.util.List;

public class ExpenseRecordAdapter extends RecyclerView.Adapter<ExpenseRecordAdapter.ViewHolder> {
    private List<ExpenseRecord> recordList;
    private Context context;

    public ExpenseRecordAdapter(Context context, List<ExpenseRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, outAmountLabelTextView, outAmountTextView, inAmountLabelTextView, inAmountTextView;
        LinearLayout recordDetailContainer;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            outAmountLabelTextView = view.findViewById(R.id.outAmountLabelTextView);  // 修改 ID
            outAmountTextView = view.findViewById(R.id.outAmountTextView);  // 修改 ID
            inAmountLabelTextView = view.findViewById(R.id.inAmountLabelTextView);  // 修改 ID
            inAmountTextView = view.findViewById(R.id.inAmountTextView);  // 修改 ID
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
        ExpenseRecord record = recordList.get(position);

        // 设置日期
        holder.dateTextView.setText(record.getDate());

        // 计算当天的收入和支出
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        for (ExpenseRecord r : recordList) {
            if (r.getDate().equals(record.getDate())) {
                if (r.getAmount() > 0) {
                    totalIncome += r.getAmount();
                } else {
                    totalExpense += Math.abs(r.getAmount());
                }
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
        for (ExpenseRecord r : recordList) {
            if (r.getDate().equals(record.getDate())) {
                View recordDetailView = LayoutInflater.from(context).inflate(R.layout.item_record_single, holder.recordDetailContainer, false);

                TextView timeTextView = recordDetailView.findViewById(R.id.timeTextView);
                TextView categoryTextView = recordDetailView.findViewById(R.id.categoryTextView);
                TextView remarksTextView = recordDetailView.findViewById(R.id.remarksTextView);
                TextView amountTextView = recordDetailView.findViewById(R.id.amountTextView);

                // 设置记录详情
                timeTextView.setText(r.getTime());
                categoryTextView.setText(r.getCategory());
                remarksTextView.setText(r.getRemarks());
                amountTextView.setText(String.valueOf(r.getAmount()));

                // 设置金额颜色为黑色
                amountTextView.setTextColor(ContextCompat.getColor(context, R.color.black));

                // 确保金额在右侧显示
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) amountTextView.getLayoutParams();
                params.gravity = Gravity.END;  // 确保金额在最右侧显示
                amountTextView.setLayoutParams(params);

                // 将当前记录添加到容器中
                holder.recordDetailContainer.addView(recordDetailView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
