package com.example.fruitappexercise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.User;
import com.example.fruitappexercise.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etUsername, etPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.register_et_fullname);
        etEmail = findViewById(R.id.register_et_email);
        etUsername = findViewById(R.id.register_et_username);
        etPassword = findViewById(R.id.register_et_password);
        btnRegister = findViewById(R.id.register_btn_submit);
        tvLogin = findViewById(R.id.register_tv_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            performRegister(fullName, email, username, password);
        });

        tvLogin.setOnClickListener(v -> finish()); // Quay lại màn hình Login
    }

    private void performRegister(String fullName, String email, String username, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Kiểm tra xem username đã tồn tại chưa (Nếu cần thêm hàm check trong DAO)
            // Hiện tại ta cứ Insert luôn, nếu lỗi Room sẽ báo (hoặc bạn có thể bổ sung hàm check)
            
            User newUser = new User(username, password, fullName, email, Constants.ROLE_USER);
            AppDatabase.getDatabase(this).appDao().insertUser(newUser);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại Login
            });
        });
    }
}