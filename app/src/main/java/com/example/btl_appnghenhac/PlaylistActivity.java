package com.example.btl_appnghenhac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Adapter.SongAdapter_PlaylistActivity;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.Object.Song;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    TextView tv_playlistName;
    ImageView img_playlistImage, iv_back;
    RecyclerView recyclerView;

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

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        Playlist playlist = (Playlist) bundle.get("playlist");
        tv_playlistName.setText(playlist.getPlaylistName());
        img_playlistImage.setImageResource(playlist.getPlaylistImage());

        ArrayList<Song> songArrayList = new ArrayList<Song>();
        songArrayList.add(new Song(1, R.drawable.stbest, "name song 1", "artist name 1", 200));
        songArrayList.add(new Song(2, R.drawable.stbest, "name song 2", "artist name 2", 200));
        songArrayList.add(new Song(3, R.drawable.stbest, "name song 3", "artist name 3", 200));
        songArrayList.add(new Song(4, R.drawable.stbest, "name song 4", "artist name 4", 200));

        SongAdapter_PlaylistActivity adapter = new SongAdapter_PlaylistActivity(songArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }
}