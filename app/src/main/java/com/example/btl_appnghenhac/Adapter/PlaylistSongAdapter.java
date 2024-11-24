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
import com.example.btl_appnghenhac.MusicService;
import com.example.btl_appnghenhac.Object.Playlist;
import com.example.btl_appnghenhac.Object.PlaylistCreated;
import com.example.btl_appnghenhac.Object.Song;
import com.example.btl_appnghenhac.PlaylistActivity;
import com.example.btl_appnghenhac.R;
import com.example.btl_appnghenhac.SongPlayingActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.viewHolder> {
    private Context context;
    private ArrayList<Object> items;
    private String type; // "playlist" or "song"
    private int onlineCode;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public PlaylistSongAdapter(Context context, ArrayList<Object> items, String type, int onlineCode) {
        this.context = context;
        this.items = items;
        this.type = type;
        this.onlineCode = onlineCode;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (onlineCode == 0){
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
            else if (type.equals("playlistCreated")){
                PlaylistCreated playlist = (PlaylistCreated) items.get(position);
                holder.textView.setText(playlist.getPlaylistNamec());
                Glide.with(context).load(playlist.getPlaylistUrlc()).into(holder.imageView);
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playlistCreatedOnClick(playlist);
                    }
                });
            }
            else if (type.equals("playlistPopup")){
                PlaylistCreated playlist = (PlaylistCreated) items.get(position);
                holder.textView.setText(playlist.getPlaylistNamec());
                Glide.with(context).load(playlist.getPlaylistUrlc()).into(holder.imageView);
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playlistPopUpOnClick(playlist);
                    }
                });
            }
        }
        else {
            PlaylistCreated playlistCreated = (PlaylistCreated) items.get(position);
            holder.textView.setText(playlistCreated.getPlaylistNamec());
            Glide.with(context).load(playlistCreated.getPlaylistUrlc()).into(holder.imageView);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playlistCreatedOnClick(playlistCreated);
                }
            });
        }
    }

    private void playlistPopUpOnClick(PlaylistCreated playlist) {
        // Get the current song from the MusicService
        Song currentSong = getCurrentSongFromService();

        if (currentSong != null) {
            int songID = currentSong.getSongID(); // Assuming Song class has a getSongID() method to get the song's ID

            db.collection("playlistCreated").document(String.valueOf(playlist.getPlaylistIDc()))
                    .update("song", FieldValue.arrayUnion(songID))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Song added to playlist: " + playlist.getPlaylistNamec(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to add song to playlist.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "No current song is playing.", Toast.LENGTH_SHORT).show();
        }
    }

    private Song getCurrentSongFromService() {
        // Use context to bind to MusicService and get the current song
        MusicService musicService = ((SongPlayingActivity) context).getMusicService();
        return musicService != null ? musicService.getCurrentSong() : null;
    }

    private void playlistCreatedOnClick(PlaylistCreated playlist) {
        Intent intent = new Intent(context, PlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlistCreated", playlist);
        intent.putExtras(bundle);
        context.startActivity(intent);
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
        bundle.putInt("code", 1); // code to check if its searching or not
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
