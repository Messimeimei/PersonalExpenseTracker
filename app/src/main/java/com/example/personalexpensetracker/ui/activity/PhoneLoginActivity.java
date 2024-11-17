package com.example.personalexpensetracker.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.data.dao.UserDao;
import com.example.personalexpensetracker.data.database.AppDatabase;
import com.example.personalexpensetracker.data.model.User;
import com.example.personalexpensetracker.utils.AppExecutors;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.example.personalexpensetracker.utils.DialogUtils;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText phoneEditText, passwordEditText;
    private CheckBox agreementCheckBox;
    private Button loginButton, registerButton;
    private ImageView backButton, passwordEyeIcon;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // 设置输入框的圆角
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_smoke)));
        shapeDrawable.setShapeAppearanceModel(
                shapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCornerSizes(24) // 设置圆角大小
                        .build());

        passwordEyeIcon = findViewById(R.id.passwordEyeIcon);
        phoneEditText = findViewById(R.id.phoneEditText);
        phoneEditText.setBackground(shapeDrawable);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setBackground(shapeDrawable);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDao();

        agreementCheckBox = findViewById(R.id.agreementCheckBox);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 执行登录逻辑
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (phone.isEmpty() || !isValidPhone(phone)) {
                    showToast("手机号格式不正确！");
                } else if (password.isEmpty()) {
                    showToast("请输入密码！");
                } else if (!agreementCheckBox.isChecked()) {
                    DialogUtils.showAgreementDialog(PhoneLoginActivity.this, new Runnable() {
                        @Override
                        public void run() {
                            // 用户点击同意协议，勾选复选框并弹出手机号确认框
                            agreementCheckBox.setChecked(true);
                            checkLogin(phone, password);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            // 用户点击取消，不做任何操作，保持状态
                        }
                    });
                } else {
                    // 检查登录信息
                    checkLogin(phone, password);
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到注册页面
                Intent intent = new Intent(PhoneLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 初始状态：眼睛是闭上的，密码不可见
        passwordEyeIcon.setImageResource(R.drawable.ic_eye_close);
        passwordEditText.setInputType(0x00000081);  // 密码不可见
        // 密码可见性切换
        passwordEyeIcon.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, passwordEyeIcon, R.drawable.ic_eye_open, R.drawable.ic_eye_close));

    }

    // 检查登录信息
    private void checkLogin(String phone, String password) {
        // 同样在子线程中执行
        AppExecutors.getDiskIO().execute(() -> {
            User user = userDao.getUserByPhone(phone);  // 根据手机号获取该用户
            if (user == null) {
                runOnUiThread(() -> {
                    Toast.makeText(PhoneLoginActivity.this, "该手机号未注册", Toast.LENGTH_SHORT).show();
                });
            }  else if (!user.getPassword().equals(password)) {
                // 密码错误
                runOnUiThread(() -> {
                    Toast.makeText(PhoneLoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                });
            } else {
                // 登录成功
                runOnUiThread(() -> {
                    Toast.makeText(PhoneLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                });
                // 保存登录状态
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("userId",user.getUserId());
                editor.putString("formattedUserId", String.valueOf(user.getFormattedId()));
                editor.putString("nickname", user.getNickname());
                editor.putString("phone", user.getPhone());
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // 跳转到下一个页面
                startActivity(new Intent(PhoneLoginActivity.this, ExpenseRecordDisplayActivity.class));
                finish();
            }
        });
    }

    // 密码可见性切换
    private void togglePasswordVisibility(EditText editText, ImageView eyeIcon, int visibleIcon, int hiddenIcon) {
        // 根据点击的是哪个眼睛图标来切换相应输入框的可见性
        boolean isVisible = editText.getTransformationMethod() instanceof PasswordTransformationMethod;

        if (isVisible) {
            // 设置为可见密码
            editText.setTransformationMethod(null);  // 不使用隐藏密码的转换方法
            eyeIcon.setImageResource(visibleIcon);  // 设置睁眼图标
        } else {
            // 设置为隐藏密码
            editText.setTransformationMethod(new PasswordTransformationMethod());  // 使用隐藏密码的转换方法
            eyeIcon.setImageResource(hiddenIcon);  // 设置闭眼图标

        }

        // 将光标移到文本末尾，避免光标丢失
        editText.setSelection(editText.getText().length());
    }

    // 检查手机号格式是否正确
    private boolean isValidPhone(String phone) {
        // 这里可以使用正则表达式来验证手机号格式
        return phone.matches("[1][3-9][0-9]{9}");
    }

    // 显示Toast提示
    private void showToast(String message) {
        Toast toast = Toast.makeText(PhoneLoginActivity.this, message, Toast.LENGTH_SHORT);

        // 设置Toast显示的位置，位置为屏幕顶部（Gravity.TOP）
        toast.setGravity(Gravity.TOP, 0, 20); // 200是Y轴偏移量，控制距离顶部的距离

        // 显示Toast
        toast.show();
    }
}
