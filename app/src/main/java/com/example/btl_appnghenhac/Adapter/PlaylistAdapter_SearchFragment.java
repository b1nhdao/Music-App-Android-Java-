package com.example.btl_appnghenhac.Adapter;

import android.content.Context;
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

import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.R;

import java.util.ArrayList;

public class PlaylistAdapter_SearchFragment extends RecyclerView.Adapter<PlaylistAdapter_SearchFragment.viewHolder> {
    ArrayList<Playlist> playlistArrayList;
    Context context;
    public PlaylistAdapter_SearchFragment(Context context, ArrayList<Playlist> playlistArrayList) {
        this.context = context;
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
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, playlist.getPlaylistName(), Toast.LENGTH_SHORT).show();
            }
        });
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
