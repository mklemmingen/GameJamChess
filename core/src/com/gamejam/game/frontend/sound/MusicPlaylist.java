package com.gamejam.game.frontend.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.gamejam.game.GameJam;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MusicPlaylist {
    /*
     * MusicPlaylist.java is the object for the music playlist in the game Boom Chess.
     * It holds the playlist of music and the methods to play, pause, resume, and dispose of the music.
     */
    private final List<Song> songs;
    private int currentIndex = 0; // Initialize to -1 to indicate no song has been played yet
    private final LinkedList<Integer> lastIndex = new LinkedList<>();
    private boolean hasStartedOnce = false;

    public MusicPlaylist() {
        /*
        * Constructor for MusicPlaylist.java
        * does not take any arguments.
        */
        songs = new ArrayList<>();
    }

    public void addSong(String fileName, String songName, String artistName) {
        /*
         * addSong adds a song, given a fileName/Direction to a Music Object, to the playlist.
         * Adds a listener to go to the next song when the current song is finished.
         */
        Music song = Gdx.audio.newMusic(Gdx.files.internal(fileName));
        song.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                nextSong();
            }
        });
        songs.add(new Song(songName, artistName, song));

        Gdx.app.log("Playlist", "Song " + songName + " by " + artistName + " added to playlist");
    }

    public void play() {

        songs.get(currentIndex).getSong().stop();

        int randomIndex;
        if(songs.size() == 1){
            // if there is only one song in the playlist, then it is played,
            // prevents endless loop
            randomIndex = 0;
        }
        else {
            if(!hasStartedOnce){
                // if the playlist has not been played before, then the currentIndex is set to
                // a random index with 0 being included in the range of random numbers
                randomIndex = new Random().nextInt(songs.size());
            } else {

                if(songs.size() >= 2) {
                    // add currentIndex to the first spot of the linked-list and cut the linkedlist to
                    // the size of ArrayList.size/2
                    lastIndex.addFirst(currentIndex);
                    if (lastIndex.size() > songs.size() / 2) {
                        lastIndex.removeLast();
                    }
                }

                // create a list of all index int that are not in the linked-list
                List<Integer> notInList = new ArrayList<>();
                for (int i = 0; i < songs.size(); i++) {
                    if (!lastIndex.contains(i)) {
                        notInList.add(i);
                    }
                }

                // if the playlist has been played before, then the currentIndex
                // has been set to all numbers not currently currentIndex or in the Linked-List
                // through the use of the notInList ArrayList

                // choose a random index from the notInList ArrayList
                randomIndex = notInList.get(new Random().nextInt(notInList.size()));
            }

            Gdx.app.log("Playlist", "Random index: " + randomIndex +
                    " Current Song: " + getCurrentSong().getSongName());
        }

        hasStartedOnce = true;

        currentIndex = randomIndex;
        Music playSong = songs.get(currentIndex).getSong();

        AddToStage();

        playSong.play();
        playSong.setVolume(GameJam.getVolume());
    }

    private void AddToStage() {
        /*
         * adds the song name to the music label
         */

        GameJam.getMusicLabel().setText(songs.get(currentIndex).getSongName() + "\n" + "by " + "'" +
                songs.get(currentIndex).getArtistName() + "'");
    }


    public void pause() {
        if (!songs.isEmpty()) {
            songs.get(currentIndex).getSong().pause();
        }
    }

    public void resume() {
        if (!songs.isEmpty()) {
            songs.get(currentIndex).getSong().play();
        }
    }

    public void nextSong() {
        /*
         * nextSong goes to the next song in the playlist.
         * Responds to the isLooping variable
         */

        songs.get(currentIndex).getSong().stop();
        play();
    }

    public void stop() {
        if (!songs.isEmpty()) {
            songs.get(currentIndex).getSong().stop();
        }
    }

    public void setVolume(float volume){
        // can take but must not take a int value, if there is one, it is taken as the volume for setVolume
        if (!songs.isEmpty()) {
            songs.get(currentIndex).getSong().setVolume(volume);
        }
        if(volume==0){
            // hide the musiclabel by making it invisible
            GameJam.getMusicLabel().setVisible(false);
        } else {
            // show the musiclabel by making it visible
            GameJam.getMusicLabel().setVisible(true);
        }
    }

    public void setLooping(boolean isLooping){
        /*
        * setLooping never lets the playlist end. It loops the playlist.
         */

    }

    public boolean isPlaying() {
    /*
    * isPlaying returns a boolean value of whether a song is playing.
    */
        if (!songs.isEmpty()) {
            return songs.get(currentIndex).getSong().isPlaying();
        }
        return false;
    }

    public void dispose() {
        for (Song song : songs) {
            song.dispose();
        }
    }

    public Song getCurrentSong(){
        return songs.get(currentIndex);
    }
}
