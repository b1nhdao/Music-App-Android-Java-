package com.example.btl_appnghenhac;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Fragment.HomeFragment;
import com.example.btl_appnghenhac.Fragment.LibraryFragment;
import com.example.btl_appnghenhac.Fragment.SearchFragment;
import com.example.btl_appnghenhac.Object.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView img_songImage, img_play, img_next, iv_back;
    TextView tv_songName, tv_songArtist;
    FirebaseFirestore db;
    MusicService musicService;
    boolean serviceBound = false;
    Button btn_viewUsers;

    private BroadcastReceiver miniPlayerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.btl_appnghenhac.UPDATE_MINI_PLAYER")) {
                String songName = intent.getStringExtra("songName");
                String songArtist = intent.getStringExtra("songArtist");
                String songImageUrl = intent.getStringExtra("songImageUrl");

                // Update mini player UI
                tv_songName.setText(songName);
                tv_songArtist.setText(songArtist);
                Glide.with(MainActivity.this).load(songImageUrl).into(img_songImage);
                updatePlayButton(); // Update the play button to reflect the current playing state
            }
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_main);

        img_songImage = findViewById(R.id.img_songImage);
        img_play = findViewById(R.id.img_play);
        img_next = findViewById(R.id.img_next);
        tv_songName = findViewById(R.id.tv_songName);
        tv_songArtist = findViewById(R.id.tv_songArtist);
        btn_viewUsers = findViewById(R.id.btn_viewUsers);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.getInt("role",2) == 1){
                btn_viewUsers.setVisibility(View.VISIBLE);
            }
        }

        btn_viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewUsersActivity.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();

        loadFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                loadFragment(new HomeFragment());
                return true;
            }
            if(item.getItemId() == R.id.nav_search){
                loadFragment(new SearchFragment());
                return true;
            }
            if(item.getItemId() == R.id.nav_library){
                loadFragment(new LibraryFragment());
                return true;
            }
            return false;
        });

        setupControlButtons();

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter("com.example.btl_appnghenhac.UPDATE_MINI_PLAYER");
        registerReceiver(miniPlayerUpdateReceiver, filter);

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void setupControlButtons() {
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
        img_next.setOnClickListener(view -> {
            if (musicService != null) {
                musicService.playNextSong();
                updateMiniPlayer();
            }
        });
    }

    private void updateMiniPlayer() {
        if (musicService != null && musicService.getCurrentSong() != null) {
            Song currentSong = musicService.getCurrentSong();
            tv_songName.setText(currentSong.getSongName());
            tv_songArtist.setText(currentSong.getSongArtistName());
            Glide.with(this).load(currentSong.getSongImageUrl()).into(img_songImage);
            updatePlayButton();
        }
    }

    private void updatePlayButton() {
        if ( musicService.isPlaying()) {
            img_play.setImageResource(R.drawable.play1);
        } else {
            img_play.setImageResource(R.drawable.play2);
        }
    }

//    private void loadSongDetailsFromPreferences() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MusicPreferences", MODE_PRIVATE);
//        songID = sharedPreferences.getInt("songID", 0);
//        String songName = sharedPreferences.getString("songName", "");
//        String songArtist = sharedPreferences.getString("songArtist", "");
//        String songImageUrl = sharedPreferences.getString("songImageUrl", "");
//
//        if (!songName.isEmpty() && !songArtist.isEmpty() && !songImageUrl.isEmpty()) {
//            tv_songName.setText(songName);
//            tv_songArtist.setText(songArtist);
//            Glide.with(this).load(songImageUrl).into(img_songImage);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        unregisterReceiver(miniPlayerUpdateReceiver);
    }
}