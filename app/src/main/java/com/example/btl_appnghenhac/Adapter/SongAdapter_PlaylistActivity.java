package com.example.btl_appnghenhac.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Object.Song;
import com.example.btl_appnghenhac.R;
import com.example.btl_appnghenhac.SongPlayingActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SongAdapter_PlaylistActivity extends RecyclerView.Adapter<SongAdapter_PlaylistActivity.ViewHolder> {
    ArrayList<Song> songArrayList = new ArrayList<Song>();
    Context context;

    public SongAdapter_PlaylistActivity(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        Glide.with(context)
                .load(song.getSongImageUrl())
                .into(holder.img_song);

        holder.tv_songArtist.setText(song.getSongArtistName());
        holder.tv_songName.setText(song.getSongName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSongClick(song, position);
            }
        });

        holder.img_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songId = String.valueOf(song.getSongID());
                onFavouriteClick(song, songId);
                if (!song.isSongFavourite()){
                    holder.img_favourite.setImageResource(R.drawable.baseline_favorite_24);
                }
                else{
                    holder.img_favourite.setImageResource(R.drawable.baseline_favorite_border_24);

                }
            }
        });

        if (song.isSongFavourite()){
            Glide.with(context)
                    .load(R.drawable.baseline_favorite_24)
                    .into(holder.img_favourite);
        }
        else{
            Glide.with(context)
                    .load(R.drawable.baseline_favorite_border_24)
                    .into(holder.img_favourite);
        }
    }

    public void onFavouriteClick(Song song, String songId){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (!song.isSongFavourite()) {
            firestore.collection("song").document(songId)
                    .update("songFavourite", true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirestoreUpdate", "Đã cập nhật vào danh sách yêu thích");
                            Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            song.setSongFavourite(true);
                        } else {
                            Log.e("FirestoreUpdate", "Cập nhật thất bại", task.getException());
                            Toast.makeText(context, "Không thể thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            firestore.collection("song").document(songId)
                    .update("songFavourite", false)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirestoreUpdate", "Đã xóa khỏi danh sách yêu thích");
                            Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            song.setSongFavourite(false);
                        } else {
                            Log.e("FirestoreUpdate", "Xóa thất bại", task.getException());
                            Toast.makeText(context, "Không thể xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    public void onSongClick(Song song, int position){
        Intent intent = new Intent(context, SongPlayingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songList", songArrayList);
        bundle.putInt("currentSongIndex", position);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView img_song, img_favourite;
        TextView tv_songName, tv_songArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            img_song = itemView.findViewById(R.id.img_song);
            tv_songName = itemView.findViewById(R.id.tv_songName);
            tv_songArtist = itemView.findViewById(R.id.tv_songArtist);
            img_favourite = itemView.findViewById(R.id.img_favourite);
        }
    }
}
