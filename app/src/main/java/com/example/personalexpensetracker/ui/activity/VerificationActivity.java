package com.example.personalexpensetracker.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.personalexpensetracker.R;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.Timer;
import java.util.TimerTask;

public class VerificationActivity extends AppCompatActivity {

    private EditText phoneUneditableText, verificationCodeEditText;
    private ImageView backButton;
    private Button getVerificationCodeButton, nextButton;
    private int countDown = 60;
    private boolean isVerificationCodeSent = false;
    private Timer timer;
    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varification_code);

        // 设置输入框的圆角
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setFillColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_smoke)));
        shapeDrawable.setShapeAppearanceModel(
                shapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCornerSizes(24) // 设置圆角大小
                        .build());

        phoneUneditableText = findViewById(R.id.phoneUneditableText);
        phoneUneditableText.setBackground(shapeDrawable);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        verificationCodeEditText.setBackground(shapeDrawable);
        getVerificationCodeButton = findViewById(R.id.getVerificationCodeButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 显示之前注册的手机号
        Intent intent = getIntent();
        String registeredPhone = intent.getStringExtra("phoneNumber"); // 替换为真实手机号
        phoneUneditableText.setText(registeredPhone);

        // 设置手机号输入框为不可编辑
        phoneUneditableText.setFocusable(false); // 禁止获取焦点
        phoneUneditableText.setClickable(false); // 禁止点击
        phoneUneditableText.setCursorVisible(false); // 隐藏光标

        // 获取验证码按钮点击事件
        getVerificationCodeButton.setOnClickListener(v -> {
            // 如果已经发送过验证码，需要重置倒计时
            if (isVerificationCodeSent) {
                resetCountDown();
            } else {
                // 调用接口发送验证码
                sendVerificationCode(registeredPhone);
            }
        });

        // 验证码输入框监听
        verificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkNextButtonState();
            }
        });

        // 下一步按钮点击事件
        nextButton.setOnClickListener(v -> {
            // 处理下一步逻辑
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        // 调用接口发送验证码
        isVerificationCodeSent = true;
        startCountDown();
    }

    private void resetCountDown() {
        // 重置倒计时
        countDown = 60;
        getVerificationCodeButton.setEnabled(false);
        startCountDown();
    }

    private void startCountDown() {
        // 启动倒计时
        getVerificationCodeButton.setEnabled(false);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (countDown > 0) {
                        getVerificationCodeButton.setText(countDown + "s");
                        countDown--;
                    } else {
                        getVerificationCodeButton.setText("重新获取");
                        getVerificationCodeButton.setEnabled(true);
                        timer.cancel();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void checkNextButtonState() {
        boolean isCodeValid = verificationCodeEditText.getText().length() == 6;
        if (isCodeValid) {
            nextButton.setEnabled(isCodeValid);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Argentina_blue)));
            nextButton.setTextColor(getResources().getColor(R.color.black));
        } else {
            nextButton.setEnabled(isCodeValid);
            nextButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_smoke)));
            nextButton.setTextColor(getResources().getColor(R.color.gains_boro));
        }
    }
}
