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

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 3, exportSchema = false)
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

    public static AppDatabase getInstance(final Context context) {
        return getDatabase(context);
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                AppDatabase database = instance;
                if (database != null && database.categoryDao().getAllCategories().isEmpty()) {
                    seedData(database);
                }
            });
        }
    };

    private static void seedData(AppDatabase db) {
        // Categories
        long cat1 = db.categoryDao().insert(new Category("Trái cây", "Trái cây tươi theo mùa"));
        long cat2 = db.categoryDao().insert(new Category("Quả mọng", "Quả mọng ngọt hữu cơ"));
        long cat3 = db.categoryDao().insert(new Category("Cam quýt", "Cam quýt mọng nước"));

        // Products - Prices in VND, using resource names for imagePath
        db.productDao().insert(new Product((int)cat1, "Táo đỏ", 50000, "Táo đỏ giòn (mỗi kg)", "apple"));
        db.productDao().insert(new Product((int)cat1, "Chuối", 20000, "Chuối chín vàng (mỗi nải)", "banana"));
        db.productDao().insert(new Product((int)cat2, "Dâu tây", 150000, "Dâu tây tươi ngọt (500g)", "strawberry"));
        db.productDao().insert(new Product((int)cat2, "Việt quất", 200000, "Việt quất rừng hữu cơ (250g)", "blueberry"));
        db.productDao().insert(new Product((int)cat3, "Cam", 45000, "Cam sành mọng nước (mỗi kg)", "orange"));
        db.productDao().insert(new Product((int)cat3, "Chanh", 30000, "Chanh vàng tươi (mỗi kg)", "lemon"));

        // User
        db.userDao().insert(new User("admin", "admin123", "Quản trị viên", "admin@example.com", "ADMIN"));
        db.userDao().insert(new User("user", "user123", "Người dùng", "user@example.com", "USER"));
    }
}