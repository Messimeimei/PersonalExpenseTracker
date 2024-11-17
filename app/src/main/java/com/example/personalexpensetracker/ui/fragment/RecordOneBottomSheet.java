package com.example.personalexpensetracker.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.view.View;

import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.data.dao.CategoryDao;
import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.database.AppDatabase;
import com.example.personalexpensetracker.data.model.Category;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.ExpenseRecordWithCategory;
import com.example.personalexpensetracker.ui.activity.ExpenseRecordDisplayActivity;
import com.example.personalexpensetracker.utils.AppExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;
import java.util.List;

public class RecordOneBottomSheet extends BottomSheetDialogFragment {

    private OnRecordOneSelectedListener listener;
    private LinearLayout selectedDateButton;
    private GridLayout icon_grid;
    private TextView btnDateSelector;
    private EditText tv_amount;
    private Button currentSelectedButton, btnConfirm;
    private CategoryDao categoryDao;
    private Category selectedCategory;  // 保存当前选中的类别
    private ExpenseRecordDao expenseRecordDao;

    public interface OnRecordOneSelectedListener {
        void onRecordOneSelected();
    }

    public void setOnRecordOneSelectedListener(OnRecordOneSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.dialog_record_one, container, false);

        AppDatabase db = AppDatabase.getInstance(getActivity());
        expenseRecordDao = db.expenseRecordDao();

        // 对确定按钮进行限制
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setEnabled(false);  // 默认不可点击
        btnConfirm.setBackgroundColor(getResources().getColor(R.color.background_light_blue));  // 变淡背景色
        tv_amount = view.findViewById(R.id.tv_amount);

        btnConfirm.setOnClickListener(v -> {
            String amountText = tv_amount.getText().toString().trim();
            if (!amountText.isEmpty() && selectedCategory != null) {
                double amount = Double.parseDouble(amountText);

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                Long userId = sharedPreferences.getLong("userId", 0000001);
                Log.d("记录插到哪位用户", "查看一下当前记录插到了哪个用户那里: " + userId);  // 打印userId


                ExpenseRecord record = new ExpenseRecord();
                record.setType(currentSelectedButton.getText().toString()); // 设置支出、收入、不计入收支
                record.setCategoryName(selectedCategory.getCategoryName());
                record.setCategoryIcon(selectedCategory.getCategoryIcon());
                record.setAmount(amount);
                record.setDate(btnDateSelector.getText().toString());
                record.setCategoryId(selectedCategory.getCategoryId());
                record.setUserId(userId);

                AppExecutors.getDiskIO().execute(() -> {
                    expenseRecordDao.insertRecord(record);  // 使用实际的 DAO 插入方法
                    getActivity().runOnUiThread(() -> {
                        if (listener != null) listener.onRecordOneSelected();  // 回调刷新显示

                        dismiss();
                    });
                });
            }
        });


