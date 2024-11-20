package com.example.btl_appnghenhac;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.btl_appnghenhac.Object.Playlist;
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
    SongAdapter_PlaylistActivity adapter;
    ArrayList<Song> songArrayList;

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
            }
        });

        Bundle bundle = getIntent().getExtras();
        Playlist playlist = (Playlist) bundle.get("playlist");

        tv_playlistName.setText(playlist.getPlaylistName());
        Glide.with(this)
                .load(playlist.getPlaylistUrl())
                .into(img_playlistImage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        songArrayList = new ArrayList<>();
        adapter = new SongAdapter_PlaylistActivity(songArrayList, this);


        recyclerView.setAdapter(adapter);

        getDataPlaylistFromFirebase(playlist.getPlaylistID());

        btn_playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(PlaylistActivity.this, SongPlayingActivity.class);
                intent2.putExtra("songList", songArrayList);
                intent2.putExtra("songObject", songArrayList.get(0)); // 'selectedSong' phải là đối tượng kiểu Song
                startActivity(intent2);
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
                                fetchSongsFromIds(playlist.getSong());
                            }
                        } else {
                            Log.w(TAG, "Error getting playlist details.", task.getException());
                        }
                    }
                });
    }

    // Lấy thông tin bài hát từ danh sách songId
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