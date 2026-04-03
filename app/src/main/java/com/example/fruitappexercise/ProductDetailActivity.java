package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.Order;
import com.example.fruitappexercise.model.OrderDetail;
import com.example.fruitappexercise.model.Product;
import com.example.fruitappexercise.utils.SharedPrefManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription;
    private EditText etQuantity;
    private Button btnAddToCart;

    private int productId;
    private Product product;
    private SharedPrefManager prefManager;
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        prefManager = SharedPrefManager.getInstance(this);

        // Get Product ID from Intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            finish();
            return;
        }

        initViews();
        loadProductData();

        btnAddToCart.setOnClickListener(v -> handleAddToCart());
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void loadProductData() {
        AppDatabase db = AppDatabase.getInstance(this);
        product = db.productDao().getProductById(productId);

        if (product != null) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(currencyFormat.format(product.getPrice()));
            tvProductDescription.setText(product.getDescription());
            
            // Dynamic image loading from resources
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                int resId = getResources().getIdentifier(product.getImagePath(), "drawable", getPackageName());
                if (resId != 0) {
                    ivProductImage.setImageResource(resId);
                }
            }
        }
    }

    private void handleAddToCart() {
        if (!prefManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int quantity = 1;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString());
        } catch (Exception e) {}

        if (quantity <= 0) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = prefManager.getCurrentUserId();
        AppDatabase db = AppDatabase.getInstance(this);

        // 1. Get or Create Pending Order
        Order pendingOrder = db.orderDao().getPendingOrderByUserId(userId);
        long orderId;

        if (pendingOrder == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Order newOrder = new Order(userId, currentDate, "Pending", 0.0);
            orderId = db.orderDao().insertOrder(newOrder);
        } else {
            orderId = pendingOrder.getId();
        }

        // 2. Add Order Detail
        OrderDetail detail = new OrderDetail((int) orderId, product.getId(), quantity, product.getPrice());
        db.orderDao().insertOrderDetail(detail);

        // 3. Update Order Total Amount
        if (pendingOrder == null) {
            pendingOrder = db.orderDao().getOrderById((int)orderId);
        }
        pendingOrder.setTotalAmount(pendingOrder.getTotalAmount() + (product.getPrice() * quantity));
        db.orderDao().updateOrder(pendingOrder);

        Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        finish();
    }
}