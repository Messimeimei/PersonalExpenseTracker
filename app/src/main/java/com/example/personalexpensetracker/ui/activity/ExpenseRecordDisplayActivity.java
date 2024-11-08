package com.example.personalexpensetracker.ui.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.adapter.ExpenseRecordAdapter;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.ui.fragment.MonthSelectionBottomSheet;
import com.example.personalexpensetracker.ui.fragment.TypeSelectionBottomSheet;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRecordDisplayActivity extends AppCompatActivity implements TypeSelectionBottomSheet.OnTagSelectedListener, MonthSelectionBottomSheet.OnMonthSelectedListener {

    private RecyclerView recordsRecyclerView;
    private LinearLayout typeSelectorButton, monthYearButton;
    private TextView typeNameTextView, monthYearText, detailsText, statsText, assetsText, settingsText;
    private ImageView detailsIcon, statsIcon, assetsIcon, settingsIcon;
    private LinearLayout detailsButton, statsButton, assetsButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_display);

        RecyclerView recyclerView = findViewById(R.id.recordsRecyclerView);
        View addRecordButton = findViewById(R.id.addRecordButton);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 计算滚动的偏移量
                float scrollY = recyclerView.computeVerticalScrollOffset();

                // 根据滚动的偏移量计算透明度，最大透明度为 1，最小为 0.7
                float opacity = 1 - Math.min(scrollY / 1000f, 0.3f);
                addRecordButton.setAlpha(opacity); // 设置透明度
            }

            // 当滚动停止时，恢复透明度
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 滚动停止时恢复透明度
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    addRecordButton.setAlpha(1.0f);
                }
            }
        });

        detailsIcon = findViewById(R.id.detailsIcon);
        statsIcon = findViewById(R.id.statsIcon);
        assetsIcon = findViewById(R.id.assetsIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        detailsText = findViewById(R.id.detailsText);
        statsText = findViewById(R.id.statsText);
        assetsText = findViewById(R.id.assetsText);
        settingsText = findViewById(R.id.settingsText);
        detailsButton = findViewById(R.id.detailsButton);
        statsButton = findViewById(R.id.statsButton);
        assetsButton = findViewById(R.id.assetsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // 图标点击变换颜色，处理点击事件
        detailsIcon.setColorFilter(ContextCompat.getColor(this, R.color.Argentina_blue));  // 默认明细被选中
        detailsText.setTextColor(ContextCompat.getColor(this, R.color.Argentina_blue));
        detailsButton.setOnClickListener(v -> onButtonClick(detailsIcon, detailsText));
        statsButton.setOnClickListener(v -> onButtonClick(statsIcon, statsText));
        assetsButton.setOnClickListener(v -> onButtonClick(assetsIcon, assetsText));
        settingsButton.setOnClickListener(v -> onButtonClick(settingsIcon, settingsText));

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
        recordList.add(new ExpenseRecord("2024-11-10", "07:45", "支出", "交通", "地铁票", -20.0));
        recordList.add(new ExpenseRecord("2024-11-11", "07:45", "支出", "交通", "地铁票", -20.0));


        recordsRecyclerView = findViewById(R.id.recordsRecyclerView);
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExpenseRecordAdapter adapter = new ExpenseRecordAdapter(this, recordList);
        recordsRecyclerView.setAdapter(adapter);

        // 设置全部类型按钮的圆角
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.background_light_white)));
        shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel().toBuilder().setAllCornerSizes(24) // 设置圆角大小
                .build());

        // 获取选择类型按钮和年份选择按钮
        typeSelectorButton = findViewById(R.id.typeSelectorButton);
        monthYearButton = findViewById(R.id.monthYearButton);
        typeNameTextView = findViewById(R.id.typeNameTextView);
        monthYearText = findViewById(R.id.monthYearText);

        // 设置按钮点击事件
        typeSelectorButton.setBackground(shapeDrawable);
        typeSelectorButton.setOnClickListener(v -> {
            // 创建并显示底部弹窗
            TypeSelectionBottomSheet bottomSheet = new TypeSelectionBottomSheet();
            bottomSheet.setOnTagSelectedListener(this);  // 设置回调监听器
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        monthYearButton.setOnClickListener(v -> {
            // 创建并显示底部弹窗
            MonthSelectionBottomSheet bottomSheet = new MonthSelectionBottomSheet();
            bottomSheet.setOnMonthSelectedListener(this);  // 设置回调监听器
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public void onTagSelected(String tag) {
        // 更新按钮上的文字
        typeNameTextView.setText(tag);
    }

    @Override
    public void onMonthSelected(String tag) {
        // 更新按钮上的文字
        monthYearText.setText(tag);
    }

    private void onButtonClick(ImageView clickedIcon, TextView clickedText) {
        // 改变所有图标和文字颜色为灰色
        resetColors();
        // 将点击的图标和文字颜色改变为 Argentina_blue
        clickedIcon.setColorFilter(ContextCompat.getColor(this, R.color.Argentina_blue));
        clickedText.setTextColor(ContextCompat.getColor(this, R.color.Argentina_blue));

    }

    private void resetColors() {
        // 将所有图标的颜色设置回灰色
        detailsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_deep_grey));
        statsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_deep_grey));
        assetsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_deep_grey));
        settingsIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_deep_grey));
        detailsText.setTextColor(ContextCompat.getColor(this, R.color.text_deep_grey));
        statsText.setTextColor(ContextCompat.getColor(this, R.color.text_deep_grey));
        assetsText.setTextColor(ContextCompat.getColor(this, R.color.text_deep_grey));
        settingsText.setTextColor(ContextCompat.getColor(this, R.color.text_deep_grey));

    }
}
