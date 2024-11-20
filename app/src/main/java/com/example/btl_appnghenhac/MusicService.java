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
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.btl_appnghenhac.Object.Song;

import java.util.ArrayList;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songList;
    private int currentSongIndex;
    private final IBinder binder = new LocalBinder();
    private static final String CHANNEL_ID = "MusicServiceChannel";

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void setSongList(ArrayList<Song> songs) {
        this.songList = songs;
    }

    public void playSong(int songIndex) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        currentSongIndex = songIndex;

        Song song = songList.get(songIndex);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(song.getSongURL());
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

//            showNotification(song);
        } catch (Exception e) {
            Log.e("MusicService", "Error playing song", e);
        }
    }

    public void playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        playSong(currentSongIndex);
    }

    public void playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        playSong(currentSongIndex);
    }

    public void pauseOrResume() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    private void showNotification(Song song) {
        Intent intent = new Intent(this, SongPlayingActivity.class);
        intent.putExtra("songList", songList);
        intent.putExtra("currentSongIndex", currentSongIndex);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(song.getSongName())
                .setContentText(song.getSongArtistName())
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.back, "Previous", getActionIntent("PREVIOUS"))
                .addAction(mediaPlayer.isPlaying() ? R.drawable.play1 : R.drawable.play2, "Pause/Play", getActionIntent("TOGGLE"))
                .addAction(R.drawable.next, "Next", getActionIntent("NEXT"))
                .build();

//        startForeground(1, notification);
    }

    private PendingIntent getActionIntent(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PREVIOUS":
                    playPreviousSong();
                    break;
                case "TOGGLE":
                    pauseOrResume();
                    break;
                case "NEXT":
                    playNextSong();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
