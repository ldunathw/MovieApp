package com.example.MovieApp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import com.example.MovieApp.Model.User;

public class UserDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Create a new user
    public long addUser(String fullName, String phoneNumber, String gender, String birthday, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);
        values.put(DatabaseHelper.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_BIRTHDAY, birthday);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    // Read user data by ID
    public Cursor getUser(String id) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, null,
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Read all users
    public Cursor getAllUsers() {
        return db.query(DatabaseHelper.TABLE_USER, null, null, null,
                null, null, null);
    }

    // Update a user
    public int updateUser(String fullName, String phoneNumber, String avatar, String gender, String birthday, String email) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);
        values.put(DatabaseHelper.COLUMN_AVATAR, avatar);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_BIRTHDAY, birthday);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);

        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COLUMN_PHONE_NUMBER + "=?", new String[]{String.valueOf(phoneNumber)});
    }

    // Delete a user
    public void deleteUser(long id) {
        db.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
    }
    // Retrieve a user by phone number
    @SuppressLint("Range")
    public User getUserByPhoneNumber(String phoneNumber) {
        User user = null;
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                null,
                DatabaseHelper.COLUMN_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FULL_NAME)));
            user.setAvatar(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AVATAR)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER)));
            user.setGender(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER)));
            user.setBirthday(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
        }

        cursor.close();
        return user;
    }
    public boolean isEmailExists(String email) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COLUMN_EMAIL},
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COLUMN_PHONE_NUMBER},
                DatabaseHelper.COLUMN_PHONE_NUMBER + "=?",
                new String[]{phoneNumber}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    @SuppressLint("Range")
    public User login(String phoneNumber, String password) {
        User user = null;
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER,
                null,
                DatabaseHelper.COLUMN_PHONE_NUMBER + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                new String[]{phoneNumber, password}, null, null, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FULL_NAME)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE_NUMBER)));
            user.setGender(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER)));
            user.setBirthday(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BIRTHDAY)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
        }

        cursor.close();
        return user;
    }
    // Update password for a user based on phone number
    public int updatePassword(String phoneNumber, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);

        // Update the password where phone number matches
        return db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COLUMN_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber});
    }

    public void updateUserAvatar(String phoneNumber, String avatarUri) {
        ContentValues values = new ContentValues();
        values.put("avatar", avatarUri);
        db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COLUMN_PHONE_NUMBER + "=?", new String[]{String.valueOf(phoneNumber)});

    }
}
