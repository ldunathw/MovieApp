package com.example.MovieApp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Movie_Database.db";
    private static final int DATABASE_VERSION = 5;

    // User table name
    public static final String TABLE_USER = "user";

    // User Table Columns names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Film Favorite table name
    public static final String TABLE_FILM_FAVORITE = "film_favorite";
    // Film Favorite Table Columns names
    public static final String COLUMN_FAVORITE_ID = "id";
    public static final String COLUMN_USER_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_FILM_ID = "film_id";

    // Film Watchlist table name
    public static final String TABLE_FILM_WATCHLIST = "film_watchlist";
    // Film Watchlist Table Columns names
    public static final String COLUMN_WATCHLIST_ID = "id";
    public static final String USER_PHONE_NUMBER = "phone_number";
    public static final String FILM_ID = "film_id";

    // Create table SQL query
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_FULL_NAME + " TEXT,"
            + COLUMN_PHONE_NUMBER + " TEXT,"
            + COLUMN_AVATAR + " TEXT,"
            + COLUMN_GENDER + " TEXT,"
            + COLUMN_BIRTHDAY + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    private static final String CREATE_FILM_FAVORITE_TABLE = "CREATE TABLE " + TABLE_FILM_FAVORITE + "("
            + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_PHONE_NUMBER + " TEXT,"
            + COLUMN_FILM_ID + " TEXT"
            + ")";

    private static final String CREATE_FILM_WATCHLIST_TABLE = "CREATE TABLE " + TABLE_FILM_WATCHLIST + "("
            + COLUMN_WATCHLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_PHONE_NUMBER + " TEXT,"
            + FILM_ID + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FILM_FAVORITE_TABLE);
        db.execSQL(CREATE_FILM_WATCHLIST_TABLE);
        insertDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILM_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILM_WATCHLIST);

        // Create tables again
        onCreate(db);
    }

    void insertDefaultUsers(SQLiteDatabase db) {
        addUser(db, "John Doe", "1234567890", "Male", "1990-01-01", "johndoe@example.com", "password123");
        addUser(db, "Jane Smith", "0987654321", "Female", "1992-02-02", "janesmith@example.com", "password456");
    }

    public long addUser(SQLiteDatabase db, String fullName, String phoneNumber, String gender, String birthday, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FULL_NAME, fullName);
        values.put(DatabaseHelper.COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_BIRTHDAY, birthday);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        return db.insert(DatabaseHelper.TABLE_USER, null, values);
    }
}
