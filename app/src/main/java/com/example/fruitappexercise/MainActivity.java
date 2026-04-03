package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitappexercise.adapter.CategoryAdapter;
import com.example.fruitappexercise.adapter.ProductAdapter;
import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.Category;
import com.example.fruitappexercise.model.Product;
import com.example.fruitappexercise.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener {

    private TextView tvWelcome;
    private Button btnAuth;
    private RecyclerView rvCategories, rvProducts;
    private ExtendedFloatingActionButton fabOrder;
    
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private AppDatabase database;
    private SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getDatabase(this);
        prefManager = SharedPrefManager.getInstance(this);

        initViews();
        setupRecyclerViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.home_tv_welcome);
        btnAuth = findViewById(R.id.home_btn_auth);
        rvCategories = findViewById(R.id.home_rv_categories);
        rvProducts = findViewById(R.id.home_rv_products);
        fabOrder = findViewById(R.id.home_fab_order);
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        rvProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Category> categories = database.appDao().getAllCategories();
            List<Product> products = database.appDao().getAllProducts();

            runOnUiThread(() -> {
                categoryAdapter.setCategories(categories);
                productAdapter.setProducts(products);
            });
        });
    }

    private void setupListeners() {
        btnAuth.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                prefManager.logout();
                updateUI();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        fabOrder.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                Toast.makeText(this, "Đang chuyển sang màn hình Tạo Đơn hàng...", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(this, CheckoutActivity.class);
                // startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để tạo đơn hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCategoryClick(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> filteredProducts = database.appDao().getProductsByCategory(category.getId());
            runOnUiThread(() -> productAdapter.setProducts(filteredProducts));
        });
    }

    @Override
    public void onProductClick(Product product) {
        Toast.makeText(this, "Xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCart(Product product) {
        // Kiểm tra đăng nhập khi bấm nút THÊM
        if (prefManager.isLoggedIn()) {
            Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ", Toast.LENGTH_SHORT).show();
            // Sau này Dev B sẽ code thêm logic thêm vào bảng OrderDetail ở đây
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}