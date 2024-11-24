package com.example.btl_appnghenhac;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.btl_appnghenhac.Adapter.SongAdapter_PlaylistActivity;
import com.example.btl_appnghenhac.Adapter.PlaylistSongAdapter;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.Object.PlaylistCreated;
import com.example.btl_appnghenhac.Object.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    Button btn_playList;
    TextView tv_playlistName;
    ImageView img_playlistImage, iv_back, img_favourite;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HomeFragment";
    RecyclerView.Adapter adapter;
    ArrayList<Song> songArrayList;
    int codeIsFavourute;
    int codeLibrary = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_playlistName = findViewById(R.id.tv_playlistName);
        img_playlistImage = findViewById(R.id.img_playlistImage);
        recyclerView = findViewById(R.id.recyclerView);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        btn_playList = findViewById(R.id.btn_playList);
        img_favourite = findViewById(R.id.img_favourite);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(PlaylistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        songArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        if (bundle.containsKey("playlist")) {
            Playlist playlist = (Playlist) bundle.get("playlist");
            tv_playlistName.setText(playlist.getPlaylistName());
            Glide.with(this)
                    .load(playlist.getPlaylistUrl())
                    .into(img_playlistImage);

            // Use SongAdapter_PlaylistActivity for playlist data
            String playlistId = String.valueOf(playlist.getPlaylistID());
            adapter = new SongAdapter_PlaylistActivity(songArrayList, this, 1, playlistId); // Pass the playlist ID

            recyclerView.setAdapter(adapter);
            getDataPlaylistFromFirebase(playlist.getPlaylistID());
        }
        else if (bundle.containsKey("playlistCreated")) {
            PlaylistCreated playlistCreated = (PlaylistCreated) bundle.get("playlistCreated");
            String playlistId = String.valueOf(playlistCreated.getPlaylistIDc());
            tv_playlistName.setText(playlistCreated.getPlaylistNamec());
            Glide.with(this)
                    .load(playlistCreated.getPlaylistUrlc())
                    .into(img_playlistImage);

            // Use PlaylistSongAdapter for playlistCreated data
            adapter = new SongAdapter_PlaylistActivity(songArrayList, this,  1, playlistId);
            recyclerView.setAdapter(adapter);
            getDataPlaylistCreatedFromFirebase(playlistCreated.getPlaylistIDc());
            codeLibrary = 1;
        }

        codeIsFavourute = bundle.getInt("codeIsFavourite", 0);

        btn_playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songArrayList.isEmpty()) {
                    Toast.makeText(PlaylistActivity.this, "Playlist is empty.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent2 = new Intent(PlaylistActivity.this, SongPlayingActivity.class);
                    intent2.putExtra("songList", songArrayList);
                    intent2.putExtra("songObject", songArrayList.get(0)); // 'selectedSong' must be of type Song
                    startActivity(intent2);
                }
            }
        });
    }

    private void getDataPlaylistFromFirebase(int playlistId) {
        String playlistIdStr = String.valueOf(playlistId);
        db.collection("playlist").document(playlistIdStr).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Playlist playlist = task.getResult().toObject(Playlist.class);
                            if (playlist != null && playlist.getSong() != null) {
                                if (codeIsFavourute == 0) {
                                    fetchSongsFromIds(playlist.getSong());
                                } else if (codeIsFavourute == 1) {
                                    fetchFavouriteSongs();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting playlist details.", task.getException());
                        }
                    }
                });
    }

    private void getDataPlaylistCreatedFromFirebase(int playlistId) {
        String playlistIdStr = String.valueOf(playlistId);
        db.collection("playlistCreated").document(playlistIdStr).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaylistCreated playlistCreated = task.getResult().toObject(PlaylistCreated.class);
                            if (playlistCreated != null && playlistCreated.getSong() != null) {
                                if (codeIsFavourute == 0) {
                                    fetchSongsFromIds(playlistCreated.getSong());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting playlist details.", task.getException());
                        }
                    }
                });
    }

    private void fetchFavouriteSongs() {
        songArrayList.clear();
        db.collection("song")
                .whereEqualTo("songFavourite", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Song song = document.toObject(Song.class);
                                if (song != null) {
                                    songArrayList.add(song);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting favourite songs.", task.getException());
                        }
                    }
                });
    }

    private void fetchSongsFromIds(ArrayList<Integer> songIds) {
        songArrayList.clear();
        for (int songId : songIds) {
            String songIdStr = String.valueOf(songId);
            db.collection("song").document(songIdStr).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Song song = task.getResult().toObject(Song.class);
                                if (song != null) {
                                    songArrayList.add(song);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.w(TAG, "Error getting song details.", task.getException());
                            }
                        }
                    });
        }
    }
}
