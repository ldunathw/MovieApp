package com.example.MovieApp.Activities;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieApp.Adapters.FilmListAdapter;
import com.example.MovieApp.Adapters.SlidersAdapter;
import com.example.MovieApp.Adapters.WatchListAdapter;
import com.example.MovieApp.Database.FilmFavoriteDAO;
import com.example.MovieApp.Database.FilmWatchlistDAO;
import com.example.MovieApp.Domains.Film;
import com.example.MovieApp.Domains.SliderItems;
import com.example.MovieApp.Model.FilmFavorite;
import com.example.MovieApp.Model.FilmWatchlist;
import com.example.MovieApp.R;
import com.example.MovieApp.databinding.ActivityFavoriteBinding;
import com.example.MovieApp.databinding.ActivityWacthlistBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatchlistActivity extends AppCompatActivity {
    ActivityWacthlistBinding binding;
    FilmWatchlistDAO watchListDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWacthlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbPda);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.back);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.SRC_ATOP);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        }
        // Enable the Up button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Set click listener for the back button in the Toolbar
        binding.tbPda.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle navigation back
            }
        });
        watchListDAO = new FilmWatchlistDAO(this);
        loadList();
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle navigation back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchListDAO.close();
    }
    private void loadList(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone_number", "");

        List<FilmWatchlist> filmFavorites = watchListDAO.getFilmWatchlist(phoneNumber);
        Log.wtf("chjek", ""+ filmFavorites.size());
        if(!filmFavorites.isEmpty()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Banners");
            ArrayList<SliderItems> items = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            SliderItems film = issue.getValue(SliderItems.class);
                            String filmName = film.getName();
                            if(checkFilmInFavoriteList(filmFavorites, filmName)){
                                items.add(issue.getValue(SliderItems.class));
                            }
                        }
                        for(int i =0; i< items.size(); i++){
                            Log.wtf("check ", items.get(i).getName());
                        }
                        if (!items.isEmpty()) {
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(WatchlistActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            binding.recyclerView.setAdapter(new WatchListAdapter(items, phoneNumber));

                        }
                        binding.progressBarUpcoming.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            binding.progressBarUpcoming.setVisibility(View.GONE);
        }
    }
    private boolean checkFilmInFavoriteList(List<FilmWatchlist> films, String filmName) {
        for (FilmWatchlist film : films) {
            if (film.getFilmId().equals(filmName)) {
                return true; // Found the film in the favorite list
            }
        }
        return false; // Film not found in the favorite list
    }

}