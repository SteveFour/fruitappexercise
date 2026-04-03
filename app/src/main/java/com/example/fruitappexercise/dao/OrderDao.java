package com.example.fruitappexercise.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.fruitappexercise.model.Order;
import com.example.fruitappexercise.model.OrderDetail;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insertOrder(Order order);

    @Update
    void updateOrder(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'Pending' LIMIT 1")
    Order getPendingOrderByUserId(int userId);

    @Insert
    void insertOrderDetail(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getOrderDetailsByOrderId(int orderId);

    @Query("SELECT * FROM orders WHERE id = :orderId")
    Order getOrderById(int orderId);
}