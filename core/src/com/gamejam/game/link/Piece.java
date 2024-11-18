package com.gamejam.game.link;

public enum Piece {
    /*
     * This enum is used to store the different types of pieces that can be used in the game
     */

    KING, // moves one square in any direction
    QUEEN, // moves any number of squares in any direction
    BISHOP, // moves any number of squares diagonally
    KNIGHT, // moves in an L shape
    ROOK, // moves any number of squares horizontally or vertically
    PAWN, // moves one square forward, but captures diagonally
    PROGRAMMER, // moves two squares in any direction, but hits only one square away
    CHANCELLOR, // moves like a king, but hits like a rook
    OBSTACLE // cannot move to that one
}
