package com.gamejam.game.frontend.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gamejam.game.GameJam;

public class LoadingScreenStage extends Stage {

    private float loadingElapsed = 0;
    private boolean endLoadingScreen = false;
    private Sound loadingSound;
    private Image loadingLogo;
    private SpriteBatch batch;

    public LoadingScreenStage(Viewport viewport, SpriteBatch batch) {
        super(viewport);
        this.batch = batch;

        Gdx.app.log("LoadingScreen", "LoadingScreen initializing");

        float tileSize = GameJam.getTileSize();

        Gdx.app.log("LoadingScreen", "Loading gamejamlogo");
        loadingLogo = new Image(new Texture(Gdx.files.internal("gamejamlogo.png")));
        loadingLogo.setSize(tileSize * 4, tileSize * 4);
        loadingLogo.setPosition((float) Gdx.graphics.getWidth() / 2 - loadingLogo.getWidth()/2,
                (float) Gdx.graphics.getHeight() / 2 - loadingLogo.getHeight()/2);

        // add the loadingLogo to the stage
        addActor(loadingLogo);
        // play the loading screen jingle
        playLoadingScreenJingle();

        Gdx.app.log("LoadingScreen", "LoadingScreen initialized");
    }

    public void endLoadingScreen() {
        endLoadingScreen = true;
    }

    @Override
    public void draw() {
        batch.begin();
        super.draw();
        batch.end();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        loadingElapsed += delta;
        // run loading screen for 4 seconds at least
        if (loadingElapsed > 4) {
            startMenu();
        }
    }

    private void startMenu() {
        GameJam.createMainMenuStage();
        Gdx.app.log("LoadingScreen", "LoadingScreen finished");
        loadingLogo.clear();
        clear();
        dispose();
    }

    public void playLoadingScreenJingle() {

        loadingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gamejamjingle.mp3"));
        Gdx.app.log("GameJam", "Loading Screen Jingle played");
        loadingSound.play(GameJam.getVolume());
    }
}

