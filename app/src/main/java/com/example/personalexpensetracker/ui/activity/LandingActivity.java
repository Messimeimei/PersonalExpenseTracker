package com.example.personalexpensetracker.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.utils.DialogUtils;

public class LandingActivity extends AppCompatActivity {
    private CheckBox agreementCheckBox;
    private Button chooseLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查登录状态
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false); // 默认没有登录

        if (isLoggedIn) {
            // 如果已登录，跳转到登录后的页面
            Intent intent = new Intent(LandingActivity.this, ExpenseRecordDisplayActivity.class); // HomeActivity 是登录后的页面
            startActivity(intent);
            finish(); // 关闭当前活动
            return; // 直接返回，不加载当前页面的内容
        }

        // 如果未登录，显示登录页面
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
                    DialogUtils.showAgreementDialog(LandingActivity.this, new Runnable() {
                                @Override
                                public void run() {
                                    agreementCheckBox.setChecked(true);
                                    showLoginOptions();
                                }
                            },
                            new Runnable() {
                                @Override
                                public void run() {
                                    // 不操作
                                }
                            }
                    );
                }
            }
        });
    }

    private void showLoginOptions() {
        String[] options = {"微信登录", "手机号登录", "注册"};
        new AlertDialog.Builder(this)
                .setTitle("选择登录方式")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            confirmWeChatLogin();
                        } else if (which == 1) {
                            startActivity(new Intent(LandingActivity.this, PhoneLoginActivity.class));
                        } else if (which == 2) {
                            startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
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
