package com.example.admin.music.Main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.admin.music.R;
import com.example.admin.music.Service.MusicService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, OnSongListener {

    private TextView mText_SongName, mText_PlayingTime, mText_SongLength;
    private SeekBar mSeekBar_Song;
    private ImageButton mImagebutton_Previous, mImagebutton_Play, mImagebutton_Next;
    private MusicService musicService;
    private boolean isBound = false;
    private String divide = " - ";
    public static final String TAG = "empty";
    SimpleDateFormat timeSong = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find view by id
        findID();

        //set on click
        mImagebutton_Next.setOnClickListener(this);
        mImagebutton_Play.setOnClickListener(this);
        mImagebutton_Previous.setOnClickListener(this);
        //seekbar on change
        mSeekBar_Song.setOnSeekBarChangeListener(this);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("pos", 0);

        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: ");
    }

    //button onClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_next:
                musicService.nextMusic();
                mSeekBar_Song.setProgress(0);
                break;

            case R.id.button_play:
                musicService.playPause();
                updateTimeSong();
                break;
            case R.id.button_previous:
                musicService.previousMusic();
                mSeekBar_Song.setProgress(0);
                break;
        }
    }

    // start service
    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBound = true;
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            musicService = binder.getService();
            musicService.setOnSongListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }


    //on seekbar change listener

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //seek to another possition in the song
        try {
            musicService.getMediaPlayer().seekTo(mSeekBar_Song.getProgress());
        } catch (Exception e) {
            mSeekBar_Song.setProgress(0);
        }
    }

    //set text for song's time and set max for seekbar
    @Override
    public void onSongStart(Song song) {
        mText_SongLength.setText(timeSong.format(musicService.getMediaPlayer().getDuration()));
        mSeekBar_Song.setMax(musicService.getMediaPlayer().getDuration());
        onPlayPause();
    }

    //check the playing state of music service
    @Override
    public void onPlayPause() {
        if (musicService.getMediaPlayer().isPlaying()) {
            mImagebutton_Play.setImageResource(R.drawable.pausee);
        } else {
            mImagebutton_Play.setImageResource(R.drawable.play);
        }
    }

    //set text for song's name
    @Override
    public void textSong(Song song) {
        mText_SongName
                .setText(String.format("%s%s%s", song.getmName(), divide, song.getmArtist()));
    }

    //when a song finishes
    @Override
    public void onSongComplete() {
        mSeekBar_Song.setProgress(0);
        updateTimeSong();
    }

    //update time for mTextPlayingTime and seekbar
    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mText_PlayingTime
                        .setText(timeSong
                                .format(musicService.getMediaPlayer().getCurrentPosition()));
                mSeekBar_Song
                        .setProgress(musicService.getMediaPlayer().getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    //find view by id
    private void findID() {
        mText_SongName = findViewById(R.id.text_songName);
        mText_PlayingTime = findViewById(R.id.text_playing);
        mText_SongLength = findViewById(R.id.text_length);
        mSeekBar_Song = findViewById(R.id.seekbar_musicSeekBar);
        mImagebutton_Previous = findViewById(R.id.button_previous);
        mImagebutton_Play = findViewById(R.id.button_play);
        mImagebutton_Next = findViewById(R.id.button_next);
    }

}
