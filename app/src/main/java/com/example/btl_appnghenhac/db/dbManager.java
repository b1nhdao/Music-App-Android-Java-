package com.example.btl_appnghenhac.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.btl_appnghenhac.Object.User;

import java.util.ArrayList;

public class dbManager {
    private Context context;
    private SQLiteDatabase db;
    private dbHelper helper;

    public dbManager(Context context) {
        this.context = context;
    }

    public dbManager open(){
        helper = new dbHelper(context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close(){
        db.close();
    }

    //insert user
    public void insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(helper.username, user.getUsername());
        values.put(helper.password, user.getPassword());
        values.put(helper.role, user.getRole()); // Ensure role is added
        db.insert(helper.tblName, null, values);
    }

    //select all
    public ArrayList<User> getAllUser(){
        ArrayList<User> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + helper.tblName, null);
        if (cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String password = cursor.getString(2);
                int role = cursor.getInt(3);
                result.add(new User(id, username, password, role));
            }
        }
        return result;
    }

    //get User by username
    public User getUserByUsername(String username) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + helper.tblName + " WHERE " + helper.username + " = ?";
            cursor = db.rawQuery(query, new String[]{username});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String username1 = cursor.getString(1);
                String password = cursor.getString(2);
                int role = cursor.getInt(3);
                return new User(id, username1, password, role);
            }
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    //check login
    public boolean checkLogin(String username, String password) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + helper.tblName + " WHERE " + helper.username + " = ? AND " + helper.password + " = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            return cursor != null && cursor.moveToFirst(); // Return true if the user exists and the password matches
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
