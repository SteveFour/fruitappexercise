package com.example.fruitappexercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.Order;
import com.example.fruitappexercise.model.OrderDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private TextView tvOrderId, tvDate, tvTotal;
    private RecyclerView rvItems;
    private Button btnBackHome;
    
    private CartAdapter adapter;
    private List<OrderDetail> items = new ArrayList<>();
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        initViews();
        loadInvoiceData(orderId);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvInvoiceOrderId);
        tvDate = findViewById(R.id.tvInvoiceDate);
        tvTotal = findViewById(R.id.tvInvoiceTotal);
        rvItems = findViewById(R.id.rvInvoiceItems);
        btnBackHome = findViewById(R.id.btnBackToHome);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(items);
        rvItems.setAdapter(adapter);
    }

    private void loadInvoiceData(int orderId) {
        AppDatabase db = AppDatabase.getInstance(this);
        Order order = db.orderDao().getOrderById(orderId);
        List<OrderDetail> details = db.orderDao().getOrderDetailsByOrderId(orderId);

        if (order != null) {
            tvOrderId.setText("Order ID: #" + order.getId());
            tvDate.setText("Date: " + order.getOrderDate());
            tvTotal.setText(currencyFormat.format(order.getTotalAmount()));
            
            items.clear();
            items.addAll(details);
            adapter.notifyDataSetChanged();
        }
    }
}