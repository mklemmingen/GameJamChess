package com.gamejam.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gamejam.game.backend.BackMan;
import com.gamejam.game.frontend.actors.OneSecondsSelfDestruct;
import com.gamejam.game.frontend.sound.MusicPlaylist;
import com.gamejam.game.frontend.stage.LoadingScreenStage;
import com.gamejam.game.frontend.stage.MenuStage;
import com.gamejam.game.link.Coordinate;

public class GameJam extends ApplicationAdapter {

	// -------------------------------------------- VARIABLES ----------------------------------------------------------

	// used for essential resolution and drawing matters ---------------------------------------------------------------
	private static SpriteBatch batch;
	private static Viewport viewport;

	// size of tiles on the board --------------------------------------------------------------------------------------
	private static float tileSize = 64;
	private static Skin skin;
	private static int numberObstacle; // number of obstacles in the default game mode, SPECIFIC TO CHESS VARIANT
	private static Stage currentStage;
	private static Stage informationStage;

	// for setting the current "Mover" of the game / if a Move has been valid ------------------------------------------
	private static boolean legitTurn = false;

	// music -------------------------------------------- music and sound ----------------------------------------------
	private static MusicPlaylist background_music;

	// volume of Sounds

	private static float volume = 0.1f;  // variable to store the current MUSIC volume level

	private static float soundVolume = 1.5f;  // variable to store the current SOUND volume level

	// for the return to menu button, we have to have a boolean keeping processTurn from running if not true -----------
	private static boolean inGame = false;

	// -----------------------------------------------------------------------------------------------------------------

	// android relative sizing capability

	private static Stage backgroundStage;

	private static float screenWidth;
	private static float screenHeigth;

	private static float buttonWidth;
	private static float buttonHeight;

	// -------------------------------------------- --------------------------------------------------------------------
	// sound and music -- and buttons

	private static Sound gameJam;

	private static Stage songNameStage;
	private static Label musicLabel;
	private static float musicLabelScale;

	private static Button soundButton;
	private static Image soundOnImage; // inserted into Pheno when sound is on
	private static Image soundOffImage; // inserted into Pheno when sound is off

	private static Image settingButtonImage;
	private static Button settingButton;

	// -------------------------------------------- Number of Columns and Rows -----------------------------------------

	//
	private static final int numberColumns = 8;
	private static final int numberRows = 8;

	// BACKEND --------------- creating the Backend Manager class that implements the linking interface ----------------

	private static BackMan backend;

	// -------------------------------------------- START OF METHODS ---------------------------------------------------

	@Override
	public void create() {

		Gdx.app.log("GameJam", "GameJam started, create called");

		// create the backend manager
		backend = new BackMan();

		// creation of the batch for drawing the images -------------------------------------
		batch = new SpriteBatch();
		viewport = new ScreenViewport();

		informationStage = new Stage(viewport, batch);

		// loading Screen is going for a fixed amount of time + till loadingStage method end() called
		switchToStage(new LoadingScreenStage(viewport, batch)); // start the loading screen

		// skin of the UI -------------------------------------------------------------------
		// skin (look) of the buttons via the prearranged json file
		skin = new Skin(Gdx.files.internal("menu.commodore64/uiskin.json"));

		// resize all stages for the beginning ----------------------------------------------
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		screenWidth = Gdx.graphics.getWidth();
		screenHeigth = Gdx.graphics.getHeight();

		buttonWidth = tileSize * 2;
		buttonHeight = tileSize / 2;

		Gdx.app.log("GameJam", "GameJam started, create finished succesfully");

		loadAllAssets();
	}
	
