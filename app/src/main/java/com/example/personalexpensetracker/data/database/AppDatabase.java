package com.example.personalexpensetracker.data.database;

import com.example.personalexpensetracker.data.dao.CategoryDao;
import com.example.personalexpensetracker.data.dao.ExpenseRecordDao;
import com.example.personalexpensetracker.data.dao.UserDao;
import com.example.personalexpensetracker.data.model.Category;
import com.example.personalexpensetracker.data.model.ExpenseRecord;
import com.example.personalexpensetracker.data.model.User;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, ExpenseRecord.class, Category.class}, version = 13)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ExpenseRecordDao expenseRecordDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        // 所有其他需要用到数据库实例的地方通过 AppDatabase db = AppDatabase.getInstance(this)即可
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    // 这个是清除所有旧数据完成数据库迁移，需要自己定义一个迁移方案
                                    AppDatabase.class, "user_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

