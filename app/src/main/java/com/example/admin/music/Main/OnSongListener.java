package com.example.admin.music.Main;


public interface OnSongListener {

    //start playing a song
    void onSongStart(Song song);

    void onPlayPause();

    void textSong(Song song);

    void onSongComplete();

}