	@Override
	public void render() {
		/*
		* render is called every frame, main-game loop of the game, holds all stages in nested ifs and the processTurn
		 */
		// clear the screen at each frame  ---------------------------
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// set the color of the screen to black
		Gdx.gl.glClearColor(0, 0, 0, 0);

		// interaction stage ------------------------------------------
		// for the stages, see method switchToStage
		currentStage.act(Gdx.graphics.getDeltaTime());
		currentStage.draw();

		// information stage for textual and/or signal needs that cannot be displayed from within currentStage -----
		informationStage.act(Gdx.graphics.getDeltaTime());
		informationStage.draw();
	}

	private void loadAllAssets() {
		/*
		This method gets called during the main loading Stage runs
		 */

		// load the sound for the game

		Gdx.app.log("GameJam", "Loading Assets: Music Loading starting"); // ------------------------

		// load the background music into MusicPlaylist object
		background_music = new MusicPlaylist();
		background_music.addSong("music/8BitCorpoLofi.mp3",
				"Lofi 8 Bit Mixtape", "miguelangell960");

		// music label
		// load empty musicLabel and put its position to the lower left of the screen
		musicLabel = new Label(" ", skin);
		musicLabel.setPosition(tileSize, tileSize*1.25f);
		musicLabel.setColor(Color.WHITE);
		if(musicLabelScale == 0) {
			musicLabelScale = 0.75f;
		}
		musicLabel.setFontScale(musicLabelScale);

		informationStage.addActor(musicLabel);

		// load the audio buttons and images
		// sound at top right -----------------------------------------------------------------------------------------
		// Sound on
		soundOnImage = new Image(new Texture(Gdx.files.internal("misc/sound.png")));
		soundOnImage.setSize(tileSize, tileSize);
		soundOnImage.setPosition(Gdx.graphics.getWidth() - tileSize*2, Gdx.graphics.getHeight() - tileSize*2);

		// sound off
		soundOffImage = new Image(new Texture(Gdx.files.internal("misc/soundOff.png")));
		soundOffImage.setSize(tileSize, tileSize);
		soundOffImage.setPosition(Gdx.graphics.getWidth() - tileSize*2, Gdx.graphics.getHeight() - tileSize*2);

		// At start, its SoundOn
		soundButton = new Button(soundOnImage, new Button.ButtonStyle());
		soundButton.setSize(tileSize*2, tileSize*2);
		soundButton.setPosition(Gdx.graphics.getWidth() - soundButton.getWidth(),
				Gdx.graphics.getHeight() - soundButton.getHeight());

		// settings at top left -------------------------------------------------------------------------------------
		settingButtonImage = new Image(new Texture(Gdx.files.internal("misc/settings.png")));
		settingButtonImage.setSize(tileSize, tileSize);
		settingButtonImage.setPosition(tileSize, Gdx.graphics.getHeight());
		settingButton = new Button(settingButtonImage, new Button.ButtonStyle());
		settingButton.setSize(tileSize*2, tileSize*2);
		settingButton.setPosition(0, Gdx.graphics.getHeight() - settingButton.getHeight());

		// Listeners ------------------
		soundButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// when volume 0, set to 1 and vice versa
				// ongoing sound volumes are not affected, but future ones though to a change in soundVolume
				soundButton.clearChildren();
				if (volume == 0) {
					soundButton.add(soundOnImage);
					volume = 0.1f;
					soundVolume = 0.5f;
					background_music.setVolume(volume);
				} else {
					soundButton.add(soundOffImage);
					volume = 0;
					soundVolume = 0;
					background_music.setVolume(volume);
				}
			}
		});

		settingButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// for now, just display a texture for 3 seconds that says "Settings not implemented"
				// if you would like settings, here is the place you should add a method to display it
				OneSecondsSelfDestruct settingsNotImpl = new OneSecondsSelfDestruct(
						new Image(new Texture("misc/settingsNotimplemented.png")),
						(float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() - tileSize*2);
				informationStage.addActor(settingsNotImpl);
			}
		});

		Gdx.app.log("GameJam", "Loading Assets: Music Loading finished"); // -----------------------

		// sets the InputProcessor, a Gdx Tool for handling userinput, to the currentStage, since it's the only
		// stage that needs input. Use multiplexer if multiple needed, then change back to single InputProcess.
		Gdx.input.setInputProcessor(currentStage);

		Gdx.app.log("BoomChess", "Loading Assets: Finished");
	}


	@Override
	public void resize(int width, int height) {
		/*
		 * fits the needed values when a resize has happened, when a resize has happened
		 */
		currentStage.getViewport().update(width, height, true);
		informationStage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		/*
		* dispose is used when the game is exited, disposes of all assets
		 */
		batch.dispose();
		skin.dispose();

		currentStage.dispose();
		informationStage.dispose();

		 // dispose of all music
		 background_music.dispose();
	}


	public static void switchToStage(Stage newStage) {
		/*
		* this method removes the currentStage and loads a new one
		* used generally in the Stage classes at the end to load the created Stages
		* or combined with a return Stage createStage method
		 */

		Gdx.app.log("GameJam", "switchToStage called. StageName: " + newStage.getClass().getSimpleName());

		if (currentStage != null){
			currentStage.clear();}
		newStage.setViewport(viewport);
		currentStage = newStage;
		Gdx.input.setInputProcessor(currentStage);
	}

	public static void addSoundAndSettingButtons(Stage stage) {
		/*
		 * method for adding the sound and setting buttons to a stage
		 */

		// settings -----------------------------------------------------
		stage.addActor(settingButton);
		// sounds --------------------------------------------------------
		stage.addActor(soundButton);
	}

	public static void createMainMenuStage() {
		/*
		* method for creating the stage for the main menu
		 */

		// start the song, if not already playing
		if(!(background_music.isPlaying())) {
			background_music.setVolume(volume);
			background_music.play();
		}

		switchToStage(new MenuStage());
	}

	public static Coordinate calculateTileByPX(int pxCoordinateX, int pxCoordinateY) {
		/*
		* method for calculating the tile coordinates by pixel coordinates
		 */

		// In LibGDX, the origin of the screen is the top left! i traditional, its bottom left!

		// method for checking which tile a pxCoordinateX and pxCoordinateY is in, creating the coordinates object
		// of the respective tile and returning it

		// we calculate this by first getting the screen width and height
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		// we then calculate the upper left corner of the gameBoard by subtracting the screenWidth and screenHeight by
		// the gameBoard width and height and then dividing it by 2a
		int upperLeftCornerX = (screenWidth - (int) tileSize*8) / 2;
		int upperLeftCornerY = (screenHeight - (int) tileSize*8) / 2;

		// Adjust the pxCoordinate by adding 1 to ensure boundary pixels are correctly classified.
		int adjustedPxX = pxCoordinateX + 1;
		int adjustedPxY = pxCoordinateY + 1;

		// Adjust the Y-coordinate to reflect the difference in coordinate systems ( see BUGFIX )
		adjustedPxY = screenHeight - adjustedPxY;

		// we then calculate the tileX and tileY by subtracting the upperLeftCornerX and upperLeftCornerY from the
		// adjustedPxX and adjustedPxY and dividing it by the tile size
		int tileX = (adjustedPxX - upperLeftCornerX) / (int) tileSize;
		int tileY = (adjustedPxY - upperLeftCornerY) / (int) tileSize;

		// we then set the tileX and tileY in the iconTileCoordinate object

        return new Coordinate(tileX, tileY);
	}

	public static Coordinate calculateTileByPXNonGDX(int pxCoordinateX, int pxCoordinateY) {
		/*
		 * method for calculating the tile coordinates by pixel coordinates,
		 * literally mirrored tile to the calculateTileByPX method
		 */

		// method for checking which tile a pxCoordinateX and pxCoordinateY is in, creating the coordinates object
		// of the respective tile and returning it

		Coordinate iconTileCoordinate = calculateTileByPX(pxCoordinateX, pxCoordinateY);

		// Invert the tilePositionY for libGDX coordinate System compliance
		int invertedTilePositionY = 7 - iconTileCoordinate.getY();

        return new Coordinate(iconTileCoordinate.getX(), invertedTilePositionY);
	}

	// gets and sets for all the private variables --------------------------------------------

	public static float getTileSize() {
		return tileSize;
	}

	public static Skin getSkin() {
		return skin;
	}

	public static void setTileSize(float tileSize) {
		GameJam.tileSize = tileSize;
	}

	public static void setSkin(Skin skin) {
		GameJam.skin = skin;
	}

	public static int getNumberObstacle() {
		return numberObstacle;
	}

	public static void setNumberObstacle(int numberObstacle) {
		GameJam.numberObstacle = numberObstacle;
	}

	public static boolean isLegitTurn() {
		return legitTurn;
	}

	public static void setLegitTurn(boolean legitTurn) {
		GameJam.legitTurn = legitTurn;
	}

	public static MusicPlaylist getBackground_music() {
		return background_music;
	}

	public static void setBackground_music(MusicPlaylist background_music) {
		GameJam.background_music = background_music;
	}

	public static float getVolume() {
		return volume;
	}

	public static void setVolume(float volume) {
		GameJam.volume = volume;
	}

	public static float getSoundVolume() {
		return soundVolume;
	}

	public static void setSoundVolume(float soundVolume) {
		GameJam.soundVolume = soundVolume;
	}


	public static boolean isInGame() {
		return inGame;
	}

	public static void setInGame(boolean inGame) {
		GameJam.inGame = inGame;
	}

	public static Stage getBackgroundStage() {
		return backgroundStage;
	}

	public static void setBackgroundStage(Stage backgroundStage) {
		GameJam.backgroundStage = backgroundStage;
	}

	public static float getScreenWidth() {
		return screenWidth;
	}

	public static void setScreenWidth(float screenWidth) {
		GameJam.screenWidth = screenWidth;
	}

	public static float getScreenHeigth() {
		return screenHeigth;
	}

	public static void setScreenHeigth(float screenHeigth) {
		GameJam.screenHeigth = screenHeigth;
	}

	public static float getButtonWidth() {
		return buttonWidth;
	}

	public static void setButtonWidth(float buttonWidth) {
		GameJam.buttonWidth = buttonWidth;
	}

	public static float getButtonHeight() {
		return buttonHeight;
	}

	public static void setButtonHeight(float buttonHeight) {
		GameJam.buttonHeight = buttonHeight;
	}

	public static Sound getGameJam() {
		return gameJam;
	}

	public static void setGameJam(Sound gameJam) {
		GameJam.gameJam = gameJam;
	}

	public static Stage getSongNameStage() {
		return songNameStage;
	}

	public static void setSongNameStage(Stage songNameStage) {
		GameJam.songNameStage = songNameStage;
	}

	public static Label getMusicLabel() {
		return musicLabel;
	}

	public static void setMusicLabel(Label musicLabel) {
		GameJam.musicLabel = musicLabel;
	}

	public static float getMusicLabelScale() {
		return musicLabelScale;
	}

	public static void setMusicLabelScale(float musicLabelScale) {
		GameJam.musicLabelScale = musicLabelScale;
	}

	public static void setBatch(SpriteBatch batch) {
		GameJam.batch = batch;
	}

	public static SpriteBatch getBatch() {
		return batch;
	}

	public static void setCurrentStage(Stage currentStage) {
		GameJam.currentStage = currentStage;
	}

	public static Stage getCurrentStage() {
		return currentStage;
	}

	// backend -----------------------------------------------------------------------------------------------------

	public static BackMan getBackend() {
		return backend;
	}

	public static int getNumberColumns() {
		return numberColumns;
	}

	public static int getNumberRows() {
		return numberRows;
	}

}