        tv_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    btnConfirm.setEnabled(false);
                    btnConfirm.setBackgroundColor(getResources().getColor(R.color.background_light_blue));
                } else {
                    btnConfirm.setEnabled(true);
                    btnConfirm.setBackgroundColor(getResources().getColor(R.color.Argentina_blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // 获取按钮和GridLayout引用
        Button btnExpense = view.findViewById(R.id.btn_expense);
        Button btnIncome = view.findViewById(R.id.btn_income);
        Button btnNoAccount = view.findViewById(R.id.btn_no_account);
        icon_grid = view.findViewById(R.id.icon_grid);  // 这个是ScrollView中的GridLayout

        // 从数据库获取所有类别
        categoryDao = db.categoryDao();

        AppExecutors.getDiskIO().execute(() -> {
            List<Category> categories = categoryDao.getAllCategories();
            // 调用方法来加载数据
            loadCategoriesIntoGrid(categories, view);
        });


        // 设置支出、收入和不计入收支按钮初始状态
        setupButtonSelection(btnExpense);
        setupButtonSelection(btnIncome);
        setupButtonSelection(btnNoAccount);

        // 默认选中
        handleSelection(btnExpense);
        loadCategoriesByType("支出");

        // 设置支出、收入和不计入收支按钮点击事件
        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategoriesByType("支出");
                handleSelection(btnExpense);
            }
        });

        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategoriesByType("收入");
                handleSelection(btnIncome);
            }
        });

        btnNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategoriesByType("不计入收支");
                handleSelection(btnNoAccount);
            }
        });

        // 获取视图组件
        selectedDateButton = view.findViewById(R.id.selectedDateButton);
        btnDateSelector = view.findViewById(R.id.btnDateSelector);


        // 显示当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        btnDateSelector.setText(String.format("%d-%d-%d", year, month, day));

        // 日期选择器
        selectedDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) ->
                            btnDateSelector.setText(String.format("%d-%d-%d", year1, monthOfYear + 1, dayOfMonth)),
                    year, month - 1, day);
            datePickerDialog.show();
        });

        // 关闭按钮点击事件
        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        // 设置 OnTouchListener，防止系统键盘弹出
        tv_amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus(); // 使 EditText 获得焦点显示光标
                return true; // 返回 true，拦截触摸事件，不弹出系统键盘
            }
        });


        // 删除按钮的点击事件
        Button deleteButton = view.findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tv_amount.getText().toString();
                if (!text.isEmpty()) {
                    tv_amount.setText(text.substring(0, text.length() - 1));
                    tv_amount.setSelection(tv_amount.getText().length()); // 保持光标在文本末尾
                }
            }
        });


        // 数字按钮的点击事件
        view.findViewById(R.id.btn_dot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText(".");
            }
        });

        view.findViewById(R.id.btn_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("0");
            }
        });

        view.findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("1");
            }
        });

        view.findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("2");
            }
        });

        view.findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("3");
            }
        });

        view.findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("4");
            }
        });

        view.findViewById(R.id.btn_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("5");
            }
        });

        view.findViewById(R.id.btn_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("6");
            }
        });

        view.findViewById(R.id.btn_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("7");
            }
        });

        view.findViewById(R.id.btn_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("8");
            }
        });

        view.findViewById(R.id.btn_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendText("9");
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 设置弹窗宽度为全屏
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // 设置动画效果
            getDialog().getWindow().getAttributes().windowAnimations = R.style.BottomSheetSlideUpAnimation;
        }

        // 允许点击外部区域关闭弹窗
        setCancelable(true);

        // 禁用滑动关闭
        if (getDialog() != null && getDialog().getWindow() != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) getView().getParent());
            behavior.setDraggable(false); // 禁用上下滑动
        }
    }

    // 用于将输入的数字添加到 EditText 中
    private void appendText(String newChar) {
        tv_amount.append(newChar);
        tv_amount.setSelection(tv_amount.getText().length()); // 确保光标始终位于文本的末尾
    }

    // 设置按钮样式的方法
    private void setupButtonSelection(Button button) {
        button.setOnClickListener(v -> handleSelection(button));
    }

    // 更新按钮的选择状态和样式
    private void handleSelection(Button selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setBackgroundColor(getResources().getColor(R.color.background_light_grey)); // 重置为未选中背景
            currentSelectedButton.setTextColor(getResources().getColor(R.color.text_light_grey));
        }
        currentSelectedButton = selectedButton;
        selectedButton.setBackgroundColor(getResources().getColor(R.color.background_light_blue)); // 淡蓝色背景
        selectedButton.setTextColor(getResources().getColor(R.color.Argentina_blue)); // 阿根廷蓝色
    }

    private void loadCategoriesByType(final String type) {
        AppExecutors.getDiskIO().execute(() -> {
            final List<Category> categories = categoryDao.getCategoriesByUserIdAndType(type);
            getActivity().runOnUiThread(() -> {
                loadCategoriesIntoGrid(categories, getView());
                if (!categories.isEmpty()) {
                    // 默认选择第一个类别
                    selectedCategory = categories.get(0);
                    // 更新选中样式
                    LinearLayout firstCategoryView = (LinearLayout) icon_grid.getChildAt(0);
                    if (firstCategoryView != null) {
                        firstCategoryView.setBackgroundColor(getResources().getColor(R.color.background_grey));
                    }
                }
            });
        });
    }


    public void loadCategoriesIntoGrid(List<Category> categories, View view) {
        // 清空 GridLayout
        icon_grid.removeAllViews();
        int columns = 6; // 列数
        icon_grid.setColumnCount(columns);

// 获取需要的填充数
        int totalItems = categories.size();
        int rows = (int) Math.ceil((double) totalItems / columns);
        int fillersNeeded = (rows * columns) - totalItems; // 确保行数适配总项数

        for (int i = 0; i < totalItems + fillersNeeded; i++) {
            if (i < totalItems) {
                Category category = categories.get(i);

                // 创建每个项目的容器
                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setClickable(true);
                itemLayout.setFocusable(true);
                itemLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                itemLayout.setOnClickListener(v -> {
                    clearAllSelections();
                    v.setBackgroundColor(getResources().getColor(R.color.background_grey));
                    selectedCategory = category;  // 设置当前选中的类别
                });

                itemLayout.setOrientation(LinearLayout.VERTICAL);
                itemLayout.setGravity(Gravity.CENTER);

                // 设置宽高和间距
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0; // 宽度使用权重
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(16, 16, 16, 16);
                itemLayout.setLayoutParams(params);

                // 创建 ImageView 和 TextView
                ImageView iconView = new ImageView(getContext());
                iconView.setImageResource(category.getCategoryIcon());
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(120, 120);
                iconView.setLayoutParams(iconParams);

                TextView textView = new TextView(getContext());
                textView.setText(category.getCategoryName());
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(14);

                // 将 ImageView 和 TextView 添加到 itemLayout
                itemLayout.addView(iconView);
                itemLayout.addView(textView);

                // 添加 itemLayout 到 GridLayout
                icon_grid.addView(itemLayout);
            } else {
                // 如果需要填充空白项以对齐网格，则添加占位 View
                View placeholder = new View(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                placeholder.setLayoutParams(params);
                icon_grid.addView(placeholder);
            }
        }

    }

    // 用于清除其他项的选中背景
    private void clearAllSelections() {
        for (int i = 0; i < icon_grid.getChildCount(); i++) {
            View child = icon_grid.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setBackgroundColor(getResources().getColor(R.color.transparent)); // 重置背景颜色
            }
        }
    }



}
