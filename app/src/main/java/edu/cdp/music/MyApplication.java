package edu.cdp.music;

import android.app.Application;

import java.util.List;

import edu.cdp.music.bean.Song;

public class MyApplication extends Application {
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
