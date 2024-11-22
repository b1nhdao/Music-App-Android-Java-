package com.example.btl_appnghenhac.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.btl_appnghenhac.Adapter.PlaylistSongAdapter;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.Object.Song;
import com.example.btl_appnghenhac.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    ToggleButton tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;
    FirebaseFirestore db;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        img_search = view.findViewById(R.id.img_search);
        edt_search = view.findViewById(R.id.edt_search);
        tgbtn_song = view.findViewById(R.id.tgbtn_song);
        tgbtn_artist = view.findViewById(R.id.tgbtn_artist);
        tgbtn_album = view.findViewById(R.id.tgbtn_album);
        tgbtn_playlist = view.findViewById(R.id.tgbtn_playlist);
        recyclerView = view.findViewById(R.id.recyclerView);

        tgbtn_song.setChecked(true);

        inactiveOtherButtons(tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_artist, tgbtn_song, tgbtn_album, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_album, tgbtn_artist, tgbtn_song, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_playlist, tgbtn_artist, tgbtn_album, tgbtn_song);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchValue = edt_search.getText().toString();
                String selectedCategory = getValueToggleButton(tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist);
                switch (selectedCategory) {
                    case "Playlist":
                        searchPlaylists(searchValue);
                        break;
                    case "Bài hát":
                        searchSongs(searchValue);
                        break;
                    default:
                        Toast.makeText(getContext(), "Chỉ hỗ trợ tìm kiếm playlist và bài hát tại thời điểm này.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    private void searchPlaylists(String playlistName) {
        CollectionReference collectionReference = db.collection("playlist");

        // Normalize the search term to ignore case, accents, and other diacritics
        String normalizedPlaylistName = playlistName.toLowerCase();

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<Object> playlistResults = new ArrayList<>();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Playlist playlist = document.toObject(Playlist.class);
                                String normalizedPlaylistTitle = playlist.getPlaylistName().toLowerCase();

                                if (normalizedPlaylistTitle.contains(normalizedPlaylistName)) {
                                    playlistResults.add(playlist);
                                }
                            }

                            // Update RecyclerView with search results
                            PlaylistSongAdapter adapter = new PlaylistSongAdapter(getActivity(), playlistResults, "playlist", 0);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy playlist nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Có lỗi xảy ra khi tìm kiếm.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void searchSongs(String songName) {
        CollectionReference collectionReference = db.collection("song");

        // Normalize the search term to ignore case, accents, and other diacritics
        String normalizedSongName = songName.toLowerCase();

        collectionReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<Object> songResults = new ArrayList<>();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Song song = document.toObject(Song.class);
                                String normalizedSongTitle = song.getSongName().toLowerCase();

                                if (normalizedSongTitle.contains(normalizedSongName)) {
                                    songResults.add(song);
                                }
                            }

                            // Update RecyclerView with search results
                            PlaylistSongAdapter adapter = new PlaylistSongAdapter(getActivity(), songResults, "song", 0);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy bài hát nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Có lỗi xảy ra khi tìm kiếm.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public String getValueToggleButton(ToggleButton toggleButton, ToggleButton toggleButton1, ToggleButton toggleButton2, ToggleButton toggleButton3) {
        if (toggleButton.isChecked()) {
            return toggleButton.getText().toString();
        }
        if (toggleButton1.isChecked()) {
            return toggleButton1.getText().toString();
        }
        if (toggleButton2.isChecked()) {
            return toggleButton2.getText().toString();
        }
        if (toggleButton3.isChecked()) {
            return toggleButton3.getText().toString();
        } else
            return "Maybe something wrong, please just choose a category that you want to search again :D";
    }

    public void inactiveOtherButtons(ToggleButton toggleButton, ToggleButton toggleButton1, ToggleButton toggleButton2, ToggleButton toggleButton3) {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
            }
        });
    }
}
