package com.example.fridge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private static DBManager instance;
    private SQLiteDatabase database;

    private DBManager(Context context) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public static synchronized DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context.getApplicationContext());
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
