package com.example.btl_appnghenhac.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.btl_appnghenhac.Adapter.PlaylistAdapter_HomeFragment;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    RecyclerView recyclerView1, recyclerView2, recyclerView3;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PlaylistAdapter_HomeFragment adapter;
    ArrayList<Playlist> playlistArrayList;

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

//        ArrayList<Playlist> list = new ArrayList<>();
//        list.add(new Playlist(1, R.drawable.stbest, "playlist name 1"));
//        list.add(new Playlist(2, R.drawable.stbest, "playlist name 2"));
//        list.add(new Playlist(3, R.drawable.stbest, "playlist name 1"));
//        list.add(new Playlist(4, R.drawable.stbest, "playlist name 1"));
//        PlaylistAdapter_HomeFragment adapter = new PlaylistAdapter_HomeFragment(getContext(),list);
//
//        ArrayList<Playlist> list1 = new ArrayList<>();
//        list1.add(new Playlist(1, R.drawable.ic_launcher_background, "playlist name 1"));
//        list1.add(new Playlist(2, R.drawable.ic_launcher_background, "playlist name 2"));
//        list1.add(new Playlist(3, R.drawable.ic_launcher_background, "playlist name 1"));
//        list1.add(new Playlist(4, R.drawable.ic_launcher_background, "playlist name 1"));
//
//
//        PlaylistAdapter_HomeFragment adapter1 = new PlaylistAdapter_HomeFragment(getActivity(), list1);
//
//        // Thiết lập LayoutManager và Adapter cho RecyclerView
//        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerView1.setAdapter(adapter);
//
//        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerView2.setAdapter(adapter1);
//
//        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerView3.setAdapter(adapter);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        playlistArrayList = new ArrayList<>();
        adapter = new PlaylistAdapter_HomeFragment(getActivity(), playlistArrayList);
        recyclerView1.setAdapter(adapter);

        getDataPlaylistFromFirebase(); // Lấy dữ liệu từ Firestore và cập nhật RecyclerView

        return view;
    }

    private void getDataPlaylistFromFirebase() {
        db.collection("playlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            playlistArrayList.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                            for (DocumentSnapshot document : task.getResult()) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlistArrayList.add(playlist); // Thêm vào danh sách
                            }
                            adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getDataPlaylist(){
        DocumentReference docRef = db.collection("playlist").document("1");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
