// DialogUtils.java,弹出窗口帮助用户点击确定协议
package com.example.personalexpensetracker.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.example.personalexpensetracker.R;

public class DialogUtils {

    public static void showAgreementDialog(Context context, final Runnable onAgree, final Runnable onCancel) {
        // 传入上下文和点击不同按钮时的操作函数即可，这样不同函数调用可以执行不同的方法
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置自定义布局
        View dialogView = View.inflate(context, R.layout.dialog_agreement_checkbox, null);
        builder.setView(dialogView);

        // 获取布局中的视图
        Button okButton = dialogView.findViewById(R.id.button_ok);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);

        // 创建对话框
        AlertDialog dialog = builder.create();

        // 设置按钮点击事件
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgree != null) {
                    onAgree.run(); // 执行同意操作
                }
                dialog.dismiss(); // 关闭对话框
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancel != null) {
                    onCancel.run(); // 执行取消操作（如果需要）
                }
                dialog.dismiss(); // 关闭对话框
            }
        });

        dialog.setCancelable(true); // 允许外部点击关闭
        dialog.show(); // 显示对话框
    }
}
