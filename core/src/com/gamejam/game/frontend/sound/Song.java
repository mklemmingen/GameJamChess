package com.gamejam.game.frontend.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Song {
    /*
    Song holds a music object and a String combining song name and artist name.
     */

    private final String songName;
    private final String artistName;
    private final Music song;

    public Song(String songName, String artistName, Music song) {
        /*
        Constructor for Song.java
        Takes a songName, artistName, and a Music object.
         */
        Gdx.app.log("Song", "Song with Name" + songName + " and Artist " + artistName + " created");
        this.songName = songName;
        this.artistName = artistName;
        this.song = song;
    }

    public Music getSong() {
        /*
        getSong returns the Music object of the Song.
         */
        return song;
    }

    public String getArtistName() {
        /*
        getArtistName returns the artist name of the Song.
         */
        return artistName;
    }

    public String getSongName() {
        /*
        getSongName returns the song name of the Song.
         */
        return songName;
    }

    public void dispose() {
        /*
        dispose disposes of the Music object of the Song.
         */
        song.dispose();
    }

}
