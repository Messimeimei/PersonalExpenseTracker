package com.example.personalexpensetracker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.example.personalexpensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MonthSelectionBottomSheet extends BottomSheetDialogFragment {

    private OnMonthSelectedListener listener;

    public interface OnMonthSelectedListener {
        void onMonthSelected(String month);
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.dialog_select_month, container, false);

        // 关闭按钮点击事件
        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        // 获取GridLayout和所有按钮
        GridLayout Year2022LinearLayout = view.findViewById(R.id.Year2022GridLayout);
        GridLayout Year2023GridLayout = view.findViewById(R.id.Year2023GridLayout);
        GridLayout Year2024GridLayout = view.findViewById(R.id.Year2024GridLayout);


        // 设置点击事件
        setGridButtonClickListener(Year2022LinearLayout);
        setGridButtonClickListener(Year2023GridLayout);
        setGridButtonClickListener(Year2024GridLayout);

        return view;
    }

    private void setGridButtonClickListener(GridLayout gridLayout) {
        // 提取 GridLayout ID 并将其转换为年份字符串，例如 "Year2022GridLayout" 提取出 "2022"
        String fullIdName = getResources().getResourceEntryName(gridLayout.getId());
        String year = fullIdName.replaceAll("[^0-9]", ""); // 去除非数字字符，保留年份部分

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnClickListener(v -> {
                    if (listener != null) {
                        // 拼接年份和月份，格式如 "2022年12月"
                        String selectedDate = year + "年" + button.getText().toString();
                        listener.onMonthSelected(selectedDate); // 返回拼接后的日期
                    }
                    dismiss(); // 选择后关闭底部弹窗
                });
            }
        }
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
    }

}
