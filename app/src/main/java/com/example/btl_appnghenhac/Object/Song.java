package com.example.btl_appnghenhac.Object;

import java.io.Serializable;

public class Song implements Serializable {
    int songID;
    int songImage;
    String songName;
    String songArtistName;
    int songAlbumID;
    int songPlaylistID;
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

    public Song(int songID, String songName, String songArtistName, boolean songFavourite, int songDuration, String songImageUrl, String songURL) {
        this.songID = songID;
        this.songName = songName;
        this.songArtistName = songArtistName;
        this.songFavourite = songFavourite;
        this.songDuration = songDuration;
        this.songImageUrl = songImageUrl;
        this.songURL = songURL;
    }

    public Song(int songID, int songImage, String songName, String songArtistName, int songAlbumID, int songPlaylistID, boolean songFavourite, int songCounter, int songDuration) {
        this.songID = songID;
        this.songImage = songImage;
        this.songName = songName;
        this.songArtistName = songArtistName;
        this.songAlbumID = songAlbumID;
        this.songPlaylistID = songPlaylistID;
        this.songFavourite = songFavourite;
        this.songCounter = songCounter;
        this.songDuration = songDuration;
    }

    public Song(int songID, int songImage, String songName, String songArtistName, int songDuration) {
        this.songID = songID;
        this.songImage = songImage;
        this.songName = songName;
        this.songArtistName = songArtistName;
        this.songDuration = songDuration;
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

    public int getSongImage() {
        return songImage;
    }

    public void setSongImage(int songImage) {
        this.songImage = songImage;
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

    public int getSongAlbumID() {
        return songAlbumID;
    }

    public void setSongAlbumID(int songAlbumID) {
        this.songAlbumID = songAlbumID;
    }

    public int getSongPlaylistID() {
        return songPlaylistID;
    }

    public void setSongPlaylistID(int songPlaylistID) {
        this.songPlaylistID = songPlaylistID;
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
