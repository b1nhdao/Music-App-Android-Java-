package com.example.btl_appnghenhac.Object;

public class Playlist {
    int playlistID;
    int playlistImage;
    String playlistName;

    public Playlist(int playlistID, int playlistImage, String playlistName) {
        this.playlistID = playlistID;
        this.playlistImage = playlistImage;
        this.playlistName = playlistName;
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
