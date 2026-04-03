package com.example.fruitappexercise.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.fruitappexercise.dao.*;
import com.example.fruitappexercise.model.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract AppDao appDao();

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "fruit_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    // Alias for getDatabase as some parts of the app use getInstance
    public static AppDatabase getInstance(final Context context) {
        return getDatabase(context);
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                seedData(instance);
            });
        }
    };

    private static void seedData(AppDatabase db) {
        // Categories
        long cat1 = db.categoryDao().insert(new Category("Fruits", "Fresh seasonal fruits"));
        long cat2 = db.categoryDao().insert(new Category("Berries", "Organic sweet berries"));
        long cat3 = db.categoryDao().insert(new Category("Citrus", "Tangy and juicy citrus"));

        // Products - Prices in VND
        db.productDao().insert(new Product((int)cat1, "Apple", 50000, "Crispy Red Apple (per kg)", ""));
        db.productDao().insert(new Product((int)cat1, "Banana", 20000, "Ripe Yellow Banana (per bunch)", ""));
        db.productDao().insert(new Product((int)cat2, "Strawberry", 150000, "Sweet Fresh Strawberry (500g)", ""));
        db.productDao().insert(new Product((int)cat2, "Blueberry", 200000, "Wild organic Blueberry (250g)", ""));
        db.productDao().insert(new Product((int)cat3, "Orange", 45000, "Juicy Navel Orange (per kg)", ""));
        db.productDao().insert(new Product((int)cat3, "Lemon", 30000, "Fresh Yellow Lemon (per kg)", ""));

        // User
        db.userDao().insert(new User("admin", "admin123", "Default Admin", "admin@example.com", "ADMIN"));
        db.userDao().insert(new User("user", "user123", "Normal User", "user@example.com", "USER"));
    }
}
