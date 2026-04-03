package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitappexercise.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnAuth;
    private SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = SharedPrefManager.getInstance(this);
        
        tvWelcome = findViewById(R.id.home_tv_welcome);
        btnAuth = findViewById(R.id.home_btn_auth);

        updateUI();

        btnAuth.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                // Logout
                prefManager.logout();
                updateUI();
            } else {
                // Login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại UI khi quay lại từ màn hình Login
        updateUI();
    }

    private void updateUI() {
        if (prefManager.isLoggedIn()) {
            tvWelcome.setText("Chào, " + prefManager.getUsername()); 
            btnAuth.setText("Đăng xuất");
        } else {
            tvWelcome.setText("Chào mừng đến với Fruit Shop");
            btnAuth.setText("Đăng nhập");
        }
    }
}