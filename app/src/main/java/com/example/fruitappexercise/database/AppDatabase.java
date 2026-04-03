package com.example.fruitappexercise.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.fruitappexercise.model.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "fruit_shop_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                AppDao dao = INSTANCE.appDao();
                dao.insertCategory(new Category("Trái cây Việt Nam", "vn_fruits"));
                dao.insertCategory(new Category("Trái cây Nhập khẩu", "import_fruits"));
                dao.insertCategory(new Category("Giỏ quà trái cây", "fruit_baskets"));

                dao.insertProduct(new Product("Xoài Cát Hòa Lộc", 50000, "Xoài ngọt, thơm", "xoai", 1, 100));
                dao.insertProduct(new Product("Táo Envy", 120000, "Táo nhập khẩu Mỹ", "tao_envy", 2, 50));
                dao.insertProduct(new Product("Nho mẫu đơn", 300000, "Nho Hàn Quốc", "nho_mau_don", 2, 30));

                dao.insertUser(new User("admin", "admin123", "Quản trị viên", "admin@fruitapp.com", "admin"));
                dao.insertUser(new User("user", "123456", "Người dùng mẫu", "user@gmail.com", "user"));
            });
        }
    };
}