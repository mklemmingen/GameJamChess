package com.gamejam.game.frontend.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.gamejam.game.GameJam;

import static com.gamejam.game.GameJam.switchToStage;

public class MenuStage extends Stage{

    public MenuStage() {

        Gdx.app.log("MenuStage", "MenuStage initializing");

        // Begin of Main Menu Layout - Root Table arranges content automatically and adaptively as ui-structure
        final Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        Skin skin = GameJam.getSkin();
        float tileSize = GameJam.getTileSize();

        TextButton play2Button = new TextButton("Play 2 Player Game", skin);
        root.add(play2Button).padBottom(tileSize/4);

        play2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStage gameStage = new GameStage();
                switchToStage(gameStage);
            }
        });
        root.row();

        TextButton exitButton = new TextButton("Exit", skin);
        root.add(exitButton).padBottom(tileSize/4).padRight(tileSize/4);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // for exiting the game
                Gdx.app.exit();
                // for ending all background activity on Windows systems specifically
                System.exit(0);
            }
        });
        root.row();

        GameJam.addSoundAndSettingButtons(this);

       Gdx.app.log("MenuStage", "MenuStage initialized");
    }

}
