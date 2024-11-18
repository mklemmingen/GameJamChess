package com.gamejam.game.frontend.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gamejam.game.GameJam;
import com.gamejam.game.backend.BackMan;
import com.gamejam.game.frontend.actors.OneSecondsSelfDestruct;
import com.gamejam.game.link.*;

import static com.gamejam.game.GameJam.*;

public class GameStage extends Stage{

    private BackMan backend;
    Tile[][] gameBoardArray;

    private Table rootBoard; // rootTable for the gameBoard

    private Image board; // the board image
    private Image decovertical; // the vertical decoration
    private Image decohorizontal; // the horizontal decoration
    private Image splatterBottom;
    private Image splatterTop;

    // ----- DataFIllUpHitPieces ---------------------------------------------------------------------------------------

    private int pieceCounter;
    private int counter;

    // ----- Player Data -----------------------------------------------------------------------------------------------

    // player 1 data
    private Table player1stats;
    private Image player1WHITElogo;
    private Array<Piece> lostPieces1Data;

    // player 2 data
    private Table player2stats;
    private Image player2BLACKlogo;
    private Array<Piece> lostPieces2Data;

    // ----- Asset Variables -------------------------------------------------------------------------------------------

    private Texture impossibleMoveLogo;
    private  Texture empty;
    private  Texture obstacle;

    // white pieces ---------------------------------------------------------------------------------
    private  Texture whitePawn;
    private  Texture whiteRook;
    private  Texture whiteKnight;
    private  Texture whiteBishop;
    private  Texture whiteQueen;
    private  Texture whiteKing;
    private  Texture whiteProgrammer;
    private  Texture whiteChancellor;

    // black pieces ---------------------------------------------------------------------------------
    private  Texture blackPawn;
    private  Texture blackRook;
    private  Texture blackKnight;
    private  Texture blackBishop;
    private  Texture blackQueen;
    private  Texture blackKing;
    private  Texture blackProgrammer;
    private  Texture blackChancellor;

    // sounds ---------------------------------------------------------------------------------------
    private Sound hitSound;

    public GameStage() {
        Gdx.app.log("GameStage", "Creating GameStage");

        Gdx.app.log("Game", "Starting Game");
        backend = GameJam.getBackend();
        backend.startGame();
        if(backend.getBoard() == null){
            Gdx.app.log("Game", "backend.getBoard() is null after startGame called");
        }
        Gdx.app.log("Game", "Game Started");

        loadAllAssets();

        Gdx.app.log("GameStage", "visually creating the board game with pieces");
        rootBoard = new Table(); // create rootTable
        addActor(rootBoard); // add rootTable to gameStage
        addActor(buttonsTable(getTileSize())); // add the buttonsTable to the gameStage
        GameJam.addSoundAndSettingButtons(this); // add the sound and setting buttons to the stage

        // at the board image in the horizontal center
        addBoardVisualisation();

        // first call of updateVisualisePlayerData
        intialisePlayerData();
        updateVisualisePlayerData();

        updateGameStage(); // first update of the gameStage
    }

    // ----- Game Methods ----------------------------------------------------------------------------------------------

