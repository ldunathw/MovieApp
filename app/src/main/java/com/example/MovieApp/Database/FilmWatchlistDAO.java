package com.example.MovieApp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.MovieApp.Model.FilmWatchlist;

import java.util.ArrayList;
import java.util.List;

public class FilmWatchlistDAO {
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    public FilmWatchlistDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long addFilmWatchlist(String phoneNumber, String filmId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER_PHONE_NUMBER, phoneNumber);
        values.put(DatabaseHelper.FILM_ID, filmId);

        return database.insert(DatabaseHelper.TABLE_FILM_WATCHLIST, null, values);
    }

    public List<FilmWatchlist> getFilmWatchlist(String phoneNumber) {
        List<FilmWatchlist> watchlist = new ArrayList<>();

        String[] columns = {
                DatabaseHelper.COLUMN_WATCHLIST_ID,
                DatabaseHelper.USER_PHONE_NUMBER,
                DatabaseHelper.FILM_ID
        };

        String selection = DatabaseHelper.USER_PHONE_NUMBER + " = ?";
        String[] selectionArgs = { phoneNumber };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_FILM_WATCHLIST,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        Log.wtf("check q", ""+cursor);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WATCHLIST_ID));
                String userPhoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_PHONE_NUMBER));
                String filmId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FILM_ID));

                FilmWatchlist filmWatchlist = new FilmWatchlist(id, userPhoneNumber, filmId);
                watchlist.add(filmWatchlist);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return watchlist;
    }

    public boolean isFilmWatchlistExists(String phoneNumber, String filmId) {
        String[] columns = { DatabaseHelper.COLUMN_WATCHLIST_ID };
        String selection = DatabaseHelper.USER_PHONE_NUMBER + " = ? AND " + DatabaseHelper.FILM_ID + " = ?";
        String[] selectionArgs = { phoneNumber, filmId };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_FILM_WATCHLIST,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void close() {
        dbHelper.close();
    }
}
