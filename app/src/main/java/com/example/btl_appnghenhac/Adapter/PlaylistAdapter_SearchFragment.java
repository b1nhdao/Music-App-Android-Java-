package com.example.btl_appnghenhac.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

public class PlaylistAdapter_SearchFragment extends RecyclerView.Adapter<PlaylistAdapter_SearchFragment.viewHolder> {
    ArrayList<Playlist> playlistArrayList;

    public PlaylistAdapter_SearchFragment(ArrayList<Playlist> playlistArrayList) {
        this.playlistArrayList = playlistArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Playlist playlist = playlistArrayList.get(position);
        holder.imageView.setImageResource(playlist.getPlaylistImage());
        holder.textView.setText(playlist.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return playlistArrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
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