    /*
     * Method to create the game stage, each actor is added to the stage and the stage is returned
     */
    private void updateGameStage() {
        Gdx.app.log("GameStage", "Updating the GameStage");

        // ----- getting and setting all info from GameJam we need to create the current iteration of the board

        // update backend
        backend = GameJam.getBackend();

        // update board
        gameBoardArray = backend.getBoard().getGameBoard();

        // print out the board for debugging
        backend.getBoard().printBoard();

        if(gameBoardArray == null){
            Gdx.app.log("Game", "The Tile[][] inside Board is null");
            Gdx.app.log("ERROR", "You cant see any pieces now!");
            return;
        }

        // getting GameState and setting the playerLogo to have a slight breathing animation to indicate who's turn
        GameState state = backend.getGameState();
        if (state == GameState.WHITE_TURN) {
            player2BLACKlogo.clearActions();
            player1WHITElogo.addAction(getBreathingAction(player1WHITElogo));
        } else {
            player1WHITElogo.clearActions();
            player2BLACKlogo.addAction(getBreathingAction(player2BLACKlogo));
        }

        // -----------------------------------------------------------------------------

        float tileSize = getTileSize();
        Table root = new Table();
        // for the size of the tiles
        int numRows = getNumberRows();
        int numColumns = getNumberColumns();

        // root of size numRows to numColumns and center of screen
        root.setSize(numColumns * tileSize, numRows * tileSize);
        root.setPosition((float) Gdx.graphics.getWidth() / 2 - root.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - root.getHeight() / 2);

        for (int j = 0; j < numRows; j++) {
            root.row();
            for (int i = 0; i < numColumns; i++) {

                // tileWidget is "the Piece" or an "empty" tile -----------------------

                final Stack tileWidget = new Stack();
                tileWidget.setSize(tileSize, tileSize);

                TextureRegion pieceTextureRegion = getTextFromTile(gameBoardArray[i][j]);
                if (pieceTextureRegion != null) {
                    Image pieceVis = new Image(pieceTextureRegion);
                    pieceVis.setSize(tileSize, tileSize);
                    tileWidget.add(pieceVis);
                } else {
                    Gdx.app.log("ERROR", "The pieceTextureRegion is null");
                }

                final int finalI = i;
                final int finalJ = j;

                // only add the listeners (to Drag and Drop) to tiles that aren't null in their piecetyp and color
                if (!(gameBoardArray[i][j].getPieceType() == null)) {
                    tileWidget.addListener(new DragListener() {
                        @Override
                        public void dragStart(InputEvent event, float x, float y, int pointer) {
                            // Code here will run when the player starts dragging the actor.

                            Gdx.app.log("GameStage", "a drag has started");

                            // if the current color is not the color of the player, we return and display a move not
                            // allowed actor on screen for 2 seconds

                            GameState state = GameJam.getBackend().getGameState();
                            if(gameBoardArray[finalI][finalJ].getPieceType() == Piece.OBSTACLE){
                                event.cancel();
                            } else if (state == GameState.WHITE_TURN &
                                    gameBoardArray[finalI][finalJ].getTeamColor() == TeamColor.BLACK) {
                                // we return and display a move not allowed actor on screen for 2 seconds
                                moveNotAllowed();
                                event.cancel();
                            } else if (state == GameState.BLACK_TURN &
                                    gameBoardArray[finalI][finalJ].getTeamColor() == TeamColor.WHITE) {
                                // we return and display a move not allowed actor on screen for 2 seconds
                                moveNotAllowed();
                                event.cancel();
                            }
                            tileWidget.setZIndex(9999);
                        }

                        @Override
                        public void drag(InputEvent event, final float x, final float y, int pointer) {
                            // move the tileWidget with the mouse
                            tileWidget.moveBy(x - tileWidget.getWidth() / 2, y - tileWidget.getHeight() / 2);
                        }


                        @Override
                        public void dragStop(InputEvent event, float x, float y, int pointer) {

                            // Code here will run when the player lets go of the actor

                            // Get the position of the tileWidget relative to the parent actor (the gameBoard)
                            Vector2 localCoords = new Vector2(x, y);
                            // Convert the position to stage (screen) coordinates
                            Vector2 screenCoords = tileWidget.localToStageCoordinates(localCoords);

                            Coordinate currentCoord = calculateTileByPX((int) screenCoords.x, (int) screenCoords.y);

                            // Coordinate
                            Coordinate startingCoord = new Coordinate(finalI, finalJ);
                            Coordinate targetCoord = new Coordinate(currentCoord.getX(), currentCoord.getY());

                            // ask via Interface link if the moves is Valid
                            boolean moveLegit = backend.isValidMove(startingCoord, targetCoord);

                            if (moveLegit) {
                                // temp save piecetype of targetCoord
                                Tile tempTile = gameBoardArray[currentCoord.getX()][currentCoord.getY()];
                                // temp piecetype
                                Piece tempPiece = tempTile.getPieceType();
                                // temp save of color of tempTile
                                TeamColor tempColor = tempTile.getTeamColor();

                                // Board.update with oldX, oldY, newX, newY
                                boolean hitTarget = backend.movePiece(startingCoord, targetCoord);
                                if (hitTarget) {
                                    playHitSound();
                                    addPieceTypetoPlayerData(tempPiece, tempColor);
                                }
                                Gdx.app.log("GameStage", "a drag has stopped, a move WAS made");
                                updateVisualisePlayerData();
                                updateGameStage();
                            } else {
                                Gdx.app.log("GameStage", "a drag has stopped, a move WAS NOT made");
                                // we return and display a move not allowed actor on screen for 2 seconds
                                moveNotAllowed();
                                // resets gameBoard, since move wasn't allowed, and we have to set the drag back to start
                            }
                        }
                    });
                }
                root.add(tileWidget).size(tileSize);
            }
        }
        // Setting the root table to the rootBoard and adding it to the stage
        rootBoard.clear();
        rootBoard.add(root).center();
        rootBoard.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        rootBoard.setPosition(0, 0);
        rootBoard.setZIndex(1);

        // making sure that the board is underlaying the drag and drop layer
        board.setZIndex(0);

        Gdx.app.log("GameStage", "GameStage updated");
    }

