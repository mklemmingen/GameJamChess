package com.gamejam.game.link;

import com.badlogic.gdx.Gdx;

public class Tile {
    /*
     * Tile.java is the object for the individual tiles on the board
     */
    private Piece pieceType;
    private TeamColor teamColor;

    public Tile(Piece pieceType, TeamColor teamColor) {
        /*
         * Constructor for the Tile object, takes Piece and team color argument
         * If empty, teamColor doesn't matter
         */
        this.pieceType = pieceType;
        this.teamColor = teamColor;
    }

    public Tile(){
        /*
         * Overloaded constructor for an empty tile object
         */
        this.pieceType = null;
        this.teamColor = null;
    }

    public Piece getPieceType() {
        /*
         * Getter method for the pieceType
         */
        return pieceType;
    }

    public TeamColor getTeamColor() {
        /*
         * Getter method for the teamColor
         */
        return teamColor;
    }
}
