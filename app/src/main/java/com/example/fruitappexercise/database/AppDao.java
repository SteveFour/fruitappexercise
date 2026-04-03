package com.example.fruitappexercise.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.fruitappexercise.model.*;
import java.util.List;

@Dao
public interface AppDao {
    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Insert
    void insertCategory(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Insert
    void insertProduct(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<Product> getProductsByCategory(int categoryId);

    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderDetail(OrderDetail orderDetail);
}