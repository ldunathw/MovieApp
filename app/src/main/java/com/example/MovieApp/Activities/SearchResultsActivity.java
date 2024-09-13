package com.example.MovieApp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MovieApp.Adapters.FilmListAdapter;
import com.example.MovieApp.Domains.Film;
import com.example.MovieApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarSearch;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        progressBarSearch = findViewById(R.id.progressBarSearch);

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();

        String query = getIntent().getStringExtra("query");
        if (query != null && !query.isEmpty()) {
            searchMovies(query);
        }
    }

    private void searchMovies(String query) {
        DatabaseReference myRef = database.getReference("Items");
        progressBarSearch.setVisibility(View.VISIBLE);
        ArrayList<Film> searchResults = new ArrayList<>();

        myRef.orderByChild("Title").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        searchResults.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Film film = issue.getValue(Film.class);
                                if (film != null) {
                                    searchResults.add(film);
                                    System.out.println("Film title from query: " + film.getTitle());
                                }
                            }
                            if (!searchResults.isEmpty()) {
                                FilmListAdapter adapter = new FilmListAdapter(searchResults);
                                recyclerViewSearchResults.setAdapter(adapter);
                            } else {
                                Toast.makeText(SearchResultsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchResultsActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                        }
                        progressBarSearch.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBarSearch.setVisibility(View.GONE);
                        Toast.makeText(SearchResultsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
