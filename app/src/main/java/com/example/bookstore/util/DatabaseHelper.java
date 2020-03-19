package com.example.bookstore.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bookstore.model.ListItem;
import com.example.bookstore.model.UserList;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Books";
    private static final String TABLE_NAME = "BooksTable";
    private static final String CART_TABLE = "CartTable";
    private static final String USER_TABLE = "UserTable";
    private static final String COL_Id = "Id";
    private static final String COL_Name = "Name";
    private static final String COL_Description = "Description";
    private static final String COL_Image = "Image";
    private static final String COL_Price = "Price";
    private static final String COL_Address = "Address";
    private static final String COL_Email = "Email";
    private static final String COL_Password = "Password";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (Id Integer Primary Key AutoIncrement,Name TEXT,Description TEXT,Image BLOB,Price Integer)");
        db.execSQL("CREATE TABLE " + CART_TABLE + " (Id Integer Primary Key AutoIncrement,Name TEXT,Description TEXT,Image BLOB,Price Integer)");
        db.execSQL("CREATE TABLE " + USER_TABLE + "(Id Integer Primary Key AutoIncrement,Name Text,Email Text,Password Text,Address Text,Image BLOB)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);

    }

    public void insertBook(ListItem listItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Name, listItem.getHead());
        values.put(COL_Description, listItem.getDesc());
        values.put(COL_Image, listItem.getmImage());
        values.put(COL_Price, listItem.getPrice());
        db.insert(TABLE_NAME, null, values);
    }

    public boolean insertCart(ListItem listItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Name, listItem.getHead());
        values.put(COL_Description, listItem.getDesc());
        values.put(COL_Image, listItem.getmImage());
        values.put(COL_Price, listItem.getPrice());
        long result = db.insert(CART_TABLE, null, values);
        return result != -1;
    }

    public boolean insertUser(UserList userList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Name, userList.getUserName());
        values.put(COL_Email, userList.getUserEmail());
        values.put(COL_Password, userList.getUserPassword());
        values.put(COL_Address, userList.getUserAddress());
        values.put(COL_Image, userList.getUserImage());
        long result = db.insert(USER_TABLE, null, values);
        return result != -1;
    }

    public boolean checkUser(String Name, String Password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(" SELECT Name,Password FROM " + USER_TABLE + " WHERE Name=? AND Password=? ", new String[]{Name, Password});
        if (result.moveToFirst()) {
            result.close();
            return true;
        } else {
            result.close();
            return false;
        }
    }

    public List<ListItem> getCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ListItem> item = new ArrayList<>();
        Cursor res = db.rawQuery(" SELECT * FROM " + CART_TABLE, null);

        try {
            if (res.moveToFirst()) {
                do {
                    int id = res.getInt(0);
                    String name = res.getString(1);
                    String desc = res.getString(2);
                    byte[] image = res.getBlob(3);
                    int price = res.getInt(4);
                    item.add(new ListItem(id,name, desc, image, price));
                } while (res.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            res.close();
        }
        return item;
    }

    public List<ListItem> getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        List<ListItem> item = new ArrayList<>();
        Cursor res = db.rawQuery(" SELECT * FROM " + TABLE_NAME, null);

        try {
            if (res.moveToFirst()) {
                do {
                    String name = res.getString(1);
                    String desc = res.getString(2);
                    byte[] image = res.getBlob(3);
                    int price = res.getInt(4);
                    item.add(new ListItem(name, desc, image, price));
                } while (res.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            res.close();
        }
        return item;
    }

    public UserList getUser(String Name) {

        SQLiteDatabase db = this.getWritableDatabase();
        UserList item = null;
        Cursor res = db.rawQuery(" SELECT * FROM " + USER_TABLE + " WHERE Name=?", new String[]{Name});
        try {
            if (res.moveToFirst()) {
                do {
                    String name = res.getString(1);
                    String email = res.getString(2);
                    String pass = res.getString(3);
                    String address = res.getString(4);
                    byte[] image = res.getBlob(5);
                    item = new UserList(name, email, pass, address, image);
                } while (res.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            res.close();
        }
        return item;
    }

    public boolean updateUser(int id, String name, String email, String address, String pass) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Name, name);
        values.put(COL_Email, email);
        values.put(COL_Address, address);
        values.put(COL_Password, pass);
        long result = db.update(USER_TABLE, values, COL_Id + " = " + id, null);
        return result != -1;
    }

    public void deleteCart(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CART_TABLE,  COL_Id+ " = " +id, null);

    }

    public Double getPrice() {
        double netPrice = 0.0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT Price FROM " + CART_TABLE, null);
        if (result.moveToFirst()) {
            do {
                netPrice = netPrice + result.getInt(0);
            } while (result.moveToNext());
            result.close();
        }
        return netPrice;

    }

    public boolean checkEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT Count(*) FROM " + TABLE_NAME, null);
        result.moveToFirst();
        int count = result.getInt(0);
        result.close();
        return count <= 0;
    }

    public void setAddress(String address,String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Address, address);
        db.update(USER_TABLE, values," Name=? ",new String[]{name});

    }

    public String getAddress() {
        SQLiteDatabase db = getReadableDatabase();
        String address;
        Cursor result = db.rawQuery(" SELECT Address FROM " + USER_TABLE, null);
        if (result.moveToFirst()) {
            do {
                address = result.getString(0);
            } while (result.moveToNext());
            result.close();
            return address;
        } else {
            result.close();
            return null;
        }
    }

    public int getUsId(String name) {
        SQLiteDatabase db = getReadableDatabase();
        int id;
        Cursor result = db.rawQuery(" SELECT Id FROM " + USER_TABLE + " WHERE Name=? ", new String[]{name});
        if (result.moveToFirst()) {
            id = result.getInt(0);
            result.close();
            return id;
        } else {
            result.close();
            return 0;
        }

    }

    public void addImage(byte[] bytes, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_Image, bytes);
        db.update(USER_TABLE, values, "Name=?", new String[]{name});
    }

    public boolean matchUserName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(" SELECT Name FROM " + USER_TABLE, null);
        if(result.moveToFirst()) {
            return result.getString(0).contentEquals(name);
        }else{
            return false;
        }
    }
}

