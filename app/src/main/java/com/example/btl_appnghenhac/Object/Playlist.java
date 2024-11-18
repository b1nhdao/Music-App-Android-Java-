package com.example.btl_appnghenhac.Object;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    int playlistID;
    int playlistImage;
    String playlistName;
    String playlistUrl;
    private ArrayList<Integer> song;


    public Playlist(int playlistID, int playlistImage, String playlistName) {
        this.playlistID = playlistID;
        this.playlistImage = playlistImage;
        this.playlistName = playlistName;
    }

    public Playlist(int playlistID, int playlistImage, String playlistName, String playlistUrl) {
        this.playlistID = playlistID;
        this.playlistImage = playlistImage;
        this.playlistName = playlistName;
        this.playlistUrl = playlistUrl;
    }

    public Playlist(int id, String name, String image, ArrayList<Integer> songs) {
        this.playlistID = id;
        this.playlistName = name;
        this.playlistUrl = image;
        this.song = songs;
    }

    public Playlist(){

    }

    public ArrayList<Integer> getSong() {
        return song;
    }

    public void setSong(ArrayList<Integer> song) {
        this.song = song;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public int getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(int playlistID) {
        this.playlistID = playlistID;
    }

    public int getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(int playlistImage) {
        this.playlistImage = playlistImage;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
