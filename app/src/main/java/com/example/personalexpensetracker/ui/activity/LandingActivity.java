package com.example.personalexpensetracker.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalexpensetracker.R;

public class LandingActivity extends AppCompatActivity {
    private CheckBox agreementCheckBox;
    private Button chooseLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        agreementCheckBox = findViewById(R.id.agreementCheckBox);
        chooseLoginButton = findViewById(R.id.chooseLoginButton);

        chooseLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreementCheckBox.isChecked()) {
                    showLoginOptions();
                }
                else {
                    showAgreementDialog();
                }
            }
        });
    }

    private void showAgreementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置自定义布局
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agreement_checkbox, null);
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
                agreementCheckBox.setChecked(true);
                showLoginOptions();
                dialog.dismiss(); // 关闭对话框
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // 关闭对话框
            }
        });

        dialog.setCancelable(true); // 允许外部点击关闭
        dialog.show(); // 显示对话框
    }

    private void showLoginOptions() {

        String[] options = {"微信登录", "手机号登录", "注册"};
        new AlertDialog.Builder(this)
                .setTitle("选择登录方式")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // 微信登录
                            confirmWeChatLogin();
                        } else if (which == 1) {
                            // 手机号登录
                            startActivity(new Intent(LandingActivity.this, PhoneLoginActivity.class));
                        } else if (which == 2) {
                            // 注册
                        }
                    }
                }).show();
    }

    private void confirmWeChatLogin() {
        new AlertDialog.Builder(this)
                .setTitle("确认微信登录")
                .setMessage("你确定要使用微信登录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 执行微信登录逻辑
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
