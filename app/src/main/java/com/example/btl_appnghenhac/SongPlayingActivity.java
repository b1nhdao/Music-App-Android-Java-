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

import java.util.ArrayList;
import java.util.Collections;

public class SongPlayingActivity extends AppCompatActivity {

    ImageView img_songImage, iv_back;
    TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd;
    ImageView img_shuffle, img_back, img_play, img_skip, img_loop;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ArrayList<Song> songArrayList;
    int currentSongIndex =  0;
    boolean isLooping = false;
    boolean isShuffling = false;
    private boolean isSingleSongMode = false; // Mặc định là phát từ danh sách
    private ArrayList<Song> shuffledList = new ArrayList<>();

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
        if (bundle != null) {
            songArrayList = (ArrayList<Song>) bundle.getSerializable("songList");
            currentSongIndex = bundle.getInt("currentSongIndex", 0);
            playSong(songArrayList.get(currentSongIndex), false);
        }

        Song song = songArrayList.get(currentSongIndex);

        //not really need it, but still. I mean, i can actually delete it cuz
        //i've already have one in playSong function, but for better UX. I suppose ?
        Glide.with(this)
                .load(song.getSongImageUrl())
                .into(img_songImage);
        tv_songName.setText(song.getSongName());
        tv_songArtist.setText(song.getSongArtistName());
        tv_timeEnd.setText(convertDurationToString(song.getSongDuration()));
        seekBar.setMax(song.getSongDuration());

//        ArrayList<Song> songArrayList = (ArrayList<Song>) bundle.get()

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

        if (songArrayList == null) {
            playSong(song, true); // Chơi bài đầu tiên
        }


        if (songArrayList != null && !songArrayList.isEmpty()) {
            playSong(songArrayList.get(currentSongIndex), false); // Chơi bài đầu tiên
        }

        img_loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLooping){
                    isLooping = false;
                    Toast.makeText(SongPlayingActivity.this, "Tắt lặp", Toast.LENGTH_SHORT).show();
                }
                else{
                    isLooping = true;
                    Toast.makeText(SongPlayingActivity.this, "Lặp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffling){
                    toggleShuffle();
                }
                else{
                    toggleShuffle();
                }
            }
        });

        img_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
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

    private void playSong(Song song, boolean isSingle) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(song.getSongURL());
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();

                Glide.with(this).load(song.getSongImageUrl()).into(img_songImage);
                tv_songName.setText(song.getSongName());
                tv_songArtist.setText(song.getSongArtistName());
                tv_timeEnd.setText(convertDurationToString(song.getSongDuration()));
                seekBar.setMax(mediaPlayer.getDuration());

                updateSeekBar();

                // Xử lý khi bài hát kết thúc
                mediaPlayer.setOnCompletionListener(mp1 -> {
                    if (isLooping) {
                        playSong(song, isSingle);
                    } else if (!isSingleSongMode) {
                        playNextSong();
                    }
                });
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("SongPlayingActivity", "Error playing song", e);
        }
    }

    private void toggleShuffle() {
        isShuffling = !isShuffling;

        if (isShuffling) {
            // Tạo danh sách trộn từ danh sách gốc
            shuffledList = new ArrayList<>(songArrayList);
            Collections.shuffle(shuffledList);
        } else {
            shuffledList.clear(); // Xóa danh sách trộn nếu tắt Shuffle
        }

        Toast.makeText(this, isShuffling ? "Shuffle ON" : "Shuffle OFF", Toast.LENGTH_SHORT).show();
    }

    private void playNextSong() {
        if (isShuffling && !shuffledList.isEmpty()) {
            currentSongIndex = shuffledList.indexOf(songArrayList.get(currentSongIndex));
            currentSongIndex = (currentSongIndex + 1) % shuffledList.size();
            playSong(shuffledList.get(currentSongIndex), false);
        } else {
            currentSongIndex = (currentSongIndex + 1) % songArrayList.size();
            playSong(songArrayList.get(currentSongIndex), false);
        }
    }

    private void playPreviousSong() {
        if (isShuffling && !shuffledList.isEmpty()) {
            currentSongIndex = shuffledList.indexOf(songArrayList.get(currentSongIndex));
            currentSongIndex = (currentSongIndex - 1 + shuffledList.size()) % shuffledList.size();
            playSong(shuffledList.get(currentSongIndex), false);
        } else {
            currentSongIndex = (currentSongIndex - 1 + songArrayList.size()) % songArrayList.size();
            playSong(songArrayList.get(currentSongIndex), false);
        }
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
                handler.postDelayed(this, 1000);
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