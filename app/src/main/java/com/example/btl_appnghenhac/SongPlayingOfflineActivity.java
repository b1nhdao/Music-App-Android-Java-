package com.example.btl_appnghenhac;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SongPlayingOfflineActivity extends AppCompatActivity {

    private ImageView img_songImage1, iv_back, img_play, img_skip, img_back_btn;
    private TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> songUriList;
    private int currentSongIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_playing);

        getViews();

        // Get data from intent
        Intent intent = getIntent();
        currentSongIndex = intent.getIntExtra("currentSongIndex", 0);
        songUriList = intent.getStringArrayListExtra("songList");

        // Play the selected song
        playSong(Uri.parse(songUriList.get(currentSongIndex)));

        img_play.setOnClickListener(v -> togglePlayPause());
        img_skip.setOnClickListener(v -> playNextSong());
        img_back_btn.setOnClickListener(v -> playPreviousSong());

        // Handle seekbar changes by user
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void getViews() {
        iv_back = findViewById(R.id.iv_back);
        img_songImage1 = findViewById(R.id.img_songImage1);
        tv_songName = findViewById(R.id.tv_songName);
        tv_songArtist = findViewById(R.id.tv_songArtist);
        tv_timeCurrent = findViewById(R.id.tv_timeCurrent);
        tv_timeEnd = findViewById(R.id.tv_timeEnd);
        img_play = findViewById(R.id.img_play);
        img_skip = findViewById(R.id.img_skip);
        img_back_btn = findViewById(R.id.img_back);
        seekBar = findViewById(R.id.seekBar);
    }

    private void playSong(Uri audioUri) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            // Use MediaMetadataRetriever to get song metadata
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(this, audioUri);

            String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            byte[] artBytes = metadataRetriever.getEmbeddedPicture();
            String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationMs = durationStr != null ? Long.parseLong(durationStr) : 0;
            String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            tv_songArtist.setText(artist);
            // Set song information in UI
            tv_songName.setText(title != null ? title : "Unknown Title");
            if (artBytes != null) {
                Glide.with(this).load(artBytes).into(img_songImage1);
                img_songImage1.animate().rotationBy(360).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        img_songImage1.animate().rotationBy(360).withEndAction(this).setDuration(20000)
                                .setInterpolator(new LinearInterpolator()).start();
                    }
                }).setDuration(20000).setInterpolator(new LinearInterpolator()).start();
            } else {
                img_songImage1.setImageResource(R.drawable.ic_launcher_foreground);
            }
            tv_timeEnd.setText(convertDurationToString(durationMs / 1000));

            // Initialize MediaPlayer
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                img_play.setImageResource(R.drawable.play2);
                isPlaying = true;

                // Update SeekBar and duration details
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("SongPlayingActivity", "Error playing song", e);
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            img_play.setImageResource(R.drawable.play1);
            isPlaying = false;
            Toast.makeText(SongPlayingOfflineActivity.this, "Paused", Toast.LENGTH_SHORT).show();
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                img_play.setImageResource(R.drawable.play2);
                isPlaying = true;
                Toast.makeText(SongPlayingOfflineActivity.this, "Playing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playNextSong() {
        if (songUriList != null && !songUriList.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songUriList.size();
            playSong(Uri.parse(songUriList.get(currentSongIndex)));
        }
    }

    private void playPreviousSong() {
        if (songUriList != null && !songUriList.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + songUriList.size()) % songUriList.size();
            playSong(Uri.parse(songUriList.get(currentSongIndex)));
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            tv_timeCurrent.setText(convertDurationToString(mediaPlayer.getCurrentPosition() / 1000));
            seekBar.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String convertDurationToString(long duration) {
        int minutes = (int) (duration / 60);
        int seconds = (int) (duration % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}