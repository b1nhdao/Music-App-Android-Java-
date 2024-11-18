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
import com.example.btl_appnghenhac.LoginActivity;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.PlaylistActivity;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

public class PlaylistAdapter_HomeFragment extends RecyclerView.Adapter<PlaylistAdapter_HomeFragment.ViewHolder> {

    private ArrayList<Playlist> playlists;
    private Context context;

    public PlaylistAdapter_HomeFragment(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        Glide.with(context)
                .load(playlist.getPlaylistUrl())
                .into(holder.imgPlaylist);

        holder.tvPlaylistName.setText(playlist.getPlaylistName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPlaylist(playlist);
            }
        });
    }

    private void onClickPlaylist(Playlist playlist){
//        Toast.makeText(context, playlist.getPlaylistID() + " ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlist", playlist);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlaylist;
        TextView tvPlaylistName;
        CardView cardView;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            cardView = itemView.findViewById(R.id.cardView);
            imgPlaylist = itemView.findViewById(R.id.img_playlist);
            tvPlaylistName = itemView.findViewById(R.id.tv_playlistName);
        }
    }
}
