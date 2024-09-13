package com.example.MovieApp.Activities;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MovieApp.R;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoActivity extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        playerView = findViewById(R.id.playerView);

        // Initialize data source factory with authorization header
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "MovieApp"));

        // Create a player instance
        player = new SimpleExoPlayer.Builder(this)
                .setMediaSourceFactory(new ProgressiveMediaSource.Factory(dataSourceFactory))
                .build();

        // Attach player to the player view
        playerView.setPlayer(player);

        // Prepare media source
        Uri videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/appmovie-nh6.appspot.com/o/Y2meta.app-Rebel%20Moon%20%E2%80%94%20Part%20Two_%20The%20Scargiver%20%7C%20Official%20Trailer%20%7C%20Netflix-(240p).mp4?alt=media&token=0bc83f66-a231-4159-939d-cbe538e74d51");
        ProgressiveMediaSource mediaSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        // Prepare the player with the media source
        player.setMediaSource(mediaSource);

        // Prepare the player asynchronously
        player.prepare();

        // Start playback when the player is ready
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the player when the activity is destroyed
        player.release();
    }
}
