package com.example.btl_appnghenhac.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {
    public static final String dbName = "user.db";
    public static final int dbVersion = 1;

    // Khai báo các tham số hằng của table cần tạo
    public static final String tblName = "tblUser";
    public static final String userID = "userID";
    public static final String username = "username";
    public static final String password = "password";
    public static final String role = "role";

    public dbHelper(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + tblName + " ( "
                + userID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + username  + " TEXT, "
                + password  + " TEXT, "
                + role + " INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tblName);
        onCreate(sqLiteDatabase);
    }
}
