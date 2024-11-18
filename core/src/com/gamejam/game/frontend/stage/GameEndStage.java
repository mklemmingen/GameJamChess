package com.gamejam.game.frontend.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GameEndStage extends Stage{

    /*
    * GameEndstage is a stage that takes a tileSize and a winnerTeamColour as input and returns a stage that
    * says the game has ended and the winner team colour with a little sentence.
     */
    public static Stage initializeUI(String winnerTeamColour, float tileSize, Skin skin){

        Gdx.app.log("GameEnd", "The GameEndStage is being constructed");

        Stage gameEndStage = new Stage();

        // Begin of GameEndLayout - Root Table arranges content automatically and adaptively as ui-structure
        final Table endRoot = new Table();
        endRoot.setFillParent(true);

        String shoutout = "The Game has Ended.\n";

        /*
        * Label to be created with WinnerTeamColour in mind, White Background and black font
         */
        TextButton winnerLabel = new TextButton("The " + winnerTeamColour + " Team won\n" + shoutout, skin);
        winnerLabel.setColor(Color.BLACK);
        endRoot.add(winnerLabel).padBottom(tileSize /4);
        endRoot.row();
        gameEndStage.addActor(endRoot);

        Gdx.app.log("GameEnd", "The GameEndStage has been constructed and is being returned");

        return gameEndStage;
    }
}
