package com.example.MovieApp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.MovieApp.Model.FilmFavorite;

import java.util.ArrayList;
import java.util.List;

public class FilmFavoriteDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public FilmFavoriteDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Add a film favorite for a user
    public long addFilmFavorite(String phoneNumber, String filmId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.COLUMN_FILM_ID, filmId);

        return db.insert(DatabaseHelper.TABLE_FILM_FAVORITE, null, values);
    }

    // Remove a film favorite for a user
    public int removeFilmFavorite(String phoneNumber, String filmId) {
        return db.delete(DatabaseHelper.TABLE_FILM_FAVORITE,
                DatabaseHelper.COLUMN_USER_PHONE_NUMBER + "=? AND " + DatabaseHelper.COLUMN_FILM_ID + "=?",
                new String[]{phoneNumber, filmId});
    }

    // Get all film favorites for a user
    @SuppressLint("Range")
    public List<FilmFavorite> getFilmFavorites(String phoneNumber) {
        List<FilmFavorite> favorites = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FILM_FAVORITE,
                null,
                DatabaseHelper.COLUMN_USER_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                FilmFavorite favorite = new FilmFavorite();
                favorite.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_FAVORITE_ID)));
                favorite.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE_NUMBER)));
                favorite.setFilmId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FILM_ID)));
                favorites.add(favorite);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return favorites;
    }
    // Check if a film favorite exists for a user
    public boolean isFilmFavoriteExists(String phoneNumber, String filmId) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_FILM_FAVORITE,
                new String[]{DatabaseHelper.COLUMN_FAVORITE_ID},
                DatabaseHelper.COLUMN_USER_PHONE_NUMBER + "=? AND " + DatabaseHelper.COLUMN_FILM_ID + "=?",
                new String[]{phoneNumber, filmId}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
