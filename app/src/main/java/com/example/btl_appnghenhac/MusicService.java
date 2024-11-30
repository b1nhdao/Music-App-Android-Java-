package com.example.btl_appnghenhac;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.btl_appnghenhac.Object.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    private ArrayList<Song> songList;
    private int currentSongIndex;
    public static final String ACTION_PLAY_PAUSE = "com.example.btl_appnghenhac.ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.example.btl_appnghenhac.ACTION_NEXT";

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MUSIC_CHANNEL",
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void playSong(Song song, int songIndex) {
        this.currentSongIndex = songIndex; // Update current song index
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(song.getSongURL());
            mediaPlayer.prepare();
            mediaPlayer.start();
            createNotification(song);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (completionListener != null) {
                    completionListener.onSongComplete();
                }
            });
        } catch (IOException e) {
            Log.e("MusicService", "Error playing song", e);
        }
    }

    private void createNotification(Song song) {
        Intent notificationIntent = new Intent(this, SongPlayingActivity.class);

        // Pass song list, current index, and code to the Intent
        notificationIntent.putExtra("songList", songList);
        notificationIntent.putExtra("currentSongIndex", currentSongIndex);
        notificationIntent.putExtra("code", 1);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(
                this,
                0,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(
                this,
                0,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, "MUSIC_CHANNEL")
                .setContentTitle(song.getSongName())
                .setContentText(song.getSongArtistName())
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentIntent(pendingIntent) // Launch SongPlayingActivity with current info
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(R.drawable.play1, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.next, "Next", nextPendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY_PAUSE:
                    playPauseMusic();
                    break;
                case ACTION_NEXT:
                    Log.d("MusicService", "Next song action received");
                    playNextSong();
                    break;
            }
        }
        return START_STICKY;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playPauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }


    public void playNextSong() {
        if (songList != null && currentSongIndex < songList.size() - 1) {
            currentSongIndex++;
            playSong(songList.get(currentSongIndex), currentSongIndex);
        }
    }

    public void playPreviousSong() {
        if (songList != null && currentSongIndex > 0) {
            currentSongIndex--;
            playSong(songList.get(currentSongIndex), currentSongIndex);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    private OnCompletionListener completionListener;

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.completionListener = listener;
    }

    public interface OnCompletionListener {
        void onSongComplete();
    }

    public Song getCurrentSong() {
        return songList != null && currentSongIndex >= 0 && currentSongIndex < songList.size() ? songList.get(currentSongIndex) : null;
    }


}