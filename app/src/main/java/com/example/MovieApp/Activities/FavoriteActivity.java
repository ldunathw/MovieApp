package com.example.MovieApp.Activities;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MovieApp.Adapters.FilmListAdapter;
import com.example.MovieApp.Database.FilmFavoriteDAO;
import com.example.MovieApp.Domains.Film;
import com.example.MovieApp.Model.FilmFavorite;
import com.example.MovieApp.R;
import com.example.MovieApp.databinding.ActivityDetailBinding;
import com.example.MovieApp.databinding.ActivityFavoriteBinding;
import com.example.MovieApp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {
    ActivityFavoriteBinding binding;
    FilmFavoriteDAO favoriteDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
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
        favoriteDAO = new FilmFavoriteDAO(this);
        favoriteDAO.open();
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
        favoriteDAO.close();
    }
    private void loadList(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone_number", "");

        List<FilmFavorite> filmFavorites = favoriteDAO.getFilmFavorites(phoneNumber);
        if(!filmFavorites.isEmpty()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Upcomming");
            ArrayList<Film> items = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Film film = issue.getValue(Film.class);
                            String filmId = film.getTrailer().replace("https://www.youtube.com/watch?v=", "");
                            if(checkFilmInFavoriteList(filmFavorites, filmId)){
                                items.add(issue.getValue(Film.class));
                            }
                        }
                        if (!items.isEmpty()) {
                            binding.recyclerViewFavorite.setLayoutManager(new GridLayoutManager(FavoriteActivity.this, 2));
                            binding.recyclerViewFavorite.setAdapter(new FilmListAdapter(items));
                        }
                        binding.progressBarUpcoming.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            binding.progressBarUpcoming.setVisibility(View.GONE);
        }
    }
    private boolean checkFilmInFavoriteList(List<FilmFavorite> films, String filmId) {
        for (FilmFavorite film : films) {
            if (film.getFilmId().equals(filmId)) {
                return true; // Found the film in the favorite list
            }
        }
        return false; // Film not found in the favorite list
    }

}