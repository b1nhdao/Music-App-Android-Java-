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

import com.example.btl_appnghenhac.R;

public class LibraryFragment extends Fragment {

    ToggleButton tgbtn_song, tgbtn_playlist;
    EditText edt_search;
    ImageView img_search;
    RecyclerView recyclerView;

    public LibraryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        img_search = view.findViewById(R.id.img_search);
        edt_search = view.findViewById(R.id.edt_search);
        tgbtn_song = view.findViewById(R.id.tgbtn_song);
        tgbtn_playlist = view.findViewById(R.id.tgbtn_playlist);

        tgbtn_song.setChecked(true);

        inactiveOtherButtons(tgbtn_song, tgbtn_playlist);
        inactiveOtherButtons(tgbtn_playlist, tgbtn_song);

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),edt_search.getText().toString() + " | " +  getValueToggleButton(tgbtn_song, tgbtn_playlist), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.setAdapter(adapter);

        edt_search.setText("hello");


        return view;
    }

    public String getValueToggleButton(ToggleButton toggleButton, ToggleButton toggleButton1){
        if (toggleButton.isChecked()){
            return toggleButton.getText().toString();
        }
        if (toggleButton1.isChecked()){
            return toggleButton1.getText().toString();
        }
        else
            return "Maybe something wrong, please just choose a category that you want to search again :D";
    }


    public void inactiveOtherButtons(ToggleButton toggleButton, ToggleButton toggleButton1){
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButton1.setChecked(false);
            }
        });
    }
}