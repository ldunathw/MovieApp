package com.example.MovieApp.Model;

public class FilmWatchlist {
    private int id;
    private String phoneNumber;
    private String filmId;

    public FilmWatchlist(int id, String phoneNumber, String filmId) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.filmId = filmId;
    }

    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFilmId() {
        return filmId;
    }
}
