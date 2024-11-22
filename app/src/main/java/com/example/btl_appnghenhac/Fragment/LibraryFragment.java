package com.example.btl_appnghenhac.Fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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

import com.example.btl_appnghenhac.Adapter.SongOfflineAdapter;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    ToggleButton tgbtn_song, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;

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

        tgbtn_song.setChecked(true);

        inactiveOtherButtons(tgbtn_song, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_playlist, tgbtn_song);

        img_search.setOnClickListener(view1 ->
                Toast.makeText(getContext(), edt_search.getText().toString() + " | " + getValueToggleButton(tgbtn_song, tgbtn_playlist), Toast.LENGTH_SHORT).show());

        recyclerView = view.findViewById(R.id.recyclerView);

        ArrayList<String> audioFiles = getAudioFiles();
        if (audioFiles.isEmpty()) {
            Toast.makeText(getActivity(), "No audio files found", Toast.LENGTH_SHORT).show();
        }
        SongOfflineAdapter adapter = new SongOfflineAdapter(getActivity(), audioFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private ArrayList<String> getAudioFiles() {
        ArrayList<String> audioFiles = new ArrayList<>();
        ContentResolver contentResolver = requireContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String path = cursor.getString(dataIndex);
                if (path != null && path.endsWith(".mp3")) {
                    audioFiles.add(path);
                }
            } while (cursor.moveToNext());

            cursor.close();
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

    public void inactiveOtherButtons(ToggleButton toggleButton, ToggleButton toggleButton1) {
        toggleButton.setOnClickListener(view -> toggleButton1.setChecked(false));
    }
}