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

import com.example.btl_appnghenhac.Adapter.PlaylistAdapter_HomeFragment;
import com.example.btl_appnghenhac.Adapter.PlaylistAdapter_SearchFragment;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    ToggleButton tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;

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

        tgbtn_song.setChecked(true);

        inactiveOtherButtons(tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_artist, tgbtn_song, tgbtn_album, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_album, tgbtn_artist, tgbtn_song, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_playlist, tgbtn_artist, tgbtn_album, tgbtn_song);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),edt_search.getText().toString() + " | " +  getValueToggleButton(tgbtn_song, tgbtn_artist, tgbtn_album, tgbtn_playlist), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        ArrayList<Playlist> list1 = new ArrayList<>();
        list1.add(new Playlist(1, R.drawable.stbest, "item name 1"));
        list1.add(new Playlist(2, R.drawable.stbest, "item name 2"));
        list1.add(new Playlist(3, R.drawable.stbest, "item name 3"));
        list1.add(new Playlist(4, R.drawable.stbest, "item name 4"));
        list1.add(new Playlist(2, R.drawable.stbest, "item name 5"));
        list1.add(new Playlist(3, R.drawable.ic_launcher_background, "item name 6"));
        list1.add(new Playlist(3, R.drawable.ic_launcher_background, "item name 7"));


        PlaylistAdapter_SearchFragment adapter = new PlaylistAdapter_SearchFragment(list1);

        PlaylistAdapter_HomeFragment adapter1 = new PlaylistAdapter_HomeFragment(getContext(),list1);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter1);

        edt_search.setText("hello");

        return view;
    }


    public String getValueToggleButton(ToggleButton toggleButton, ToggleButton toggleButton1, ToggleButton toggleButton2, ToggleButton toggleButton3){
        if (toggleButton.isChecked()){
            return toggleButton.getText().toString();
        }
        if (toggleButton1.isChecked()){
            return toggleButton1.getText().toString();
        }
        if (toggleButton2.isChecked()){
            return toggleButton2.getText().toString();
        }
        if (toggleButton3.isChecked()){
            return toggleButton3.getText().toString();
        }
        else
            return "Maybe something wrong, please just choose a category that you want to search again :D";
    }

    public void inactiveOtherButtons(ToggleButton toggleButton, ToggleButton toggleButton1, ToggleButton toggleButton2, ToggleButton toggleButton3){
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