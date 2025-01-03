package com.example.btl_appnghenhac;

import android.app.Dialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Adapter.PlaylistSongAdapter;
import com.example.btl_appnghenhac.Object.PlaylistCreated;
import com.example.btl_appnghenhac.Object.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class SongPlayingActivity extends AppCompatActivity {

    ImageView img_songImage, iv_back, iv_menu;
    TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd, tv_sleepTime;
    ImageView img_shuffle, img_back, img_play, img_skip, img_loop;
    SeekBar seekBar;
    ArrayList<Song> songArrayList;
    int currentSongIndex = 0;
    boolean isLooping = false;
    boolean isShuffling = false;
    private ArrayList<Song> shuffledList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private MusicService musicService;
    private boolean serviceBound = false;
    int code;
    Dialog dialog, dialogTime;
    ShapeableImageView img_songImage1;
    private String TAG = "mytag";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PlaylistSongAdapter playlistAdapter;
    RecyclerView recyclerViewPlaylist;
    Runnable sleepRunnable;
    Handler sleepHandler = new Handler();


    ArrayList<PlaylistCreated> playlistCreatedArrayList = new ArrayList<>();

    int sleepTimeInMinutes = -1;

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
    }

    private void onClicktoFavOptionMenu(){
        onFavouriteClick(songArrayList.get(currentSongIndex), String.valueOf(songArrayList.get(currentSongIndex).getSongID()));
    }

    private void onClickToPlaylistOptionMenu(){
        dialog.show();
        showPlaylistCreated();
    }

    public MusicService getMusicService() {
        return musicService;
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
        if (item.getItemId() == R.id.min5){
            sleepTimeInMinutes = 1;
            setSleepTimer(sleepTimeInMinutes);
            Toast.makeText(this, "Hẹn giờ tắt sau " + sleepTimeInMinutes + " phút", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.min10){
            sleepTimeInMinutes = 10;
            setSleepTimer(sleepTimeInMinutes);
            Toast.makeText(this, "Hẹn giờ tắt sau " + sleepTimeInMinutes + " phút", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.min15){
            sleepTimeInMinutes = 15;
            setSleepTimer(sleepTimeInMinutes);
            Toast.makeText(this, "Hẹn giờ tắt sau " + sleepTimeInMinutes + " phút", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.min20){
            sleepTimeInMinutes = 20;
            setSleepTimer(sleepTimeInMinutes);
            Toast.makeText(this, "Hẹn giờ tắt sau " + sleepTimeInMinutes + " phút", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.other){
            dialogTime.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSleepTimer(int minutes) {
        if (sleepRunnable != null) {
            sleepHandler.removeCallbacks(sleepRunnable);
        }

        final int sleepTimeMillis = minutes * 5 * 1000; // Corrected to set timer in minutes
        sleepRunnable = () -> {
            if (musicService != null && musicService.isPlaying()) {
                musicService.stopSelf();
            }
            musicService.playPauseMusic();
            Toast.makeText(SongPlayingActivity.this, "Đã đến giờ tắt, chúc bạn ngủ ngon !", Toast.LENGTH_SHORT).show();
        };

        sleepHandler.postDelayed(sleepRunnable, sleepTimeMillis);

        // Display remaining time in sleep timer TextView
        sleepHandler.post(new Runnable() {
            int remainingTime = sleepTimeMillis / 1000;

            @Override
            public void run() {
                if (remainingTime > 0) {
                    tv_sleepTime.setText("Hẹn giờ: " + (remainingTime / 60) + "m " + (remainingTime % 60) + "s");
                    remainingTime--;
                    sleepHandler.postDelayed(this, 1000);
                } else {
                    tv_sleepTime.setText("Sleep timer ended");
                }
            }
        });
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
        getViews();

        tv_sleepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SongPlayingActivity.this, sleepTimeInMinutes + "", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(SongPlayingActivity.this, tv_sleepTime);
                popupMenu.getMenuInflater().inflate(R.menu.menu_pick_time_sleep, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
                popupMenu.show();
            }
        });

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


        //pop up menu
        dialog = new Dialog(SongPlayingActivity.this);
        dialog.setContentView(R.layout.playlist_popup_window);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(true);

        //another shit
        dialogTime = new Dialog(SongPlayingActivity.this);
        dialogTime.setContentView(R.layout.playlist_popup_window);
        dialogTime.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogTime.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialogTime.setCancelable(true);

        recyclerViewPlaylist = dialog.findViewById(R.id.recyclerViewPlaylist);

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
        startMusicService();

    }

    private void playCurrentSong() {
        if (songArrayList != null && !songArrayList.isEmpty() && musicService != null) {
            Song currentSong = songArrayList.get(currentSongIndex);
            musicService.playSong(currentSong, currentSongIndex); // Play song and notify MusicService

            // Update UI
            if (!isFinishing() && !isDestroyed()) {
                Glide.with(getApplicationContext()).load(currentSong.getSongImageUrl()).into(img_songImage1);
            }

            tv_songName.setText(currentSong.getSongName());
            tv_songArtist.setText(currentSong.getSongArtistName());
            tv_timeEnd.setText(convertDurationToString(musicService.getDuration() / 1000));
            seekBar.setMax(musicService.getDuration());
            updateSeekBar();

            // Send broadcast to update mini player in MainActivity
            Intent intent = new Intent("com.example.btl_appnghenhac.UPDATE_MINI_PLAYER");
            intent.putExtra("songName", currentSong.getSongName());
            intent.putExtra("songArtist", currentSong.getSongArtistName());
            intent.putExtra("songImageUrl", currentSong.getSongImageUrl());
            sendBroadcast(intent);
        }
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction("START_FOREGROUND");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        stopService(serviceIntent);
        unbindService(serviceConnection);
        serviceBound = false;
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
    // However, this was before, but i didnt change the name, so yeh, thats why it doesnt have a proper name
    // This is also for the notification interaction
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
    }

    public String convertDurationToString(int duration) {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void getViews() {
        iv_menu = findViewById(R.id.iv_menu);
        iv_menu.setVisibility(View.VISIBLE);
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
        tv_sleepTime = findViewById(R.id.tv_sleepTime);
        tv_sleepTime.setVisibility(View.VISIBLE);
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

    //yes, i took all from songAdapter
    public void onFavouriteClick(Song song, String songId){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (!song.isSongFavourite()) {
            firestore.collection("song").document(songId)
                    .update("songFavourite", true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirestoreUpdate", "Đã cập nhật vào danh sách yêu thích");
                            Toast.makeText(SongPlayingActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            song.setSongFavourite(true);
                        } else {
                            Log.e("FirestoreUpdate", "Cập nhật thất bại", task.getException());
                            Toast.makeText(SongPlayingActivity.this, "Không thể thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            firestore.collection("song").document(songId)
                    .update("songFavourite", false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirestoreUpdate", "Đã xóa khỏi danh sách yêu thích");
                            Toast.makeText(SongPlayingActivity.this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            song.setSongFavourite(false);
                        } else {
                            Log.e("FirestoreUpdate", "Xóa thất bại", task.getException());
                            Toast.makeText(SongPlayingActivity.this, "Không thể xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showPlaylistCreated() {
        db.collection("playlistCreated")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            playlistCreatedArrayList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                PlaylistCreated playlist = document.toObject(PlaylistCreated.class);
                                playlistCreatedArrayList.add(playlist);
                            }
                            playlistAdapter = new PlaylistSongAdapter(SongPlayingActivity.this, new ArrayList<>(playlistCreatedArrayList), "playlistPopup", 0);
                            recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(SongPlayingActivity.this, LinearLayoutManager.VERTICAL, false));
                            recyclerViewPlaylist.setAdapter(playlistAdapter);
                            playlistAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}