    private Action getBreathingAction(final Image playerLogo) {
        // breathing Action, where the scale changes from 1 to 1.025 every 2 seconds
        return new Action() {
            float time = 0;
            @Override
            public boolean act(float delta) {
                time += delta;
                float scale = (float) (1 + 0.025 * Math.sin(time * 2));
                playerLogo.setScale(scale);
                return false;
            }
        };
    }

    private void addPieceTypetoPlayerData(Piece piecetype, TeamColor teamColor) {
        // add the piece to the player data
        if (teamColor == TeamColor.WHITE) {
            lostPieces1Data.add(piecetype);
        } else {
            lostPieces2Data.add(piecetype);
        }
    }

    private void playHitSound() {
        // play the hit sound
        Gdx.app.log("GameStage", "Playing the hit sound");
        hitSound.play(GameJam.getSoundVolume());
    }

    private void moveNotAllowed() {
        Gdx.app.log("GameStage", "The Move was not allowed, displaying the NotAllowed Actor");
        OneSecondsSelfDestruct notAllowed = new OneSecondsSelfDestruct(new Image(impossibleMoveLogo),
                (float) Gdx.graphics.getWidth() /2, (float) Gdx.app.getGraphics().getHeight() /2);
        notAllowed.setZIndex(10);
        notAllowed.setVisible(true);
        notAllowed.setPosition((float) Gdx.graphics.getWidth() /2, (float) Gdx.app.getGraphics().getHeight() /2);
        addActor(notAllowed);
        updateGameStage();
    }

    /*
    * Method to take a piece and return the class attribute loaded Texture of that piece
     */
    private TextureRegion getTextFromTile(Tile tile) {
        // if tile has null attributes, it means it is empty
        if (tile.getPieceType() == null || tile.getTeamColor() == null) {
            return turnTextureIntoTextureRegion(empty);
        }

        TeamColor teamColor = tile.getTeamColor();
        Piece pieceType = tile.getPieceType();

        // checking for niche cases, where tile has a team color but is actually an obstacle
        if (pieceType == Piece.OBSTACLE) {
            return turnTextureIntoTextureRegion(obstacle);
        }

        switch (teamColor) {
            case WHITE:
                return getWhitePieceTexture(pieceType);
            case BLACK:
                return getBlackPieceTexture(pieceType);
            default:
                Gdx.app.log("GameStage", "NO valid team color -> Empty");
                return turnTextureIntoTextureRegion(empty);
        }
    }

    private TextureRegion turnTextureIntoTextureRegion(Texture texture) {
        return new TextureRegion(texture);
    }

    private TextureRegion getWhitePieceTexture(Piece pieceType) {
        switch (pieceType) {
            case PAWN:
                return new TextureRegion(whitePawn);
            case ROOK:
                return new TextureRegion(whiteRook);
            case KNIGHT:
                return new TextureRegion(whiteKnight);
            case BISHOP:
                return new TextureRegion(whiteBishop);
            case QUEEN:
                return new TextureRegion(whiteQueen);
            case KING:
                return new TextureRegion(whiteKing);
            case PROGRAMMER:
                return new TextureRegion(whiteProgrammer);
            case CHANCELLOR:
                return new TextureRegion(whiteChancellor);
            default:
                Gdx.app.log("GameStage", "NO valid piece type -> Empty");
                return new TextureRegion(empty);
        }
    }

    private TextureRegion getBlackPieceTexture(Piece pieceType) {
        switch (pieceType) {
            case PAWN:
                return new TextureRegion(blackPawn);
            case ROOK:
                return new TextureRegion(blackRook);
            case KNIGHT:
                return new TextureRegion(blackKnight);
            case BISHOP:
                return new TextureRegion(blackBishop);
            case QUEEN:
                return new TextureRegion(blackQueen);
            case KING:
                return new TextureRegion(blackKing);
            case PROGRAMMER:
                return new TextureRegion(blackProgrammer);
            case CHANCELLOR:
                return new TextureRegion(blackChancellor);
            default:
                Gdx.app.log("GameStage", "NO valid piece type -> Empty");
                return new TextureRegion(empty);
        }
    }

    public Stage getStage() {
        return this;
    }

