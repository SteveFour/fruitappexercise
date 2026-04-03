package com.example.fruitappexercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitappexercise.database.AppDatabase;
import com.example.fruitappexercise.model.OrderDetail;
import com.example.fruitappexercise.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<OrderDetail> cartItems;
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    public CartAdapter(List<OrderDetail> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        OrderDetail detail = cartItems.get(position);
        
        // Fetch Product info for this detail
        AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
        Product product = db.productDao().getProductById(detail.getProductId());

        if (product != null) {
            holder.tvProductName.setText(product.getName());
            holder.tvProductPrice.setText(currencyFormat.format(detail.getPrice()));
            holder.tvProductQuantity.setText("x " + detail.getQuantity());
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivCartProductImage);
            tvProductName = itemView.findViewById(R.id.tvCartProductName);
            tvProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvCartQuantity);
        }
    }
}