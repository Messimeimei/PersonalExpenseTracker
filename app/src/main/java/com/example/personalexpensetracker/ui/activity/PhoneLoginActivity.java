package com.example.personalexpensetracker.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalexpensetracker.R;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText phoneInput;
    private EditText passwordInput;
    private CheckBox agreementCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        agreementCheckBox = findViewById(R.id.agreementCheckBox);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 执行登录逻辑
            }
        });
    }
}