    private void loadAllAssets() {
        Gdx.app.log("GameStage", "Loading Assets: Environment Textures starting"); // ----------------

        impossibleMoveLogo = new Texture("misc/impossibleMove.png");
        empty = new Texture("misc/empty.png");
        obstacle = new Texture("misc/obstacle.png");

        board = new Image(new Texture("board/board.png"));
        decovertical = new Image(new Texture("board/12345678.png"));
        decohorizontal = new Image(new Texture("board/HGFEDCBA.png"));
        splatterBottom = new Image(new Texture("board/splatterBottom.png"));
        splatterTop = new Image(new Texture("board/splatterTop.png"));

        Gdx.app.log("GameStage", "Loading Assets: Environment Textures loading finished"); // --------
        Gdx.app.log("GameStage", "Loading Assets: Piece Textures starting"); // ----------------------

        // white pieces ----------------------------------------------------
        whitePawn = new Texture("white/whitepawn.png");
        whiteRook = new Texture("white/whiterook.png");
        whiteKnight = new Texture("white/whiteknight.png");
        whiteBishop = new Texture("white/whitebishop.png");
        whiteQueen = new Texture("white/whitequeen.png");
        whiteKing = new Texture("white/whiteking.png");
        whiteProgrammer = new Texture("white/whiteprogrammer.png");
        whiteChancellor = new Texture("white/whitechancellor.png");

        // black pieces ----------------------------------------------------
        blackPawn = new Texture("black/blackpawn.png");
        blackRook = new Texture("black/blackrook.png");
        blackKnight = new Texture("black/blackknight.png");
        blackBishop = new Texture("black/blackbishop.png");
        blackQueen = new Texture("black/blackqueen.png");
        blackKing = new Texture("black/blackking.png");
        blackProgrammer = new Texture("black/blackprogrammer.png");
        blackChancellor = new Texture("black/blackchancellor.png");

        Gdx.app.log("GameStage", "Loading Assets: Piece Textures starting"); // ----------------------
        Gdx.app.log("GameStage", "Loading Assets: Sounds starting"); // ------------------------------

        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.mp3"));

        Gdx.app.log("GameStage", "Loading Assets: Sounds finished"); // ------------------------------
    }

