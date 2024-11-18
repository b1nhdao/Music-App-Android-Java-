package com.example.btl_appnghenhac;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.btl_appnghenhac.Object.Song;

public class SongPlayingActivity extends AppCompatActivity {

    ImageView img_songImage, iv_back;
    TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd;
    ImageView img_shuffle, img_back, img_play, img_skip, img_loop;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    boolean isStreaming = false;

    private Handler handler = new Handler(); // Dùng để cập nhật SeekBar
    private Runnable updateSeekBar; // Runnable để cập nhật SeekBar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_playing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getViews();
        iv_back.setVisibility(View.VISIBLE);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //temporary just for better navigation
                //HAVE TO CHANGE THIS CUZ, FINISH = NO MUSIC, technically :D
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        Song song = (Song) bundle.get("songObject");
        Glide.with(this)
                .load(song.getSongImageUrl())
                .into(img_songImage);
        tv_songName.setText(song.getSongName());
        tv_songArtist.setText(song.getSongArtistName());
        tv_timeEnd.setText(convertDurationToString(song.getSongDuration()));
        seekBar.setMax(song.getSongDuration());

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // Tạm dừng nếu nhạc đang phát
                    mediaPlayer.pause();
                    img_play.setImageResource(R.drawable.play2); // Đổi icon về biểu tượng "Play"
                    Toast.makeText(SongPlayingActivity.this, "Paused", Toast.LENGTH_SHORT).show();
                } else {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        startAudioStream(song.getSongURL());
                    } else {
                        // Tiếp tục phát nếu nhạc đang tạm dừng
                        mediaPlayer.start();
                    }
                    img_play.setImageResource(R.drawable.play1); // Đổi icon về biểu tượng "Pause"
                    Toast.makeText(SongPlayingActivity.this, "Playing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress); // Tua đến vị trí mới
                    tv_timeCurrent.setText(convertDurationToString(progress / 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tạm dừng cập nhật khi người dùng kéo SeekBar
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tiếp tục cập nhật sau khi người dùng thả SeekBar
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    handler.post(updateSeekBar);
                }
            }
        });
    }

    public void startAudioStream(String url){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        try {
            Log.d("mylog", "Playing: " + url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f,1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e){
            Log.d("mylog", "Error playing in SoundHandler: " + e.toString());
        }
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar();
    }

    private void updateSeekBar() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    tv_timeCurrent.setText(convertDurationToString(mediaPlayer.getCurrentPosition() / 1000));
                }
                handler.postDelayed(this, 1000); // Cập nhật mỗi giây
            }
        };
        handler.post(updateSeekBar);
    }


    private void stopPlaying(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
        }
    }

    public String convertDurationToString(int duration){
        int minutes = duration / 60;
        int seconds = duration % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    public void getViews(){
        iv_back = findViewById(R.id.iv_back);
        img_songImage = findViewById(R.id.img_songImage);
        tv_songName = findViewById(R.id.tv_songName);
        tv_songArtist = findViewById(R.id.tv_songArtist);
        tv_timeCurrent = findViewById(R.id.tv_timeCurrent);
        tv_timeEnd = findViewById(R.id.tv_timeEnd);
        img_shuffle = findViewById(R.id.img_shuffle);
        img_back = findViewById(R.id.img_back);
        img_play = findViewById(R.id.img_play);
        img_skip = findViewById(R.id.img_skip);
        img_loop = findViewById(R.id.img_loop);
        seekBar = findViewById(R.id.seekBar);
    }
}