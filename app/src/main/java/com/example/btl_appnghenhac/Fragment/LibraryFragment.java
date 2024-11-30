package com.example.btl_appnghenhac.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Adapter.PlaylistSongAdapter;
import com.example.btl_appnghenhac.Adapter.SongOfflineAdapter;
import com.example.btl_appnghenhac.Object.PlaylistCreated;
import com.example.btl_appnghenhac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    ToggleButton tgbtn_song, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<PlaylistCreated> playlistArrayList = new ArrayList<>();
    String TAG = "mytag";
    PlaylistSongAdapter playlistAdapter;
    SongOfflineAdapter adapterOffline;
    FloatingActionButton floatbtn_addPlaylist;
    ImageView img_playlistCreate;
    String selectedImagePath = null; // Correctly declare as global
    Dialog dialog;

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
        floatbtn_addPlaylist = view.findViewById(R.id.floatbtn_addPlaylist);

        EditText edt_playlistNameCreate;
        Button btn_pickImage, btn_create;

        // Pop up playlist create
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.create_playlist_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(true);

        // Views binding dialog
        edt_playlistNameCreate = dialog.findViewById(R.id.edt_playlistNameCreate);
        btn_pickImage = dialog.findViewById(R.id.btn_pickImage);
        btn_create = dialog.findViewById(R.id.btn_create);
        img_playlistCreate = dialog.findViewById(R.id.img_playlistCreate);

        ArrayList<Integer> songArray = new ArrayList<>(); // Initialize song array (modify this logic as per your needs)

        // Button to pick image from device
        btn_pickImage.setOnClickListener(view1 -> pickImage());

        // Button to create playlist
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlistName = edt_playlistNameCreate.getText().toString().trim();
                if (playlistName.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a name and select an image for the playlist", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedImagePath == null){
                        selectedImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.baseline_music_note_24;
                    }
                    createNewPlaylist(playlistName, selectedImagePath, songArray);
                }
            }
        });

        floatbtn_addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

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

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImagePath = getRealPathFromURI(selectedImageUri);
                img_playlistCreate.setImageURI(selectedImageUri); // Display the selected image
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    private void createNewPlaylist(String playlistName, String imagePath, ArrayList<Integer> songArray) {
        db.collection("playlistCreated")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Find the maximum document ID in the collection
                        int maxDocumentID = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                int documentID = Integer.parseInt(document.getId());
                                if (documentID > maxDocumentID) {
                                    maxDocumentID = documentID;
                                }
                            } catch (NumberFormatException e) {
                                Log.w(TAG, "Non-numeric document ID: " + document.getId());
                            }
                        }

                        // Increment to generate a new unique document ID
                        int newDocumentID = maxDocumentID + 1;

                        // Use the same value for playlistIDc or customize as needed
                        int newPlaylistIDc = newDocumentID;

                        // Create new playlist object
                        PlaylistCreated newPlaylist = new PlaylistCreated(newPlaylistIDc, playlistName, imagePath, songArray);

                        // Save new playlist to Firestore
                        db.collection("playlistCreated").document(String.valueOf(newDocumentID))
                                .set(newPlaylist)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Playlist created successfully!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss(); // Dismiss the dialog after creating the playlist
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to create playlist.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
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
        } else {
            return "Maybe something wrong, please just choose a category that you want to search again :D";
        }
    }
}
