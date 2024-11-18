package com.example.btl_appnghenhac.Object;

import java.io.Serializable;

public class Song implements Serializable {
    int songID;
    String songName;
    String songArtistName;
    boolean songFavourite;
    int songCounter;
    int songDuration;
    String songImageUrl;
    String songURL;


    public Song(){

    }

    public String getSongImageUrl() {
        return songImageUrl;
    }

    public void setSongImageUrl(String songImageUrl) {
        this.songImageUrl = songImageUrl;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(int songDuration) {
        this.songDuration = songDuration;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtistName() {
        return songArtistName;
    }

    public void setSongArtistName(String songArtistName) {
        this.songArtistName = songArtistName;
    }

    public boolean isSongFavourite() {
        return songFavourite;
    }

    public void setSongFavourite(boolean songFavourite) {
        this.songFavourite = songFavourite;
    }

    public int getSongCounter() {
        return songCounter;
    }

    public void setSongCounter(int songCounter) {
        this.songCounter = songCounter;
    }
}
