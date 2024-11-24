package com.example.btl_appnghenhac.Object;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistCreated implements Serializable {
    int playlistIDc;
    String playlistNamec;
    String playlistUrlc;
    private ArrayList<Integer> song;

    public PlaylistCreated(){

    }


    public PlaylistCreated(int playlistIDc, String playlistNamec, String playlistUrlc, ArrayList<Integer> songc) {
        this.playlistIDc = playlistIDc;
        this.playlistNamec = playlistNamec;
        this.playlistUrlc = playlistUrlc;
        this.song = song;
    }


    public int getPlaylistIDc() {
        return playlistIDc;
    }

    public void setPlaylistIDc(int playlistIDc) {
        this.playlistIDc = playlistIDc;
    }

    public String getPlaylistNamec() {
        return playlistNamec;
    }

    public void setPlaylistNamec(String playlistNamec) {
        this.playlistNamec = playlistNamec;
    }

    public String getPlaylistUrlc() {
        return playlistUrlc;
    }

    public void setPlaylistUrlc(String playlistUrlc) {
        this.playlistUrlc = playlistUrlc;
    }

    public ArrayList<Integer> getSong() {
        return song;
    }

    public void setSong(ArrayList<Integer> song) {
        this.song = song;
    }
}
