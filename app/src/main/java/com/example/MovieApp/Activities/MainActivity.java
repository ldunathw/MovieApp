package com.example.MovieApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.MovieApp.Adapters.FilmListAdapter;
import com.example.MovieApp.Adapters.MovieAdapter;
import com.example.MovieApp.Adapters.SlidersAdapter;
import com.example.MovieApp.Database.UserDAO;
import com.example.MovieApp.Domains.Film;
import com.example.MovieApp.Domains.SliderItems;
import com.example.MovieApp.Model.Movie;
import com.example.MovieApp.Model.User;
import com.example.MovieApp.R;
import com.example.MovieApp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREF_USER_NAME = "PREF_USER_NAME";
    private static final String PREF_USER_EMAIL = "PREF_USER_EMAIL";
    private UserDAO userDAO;
    ImageView imageView2;

    private FirebaseDatabase database;
    private EditText editTextSearch;
    private ImageView searchIcon;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList;
    private DatabaseReference databaseReference;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userDAO = new UserDAO(this);
        getInforUser();

        setContentView(binding.getRoot());


        editTextSearch = findViewById(R.id.editTextText);

        searchIcon = findViewById(R.id.searchIcon);

        // Set OnClickListener for search Icon
        searchIcon.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initBanner();
        initTopMoving();
        initUpcomming();

        // Set OnClickListener for imageView2 to navigate to ProfileActivity
        binding.imgAvt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivity(intent);
        });

        //set event when click item in menu
        binding.chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if(id == R.id.explorer){
                    openExplorer();
                }else if(id == R.id.favorites){
                    openFavorites();
                }else if(id == R.id.watchlist){
                    openWatchlist();
                }else if(id == R.id.profile){
                    openProfile();
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(movieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                intent.putExtra("videoUrl", movie.getUrl());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(movieAdapter);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    movieList.add(movie);
                }
                movieAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getInforUser() {
        String phoneNumber = sharedPreferences.getString("PREF_PHONE_NUMBER", "");
        User user = null;
        userDAO.open();
        user = userDAO.getUserByPhoneNumber(phoneNumber);
        userDAO.close();
        if (user != null) {
            binding.tvName.setText(user.getFullName());
            binding.tvEmail.setText(user.getEmail());
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                binding.imgAvt.setImageURI(Uri.parse(user.getAvatar()));
            } else {
                binding.imgAvt.setImageResource(R.drawable.profile); // Hình ảnh mặc định nếu không có avatar
            }

        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openExplorer() {
        // Implement explorer functionality
        // For example, start a new activity or load a fragment
    }

    private void openFavorites() {
        // Implement favorite functionality
        // For example, start a new activity or load a fragment
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        startActivity(intent);
    }

    private void openWatchlist() {
        // Implement watchlist functionality
        // For example, start a new activity or load a fragment
        Intent intent = new Intent(MainActivity.this, WatchlistActivity.class);
        startActivity(intent);
    }

    private void openProfile() {
        // Implement profile functionality
        // For example, start a new activity or load a fragment
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
    private void initUpcomming() {
        DatabaseReference myRef = database.getReference("Upcomming");
        binding.progressBarUpcoming.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewUpcoming.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarUpcoming.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initTopMoving() {
        DatabaseReference myRef = database.getReference("Items");
        binding.progressBarTop.setVisibility(View.VISIBLE);
        ArrayList<Film> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Film.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewTopMovies.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewTopMovies.setAdapter(new FilmListAdapter(items));
                    }
                    binding.progressBarTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items) {
        String phoneNumber = sharedPreferences.getString("PREF_PHONE_NUMBER", "");
        binding.viewPager2.setAdapter(new SlidersAdapter(items, binding.viewPager2, phoneNumber));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        binding.viewPager2.setPageTransformer(compositePageTransformer);
        binding.viewPager2.setCurrentItem(1);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }
}
