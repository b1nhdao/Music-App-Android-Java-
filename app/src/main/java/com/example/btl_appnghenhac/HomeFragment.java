package com.example.btl_appnghenhac;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_appnghenhac.Adapter.PlaylistAdapter_HomeFragment;
import com.example.btl_appnghenhac.Object.Playlist;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    public HomeFragment() {

    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        recyclerView3 = view.findViewById(R.id.recyclerView3);


        ArrayList<Playlist> list = new ArrayList<>();
        list.add(new Playlist(1, R.drawable.stbest, "playlist name 1"));
        list.add(new Playlist(2, R.drawable.stbest, "playlist name 2"));
        list.add(new Playlist(3, R.drawable.stbest, "playlist name 1"));
        list.add(new Playlist(4, R.drawable.stbest, "playlist name 1"));
        PlaylistAdapter_HomeFragment adapter = new PlaylistAdapter_HomeFragment(list);

        ArrayList<Playlist> list1 = new ArrayList<>();
        list1.add(new Playlist(1, R.drawable.ic_launcher_background, "playlist name 1"));
        list1.add(new Playlist(2, R.drawable.ic_launcher_background, "playlist name 2"));
        list1.add(new Playlist(3, R.drawable.ic_launcher_background, "playlist name 1"));
        list1.add(new Playlist(4, R.drawable.ic_launcher_background, "playlist name 1"));


        PlaylistAdapter_HomeFragment adapter1 = new PlaylistAdapter_HomeFragment(list1);


        // Thiết lập LayoutManager và Adapter cho RecyclerView
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView1.setAdapter(adapter);

        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(adapter1);

        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setAdapter(adapter);



        return view;
    }

}