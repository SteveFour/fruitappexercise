package com.example.fruitappexercise.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.fruitappexercise.dao.CategoryDao;
import com.example.fruitappexercise.dao.OrderDao;
import com.example.fruitappexercise.dao.ProductDao;
import com.example.fruitappexercise.dao.UserDao;
import com.example.fruitappexercise.model.Category;
import com.example.fruitappexercise.model.Order;
import com.example.fruitappexercise.model.OrderDetail;
import com.example.fruitappexercise.model.Product;
import com.example.fruitappexercise.model.User;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();

    public static synchronized AppDatabase getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fruit_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                seedData(getInstance(context));
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static void seedData(AppDatabase db) {
        // Categories
        long cat1 = db.categoryDao().insert(new Category("Fruits", "Fresh seasonal fruits"));
        long cat2 = db.categoryDao().insert(new Category("Berries", "Organic sweet berries"));
        long cat3 = db.categoryDao().insert(new Category("Citrus", "Tangy and juicy citrus"));

        // Products
        db.productDao().insert(new Product((int)cat1, "Apple", 2.5, "Crispy Red Apple", ""));
        db.productDao().insert(new Product((int)cat1, "Banana", 1.2, "Ripe Yellow Banana", ""));
        db.productDao().insert(new Product((int)cat2, "Strawberry", 4.0, "Sweet Fresh Strawberry", ""));
        db.productDao().insert(new Product((int)cat2, "Blueberry", 5.5, "Wild organic Blueberry", ""));
        db.productDao().insert(new Product((int)cat3, "Orange", 3.0, "Juicy Navel Orange", ""));
        db.productDao().insert(new Product((int)cat3, "Lemon", 1.5, "Fresh Yellow Lemon", ""));

        // User
        db.userDao().insert(new User("admin", "admin123", "Default Admin"));
        db.userDao().insert(new User("user", "user123", "Normal User"));
    }
}