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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.Object.Song;
import com.example.btl_appnghenhac.PlaylistActivity;
import com.example.btl_appnghenhac.R;
import com.example.btl_appnghenhac.SongPlayingActivity;

import java.util.ArrayList;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.viewHolder> {
    private Context context;
    private ArrayList<Object> items;
    private String type; // "playlist" or "song"

    public PlaylistSongAdapter(Context context, ArrayList<Object> items, String type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (type.equals("playlist")) {
            Playlist playlist = (Playlist) items.get(position);
            holder.textView.setText(playlist.getPlaylistName());
            Glide.with(context).load(playlist.getPlaylistUrl()).into(holder.imageView);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playlistOnClick(playlist);
                }
            });
        } else if (type.equals("song")) {
            Song song = (Song) items.get(position);
            holder.textView.setText(song.getSongName());
            Glide.with(context).load(song.getSongImageUrl()).into(holder.imageView);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    songOnClick(song, position);
                }
            });
        }
    }

    private void playlistOnClick(Playlist playlist) {
        Intent intent = new Intent(context, PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlist", playlist);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void songOnClick(Song song, int position) {
        Intent intent = new Intent(context, SongPlayingActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<Song> songList = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof Song) {
                songList.add((Song) item);
            }
        }
        bundle.putSerializable("songList", songList);
        bundle.putInt("currentSongIndex", position);
        bundle.putInt("code", 1);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            imageView = itemView.findViewById(R.id.img_playlist);
            textView = itemView.findViewById(R.id.tv_playlistName);
        }
    }
}