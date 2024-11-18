package com.gamejam.game.link;

import com.badlogic.gdx.Gdx;

public class Board {
    /*
     * Board.java is the object for the chess board
     */

    // The Board is a 2D Array of Tiles
    private Tile[][] board;

    public Board(Tile[][] board) {
        /*
         * Constructor for the Board object
         * Initializes the board with the starting positions of the pieces
         */
        this.board = board;
    }

    public Tile[][] getGameBoard(){
        return board;
    }

    /*
     * update method is used to move a piece from one coordinate to another, leaving behind an empty tile
     * returns true if the move was successful, false otherwise.
     * Does not check game logic, only if within 8x8 and moves one tile to another.
     */
    public boolean updateWithEmptyBehind(Coordinate start,
                                 Coordinate end) {

        int sx = start.getX();
        int sy = start.getY();

        int ex = end.getX();
        int ey = end.getY();

        logBegin(sx, sy, ex, ey);

        // check
        if(!(isWithinBoundaries(true, sx, sy))) {
            return false;
        }
        if(!(isWithinBoundaries(false, ex, ey))) {
            return false;
        }

        board[ex][ey] = board[sx][sy];
        board[sx][sy] = new Tile();

        logEnd(sx, sy, ex, ey);

        return true;
    }

    /*
     * update method is used to exchanged one position with another
     * returns true if the move was successful, false otherwise.
     * Does not check game logic, only if within 8x8 and moves one tile to another.
     */
    public boolean updateByExchange(Coordinate start,
                                      Coordinate end) {

        int sx = start.getX();
        int sy = start.getY();

        int ex = end.getX();
        int ey = end.getY();

       logBegin(sx, sy, ex, ey);

        // check
        if(isWithinBoundaries(true, sx, sy)) {
            return false;
        }
        if(!(isWithinBoundaries(false, ex, ey))) {
            return false;
        }

        Tile temp = board[ex][ey];
        board[ex][ey] = board[sx][sy];
        board[sx][sy] = temp;

        logEnd(sx, sy, ex, ey);

        return true;
    }

    private void logBegin(int sx, int sy, int ex, int ey) {
        Gdx.app.log("Board", "The Board is being updated with the following coordinates: " +
                sx + " " + sy + " : " + ex + " " + ey);

        if(board[ex][ey].getPieceType() == null) {
            Gdx.app.log("Board", "The piece at the end is null");
        } else {
            Gdx.app.log("Board", "BC: Start: " + board[sx][sy].getPieceType().name()
                    + " End:" + board[ex][ey].getPieceType().name());
        }
    }

    private void logEnd(int sx, int sy, int ex, int ey) {
        Gdx.app.log("Board", "The Board has been updated with the following coordinates: " +
                sx + " " + sy + " : " + ex + " " + ey);

        if(board[ex][ey].getPieceType() == null) {
            Gdx.app.log("Board", "The piece at the end is null");
        } else if (board[sx][sy].getPieceType() == null) {
            Gdx.app.log("Board", "The piece at the start is null");
        } else {
            Gdx.app.log("Board", "AC: Start: " + board[sx][sy].getPieceType().name()
                    + " End: " + board[ex][ey].getPieceType().name());
        }
    }

    // checking if the coordinates are within bounds
    private boolean isWithinBoundaries(boolean isStart, int x, int y) {
        String type = isStart ? "Start" : "End";
        if(x >= 0 && x < 8 && y >= 0 && y < 8) {
            Gdx.app.log("Board", "The " + type + " coordinates are within boundaries");
            return true;
        } else {
            Gdx.app.log("Board", "The " + type + " coordinates are outside boundaries");
            return false;
        }
    }

    public void printBoard() {
        // printing in ascii art, with the first letter of a piece
        System.out.println("----- Printing the board -----");
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (board[j][i].getPieceType() == null){
                    System.out.print(" ");
                } else {
                    System.out.print(board[j][i].getPieceType().name().charAt(0));
                }
            }
            System.out.println();
        }
        System.out.println("----- End of board -----");
    }
}

