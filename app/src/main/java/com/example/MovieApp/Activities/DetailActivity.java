package com.example.MovieApp.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.MovieApp.Adapters.CastListAdapter;
import com.example.MovieApp.Adapters.CategoryEachFilmAdapter;
import com.example.MovieApp.Database.FilmFavoriteDAO;
import com.example.MovieApp.Domains.Film;
import com.example.MovieApp.R;
import com.example.MovieApp.databinding.ActivityDetailBinding;

import eightbitlab.com.blurview.RenderScriptBlur;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private String videoUrl;
    private FilmFavoriteDAO filmFavoriteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoUrl = "https://firebasestorage.googleapis.com/v0/b/appmovie-nh6.appspot.com/o/Y2meta.app%20-%20Kung%20Fu%20Panda_%20Hia%CC%81%C2%BB%C2%87p%20sA%CC%82%C2%A9%20ra%CC%81%C2%BB%C2%93ng%20%C3%B0%C2%9F%C2%90%C2%BBa%CC%82%C2%80%C2%8Da%CC%82%C2%9D%C2%84i%CC%88%C2%B8%C2%8F%C3%B0%C2%9F%C2%90%C2%89%20Trailer%20chA%CC%83%C2%ADnh%20tha%CC%81%C2%BB%C2%A9c%20%7C%20Netflix.mp4?alt=media&token=bcd6c493-f33f-4380-ae70-f08082dade99";

        filmFavoriteDAO = new FilmFavoriteDAO(this);
        filmFavoriteDAO.open();

        setVariable();
        setupRecyclerViews();  // Setup RecyclerViews here
        reloadIconFavorite();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        AppCompatButton watchTrailerBtn = findViewById(R.id.watchTrailerBtn);

        // Set an onClickListener to the button
        watchTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, VideoActivity.class);
                // Pass the video URL to the intent
                intent.putExtra("videoUrl", videoUrl);
                startActivity(intent);
            }
        });

        //set event when click imgFavorite icon
        binding.imgFavorite.setOnClickListener(v -> {
            imgFavorite_click();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filmFavoriteDAO.close();
    }

    private void setVariable() {
        Film item = (Film) getIntent().getSerializableExtra("object");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new GranularRoundedCorners(0, 0, 50, 50));

        Glide.with(this)
                .load(item.getPoster())
                .apply(requestOptions)
                .into(binding.filmPic);

        binding.titleTxt.setText(item.getTitle());
        binding.imdbTxt.setText("IMDB " + item.getImdb());
        binding.movieTimesTxt.setText(item.getYear() + " - " + item.getTime());
        binding.movieSummery.setText(item.getDescription());

        binding.watchTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.getTrailer().replace("https://www.youtube.com/watch?v=", "");
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getTrailer()));

                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
        binding.backImg.setOnClickListener(v -> finish());

        float radius = 10f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowsBackground = decorView.getBackground();

        binding.blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowsBackground)
                .setBlurRadius(radius);
        binding.blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        binding.blurView.setClipToOutline(true);
    }

    private void setupRecyclerViews() {
        Film item = (Film) getIntent().getSerializableExtra("object");

        if (item.getGenre() != null) {
            CategoryEachFilmAdapter categoryAdapter = new CategoryEachFilmAdapter(item.getGenre());
            binding.genreView.setAdapter(categoryAdapter);
            binding.genreView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }

        if (item.getCasts() != null) {
            CastListAdapter castAdapter = new CastListAdapter(item.getCasts());
            binding.CastView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.CastView.setAdapter(castAdapter);
        }
    }

    private boolean CheckFilmFavorite() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone_number", "");
        Film item = (Film) getIntent().getSerializableExtra("object");
        String filmId = item.getTrailer().replace("https://www.youtube.com/watch?v=", "");

        return filmFavoriteDAO.isFilmFavoriteExists(phoneNumber, filmId);
    }

    private void reloadIconFavorite() {
        if (CheckFilmFavorite()) {
            binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark));
        } else {
            binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_border));
        }
    }

    private void imgFavorite_click() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone_number", "");
        Film item = (Film) getIntent().getSerializableExtra("object");
        String filmId = item.getTrailer().replace("https://www.youtube.com/watch?v=", "");

        if (CheckFilmFavorite()) {
            // Remove from favorites
            filmFavoriteDAO.removeFilmFavorite(phoneNumber, filmId);
            binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark_border));
            Toast.makeText(this, "Removed from favorites.", Toast.LENGTH_SHORT).show();
        } else {
            // Add to favorites
            filmFavoriteDAO.addFilmFavorite(phoneNumber, filmId);
            binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_bookmark));
            Toast.makeText(this, "Added to favorites.", Toast.LENGTH_SHORT).show();
        }
    }
}
