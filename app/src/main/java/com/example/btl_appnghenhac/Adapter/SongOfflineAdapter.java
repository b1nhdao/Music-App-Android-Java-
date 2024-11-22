package com.example.btl_appnghenhac.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appnghenhac.R;
import com.example.btl_appnghenhac.SongPlayingOfflineActivity;

import java.util.ArrayList;
import java.util.List;

public class SongOfflineAdapter extends RecyclerView.Adapter<SongOfflineAdapter.ViewHolder> {

    private Context context;
    private List<String> audioUris;

    public SongOfflineAdapter(Context context, List<String> audioUris) {
        this.context = context;
        this.audioUris = audioUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String audioUri = audioUris.get(position);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(context, Uri.parse(audioUri));

        String titleText = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        byte[] artBytes = metadataRetriever.getEmbeddedPicture();

        holder.textView.setText(titleText != null ? titleText : "Unknown Title");

        String artistName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        holder.textview2.setText(artistName);
        holder.FAV.setVisibility(View.INVISIBLE);
        if (artBytes != null) {
            holder.imageView.setImageBitmap(android.graphics.BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length));
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_music_note_24);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongPlayingOfflineActivity.class);

            intent.putExtra("TITLE", titleText != null ? titleText : "Unknown Title");
            intent.putExtra("ARTIST", artistName);
            intent.putExtra("ALBUM_ART", artBytes);
            intent.putExtra("AUDIO_URI", audioUri);
            intent.putExtra("currentSongIndex", position);
            intent.putStringArrayListExtra("songList", new ArrayList<>(audioUris)); // Pass the entire song list
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return audioUris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, FAV;
        TextView textView, textview2;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            imageView = itemView.findViewById(R.id.img_song);
            textView = itemView.findViewById(R.id.tv_songName);
            textview2 = itemView.findViewById(R.id.tv_songArtist);
            FAV = itemView.findViewById(R.id.img_favourite);
        }
    }
}