    private Table buttonsTable(float tileSize) {
        // create another table for the option buttons

        Gdx.app.log("GameStage", "Creating the Buttons Table");

        Table backTable = new Table();
        backTable.setSize(tileSize * 5, tileSize * 2f);
        // bottom right the table in the parent container
        backTable.setPosition(Gdx.graphics.getWidth() - backTable.getWidth(), tileSize);
        addActor(backTable); // Add the table to the stage

        // Exit to Main Menu button to return to the main menu
        TextButton menuButton = new TextButton("Main Menu", GameJam.getSkin());
        menuButton.align(Align.bottomRight);
        backTable.add(menuButton);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameJam.setInGame(false);
                createMainMenuStage();
            }
        });

        return backTable;
    }

    private void addBoardVisualisation() {
        // add the board image center of the screen
        float tileSize = getTileSize();
        board.setSize(tileSize * 8, tileSize * 8);
        board.setPosition((float) Gdx.graphics.getWidth() / 2 - board.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - board.getHeight() / 2);
        board.setZIndex(0);
        addActor(board);

        // add the vertical decoration one tile to the left of the board
        decovertical.setSize(tileSize*0.25f, tileSize * 8);
        decovertical.setPosition((float) Gdx.graphics.getWidth() / 2 - board.getWidth() / 2 - tileSize*0.5f,
                (float) Gdx.graphics.getHeight() / 2 - board.getHeight() / 2);
        decovertical.setZIndex(0);
        addActor(decovertical);

        // add the horizontal decoration one tile below the board
        // width is 8tiles
        decohorizontal.setSize(tileSize * 8, tileSize*0.8f);
        decohorizontal.setPosition((float) Gdx.graphics.getWidth() / 2 - board.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - board.getHeight() / 2 - tileSize);
        decohorizontal.setZIndex(0);
        addActor(decohorizontal);

        // add a splatter decoration to the bottom of the board
        splatterBottom.setSize(tileSize * 8, tileSize*0.5f);
        splatterBottom.setPosition((float) Gdx.graphics.getWidth() / 2 - board.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - board.getHeight() / 2 - tileSize*0.5f);
        splatterBottom.setZIndex(0);
        addActor(splatterBottom);

        // add a splatter decoration to the top of the board
        splatterTop.setSize(tileSize * 8, tileSize*0.5f);
        splatterTop.setPosition((float) Gdx.graphics.getWidth() / 2 - board.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 + board.getHeight() / 2);
        splatterTop.setZIndex(0);
        addActor(splatterTop);
    }

    private void updateVisualisePlayerData() {
        // instead of using it as a class variable that is received at construct,
        // we get it from the GameJam class to allow for dynamic resolution changes

        // player 1 -----------------------------------------------------------------
        // clear the table
        player1stats.clear();
        // iterate through player1array of lost pieces, get each pieces texture and add it to the table
        // the table is 4 tiles wide, 4 tiles high
        // it always has 16 tiles in it

        pieceCounter = 0;
        counter = 0;
        for (Piece piece : lostPieces1Data) {
            // get the texture of the piece
            TextureRegion pieceTexture = getTextFromTile(new Tile(piece, TeamColor.WHITE));
            // create an image with the texture
            fillUpLostPieces(player1stats, pieceTexture);
        }

        fillUpDataVis(player1stats);

        // ----------- player 2 ----------------------------------------------------
        // clear the table
        player2stats.clear();
        // iterate through player2array of lost pieces, get each pieces texture and add it to the table
        // the table is 4 tiles wide, 4 tiles high

        pieceCounter = 0;
        counter = 0;
        for (Piece piece : lostPieces2Data) {
            // get the texture of the piece
            TextureRegion pieceTexture = getTextFromTile(new Tile(piece, TeamColor.BLACK));
            // create an image with the texture
            fillUpLostPieces(player2stats, pieceTexture);
        }

        fillUpDataVis(player2stats);
    }

    private void fillUpLostPieces(Table playerStats, TextureRegion pieceTexture){
        float tileSize = getTileSize();
        // create an image with the texture
        Image pieceImage = new Image(pieceTexture);
        pieceImage.setSize(tileSize/2, tileSize/2);
        // add the image to the table and add a row before if the counter is 4+
        if (counter == 4){
            playerStats.row();
            counter = 0;
        }
        playerStats.add(pieceImage);
        pieceCounter += 1;
        counter += 1;
    }

    private void fillUpDataVis(Table playerStats){
        float tileSize = getTileSize();
        // add the rest of the tiles to the table (rest = 16-pieceCounter)
        for (int i = 0; i < 16-pieceCounter; i++) {
            // create an image with the texture
            Image pieceImage = new Image(empty);
            pieceImage.setSize(tileSize/8, tileSize/8);
            if (counter == 4){
                playerStats.row();
                counter = 0;
            }
            // add the image to the table
            playerStats.add(pieceImage);
            counter += 1;
        }
    }

    private void intialisePlayerData() {

        lostPieces1Data = new Array<>();
        lostPieces2Data = new Array<>();

        player1stats = new Table();
        player2stats = new Table();

        float tileSize = getTileSize();

        float statAndLogoWidth = tileSize*4;

        // player 1 data is on the left side of the screen ----------------------------------------------
        player1WHITElogo = new Image(new Texture("misc/player1chess.png"));
        // x position of player1
        float xpospl1 = (float) Gdx.graphics.getWidth()/4-statAndLogoWidth;
        // centralise at the first fourth of the screen width
        setPosPlDat(player1stats, player1WHITElogo, xpospl1);
        setSizesPlDat(player1WHITElogo, player1stats, tileSize);
        addActor(player1WHITElogo);
        addActor(player1stats);

        // player 2 data is on the right side of the screen ---------------------------------------------
        player2BLACKlogo = new Image(new Texture("misc/player2chess.png"));
        //-tilesize/2 to accomodate for the width of the 1234 deco
        float xpospl2 = (float) Gdx.graphics.getWidth()- (float) Gdx.graphics.getWidth()/4-(tileSize/2);
        // centralise at the last fourth of the screen width
        setPosPlDat(player2stats, player2BLACKlogo, xpospl2);
        setSizesPlDat(player2BLACKlogo, player2stats, tileSize);
        addActor(player2BLACKlogo);
        addActor(player2stats);
    }

    private void setPosPlDat(Table playerStats, Image playerLogo, float xpos){
        playerLogo.setPosition(xpos, getTileSize()*8);
        playerStats.setPosition(xpos, getTileSize()*8-playerStats.getHeight()-playerLogo.getHeight());
    }
    private void setSizesPlDat(Image playerLogo, Table playerStats, float tileSize){
        playerLogo.setSize(tileSize*4, tileSize*1.5f);
        playerStats.setSize(tileSize*2, tileSize*2);
    }

}
