package com.example.btl_appnghenhac.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Adapter.PlaylistSongAdapter;
import com.example.btl_appnghenhac.Adapter.SongOfflineAdapter;
import com.example.btl_appnghenhac.Object.PlaylistCreated;
import com.example.btl_appnghenhac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    ToggleButton tgbtn_song, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<PlaylistCreated> playlistArrayList = new ArrayList<>();
    String TAG = "mytag";
    PlaylistSongAdapter playlistAdapter;
    SongOfflineAdapter adapterOffline;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        img_search = view.findViewById(R.id.img_search);
        edt_search = view.findViewById(R.id.edt_search);
        tgbtn_song = view.findViewById(R.id.tgbtn_song);
        tgbtn_playlist = view.findViewById(R.id.tgbtn_playlist);
        recyclerView = view.findViewById(R.id.recyclerView);

        img_search.setOnClickListener(view1 ->
                Toast.makeText(getContext(), edt_search.getText().toString() + " | " + getValueToggleButton(tgbtn_song, tgbtn_playlist), Toast.LENGTH_SHORT).show());

        tgbtn_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tgbtn_song.isChecked()) {
                    tgbtn_playlist.setChecked(false);
                    showSongOfflineList();
                }
            }
        });

        tgbtn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tgbtn_playlist.isChecked()) {
                    tgbtn_song.setChecked(false);
                    showPlaylistCreated();
                }
            }
        });

        return view;
    }

    private void showSongOfflineList() {
        ArrayList<String> audioFiles = getAudioFiles();
        if (audioFiles.isEmpty()) {
            Toast.makeText(getActivity(), "No audio files found", Toast.LENGTH_SHORT).show();
        }
        adapterOffline = new SongOfflineAdapter(getActivity(), audioFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterOffline);
        adapterOffline.notifyDataSetChanged();
    }

    private void showPlaylistCreated() {
        db.collection("playlistCreated")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            playlistArrayList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                PlaylistCreated playlist = document.toObject(PlaylistCreated.class);
                                playlistArrayList.add(playlist);
                            }
                            playlistAdapter = new PlaylistSongAdapter(getActivity(), new ArrayList<>(playlistArrayList), "playlistCreated", 0);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(playlistAdapter);
                            playlistAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private ArrayList<String> getAudioFiles() {
        ArrayList<String> audioFiles = new ArrayList<>();
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        if (musicDirectory != null && musicDirectory.exists()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".mp3")) {
                        audioFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return audioFiles;
    }

    public String getValueToggleButton(ToggleButton toggleButton, ToggleButton toggleButton1) {
        if (toggleButton.isChecked()) {
            return toggleButton.getText().toString();
        }
        if (toggleButton1.isChecked()) {
            return toggleButton1.getText().toString();
        } else
            return "Maybe something wrong, please just choose a category that you want to search again :D";
    }
}
