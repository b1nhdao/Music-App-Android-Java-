package com.example.btl_appnghenhac.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Object.Song;
import com.example.btl_appnghenhac.R;
import com.example.btl_appnghenhac.SongPlayingActivity;

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
        holder.img_song.setImageResource(song.getSongImage());
        holder.tv_songArtist.setText(song.getSongArtistName());
        holder.tv_songName.setText(song.getSongName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSongClick(song);
            }
        });
    }

    public void onSongClick(Song song){
        Intent intent = new Intent(context, SongPlayingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songObject", song);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView img_song;
        TextView tv_songName, tv_songArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            img_song = itemView.findViewById(R.id.img_song);
            tv_songName = itemView.findViewById(R.id.tv_songName);
            tv_songArtist = itemView.findViewById(R.id.tv_songArtist);
        }
    }
}
