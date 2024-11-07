package com.example.personalexpensetracker.ui.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.adapter.ExpenseRecordAdapter;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.ui.fragment.TypeSelectionBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRecordDisplayActivity extends AppCompatActivity implements TypeSelectionBottomSheet.OnTagSelectedListener {

    private RecyclerView recordsRecyclerView;
    private LinearLayout typeSelectorButton;
    private TextView typeNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_display);

        // 设置任务栏颜色为蓝色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.Argentina_blue)); // 蓝色背景色
        }

        // 模拟记录数据
        List<ExpenseRecord> recordList = new ArrayList<>();
        recordList.add(new ExpenseRecord("2024-11-07", "22:32", "支出", "餐饮", "午餐", -50.0));
        recordList.add(new ExpenseRecord("2024-11-07", "19:15", "支出", "娱乐", "电影", -100.0));
        recordList.add(new ExpenseRecord("2024-11-08", "09:00", "收入", "工资", "月薪", 3000.0));
        recordList.add(new ExpenseRecord("2024-11-09", "07:45", "支出", "交通", "地铁票", -20.0));

        recordsRecyclerView = findViewById(R.id.recordsRecyclerView);
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExpenseRecordAdapter adapter = new ExpenseRecordAdapter(this, recordList);
        recordsRecyclerView.setAdapter(adapter);

        // 获取选择类型按钮
        typeSelectorButton = findViewById(R.id.typeSelectorButton);
        typeNameTextView = findViewById(R.id.typeNameTextView);

        // 设置按钮点击事件
        typeSelectorButton.setOnClickListener(v -> {
            // 创建并显示底部弹窗
            TypeSelectionBottomSheet bottomSheet = new TypeSelectionBottomSheet();
            bottomSheet.setOnTagSelectedListener(this);  // 设置回调监听器
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public void onTagSelected(String tag) {
        // 更新按钮上的文字
        typeNameTextView.setText(tag);
    }
}
