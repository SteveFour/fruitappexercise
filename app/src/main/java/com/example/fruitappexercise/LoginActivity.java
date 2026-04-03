package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.User;
import com.example.fruitappexercise.utils.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.login_et_username);
        etPassword = findViewById(R.id.login_et_password);
        btnLogin = findViewById(R.id.login_btn_submit);
        tvRegister = findViewById(R.id.login_tv_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            // Chuyển sang RegisterActivity nếu có
            Toast.makeText(this, "Chức năng đăng ký đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void performLogin(String username, String password) {
        // Room yêu cầu chạy trong background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = AppDatabase.getDatabase(this).appDao().login(username, password);

            runOnUiThread(() -> {
                if (user != null) {
                    // Lưu trạng thái đăng nhập
                    SharedPrefManager.getInstance(this).saveUserLogin(
                            user.getId(),
                            user.getUsername(),
                            user.getRole()
                    );

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity và đóng LoginActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}