package com.example.personalexpensetracker.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.dao.UserDao;
import com.example.personalexpensetracker.data.dao.CategoryDao;
import com.example.personalexpensetracker.data.model.Category;
import com.example.personalexpensetracker.data.database.AppDatabase;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.User;
import com.example.personalexpensetracker.utils.AppExecutors;
import com.google.android.material.button.MaterialButton;
import com.example.personalexpensetracker.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SetPasswordActivity extends AppCompatActivity {

    private EditText phoneUneditableText, nicknameEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton registerButton;
    private ImageView backButton, passwordEyeIcon, confirmPasswordEyeIcon;
    private UserDao userDao;
    private CategoryDao categoryDao;
    private ExpenseRecordDao expenseRecordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);


        // 初始化视图
        phoneUneditableText = findViewById(R.id.phoneUneditableText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        passwordEyeIcon = findViewById(R.id.passwordEyeIcon);  // 密码眼睛图标
        confirmPasswordEyeIcon = findViewById(R.id.confirmPasswordEyeIcon);  // 确认密码眼睛图标


        // 显示之前注册的手机号
        Intent intent = getIntent();
        String registeredPhone = intent.getStringExtra("phoneNumber"); // 接受传来的手机号变量
        phoneUneditableText.setText(registeredPhone);

        // 设置手机号输入框为不可编辑
        phoneUneditableText.setFocusable(false); // 禁止获取焦点
        phoneUneditableText.setClickable(false); // 禁止点击
        phoneUneditableText.setCursorVisible(false); // 隐藏光标

        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        categoryDao = db.categoryDao();
        expenseRecordDao = db.expenseRecordDao();

        // 创建一个通用的 TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }
        };

        // 为所有需要监听的 EditText 添加相同的 TextWatcher
        nicknameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);

        // 注册按钮点击事件
        registerButton.setOnClickListener(view -> {
            String nickname = nicknameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateFields() && password.equals(confirmPassword)) {
                // 注册成功
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                // 获取当前注册时间
                String registerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // 保存用户数据
                User user = new User();
                user.setNickname(nickname);
                user.setPassword(password);
                user.setPhone(phoneUneditableText.getText().toString());
                user.setRegisterDate(registerDate);
                AppExecutors.getDiskIO().execute(() -> {
                    // 插入User数据并获取userId
                    long newUserId = userDao.insert(user);
                    Log.d("注册新用户的Id", "查看一下当前注册的用户Id: " + newUserId);
                    // 插入默认类别，传入有效的userId
                    createDefaultCategories(newUserId);
                    // 获取插入的类别Id
                    List<Category> categories = categoryDao.getCategoriesByUserIdAndType("收入", newUserId);
                    if (!categories.isEmpty()) {
                        int firstCategoryId = categories.get(0).getCategoryId();
                        // 插入一条示例的ExpenseRecord，类型是收入
                        // createDefaultExpenseRecord(newUserId, firstCategoryId, categories.get(0));
                    }
                    runOnUiThread(() -> {
                        startActivity(new Intent(SetPasswordActivity.this, PhoneLoginActivity.class));
                    });
                });
            } else {
                Toast.makeText(this, "密码不匹配或格式不正确", Toast.LENGTH_SHORT).show();
            }
        });

        // 初始状态：眼睛是闭上的，密码不可见
        passwordEyeIcon.setImageResource(R.drawable.ic_eye_close);
        confirmPasswordEyeIcon.setImageResource(R.drawable.ic_eye_close);
        passwordEditText.setInputType(0x00000081);  // 密码不可见

        // 密码可见性切换
        passwordEyeIcon.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, passwordEyeIcon, R.drawable.ic_eye_open, R.drawable.ic_eye_close));
        confirmPasswordEyeIcon.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, confirmPasswordEyeIcon, R.drawable.ic_eye_open, R.drawable.ic_eye_close));
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


    // 输入框验证
    private boolean validateFields() {
        String nickname = nicknameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        setColor(false);  // 设置按钮为不可点击

        // 验证昵称
        if (nickname.isEmpty() || nickname.length() < 1 || nickname.length() > 10) {
            nicknameEditText.setError("昵称必须为1-10个字符");
            return false;
        }

        // 验证密码格式
        if (!password.matches("^[a-zA-Z0-9@]{6,18}$")) {
            passwordEditText.setError("密码必须为6-18位，且仅可包含字母、数字和@");
            return false;
        }

        // 验证密码是否一致
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("2次密码不一致");
            return false;
        }

        setColor(true);  // 设置按钮为可点击
        return true;
    }

    // 设置注册按钮颜色和可点击状态
    private void setColor(boolean clickable) {
        if (clickable) {
            registerButton.setEnabled(true);
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Argentina_blue)));
            registerButton.setTextColor(getResources().getColor(R.color.black));
        } else {
            registerButton.setEnabled(false);
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_smoke)));
            registerButton.setTextColor(getResources().getColor(R.color.gains_boro));
        }
    }

    private void createDefaultCategories(long userId) {
        String[] expenseCategories = {"食物", "娱乐", "交通"};
        String[] incomeCategories = {"工资", "奖金"};
        String[] nonBudgetCategories = {"赠送", "其他"};

        for (String categoryName : expenseCategories) {
            Category category = new Category();
            category.setUserId(userId);
            category.setCategoryName(categoryName);
            category.setType("支出");
            category.setCategoryIcon(R.drawable.app_logo);
            categoryDao.insertCategory(category);
        }

        for (String categoryName : incomeCategories) {
            Category category = new Category();
            category.setUserId(userId);
            category.setCategoryName(categoryName);
            category.setType("收入");
            category.setCategoryIcon(R.drawable.app_logo);
            categoryDao.insertCategory(category);
        }

        for (String categoryName : nonBudgetCategories) {
            Category category = new Category();
            category.setUserId(userId);
            category.setCategoryName(categoryName);
            category.setType("不计入收支");
            category.setCategoryIcon(R.drawable.app_logo);
            categoryDao.insertCategory(category);
        }
    }

    private void createDefaultExpenseRecord(long userId, int categoryId, Category category)   {
        ExpenseRecord expense = new ExpenseRecord();
        expense.setUserId(userId);
        expense.setType("收入");
        expense.setCategoryId(categoryId);
        expense.setDate("2024-11-25");
        expense.setAmount(2022);
        expense.setRemarks("去看电影了");
        expense.setCategoryName(category.getCategoryName());
        expense.setCategoryIcon(category.getCategoryIcon());

        // 插入类别到数据库
        expenseRecordDao.insertRecord(expense);
    }
}
