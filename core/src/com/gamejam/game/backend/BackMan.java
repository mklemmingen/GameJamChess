package com.gamejam.game.backend;

import com.gamejam.game.link.*;

public class BackMan implements FrontendZugriff {
    /*
        * This class implements the interface linking front to backend.
     */

    // ATTRIBUTES --------------------------------------------------------



    // METHODS -----------------------------------------------------------

    public BackMan() {
        /*
         * Constructor for the BackendManager
         */

    }

    /*
        * This method is used to start the game. Sets up all the needed Attributes for a fresh game Start.
     */
    @Override
    public void startGame() {
    }

    /*
     * This method is used to get the board from the backend, it's used in the frontend to display the current board
     * If you can, give a clone object of the board, so the frontend can't change the backend board.
     */
    @Override
    public Board getBoard() {

        return null;
    }

    /*
     * This method is used to get the current game state from the backend, it's used in the frontend to make sure
     * it's in the same state as the backend.
     * If you can, give a clone object of the GameState, so the frontend can't change the backend GameState.
     */
    @Override
    public GameState getGameState() {
        return null;
    }

    /*
     * This method is used to see if a move a Player has made is allowed. It's used in the frontend to make sure
     * that the backend controls whether a move would be allowed or not
     * returns: false if the move is not allowed, true if it is
     */
    @Override
    public boolean isValidMove(Coordinate start, Coordinate end) {
        return false;
    }

    /*
    * This method is used to move a piece on the board. Used by the frontend to move a piece of a coordinate
    * to another coordinate.
    * returns: false if no hit has taken place, true if a hit has taken place
     */
    @Override
    public boolean movePiece(Coordinate start, Coordinate end) {
        return false;
    }
}
