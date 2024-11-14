package com.example.btl_appnghenhac.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

public class PlaylistAdapter_HomeFragment extends RecyclerView.Adapter<PlaylistAdapter_HomeFragment.ViewHolder> {


    ArrayList<Playlist> arrayPlaylists;

    public PlaylistAdapter_HomeFragment(ArrayList<Playlist> arrayPlaylists) {
        this.arrayPlaylists = arrayPlaylists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = arrayPlaylists.get(position);
        holder.img_playlist.setImageResource(playlist.getPlaylistImage());
        holder.tv_playlistName.setText(playlist.getPlaylistName());
    }

    @Override
    public int getItemCount(){
        return arrayPlaylists.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_playlist;
        TextView tv_playlistName;
        CardView cardView;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            img_playlist = itemView.findViewById(R.id.img_playlist);
            tv_playlistName = itemView.findViewById(R.id.tv_playlistName);
        }
    }
}
