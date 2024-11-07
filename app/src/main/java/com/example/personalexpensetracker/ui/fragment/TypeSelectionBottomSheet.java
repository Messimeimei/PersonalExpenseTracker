package com.example.personalexpensetracker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalexpensetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TypeSelectionBottomSheet extends BottomSheetDialogFragment {

    private OnTagSelectedListener listener;

    public interface OnTagSelectedListener {
        void onTagSelected(String tag);
    }

    public void setOnTagSelectedListener(OnTagSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.dialog_type_selection, container, false);

        // 关闭按钮点击事件
        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        // 获取GridLayout和所有按钮
        GridLayout expenseGridLayout = view.findViewById(R.id.expenseGridLayout);
        GridLayout incomeGridLayout = view.findViewById(R.id.incomeGridLayout);

        // 设置点击事件
        setGridButtonClickListener(expenseGridLayout);
        setGridButtonClickListener(incomeGridLayout);

        return view;
    }

    private void setGridButtonClickListener(GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTagSelected(button.getText().toString());
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
