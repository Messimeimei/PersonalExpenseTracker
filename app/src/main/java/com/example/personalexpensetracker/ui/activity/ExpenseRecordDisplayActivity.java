package com.example.personalexpensetracker.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.adapter.ExpenseRecordAdapter;
import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.dao.CategoryDao;
import com.example.personalexpensetracker.data.database.AppDatabase;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;
import com.example.personalexpensetracker.ui.fragment.MonthSelectionBottomSheet;
import com.example.personalexpensetracker.ui.fragment.RecordOneBottomSheet;
import com.example.personalexpensetracker.ui.fragment.TypeSelectionBottomSheet;
import com.example.personalexpensetracker.ui.viewmodel.ExpenseRecordDisplayViewModel;
import com.example.personalexpensetracker.ui.viewmodel.ExpenseRecordDisplayViewModelFactory;
import com.example.personalexpensetracker.utils.AppExecutors;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.List;

public class ExpenseRecordDisplayActivity extends AppCompatActivity
        implements TypeSelectionBottomSheet.OnTagSelectedListener,
        MonthSelectionBottomSheet.OnMonthSelectedListener,
        RecordOneBottomSheet.OnRecordOneSelectedListener {

    private RecyclerView recordsRecyclerView;
    private LinearLayout typeSelectorButton, monthYearButton, addRecordButton;
    private TextView typeNameTextView, monthYearText, detailsText, statsText, assetsText, settingsText;
    private ImageView detailsIcon, statsIcon, assetsIcon, settingsIcon;
    private LinearLayout detailsButton, statsButton, assetsButton, settingsButton;
    private ExpenseRecordDisplayViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_display);

        RecyclerView recyclerView = findViewById(R.id.recordsRecyclerView);
        addRecordButton = findViewById(R.id.addRecordButton);
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

        // 初始化视图组件
        recordsRecyclerView = findViewById(R.id.recordsRecyclerView);
        addRecordButton = findViewById(R.id.addRecordButton);
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
        settingsButton.setOnClickListener(v -> {
            onButtonClick(settingsIcon, settingsText);
            openSettingsActivity();  // 调用方法跳转到设置页面
        });

        // 设置任务栏颜色为蓝色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.Argentina_blue)); // 蓝色背景色
        }


        // 设置按钮点击事件
        setupButtons();

        // 获取用户的id
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        Long userId = sharedPreferences.getLong("userId", 00000001);
        Log.d("ExpenseRecordDisplay", "查看一下当前登录的用户idId: " + userId);  // 打印userId


        // 获取数据库实例
        AppDatabase db = AppDatabase.getInstance(this);
        ExpenseRecordDao expenseRecordDao = db.expenseRecordDao();

        // 使用 Factory 创建 ViewModel
        ExpenseRecordDisplayViewModelFactory factory = new ExpenseRecordDisplayViewModelFactory(getApplication(), expenseRecordDao);
        viewModel = new ViewModelProvider(this, factory).get(ExpenseRecordDisplayViewModel.class);
        viewModel.loadExpenseRecords(userId);

        // 通过viewmodel加载数据
        viewModel.getExpenseRecordsLiveData().observe(this, recordList -> {
            recordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ExpenseRecordAdapter adapter = new ExpenseRecordAdapter(this, recordList);
            recordsRecyclerView.setAdapter(adapter);
        });

//        // 从数据库中获取用户的记录
//        AppExecutors.getDiskIO().execute(() -> {
//            // 获取带有类别信息的记录
//            List<ExpenseRecordWithCategory> recordList = expenseRecordDao.getExpenseWithCategoryByUserId(userId);
//
//
//            // 在主线程中更新 RecyclerView
//            runOnUiThread(() -> {
//                recordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//                ExpenseRecordAdapter adapter = new ExpenseRecordAdapter(this, recordList);
//                recordsRecyclerView.setAdapter(adapter);
//            });
//        });

        // 全部类型按钮的圆角
        setupButtonsShape();
    }

    private void setupButtons() {
        // 图标点击变换颜色，处理点击事件
        detailsIcon.setColorFilter(ContextCompat.getColor(this, R.color.Argentina_blue));  // 默认明细被选中
        detailsText.setTextColor(ContextCompat.getColor(this, R.color.Argentina_blue));
        detailsButton.setOnClickListener(v -> onButtonClick(detailsIcon, detailsText));
        statsButton.setOnClickListener(v -> onButtonClick(statsIcon, statsText));
        assetsButton.setOnClickListener(v -> onButtonClick(assetsIcon, assetsText));
        settingsButton.setOnClickListener(v -> {
            onButtonClick(settingsIcon, settingsText);
            openSettingsActivity();  // 跳转到设置页面
        });

        // 设置任务栏颜色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.Argentina_blue)); // 蓝色背景色
        }
    }

    private void setupButtonsShape() {
        // 设置全部类型按钮的圆角
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.background_light_white)));
        shapeDrawable.setShapeAppearanceModel(shapeDrawable.getShapeAppearanceModel().toBuilder().setAllCornerSizes(24) // 设置圆角大小
                .build());

        // 获取弹窗选择按钮
        typeSelectorButton = findViewById(R.id.typeSelectorButton);
        monthYearButton = findViewById(R.id.monthYearButton);
        typeNameTextView = findViewById(R.id.typeNameTextView);
        monthYearText = findViewById(R.id.monthYearText);

        // 全部类型按钮点击事件
        typeSelectorButton.setBackground(shapeDrawable);
        typeSelectorButton.setOnClickListener(v -> {
            // 创建并显示底部弹窗
            TypeSelectionBottomSheet bottomSheet = new TypeSelectionBottomSheet();
            bottomSheet.setOnTagSelectedListener(this);  // 设置回调监听器
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        // 年月份按钮点击事件
        monthYearButton.setOnClickListener(v -> {
            // 创建并显示底部弹窗
            MonthSelectionBottomSheet bottomSheet = new MonthSelectionBottomSheet();
            bottomSheet.setOnMonthSelectedListener(this);  // 设置回调监听器
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        // 记一笔按钮点击事件
        addRecordButton.setOnClickListener(v -> {
            RecordOneBottomSheet bottomSheet = new RecordOneBottomSheet();
            bottomSheet.setOnRecordOneSelectedListener(new RecordOneBottomSheet.OnRecordOneSelectedListener() {
                @Override
                public void onRecordOneSelected(ExpenseRecord newRecord) {
                    // 动态添加新记录到数据库
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                    long userId = sharedPreferences.getLong("userId", 00000001);

                    // 调用 ViewModel 添加记录
                    viewModel.addExpenseRecord(newRecord, userId);
                }
            });
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

    // 跳转到设置页面
    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRecordOneSelected(ExpenseRecord newRecord) {

    }
}


