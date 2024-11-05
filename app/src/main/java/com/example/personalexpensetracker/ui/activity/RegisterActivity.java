package com.example.personalexpensetracker.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.personalexpensetracker.R;
import com.example.personalexpensetracker.utils.DialogUtils;
import com.google.android.material.shape.MaterialShapeDrawable;

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneInput;
    private Button registerButton;
    private CheckBox agreementCheckbox;
    private ImageView backButton;
    private boolean isPhoneValid = false;  // Track phone validation state
    private boolean isAgreementChecked = false;  // Track agreement checkbox state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 设置输入框的圆角
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_smoke)));
        shapeDrawable.setShapeAppearanceModel(
                shapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCornerSizes(24) // 设置圆角大小
                        .build());

        phoneInput = findViewById(R.id.phoneEditText);
        phoneInput.setBackground(shapeDrawable);

        registerButton = findViewById(R.id.registerButton);
        agreementCheckbox = findViewById(R.id.agreementCheckBox);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 添加手机号输入监听
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhoneNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRegisterButtonState();
            }
        });

        // 添加协议复选框监听
        agreementCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAgreementChecked = isChecked;
            updateRegisterButtonState(); // 更新按钮状态
        });

        // 点击注册按钮时的逻辑
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhoneValid) {
                    if (isAgreementChecked) {
                        // 复选框已勾选，直接弹出确认窗口
                        showPhoneConfirmationDialog();
                    } else {
                        // 复选框未勾选，弹出协议确认窗口
                        showAgreementConfirmationDialog();
                    }
                }
            }
        });
    }

    // 1. 验证手机号格式
    private void validatePhoneNumber(String phoneNumber) {
        isPhoneValid = isPhoneNumberValid(phoneNumber);
    }

    // 2. 更新注册按钮状态
    private void updateRegisterButtonState() {
        if (isPhoneValid) {
            // 如果手机号有效，按钮可以点击
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Argentina_blue)));
            registerButton.setTextColor(getResources().getColor(R.color.black));
            registerButton.setEnabled(true);
        } else {
            // 如果手机号无效，按钮不可点击
            registerButton.setEnabled(false);
            registerButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_smoke)));
            registerButton.setTextColor(getResources().getColor(R.color.gains_boro));
        }
    }

    // 3. 手机号格式验证
    private boolean isPhoneNumberValid(String phoneNumber) {
        // 判断中国手机号格式
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }

    // 4. 确认手机号注册
    private void showPhoneConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("请确认手机号码")
                .setMessage(phoneInput.getText().toString().trim())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 执行手机号注册登录逻辑
                        String phoneNumber = phoneInput.getText().toString().trim();
                        Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber); // 使用putExtra()传递数据
                        startActivity(intent); // 启动 VerificationActivity
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 5. 弹出协议确认窗口（使用自定义的DialogUtils）
    private void showAgreementConfirmationDialog() {
        DialogUtils.showAgreementDialog(RegisterActivity.this, new Runnable() {
            @Override
            public void run() {
                // 用户点击同意协议，勾选复选框并弹出手机号确认框
                agreementCheckbox.setChecked(true);
                showPhoneConfirmationDialog();
            }
        }, new Runnable() {
            @Override
            public void run() {
                // 用户点击取消，不做任何操作，保持状态
            }
        });
    }
}
