package com.gamejam.game.link;

public interface FrontendZugriff {
    /*
    This Interface is implemented by the BackendManager, its method called by GameJam.java.
    The methods are used to interact between front- and backend.
     */

    // method to start game (cleans the board and sets up a new one)
    void startGame();

    // method to get current board, returns a Board object ith a TILE[][] attribute
    Board getBoard();

    // method to get current GameState, WHITE_TURN, BLACK_TURN, NOT_IN_GAME
    GameState getGameState();

    // check if a move is valid, true valid, false invalid
    boolean isValidMove(Coordinate start, Coordinate end);

    // method to move a piece, true hit, false no hit
    boolean movePiece(Coordinate start, Coordinate end);
}
