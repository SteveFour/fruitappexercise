package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.Order;
import com.example.fruitappexercise.model.OrderDetail;
import com.example.fruitappexercise.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    
    private CartAdapter adapter;
    private List<OrderDetail> cartItems = new ArrayList<>();
    private Order pendingOrder;
    private PreferenceManager preferenceManager;
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        preferenceManager = new PreferenceManager(this);
        
        initViews();
        loadCartData();

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvCartTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems);
        rvCartItems.setAdapter(adapter);
    }

    private void loadCartData() {
        int userId = preferenceManager.getUserId();
        AppDatabase db = AppDatabase.getInstance(this);
        
        pendingOrder = db.orderDao().getPendingOrderByUserId(userId);

        if (pendingOrder != null) {
            List<OrderDetail> details = db.orderDao().getOrderDetailsByOrderId(pendingOrder.getId());
            cartItems.clear();
            cartItems.addAll(details);
            adapter.notifyDataSetChanged();

            tvTotalAmount.setText(currencyFormat.format(pendingOrder.getTotalAmount()));
        } else {
            tvTotalAmount.setText("0 đ");
            btnCheckout.setEnabled(false);
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCheckout() {
        if (pendingOrder == null || cartItems.isEmpty()) return;

        AppDatabase db = AppDatabase.getInstance(this);
        
        // Update Order status to Paid
        pendingOrder.setStatus("Paid");
        db.orderDao().updateOrder(pendingOrder);

        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
        
        // Redirect to Invoice Screen
        Intent intent = new Intent(this, InvoiceActivity.class);
        intent.putExtra("ORDER_ID", pendingOrder.getId());
        startActivity(intent);
        finish();
    }
}