package com.example.btl_appnghenhac;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Fragment.SearchFragment;
import com.example.btl_appnghenhac.Object.Song;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class SongPlayingActivity extends AppCompatActivity {

    ImageView img_songImage, iv_back, iv_menu;
    TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd;
    ImageView img_shuffle, img_back, img_play, img_skip, img_loop;
    SeekBar seekBar;
    ArrayList<Song> songArrayList;
    int currentSongIndex = 0;
    boolean isLooping = false;
    boolean isShuffling = false;
    private boolean isSingleSongMode = false;
    private ArrayList<Song> shuffledList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private MusicService musicService;
    private boolean serviceBound = false;
    int code;
    ShapeableImageView img_songImage1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void onClickDownloadOptionMenu(){
        if (songArrayList != null && !songArrayList.isEmpty()) {
            Song currentSong = songArrayList.get(currentSongIndex);
            downloadSong(currentSong.getSongURL(), currentSong.getSongImageUrl(), currentSong.getSongArtistName(), currentSong.getSongName());
        } else {
            Toast.makeText(SongPlayingActivity.this, "No song to download", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(SongPlayingActivity.this, "download", Toast.LENGTH_SHORT).show();
    }

    private void onClicktoFavOptionMenu(){
        Toast.makeText(SongPlayingActivity.this, "favourite", Toast.LENGTH_SHORT).show();

    }

    private void onClickToPlaylistOptionMenu(){
        Toast.makeText(SongPlayingActivity.this, "playlist", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.download){
            onClickDownloadOptionMenu();
            return true;
        }
        if (item.getItemId() == R.id.toFav){
            onClicktoFavOptionMenu();
            return true;
        }
        if (item.getItemId() == R.id.toPlaylist){
            onClickToPlaylistOptionMenu();
            return true;
        }

        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_playing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iv_menu = findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(SongPlayingActivity.this, iv_menu);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
                popupMenu.show();
            }
        });

        getViews();
        iv_back.setVisibility(View.VISIBLE);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (code == 1) {
                    Intent intent = new Intent(SongPlayingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            songArrayList = (ArrayList<Song>) bundle.getSerializable("songList");
            currentSongIndex = bundle.getInt("currentSongIndex", 0);
            code = bundle.getInt("code", 0);
            saveCurrentSongToPreferences(songArrayList.get(currentSongIndex));
        }

        Intent intent = new Intent(this, MusicService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


        img_play.setOnClickListener(view -> {
            if (musicService != null) {
                musicService.playPauseMusic();
                if (musicService.isPlaying()) {
                    img_play.setImageResource(R.drawable.play1);
                } else {
                    img_play.setImageResource(R.drawable.play2);
                }
            }
        });

        img_skip.setOnClickListener(view -> playNextSong());
        img_back.setOnClickListener(view -> playPreviousSong());
        img_loop.setOnClickListener(v -> {
            isLooping = !isLooping;
            Toast.makeText(SongPlayingActivity.this, isLooping ? "Lặp" : "Tắt lặp", Toast.LENGTH_SHORT).show();
        });
        img_shuffle.setOnClickListener(v -> toggleShuffle());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.seekTo(progress);
                    tv_timeCurrent.setText(convertDurationToString(progress / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicService != null && musicService.isPlaying()) {
                    musicService.seekTo(seekBar.getProgress());
                    handler.post(updateSeekBar);
                }
            }
        });

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(MusicService.ACTION_PLAY_PAUSE);
        filter.addAction(MusicService.ACTION_NEXT);
        registerReceiver(appKilledReceiver, filter);
    }

    private void playCurrentSong() {
        if (songArrayList != null && !songArrayList.isEmpty() && musicService != null) {
            Song currentSong = songArrayList.get(currentSongIndex);
            musicService.playSong(currentSong);

            if (!isFinishing() && !isDestroyed()) {
                Glide.with(getApplicationContext()).load(currentSong.getSongImageUrl()).into(img_songImage1);
            }
            img_songImage1.animate().rotationBy(360).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Thực hiện hành động khi kết thúc xoay
                            img_songImage1.animate().rotationBy(360).withEndAction(this).setDuration(20000)
                                    .setInterpolator(new LinearInterpolator()).start();
                        }
                    }).setDuration(20000)
                    .setInterpolator(new LinearInterpolator())
                    .start();

            tv_songName.setText(currentSong.getSongName());
            tv_songArtist.setText(currentSong.getSongArtistName());
            tv_timeEnd.setText(convertDurationToString(musicService.getDuration() / 1000));
            seekBar.setMax(musicService.getDuration());
            updateSeekBar();
        }
    }


    private void playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songArrayList.size();
        playCurrentSong();
    }

    private void playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + songArrayList.size()) % songArrayList.size();
        playCurrentSong();
    }

    private void toggleShuffle() {
        isShuffling = !isShuffling;
        if (isShuffling) {
            shuffledList = new ArrayList<>(songArrayList);
            Collections.shuffle(shuffledList);
            songArrayList = shuffledList;
            currentSongIndex = 0;
            playCurrentSong();
        } else {
            shuffledList.clear();
            songArrayList = (ArrayList<Song>) getIntent().getExtras().getSerializable("songList");
            currentSongIndex = 0;
            playCurrentSong();
        }
        Toast.makeText(this, isShuffling ? "Shuffle ON" : "Shuffle OFF", Toast.LENGTH_SHORT).show();
    }

    private void updateSeekBar() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (musicService != null && musicService.isPlaying()) {
                    seekBar.setProgress(musicService.getCurrentPosition());
                    tv_timeCurrent.setText(convertDurationToString(musicService.getCurrentPosition() / 1000));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBar);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;

            musicService.setSongList(songArrayList);
            playCurrentSong();

            musicService.setOnCompletionListener(() -> {
                if (isLooping) {
                    playCurrentSong();
                } else {
                    playNextSong();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    // BroadcastReceiver to stop the service when the app is killed
    private final BroadcastReceiver appKilledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (musicService != null) {
                switch (intent.getAction()) {
                    case MusicService.ACTION_PLAY_PAUSE:
                        musicService.playPauseMusic();
                        if (musicService.isPlaying()) {
                            img_play.setImageResource(R.drawable.play1);
                        } else {
                            img_play.setImageResource(R.drawable.play2);
                        }
                        break;
                    case MusicService.ACTION_NEXT:
                        playNextSong();
                        break;
                }
            }
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                stopService(new Intent(SongPlayingActivity.this, MusicService.class));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        unregisterReceiver(appKilledReceiver);
    }

    public String convertDurationToString(int duration) {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void getViews() {
//        iv_menu.setVisibility(View.VISIBLE);
        iv_back = findViewById(R.id.iv_back);
        img_songImage = findViewById(R.id.img_songImage);
        tv_songName = findViewById(R.id.tv_songName);
        tv_songArtist = findViewById(R.id.tv_songArtist);
        tv_timeCurrent = findViewById(R.id.tv_timeCurrent);
        tv_timeEnd = findViewById(R.id.tv_timeEnd);
        img_shuffle = findViewById(R.id.img_shuffle);
        img_back = findViewById(R.id.img_back);
        img_play = findViewById(R.id.img_play);
        img_skip = findViewById(R.id.img_skip);
        img_loop = findViewById(R.id.img_loop);
        seekBar = findViewById(R.id.seekBar);
        img_songImage1 = findViewById(R.id.img_songImage1);
    }

    private void saveCurrentSongToPreferences(Song song) {
        SharedPreferences sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("songID", song.getSongID());
        editor.putString("songName", song.getSongName());
        editor.putString("songArtist", song.getSongArtistName());
        editor.putString("songImageUrl", song.getSongImageUrl());
        editor.apply();
    }
    private void downloadSong(String songUrl, String coverUrl, String artist, String songTitle) {
        new DownloadTask(songUrl, coverUrl, artist, songTitle).execute();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {
        private String songUrl, coverUrl, artist, songTitle;

        public DownloadTask(String songUrl, String coverUrl, String artist, String songTitle) {
            this.songUrl = songUrl;
            this.coverUrl = coverUrl;
            this.artist = artist;
            this.songTitle = songTitle;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Download MP3
                URL url = new URL(songUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false; // Handle connection error
                }

                InputStream input = connection.getInputStream();
                File songFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), songTitle + ".mp3");
                FileOutputStream output = new FileOutputStream(songFile);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                output.close();
                input.close();

                // Download Cover Image
                url = new URL(coverUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                input = connection.getInputStream();
                File coverFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), songTitle + "_cover.jpg");
                output = new FileOutputStream(coverFile);

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                output.close();
                input.close();

                // Add to MediaStore
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.ARTIST, artist);
                values.put(MediaStore.Audio.Media.TITLE, songTitle);
                values.put(MediaStore.Audio.Media.DATA, songFile.getAbsolutePath());
                values.put(MediaStore.Audio.Media.ALBUM, songTitle);
                values.put(MediaStore.Audio.Media.ALBUM_ARTIST, artist);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");

                ContentResolver contentResolver = getContentResolver();
                contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(SongPlayingActivity.this, "Download complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SongPlayingActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}