package com.example.btl_appnghenhac;

import android.os.Bundle;
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

import com.example.btl_appnghenhac.Object.Song;

public class SongPlayingActivity extends AppCompatActivity {

    ImageView img_songImage, iv_back;
    TextView tv_songName, tv_songArtist, tv_timeCurrent, tv_timeEnd;
    ImageView img_shuffle, img_back, img_play, img_skip, img_loop;
    SeekBar seekBar;

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
        img_songImage.setImageResource(song.getSongImage());
        tv_songName.setText(song.getSongName());
        tv_songArtist.setText(song.getSongArtistName());
        tv_timeEnd.setText(convertDurationToString(song.getSongDuration()));
        seekBar.setMax(song.getSongDuration());

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test done
                tv_timeCurrent.setText(convertDurationToString(seekBar.getProgress()));
            }
        });

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