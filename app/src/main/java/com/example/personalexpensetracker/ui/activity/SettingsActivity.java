package com.example.personalexpensetracker.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.data.database.AppDatabase;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private TextView userIdTextView, nicknameTextView, phoneTextView;
    private ImageView backButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 绑定UI组件
        backButton = findViewById(R.id.backButton);
        userIdTextView = findViewById(R.id.userIdTextView);
        nicknameTextView = findViewById(R.id.nicknameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        logoutButton = findViewById(R.id.logoutButton); // 新增的“退出登录”按钮

        // 设置任务栏颜色为蓝色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.Argentina_blue)); // 蓝色背景色
        }

        // 设置返回按钮点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取 SharedPreferences 中保存的用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("formattedUserId", "N/A");
        String nickname = sharedPreferences.getString("nickname", "N/A");
        String phone = sharedPreferences.getString("phone", "N/A");

        // 将信息显示在 TextView 中
        userIdTextView.setText(userId);
        nicknameTextView.setText(nickname);
        phoneTextView.setText(phone);

        // 设置“退出登录”按钮点击事件
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除登录状态和用户信息
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();


                // 跳转到启动页面（例如 LoginActivity）
                Intent intent = new Intent(SettingsActivity.this, PhoneLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除返回栈
                startActivity(intent);
            }
        });
    }
}
