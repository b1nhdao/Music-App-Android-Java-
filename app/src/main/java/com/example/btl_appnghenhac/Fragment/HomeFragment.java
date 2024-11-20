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
    PlaylistAdapter_HomeFragment adapter1, adapter2, adapter3;
    ArrayList<Playlist> playlistList1, playlistList2, playlistList3;
    DocumentSnapshot lastVisible1, lastVisible2;

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

        // Cài đặt LayoutManager
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Khởi tạo danh sách và adapter
        playlistList1 = new ArrayList<>();
        playlistList2 = new ArrayList<>();
        playlistList3 = new ArrayList<>();

        adapter1 = new PlaylistAdapter_HomeFragment(getActivity(), playlistList1);
        adapter2 = new PlaylistAdapter_HomeFragment(getActivity(), playlistList2);
        adapter3 = new PlaylistAdapter_HomeFragment(getActivity(), playlistList3);

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        // Lấy dữ liệu
        getFirstThreePlaylists();
        return view;

    }

//    private void getDataPlaylistFromFirebase() {
//        db.collection("playlist")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            playlistArrayList.clear(); // Xóa dữ liệu cũ trước khi thêm mới
//                            for (DocumentSnapshot document : task.getResult()) {
//                                Playlist playlist = document.toObject(Playlist.class);
//                                playlistList1.add(playlist); // Thêm vào danh sách
//                            }
//                            adapter1.notifyDataSetChanged(); // Cập nhật RecyclerView
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    private void getFirstThreePlaylists() {
        db.collection("playlist")
                .limit(3) // Lấy 3 playlist đầu tiên
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            playlistList1.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlistList1.add(playlist); // Thêm vào danh sách của RecyclerView 1
                            }
                            adapter1.notifyDataSetChanged();

                            // Lưu tài liệu cuối để dùng cho query tiếp theo
                            if (task.getResult().size() > 0) {
                                lastVisible1 = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }

                            // Lấy playlist tiếp theo cho RecyclerView 2
                            getNextThreePlaylists(lastVisible1, playlistList2, adapter2, 2);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getNextThreePlaylists(DocumentSnapshot lastVisible, ArrayList<Playlist> targetList, PlaylistAdapter_HomeFragment targetAdapter, int recyclerViewIndex) {
        if (lastVisible != null) {
            db.collection("playlist")
                    .startAfter(lastVisible) // Bắt đầu từ tài liệu cuối cùng của query trước
                    .limit(3) // Lấy 3 playlist tiếp theo
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                targetList.clear();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Playlist playlist = document.toObject(Playlist.class);
                                    targetList.add(playlist); // Thêm vào danh sách
                                }
                                targetAdapter.notifyDataSetChanged();

                                // Lưu tài liệu cuối
                                if (task.getResult().size() > 0) {
                                    if (recyclerViewIndex == 2) {
                                        lastVisible2 = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                        // Lấy playlist cho RecyclerView 3
                                        getNextThreePlaylists(lastVisible2, playlistList3, adapter3, 3);
                                    }
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

}
