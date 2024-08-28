package sample;

/*
Name: Angel Gao, Nivedita Kulkarni
Project Working Date: Dec. 1st 2021 -- Jan. 10th 2022
Assignment Title: Battleship Game, Player VS Computer
Purpose: creating a battleship game using java that can compete against with user in different levels, demonstrated
with GUI with additional file handling, timer and sound effects features
Notification: The class is being split into two parts, GUI Part where most of the code is for conveying user experience and Code
logic part where the algorithm for computer AI and the process of one round of game are coded. Also some getters and setters 
that's not used in the program from each class (except main class) were removed after the program is completed for achieving simplicity. 
 */


//This class is where the main game logic and scene are constructed, combine all the functionality of the classes created before
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Battleship extends Application {
    //public class variables, where other classes can easily access using Battleship.variable
    public static boolean myTurn;           //a boolean var used to rep. who should shoot now. myTurn = true means user's turn, myTurn = false means computer's turn
    public static String level;             //a String var used to rep. the level of one round of game, can only have value "easy" or "hard"
    public static PrintWriter pw;           //a PrintWriter var used to write on files when user when to save game progress
    public static File recordFile;          //a File var where the original information is stored when user save game progress. Will "clear" itself once the program starts
    public static boolean gameEnd;          //a boolean var used to determine if the game is over
    public static MediaPlayer backgroundMusicPlayer;        //a MediaPlayer object used to play any background music Media. It's set to public because the background music is playing on every scene (every moment except alert box shown up)


    //private class variables that other class cannot access
    private static File recordFileCopy;                     //a File var that copy all information from recordFile when user save game progress. It will exist in project folder as long as it's being created until being manually removed
    private static BufferedReader br;                       //a BufferedReader used to read information from recordFileCopy to update status and resume game
    private static StringTokenizer st;                      //a StringTokenizer var used to cooperate with BufferedReader for convenient reading files
    private static Scene homeScene;                         //a Scene var that build the home scene
    private static String musicName;                        //a String used to record the name of all sound effect .mp3 files
    private static String backgroundMusicName;              //a String used to record the name of all background music .mp3 files
    private static Media music;                             //a Media type object used to locate musicName's path and convert it into real sound effect for playing
    private static Media backgroundMusic;                   //a Media type object used to locate backgroundMusicName's path and convert it into real background music for playing
    private static String[] songList = {"Forests.mp3", "Glacier.mp3", "Dungeon.mp3", "Volcano.mp3"};    //a String list that store all background music name, can add or remove any .mp3 background music anytime from the playList
    private static int songNumber = Randomizer.nextInt(0, songList.length-1);                           //an integer that determine which background music to play initially when the program runs
    private static MediaPlayer effectPlayer;                //a Media player object used to play any Media objects
    private static Player comp = new Player();              //a Player object used to store all Ships status, player grid and opponent's grid information for computer. It's used at backend for testing code logics
    private static Player user = new Player();              //a Player object used to store all Ships status, player grid and opponent's grid information for user. It's used at backend for testing code logics
    private static Grids oppGrid;                           //a Grids object used to change color on GUI to display status for computer Grid. It's used to provide visual demonstration
    private static Grids userGrid;                          //a Grids object used to change color on GUI to display status for user Grid. It's used to provide visual demonstration
    private static Score userScore = new Score();           //a Score object used to track user's score during the game play
    private static Score compScore = new Score();           //a Score object used to track computer's score during the game play
    private static Label whoTurnOutput = new Label();       //a Label used to display whose turn is it (user or computer's) on GUI
    private static Label statusOutput = new Label();        //a Label used to display situation after one shot on GUI (who miss at row # column # or who hit what ship at row # column #)
    private static Label compGuessOutput = new Label("COMPUTER GUESS: N/A");    //a Label used to display computer's guess, initially display text "COMPUTER GUESS: N/A" on GUI
    private static Label sunkOutput = new Label();          //a Label used to display which ship is sunk after each movement (if no ship is sunk, the label is empty)
    private static Label endGameOutput = new Label();       //a Label used to display who win when the game end on GUI
    private static Label warningOutput = new Label();       //a Label used to display "invalid input!" when user input invalid information in two textfields during the game play
    private static Label userScoreOutput = new Label("User's Score: 0");        //a Label used to display user's score, initially display text "User's Score: 0" on GUI
    private static Label compScoreOutput = new Label("Computer's Score: 0");    //a Label used to display computer's score, initially display text "Computer's Score: 0" on GUI
    private static Label timerText = new Label("Timer: 0 min 0 sec");           //a Label used to display timer for a round of game, initially display text "Timer: 0 min 0 sec"
    private static Timeline timer;                          //a Timeline object that can define free form animation with one or more key frames
    private static int timerCount = 0;                      //an integer that display and update seconds animation with timer
    private static int timerMinCount = 0;                   //an integer that display and update minutes animation with timer
    private static TextField statusInput = new TextField(); //a TextField object that store the user's status input after computer provides the guesses
    private static TextField shipInput = new TextField();   //a TextField that store the user's ship input (which ship is hit/sunk) after computer provides the guesses
    private static Button submit = new Button("Submit"); //a button used to "submit" user's two (or one) inputs for each computer guess
    private static Button returnHome = new Button("Return to Home Screen");     //a button used to display alert box and ask if user want to return to home screen (to start a new game)
    private static Button save = new Button("Save Progress");                   //a button used to display alert box and ask if user want to save current game progress and return to home screen
    private static String status, shipName;                 //two String var used to store user's status and ship name inputs after user press "submit" button
    private static int[] compGuess;                         //a 2D int array with only length 2 to store computer's each guess in [row, column] form
    private static boolean compAlreadyMakeGuess;            //a boolean var used to detect if computer's already made a guess. If we don't have this var, computer will change its guesses everytime we click on the computer's grid (although no color change would happen by clicking that grid)
    private static boolean evenParity;                      //a boolean var used to determine whether shoot all even or odd grids if computer is in hard mode


    /*
    ================================================== GUI Part ================================================================
     */

    //this method takes no parameters and initialize the status for all variables that already changed in previous round of game
    //in order to prepare for next round of game if user required. UserGrid and oppGrid aren't initialized because it's initialized in setupGame method
    public static void clearStatus() {
        myTurn = false;
        gameEnd = false;
        level = null;
        comp = new Player();
        user = new Player();
        userScore = new Score();
        compScore = new Score();
        whoTurnOutput = new Label();
        statusOutput = new Label();
        compGuessOutput = new Label("COMPUTER GUESS: N/A");
        sunkOutput = new Label();
        endGameOutput = new Label();
        warningOutput = new Label();
        userScoreOutput = new Label("User's Score: 0");
        compScoreOutput = new Label("Computer's Score: 0");
        timerText = new Label("Timer: 0 min 0 sec");
        statusInput.clear();
        shipInput.clear();
        status = null;
        shipName = null;
        compGuess = null;
        compAlreadyMakeGuess = false;
        timerCount = 0;
        timerMinCount = 0;
    }


    //this method takes no parameters and read information for each variable from BattleshipRecordCopy.txt file for user resuming a game
    public static void readFile() throws IOException {
        //create a BufferedReader that can read target file
        br = new BufferedReader(new FileReader("BattleshipRecordCopy.txt"));

        //record whose turn is it
        String myTurnRecord = readLine();
        myTurn = !myTurnRecord.contains("false");

        //record the level of previous game
        String levelRecord = readLine();
        if(levelRecord.contains("easy")) {level = "easy";}
        else level = "hard";

        //record if the previous game is finished
        String gameEndRecord = readLine();
        gameEnd = !gameEndRecord.contains("false");

        //record how many seconds passed for previous game
        //String temp is used to help store the "meaningless" text on the file (such as timerCount: 3, "timerCount" is useless here) for retrieving actual meaningful value
        String temp = next();
        timerCount = readInteger();

        //record how many minutes passed for previous game
        temp = next();
        timerMinCount = readInteger();

        //record previous game user's score
        temp = next();
        int userS = readInteger();
        userScore.setScore(userS);
        userScoreOutput.setText("User's Score: " + userS);

        //record previous game computer's score
        temp = next();
        int compS = readInteger();
        compScore.setScore(compS);
        compScoreOutput.setText("Computer's Score: " + compS);

        //record the output for whoTurn, if output doesn't exist, leave whoTurnRecord label empty
        String whoTurnRecord = readLine();
        if(whoTurnRecord.length() > 15) whoTurnOutput.setText(whoTurnRecord.substring(15));
        else whoTurnOutput.setText("");

        //record the output for status, if output doesn't exist, leave statusOutput label empty
        String statusOutputRecord = readLine();
        if(statusOutputRecord.length() > 14) statusOutput.setText(statusOutputRecord.substring(14));
        else statusOutput.setText("");

        //record the output for which ship sunk, if output doesn't exist, leave sunkOutput label empty
        String sunkOutputRecord = readLine();
        if(sunkOutputRecord.length() > 12) sunkOutput.setText(sunkOutputRecord.substring(12));
        else sunkOutput.setText("");

        //update the status of all cells from left board (user's clickable board)
        temp = readLine();
        temp = readLine();
        for(int r = 0; r < 10; r++) {
            for(int c = -1; c < 10; c++) {              //we need to loop total 11 times because the -1 column is where letters A-J are
                char status = readCharacter();
                if(status == 'o'){                      //if status recorded is miss, markMiss on comp.playerGrid, user.oppGrid and oppGrid
                    comp.playerGrid.markMiss(r,c);
                    user.oppGrid.markMiss(r,c);
                    oppGrid.getOnePosition(r,c).markMissGUI();
                } else if(status == 'X') {              //if status recorded is hit, markHit on comp.playerGrid, user.oppGrid and oppGrid
                    comp.playerGrid.markHit(r,c);
                    user.oppGrid.markHit(r,c);
                    oppGrid.getOnePosition(r,c).markHitGUI();
                } else if(status == '#') {              //if status recorded is sunk, markSunk on comp.playerGrid, user.oppGrid and oppGrid
                    comp.playerGrid.markSunk(r,c);
                    user.oppGrid.markSunk(r,c);
                    oppGrid.getOnePosition(r,c).markSunkGUI();
                }
            }
        }

        //update the ship arrangements for the left board (computer's ship arrangements)
        temp = readLine();
        for(int r = 0; r < 10; r++) {
            for(int c = -1; c < 10; c++) {
                char shipLabel = readCharacter();

                //when record a meaningful letter that rep. a ship, first check it doesn't belong to column -1 (A-J labels)
                //since we only set the location using the top most cell or left most cell, when a ship is already set and
                //we've record other same letters, ignore the records. That's why we need to check isLocationReset() to ensure
                //a ship only be set location for one time
                if(!comp.ships[0].isLocationReset() && shipLabel == 'A' && c != -1){
                    comp.ships[0].setLocation(r,c);
                } else if(!comp.ships[1].isLocationReset() && shipLabel == 'B' && c != -1) {
                    comp.ships[1].setLocation(r,c);
                } else if(!comp.ships[2].isLocationReset() && shipLabel == 'R' && c != -1) {
                    comp.ships[2].setLocation(r,c);
                } else if(!comp.ships[3].isLocationReset() && shipLabel == 'S' && c != -1) {
                    comp.ships[3].setLocation(r,c);
                } else if(!comp.ships[4].isLocationReset() && shipLabel == 'D' && c != -1) {
                    comp.ships[4].setLocation(r,c);
                }
            }
        }

        //record each ship's direction and set direction to each ship
        temp = next();
        temp = next();
        temp = next();
        int carrierDire = readInteger();
        int battleshipDire = readInteger();
        int cruiserDire = readInteger();
        int submarineDire = readInteger();
        int destroyerDire = readInteger();
        comp.ships[0].setDirection(carrierDire);
        comp.ships[1].setDirection(battleshipDire);
        comp.ships[2].setDirection(cruiserDire);
        comp.ships[3].setDirection(submarineDire);
        comp.ships[4].setDirection(destroyerDire);

        //add these ships in comp.playerGrid and oppGrid
        for(int i = 0; i < 5; i++) {
            comp.playerGrid.addShip(comp.ships[i]);
            oppGrid.addShip(comp.ships[i]);
        }

        //update the status of all cells from right board (computer's target board)
        temp = readLine();
        temp = readLine();
        for(int r = 0; r < 10; r++) {
            for(int c = -1; c < 10; c++) {
                char status = readCharacter();
                if(status == 'o'){                      //if status recorded is miss, markMiss on comp.oppGrid, user.playerGrid and userGrid
                    comp.oppGrid.markMiss(r,c);
                    user.playerGrid.markMiss(r,c);
                    userGrid.getOnePosition(r,c).markMissGUI();
                } else if(status == 'X') {              //if status recorded is hit, markHit on comp.oppGrid, user.playerGrid and userGrid
                    comp.oppGrid.markHit(r,c);
                    user.playerGrid.markHit(r,c);
                    userGrid.getOnePosition(r,c).markHitGUI();
                } else if(status == '#') {              //if status recorded is sunk, markSunk on comp.oppGrid, user.playerGrid and userGrid
                    comp.oppGrid.markSunk(r,c);
                    user.playerGrid.markSunk(r,c);
                    userGrid.getOnePosition(r,c).markSunkGUI();
                }
            }
        }

        //update the ship arrangements for the right board (user's ship arrangements)
        temp = readLine();
        for(int r = 0; r < 10; r++) {
            for(int c = -1; c < 10; c++) {
                char shipLabel = readCharacter();

                //here since the computer don't know the placement of user's ship until user told it (input ship name for a hit/sunk)
                //we need to set shipName and change the status of a position has a ship (hasShip) for every single letter recorded
                //that doesn't belong to column -1 (A-J column) on comp.oppGrid and user.playerGrid
                if(shipLabel == 'A' && c != -1){
                    comp.oppGrid.setShipName(r,c,"carrier");
                    user.playerGrid.setShipName(r,c,"carrier");
                    comp.oppGrid.setHasShip(r,c,true);
                    user.playerGrid.setHasShip(r,c,true);
                } else if(shipLabel == 'B' && c != -1) {
                    comp.oppGrid.setShipName(r,c,"battleship");
                    user.playerGrid.setShipName(r,c,"battleship");
                    comp.oppGrid.setHasShip(r,c,true);
                    user.playerGrid.setHasShip(r,c,true);
                } else if(shipLabel == 'R' && c != -1) {
                    comp.oppGrid.setShipName(r,c,"cruiser");
                    user.playerGrid.setShipName(r,c,"cruiser");
                    comp.oppGrid.setHasShip(r,c,true);
                    user.playerGrid.setHasShip(r,c,true);
                } else if(shipLabel == 'S' && c != -1) {
                    comp.oppGrid.setShipName(r,c,"submarine");
                    user.playerGrid.setShipName(r,c,"submarine");
                    comp.oppGrid.setHasShip(r,c,true);
                    user.playerGrid.setHasShip(r,c,true);
                } else if(shipLabel == 'D' && c != -1) {
                    comp.oppGrid.setShipName(r,c,"destroyer");
                    user.playerGrid.setShipName(r,c,"destroyer");
                    comp.oppGrid.setHasShip(r,c,true);
                    user.playerGrid.setHasShip(r,c,true);
                }
            }
        }

        //update life for all user's ships
        temp=next();
        temp=next();
        temp=next();
        int carrierLife = readInteger();
        int battleshipLife = readInteger();
        int cruiserLife = readInteger();
        int submarineLife = readInteger();
        int destroyerLife = readInteger();
        user.ships[0].setLife(carrierLife);
        user.ships[1].setLife(battleshipLife);
        user.ships[2].setLife(cruiserLife);
        user.ships[3].setLife(submarineLife);
        user.ships[4].setLife(destroyerLife);

        //update life for all computer's ships
        temp=next();
        temp=next();
        temp=next();
        carrierLife = readInteger();
        battleshipLife = readInteger();
        cruiserLife = readInteger();
        submarineLife = readInteger();
        destroyerLife = readInteger();
        comp.ships[0].setLife(carrierLife);
        comp.ships[1].setLife(battleshipLife);
        comp.ships[2].setLife(cruiserLife);
        comp.ships[3].setLife(submarineLife);
        comp.ships[4].setLife(destroyerLife);

        //update total hits for user (how many hits user's ships already received)
        temp=next();
        temp=next();
        temp=next();
        int userTotalHits = readInteger();
        user.playerGrid.setTotalHits(userTotalHits);

        //update total hits for computer (how many hits computer's ships already received)
        temp=next();
        temp=next();
        temp=next();
        int compTotalHits = readInteger();
        comp.playerGrid.setTotalHits(compTotalHits);

        //record if computer shoot all even grids or odd grids (only be used when in hard mode)
        temp=next();
        String evenParityRecord = next();
        evenParity = evenParityRecord.equals("true");
    }


    //this method take a boolean var as parameter to tell if current round of game is resumed or new game, then build up the
    //actual game play scene depending on the information acquire from the .txt file (if resumed) or initialize everything (if new game)
    public static Parent setUpGame(boolean isResumed) throws IOException {
        //this BorderPane var is used to design the layout of game play scene
        BorderPane root = new BorderPane();
        root.setPrefHeight(570);


        //display prompt text on text field to let user what info type in which text field
        statusInput.setPromptText("Input status");
        shipInput.setPromptText("Input ship name");
        //depending on whose turn is it, update whoTurnOutput and compAlreadyMakeGuess
        if(myTurn) {
            whoTurnOutput.setText("User's Turn");
            compAlreadyMakeGuess = true;
        } else {
            whoTurnOutput.setText("Computer's Turn");
            compAlreadyMakeGuess = false;
        }
        //apply specific css visual style to each output and button
        endGameOutput.getStyleClass().add("label-bigText");
        whoTurnOutput.getStyleClass().add("label-bigText");
        userScoreOutput.getStyleClass().add("label-rightPane");
        compScoreOutput.getStyleClass().add("label-rightPane");
        timerText.getStyleClass().add("label-rightPane");
        compGuessOutput.getStyleClass().add("label-rightPane");
        warningOutput.getStyleClass().add("label-rightPane");
        returnHome.getStyleClass().add("button-inGame");
        save.getStyleClass().add("button-inGame");
        //initially, set gameEnd = false
        gameEnd = false;


        //set the background image of the game play scene, modify to suitable sizes and apply gaussian blur effect on it
        Image bImage = new Image(Paths.get("src/main/resources/sample/demo1/image5.jpg").toUri().toString());
        ImageView iView = new ImageView(bImage);
        iView.setFitHeight(570);
        iView.setFitWidth(1070);
        iView.setEffect(new GaussianBlur(6.5));
        root.getChildren().addAll(iView);


        //set on action of "save progress" button when it's being clicked
        save.setOnAction(e -> {
            clickSound();
            //if the game isn't finished, display the alert box (code in ReturnHomeAlertBox.java) to confirm if user want to save the game
            if(!gameEnd) {
                backgroundMusicPlayer.pause();
                try {ReturnHomeAlertBox.display("Save Game Progress Confirmation", (Stage)save.getScene().getWindow(), timer);}
                catch (IOException ex) {ex.printStackTrace();}
            }
            //if the game is already finished, the program doesn't allow user to save game anymore
            else {warningSound();}
        });


        //set on action of "return to Home Screen" button when it's being clicked
        returnHome.setOnAction(e-> {
            clickSound();
            backgroundMusicPlayer.pause();
            //display the alert box (code in ReturnHomeAlertBox.java) to confirm if user actually want to return to home screen
            try {ReturnHomeAlertBox.display("Return Home Screen Confirmation", (Stage)returnHome.getScene().getWindow(), timer);}
            catch (IOException ex) {ex.printStackTrace();}
        });


        //set on action of "submit" button when it's being clicked
        submit.setOnAction(e-> {
            //the submit button only works when it's computer's turn And the game isn't over
            if(!myTurn && !gameEnd) {
                status = statusInput.getText();
                shipName = shipInput.getText();

                //when the program detect user input the invalid status or invalid ship name, inputs aren't accepted and
                //text fields are being cleared (ask user to retype)
                if(!(inputValidStatus(status)) || !(inputValidShipName(status, shipName))) {
                    warningSound();
                    status = null;
                    shipName = null;
                    statusInput.clear();
                    shipInput.clear();
                }
                //when user inputs are accepted, update the computer's target board based on the computer's guess provided and
                //clear text fields, change myTurn var and whoTurnOutput text
                else {
                    compGuessUpdate(compGuess, userGrid, compScore);
                    myTurn = true;
                    whoTurnOutput.setText("User's Turn");
                    statusInput.clear();
                    shipInput.clear();
                    warningOutput.setText("");
                }
                //when it's computer's turn and computer made the final guess that cause user lost, stop the timer, pause the
                //background music, display lose sound and change related labels
                if(user.playerGrid.isLost()) {
                    backgroundMusicPlayer.pause();
                    loseSound();
                    endGameOutput.setText("YOU LOSE... ALL SHIPS ARE SUNK");
                    whoTurnOutput.setText("GAME OVER");
                    gameEnd = true;
                    timer.stop();
                }
            }
            else {
                warningSound();
                statusInput.clear();
                shipInput.clear();
            }
        });


        //set on action of oppGrid (user's target board) when any cell of board is being clicked
        oppGrid = new Grids(event -> {
            //when the game is over, mouse action doesn't work
            if(gameEnd) return;
            //get the Position object that current mouse is clicking
            Position cell = (Position) event.getSource();
            //when user click the cell that's already guessed, do nothing
            if(!cell.isNotGuessed()) return;

            //if is user's turn, update left board and change myTurn and compAlreadyMakeGuess var
            if(myTurn) {
                userGuess(user, comp, oppGrid, userScore, cell);
                myTurn = false;
                compAlreadyMakeGuess = false;
            }

            //if is user's turn and computer lose after user taking a guess, pause the background music, stop the timer, play
            //win sound and change related labels
            if(comp.playerGrid.isLost()) {
                backgroundMusicPlayer.pause();
                winSound();
                endGameOutput.setText("YOU SUNK ALL COMPUTER'S SHIPS, YOU WIN!");
                whoTurnOutput.setText("GAME OVER");
                gameEnd = true;
                timer.stop();
            }

            //when user made the guess and is being updated, computer doesn't make the guess and game isn't over, computer will
            //make a guess (the only guess) as compAlreadyMakeGuess is set to true after calling compGuess method
            if(!myTurn && !compAlreadyMakeGuess && !gameEnd) {
                whoTurnOutput.setText("Computer's Turn");
                compGuess = compGuess(user, comp, level);
                compAlreadyMakeGuess = true;
            }
        });


        //the EventHandler for userGrid is empty since clicking on computer's target board won't do anything. The board is
        //updated based on the user's inputs, not clicks (also the program don't know the user's placements of ships initially)
        userGrid = new Grids(event ->{
        });


        //if the game is a resumed one, update all vars using readFile method
        if(isResumed) readFile();


        //if the game is a new game, setup computer's ship placements and determine evenParity var
        if(!isResumed) {
            compSetUp(comp, oppGrid);
            if(Randomizer.nextInt(0,1) == 0) evenParity = true;
            else evenParity = false;
        }


        //This block of code is used to generate first computer's guess if computer goes first, since generating
        //computer's guess required click on oppGrid. With this method we don't need to click on the oppGrid
        //when computer goes first but can get computer's guess directly (only for first computer guess as this
        // block of code only run one time as it's not in the EventHandler)
        if(!myTurn) {
            whoTurnOutput.setText("Computer's Turn");
            compGuess = compGuess(user, comp, level);
            compAlreadyMakeGuess = true;
        }


        //For Debugging and Testing, printing the computer's ship arrangements
        comp.playerGrid.printShipArrangements();


        //set up two column number 1-10 for two grids and display to user for better user experience (user don't need to count
        //which column for a cell but can tell directly)
        HBox rowList = new HBox(18.3);
        HBox rowList2 = new HBox(18.3);
        for(int i = 1; i <= 10; i++) {
            Label temp = new Label();
            Label temp2 = new Label();
            temp.setText(i+"");
            temp.getStyleClass().add("label-rowColNum");
            temp2.setText(i+"");
            temp2.getStyleClass().add("label-rowColNum");
            rowList.getChildren().add(temp);
            rowList2.getChildren().add(temp2);
        }
        rowList.setPadding(new Insets(10,10,5,7));
        rowList2.setPadding(new Insets(10,10,5,7));


        //set up two row value A-J for two grids and display to user for better user experience (user don't need to count
        //which row for a cell but can tell directly)
        VBox colList = new VBox(8);
        VBox colList2 = new VBox(8);
        //using ASCII table to convert int into char
        for(int i = 65; i <= 74; i++) {
            Label temp = new Label();
            temp.setText(((char)i) + "");
            temp.getStyleClass().add("label-rowColNum");
            colList.getChildren().add(temp);
            Label temp2 = new Label();
            temp2.setText(((char)i) + "");
            temp2.getStyleClass().add("label-rowColNum");
            colList2.getChildren().add(temp2);

        }
        colList.setPadding(new Insets(40,10,0,10));
        colList2.setPadding(new Insets(40,10,0,10));


        //combine the two row lists (A-J), two column lists (1-10) with oppGrid and userGrid (layout design)
        VBox oppGridRow = new VBox(rowList, oppGrid);
        HBox oppGridRowCol = new HBox(colList, oppGridRow);
        VBox userGridRow = new VBox(rowList2, userGrid);
        HBox userGridRowCol = new HBox(colList2, userGridRow);
        //combine two grids with row list and column list together horizontally (layout design)
        HBox Grids = new HBox(50, oppGridRowCol, userGridRowCol);
        //combine status, sunk and endgame outputs together vertically (layout design)
        VBox outputs = new VBox(10, statusOutput, sunkOutput, endGameOutput);
        //combine timer, user score output and computer score output together vertically (layout design)
        VBox scoreTimerOutputs = new VBox(5, timerText, userScoreOutput, compScoreOutput);
        //combine everything that should display on right side of scene together vertically (layout design)
        VBox rightPane = new VBox(10, scoreTimerOutputs, compGuessOutput, statusInput, shipInput, submit, warningOutput);
        //combine returnHome button and save progress button together vertically (layout design)
        VBox topLeftLayout = new VBox(10, returnHome, save);
        //combine everything that should display on top of the border pane together horizontally (layout design)
        HBox topPane = new HBox(topLeftLayout, whoTurnOutput);
        topPane.setSpacing(175);


        //initialize timer (if the game is resumed, since timerCount and timerMinCount is updated, no effects)
        setUpTimer();


        //more codes for setting paddings for each GUI component in game play scene (layout design)
        Grids.setPadding(new Insets(5,10,0,30));
        rightPane.setPadding(new Insets(0,30,20,10));
        outputs.setPadding(new Insets(0,0,10,0));
        scoreTimerOutputs.setPadding(new Insets(0, 0, 15, 0));
        whoTurnOutput.setPadding(new Insets(15, 0,0,0));
        topPane.setPadding(new Insets(15,0,0,40));


        //more codes for setting alignments for each GUI component in game play scene (layout design)
        Grids.setAlignment(Pos.CENTER);
        outputs.setAlignment(Pos.CENTER);
        rightPane.setAlignment(Pos.CENTER_LEFT);
        BorderPane.setAlignment(outputs, Pos.BOTTOM_CENTER);


        //set corresponding GUI components to their location in BorderPane and return the BorderPane for setting up scene
        root.setTop(topPane);
        root.setCenter(Grids);
        root.setBottom(outputs);
        root.setRight(rightPane);
        return root;
    }


    //this method takes no parameters and will loop the background music based on .mp3 files in songList as soon as being called
    public void playBackgroundMusic() {
        //syntax for setting up Media and Media player, the volume of music and play it
        backgroundMusicName = songList[songNumber];
        backgroundMusic = new Media(Paths.get(backgroundMusicName).toUri().toString());
        backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
        backgroundMusicPlayer.setVolume(0.43);
        backgroundMusicPlayer.play();

        //when a music reach the end, it will move to next song in songList and play that music
        //when songList reach the end, the first music in songList will be played
        //as long as this method is called, background music will always be played until 1. the program stop running
        //2. alert boxes shown up 3. the game is over
        backgroundMusicPlayer.setOnEndOfMedia(() -> {
            backgroundMusicPlayer.stop();
            songNumber++;
            if(songNumber < songList.length) {playBackgroundMusic();}
            else {
                songNumber = 0;
                playBackgroundMusic();
            }
        });
    }


    //this method takes no parameter and will play the click button sound for one time when being called
    public static void clickSound() {
        musicName = "click.mp3";
        music = new Media(Paths.get(musicName).toUri().toString());
        effectPlayer = new MediaPlayer(music);
        effectPlayer.play();
    }


    //this method takes no parameter and will play the missile shooting and miss sound for one time when being called
    public static void missSound() {
        musicName = "shoot.mp3";
        music = new Media(Paths.get(musicName).toUri().toString());
        effectPlayer = new MediaPlayer(music);
        effectPlayer.play();

        //when missile shooting sound is over, the water drop sound will be played to indicate a miss
        effectPlayer.setOnEndOfMedia(() -> {
            musicName = "miss.mp3";
            music = new Media(Paths.get(musicName).toUri().toString());
            effectPlayer = new MediaPlayer(music);
            effectPlayer.play();
        });
    }


    //this method takes no parameter and will play the missile shooting and hit/sunk sound for one time when being called
    public static void hitSound() {
        musicName = "shoot.mp3";
        music = new Media(Paths.get(musicName).toUri().toString());
        effectPlayer = new MediaPlayer(music);
        effectPlayer.play();

        //when missile shooting sound is over, the exploded sound will be played to indicate a hit/sunk
        effectPlayer.setOnEndOfMedia(() -> {
            musicName = "hit.mp3";
            music = new Media(Paths.get(musicName).toUri().toString());
            effectPlayer = new MediaPlayer(music);
            effectPlayer.play();
        });
    }


    //this method takes no parameter and will play the user win sound effect when being called
    public static void winSound() {
        effectPlayer.stop();
        String musicName = "win.mp3";
        Media music = new Media(Paths.get(musicName).toUri().toString());
        MediaPlayer effectPlayer = new MediaPlayer(music);
        effectPlayer.play();
    }


    //this method takes no parameter and will play the user lose sound effect when being called
    public static void loseSound() {
        effectPlayer.stop();
        String musicName = "lose.mp3";
        Media music = new Media(Paths.get(musicName).toUri().toString());
        MediaPlayer effectPlayer = new MediaPlayer(music);
        effectPlayer.play();
    }


    //this method takes no parameter and will play the warning sound effect when being called
    public static void warningSound() {
        effectPlayer.stop();
        String musicName = "warning.mp3";
        Media music = new Media(Paths.get(musicName).toUri().toString());
        MediaPlayer effectPlayer = new MediaPlayer(music);
        effectPlayer.play();
    }


    //this method takes no parameter and will set up Animation for tracking timer during the game play
    public static void setUpTimer() {
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        //KeyFrame can define some actions at a specified point in time
        //for every one second, the timerCount for seconds will increase by 1
        KeyFrame frame = new KeyFrame(Duration.seconds(1), e-> {
            timerCount++;
            timerText.setText("Timer: " + timerMinCount + " min " + timerCount + " sec");
            //when timerCount for seconds reach 59, we increase minCount by 1 and reset timerCount to indicate 1 min. has passed
            if(timerCount == 59) {
                timerMinCount++;
                timerCount = -1;
            }
        });

        //update the Timeline objects with KeyFrame (animate) and play the animation from initial position (0 min. 0 sec. or resumed time)
        timer.getKeyFrames().add(frame);
        timer.playFromStart();
    }


    //this method takes no parameter and will make a copy of the "BattleshipRecord.txt"
    public static void makeFileCopy() {
        try{
            recordFileCopy = new File("BattleshipRecordCopy.txt");
            //when the "BattleshipRecordCopy.txt" doesn't exist (being manually removed or first time running the program
            //in current device), create the copy .txt file
            if(!recordFileCopy.exists() || !recordFileCopy.isFile()) {recordFileCopy.createNewFile();}
        } catch (Exception e) {e.printStackTrace();}

        //syntax for copying the whole file into another file
        try {Files.copy(recordFile.toPath(), recordFileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);}
        catch (Exception e)  {e.printStackTrace();}
    }


    //this method takes no parameter and will create the "BattleshipRecord.txt" file once the program run but this file will
    //also be cleared everytime the program run (which cannot store info for previous game when user exist the program)
    public static void createFile() throws FileNotFoundException {
        try{
            recordFile = new File("BattleshipRecord.txt");
            //if recordFile doesn't exist, create a new .txt file
            if(!recordFile.exists() || !recordFile.isFile()) {recordFile.createNewFile();}
        } catch (Exception e) {e.printStackTrace();}

        //assign PrintWriter pw to recordFile in order to write info on the recordFile
        pw = new PrintWriter(recordFile);
    }


    //this method takes no parameter and will write important information that's required to resume the game when user press
    //the "save progress" button to save the game
    //the info recorded are quite obvious to see based on the String in front of each variable (for notification)
    public static void writeOnFile() {
        pw.println("myTurn: " + myTurn);
        pw.println("level: " + level);
        pw.println("gameEnd: " + gameEnd);
        pw.println("timerCount: " + timerCount);
        pw.println("timerMinCount: " + timerMinCount);
        pw.println("userScore: " + userScore.getScore());
        pw.println("compScore: " + compScore.getScore());
        pw.println("whoTurnOutput: " + whoTurnOutput.getText());
        pw.println("statusOutput: " + statusOutput.getText());
        pw.println("sunkOutput: " + sunkOutput.getText());
        pw.println("Left Board: ");
        comp.playerGrid.recordStatus();             //using recordStatus method from Grid to help print the whole grid status on file
        comp.playerGrid.recordShipArrangements();   //using recordShipArrangements method from Grid to help print the whole ship arrangements on file
        pw.println("Computer Ship directions: " + comp.ships[0].getDirection() +" " + comp.ships[1].getDirection() +" " + comp.ships[2].getDirection() + " " + comp.ships[3].getDirection() + " " + comp.ships[4].getDirection());
        pw.println("Right Board: ");
        comp.oppGrid.recordStatus();                //comp.oppGrid is the same as user.playerGrid
        comp.oppGrid.recordShipArrangements();
        pw.println("User's ship life: " + user.ships[0].getLife() + " " + user.ships[1].getLife() + " " + user.ships[2].getLife() + " " + user.ships[3].getLife() + " " + user.ships[4].getLife());
        pw.println("Computer's ship life: " + comp.ships[0].getLife() + " " + comp.ships[1].getLife() + " " + comp.ships[2].getLife() + " " + comp.ships[3].getLife() + " " + comp.ships[4].getLife());
        pw.println("User ships' totalHits: " + user.playerGrid.getTotalHits());
        pw.println("Computer ships' totalHits: " + comp.playerGrid.getTotalHits());
        pw.println("evenParity: " + evenParity);
        pw.close();                                 //close the PrintWrite to indicate finish in writing on file
    }


    //this method takes a Stage object as parameter and is the main entry point of the whole javafx application
    //it's the first method being called when program runs
    public void start(Stage stage) throws Exception {

        //loading the first Parent object with fxml home-view file and set up scene based on this Parent object
        Parent homeRoot = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        homeScene = new Scene(homeRoot);
        stage.setTitle("Battleship Game");
        //apply css style on home screen
        homeScene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());

        //set up a stage for displaying scene
        stage.setScene(homeScene);
        stage.setResizable(false);

        //play the background music and create the "BattleshipRecord.txt" file
        playBackgroundMusic();
        createFile();
        stage.show();

        //make sure stage always displayed in center of the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }


    //The main() method is ignored in correctly deployed JavaFX application and serves only as fallback in case the application
    //can not be launched through deployment artifacts,
    public static void main(String[] args) {launch(args);}







    


    /*
    ================================================== Code Logic Part ================================================================
     */


    //this method takes computer's guess, userGrid and computer's score to update the appearance of userGrid when user input
    //valid status and shipName based on computer's guess. This method returns nothing but only updating color and related vars
    //in game
    public static void compGuessUpdate(int[] compGuess, Grids userGrid, Score compScore) {
        //obtain computer's row guess and column guess
        int r = compGuess[0];
        int c = compGuess[1];

        //convert computer's row guess and column guess to user-friendly outputs to display to user
        String outputR = convertIndexToStringRow(r);
        String outputC = convertIndexToStringCol(c)+"";

        //convert user input status to upper case and user input shipName to lower case
        status = status.toUpperCase();
        shipName = shipName.toLowerCase();
        //an integer type var used to determine the index of ship if being hit based on the order in Player String[] SHIP_NAME
        //order: {"carrier", "battleship", "cruiser", "submarine", "destroyer"}
        int shipInx;


        //when status equal to hit or sunk
        if(status.equals("HIT") || status.equals("SUNK")) {
            hitSound();
            //mark hit status on comp.oppGrid, user.playerGrid and userGrid
            comp.oppGrid.markHit(r, c);
            user.playerGrid.markHit(r,c);
            userGrid.getOnePosition(r,c).markHitGUI();

            //set the name of the ship being hit and change hasShip attribute into true for comp.oppGrid and user.playerGrid,
            //then update statusOutput label to display text
            //we don't update userGrid here because it's only for demonstrating GUI, all logics are made based on Player comp
            //and Player user
            comp.oppGrid.setShipName(r, c, shipName);
            user.playerGrid.setShipName(r,c, shipName);
            comp.oppGrid.setHasShip(r,c,true);
            user.playerGrid.setHasShip(r,c, true);
            statusOutput.setText("-- COMPUTER HIT USER'S " + shipName.toUpperCase() + " AT ROW '" + outputR + "' COLUMN '" + outputC + "' --");

            //update computer's score and compScoreOutput label to display text
            compScore.hitShipUpdateScore();
            compScoreOutput.setText("Computer's Score: " + compScore.getScore());

            //get index of the ship being hit/sunk and reduce that ships life in Player user
            shipInx = Arrays.asList(user.SHIP_NAME).indexOf(shipName);
            user.ships[shipInx].reduceLife();

            //check if current ship being hit is sunk after receiving hit
            if(user.ships[shipInx].isSunk()) {
                compScore.sunkShipUpdateScore();                                            //get bonus score for sunk a ship
                compScoreOutput.setText("Computer's Score: " + compScore.getScore());       //update text in compScore label to display
                changeHitToSunk(comp, user, userGrid, shipName);                            //trace other hits from the same ship and change hit status for those cells into sunk
                sunkOutput.setText("-- USER'S " + shipName.toUpperCase() + " IS SUNK! --"); //update text in sunkOutput label to display
            }
        }

        //when computer miss the target
        else {
            missSound();
            //mark miss status on comp.oppGrid, user.playerGrid and userGrid, then update text in statusOutput to display
            comp.oppGrid.markMiss(r,c);
            user.playerGrid.markMiss(r,c);
            userGrid.getOnePosition(r,c).markMissGUI();
            statusOutput.setText("-- COMPUTER MISS AT ROW '" + outputR + "' COLUMN '" + outputC +"' --");
        }
    }


    //This method takes user, comp and level of the game as parameter to determine computer's guess then perform related actions
    //(update labels) based on the computer's guess acquired
    //it will return an integer array with length 2 to indicate computer's guess
    public static int[] compGuess(Player user, Player comp, String level) {
        int[] compGuess = new int[2];

        //according to different level of computer, program will choose different strategy to determine guesses
        if(level.equals("easy")) {compGuess = compCalculateEasyGuess(comp);}
        else {compGuess = compCalculateHardGuess(user, comp);}

        //obtain row # and column # from compGuess
        int r = compGuess[0];
        int c = compGuess[1];

        //update compGuessOutput based on r and c and display to user
        //we add some spaces to compGuessOutput when column # isn't 10 because the column list on GUI (1-10) will shrink
        //(cannot display full text) as compGuessOutput is always changing
        if(convertIndexToStringCol(c) != 10) {compGuessOutput.setText("COMPUTER GUESS: " + convertIndexToStringRow(r) + "" + convertIndexToStringCol(c) + "  ");}
        else {compGuessOutput.setText("COMPUTER GUESS: " + convertIndexToStringRow(r) + "" + convertIndexToStringCol(c));}
        return compGuess;
    }


    //This method takes user, comp and if computer shoot all even grids as parameters to calculate the computer's guess in hard
    //mode. It will return an integer array with length 2 to indicate computer's guess
    public static int[] compCalculateHardGuess(Player user, Player comp) {
        int[] guess = new int[2];               //an integer array used to store computer's guess
        int[][] temp;                           //an integer 2D array used to store the temporary probability distribution for each ship
        int[][] totalProb = new int[10][10];    //an integer 2D array used to store the total probability distribution combined that of all ships
        int r = -1;                             //an integer rep. the row # of computer's guess
        int c = -1;                             //an integer rep. the column # of computer's guess
        int maxProb;                            //an integer rep. the max value in totalProb 2D array

        //check if we need to implement hunt & target strategy if there's "hit" exist on oppGrid
        String shipBeHit = checkContainHit(comp);
        if(!shipBeHit.equals("NULL")) {
            int shipInx = Arrays.asList(comp.SHIP_NAME).indexOf(shipBeHit);             //get the index of the ship being hit based on SHIP_NAME in Player class
            guess = huntAndTarget(comp, comp.ships[shipInx].getLength(), shipBeHit);    //implement huntAndTarget method to obtain the computer's guess by tracing the hits until a ship is sunk
        }

        //when there's no hit on grid, we need to calculate the probability distribution for whole grids
        else {
            for(int i = 0; i < 5; i++) {
                //for each ship in Player user, if the ship isn't sunk, the program will calculate the probability distribution
                //for that specific ship in grid (based on number of ways that can place the ship) and sum temp 2D array with
                //totalProb
                if(!user.ships[i].isSunk()) {
                    temp = probabilityDistributionForEachShip(comp, user.ships[i].getLength());
                    totalProb = sumTwoProbability(totalProb, temp);
                }
            }

            //find max value in totalProb array
            maxProb = findMaxProb(totalProb);

            //since we force computer to shoot all even/odd grids, there're cases where the maxProb locate on only even/odd grid
            //that computer cannot shoot, maxProbIsOnCorrectCell is used to determine if maxProb only locates on one type of cells
            //while computer has to shoot another
            boolean maxProbIsOnCorrectCell = false;
            //if maxProb indeed only locate on cells computer cannot shoot, we need to determine the maxProb on cells computer
            //can shoot and choose that as target, maxProbOnCurrentParity stores the maxProb on cells computer can shoot
            int maxProbOnCurrentParity = -1;

            //loop through whole totalProb 2D array
            for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 10; j++) {
                    //when computer can only shoot even cells
                    if(evenParity) {                //even cell means (row # + column #) % 2 == 0
                        if( (i+j) % 2 == 0) {       //check if current cell is even
                            //if current value on cell is greater than maxProbOnCurrentParity, update that var
                            if(totalProb[i][j] > maxProbOnCurrentParity) {maxProbOnCurrentParity = totalProb[i][j];}
                        }
                        if((i+j) % 2 == 0 && totalProb[i][j] == maxProb){   //check if current cell is even and current cell is equal to maxProb
                            //if so we confirmed maxProb will appear on cells computer can shoot, then simply choose that cell as target and update maxProbIsOnCorrectCell to true
                            maxProbIsOnCorrectCell = true;
                            break;
                        }
                    }

                    //when computer can only shoot odd cells
                    else {                          //odd cell means (row # + column #) % 2 == 1
                        if((i+j) % 2 == 1) {        //check if current cell is odd
                            //if current value on cell is greater than maxProbOnCurrentParity, update that var
                            if(totalProb[i][j] > maxProbOnCurrentParity) {maxProbOnCurrentParity = totalProb[i][j];}
                        }
                        if( (i+j) % 2 == 1 && totalProb[i][j] == maxProb) { //check if current cell is odd and current cell is equal to maxProb
                            //if so we confirmed maxProb will appear on cells computer can shoot, then simply chose that cell as target and update maxProbIsOnCorrectCell to true
                            maxProbIsOnCorrectCell = true;
                            break;
                        }
                    }
                }
                //break the loop if maxProb locate on some cells that computer can shoot
                if(maxProbIsOnCorrectCell) break;
            }


            //When the game enter the last stage, the probability actually don't matter too much as multiple cells all have
            //the same probability (due to small ship size and smaller ship will likely be hit at last)
            //Therefore the program treat "detecting >= 30 cells that have status NOT unguessed as the condition
            //when meet this condition, computer will ignore the probability and shoot cells from edge to centre
            if(findTotalNumOfCheckedCells(comp) >= 30) {
                int[] tempGuess = findCellAtMostEdge(comp, totalProb);
                r = tempGuess[0];
                c = tempGuess[1];
            }

            //When the game is still in early/middle phase, use probability equation
            //there's a high possibility that a grid occur multiple maxProb on different cells, therefore the program will
            //randomly choose the maxProb to shoot for after confirming maxProb exist on cells that computer can shoot
            else {
                if(maxProbIsOnCorrectCell) {
                    ArrayList<Integer> possibleT[] = getPossibleTarget(totalProb, maxProb);
                    int randInx = Randomizer.nextInt(0,possibleT[0].size()-1);
                    r = possibleT[0].get(randInx);
                    c = possibleT[1].get(randInx);
                }

                //when maxProb doesn't exist on cells that computer can shoot, maxProbOnCurrentParity replace the usage of maxProb.
                //Since there may also be multiple cells with maxProbOnCurrentParity value, the program will randomly choose which
                //one to shoot
                else {
                    ArrayList<Integer> possibleT[] = getPossibleTarget(totalProb, maxProbOnCurrentParity);
                    int randInx = Randomizer.nextInt(0,possibleT[0].size()-1);
                    r = possibleT[0].get(randInx);
                    c = possibleT[1].get(randInx);
                }
            }

            //assign row # and column # to computer's guess
            guess[0] = r;
            guess[1] = c;
        }

        //FOR DEBUG: print out totalProb
        //System.out.println();
        //printTotalProbability(totalProb);
        return guess;
    }


    //this method take totalProb 2D array and int maxProb as parameter to record all possible cells that qualify certain
    //conditions to be the next target. And return a list of ArrayList with length 2 to indicate possible targets
    public static ArrayList<Integer>[] getPossibleTarget(int[][] totalProb, int maxProb) {
        ArrayList<Integer> possibleT[] = new ArrayList[2];      //a list of arrayList used to record possible targets, first array will record row # and second record col #
        possibleT[0] = new ArrayList<>();                       //initialize ArrayList in list
        possibleT[1] = new ArrayList<>();

        //loop through user's board to check which cell qualify to be the next target
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {

                //condition to be next target:  1. the value on cell is equal to maxProb in the whole board
                //2. if computer only shoot even cells, current cell is even
                //3. if computer only shoot odd cells, current cell is odd
                if(totalProb[r][c] == maxProb) {
                    if(evenParity && (r+c) % 2 == 0) {
                        possibleT[0].add(r);
                        possibleT[1].add(c);
                    } else if(!evenParity && (r+c) % 2 == 1) {
                        possibleT[0].add(r);
                        possibleT[1].add(c);
                    }
                }
            }
        }
        return possibleT;
    }


    //this method will loop through whole opponent's board and check how many cells have already been marked as
    //hit, miss, or sunk. Taking Player comp as parameter and return # of cells that's not in status "unguessed"
    public static int findTotalNumOfCheckedCells(Player comp) {
        int num = 0;
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {
                if(comp.oppGrid.getPositionStatus(r,c) != -1) num++;
            }
        }
        return num;
    }


    //this method takes Player comp and totalProb as parameter for finding the cell that's "at edge" and haven't been checked
    //Return an int array to indicate that edge cell as target
    public static int[] findCellAtMostEdge(Player comp, int[][] totalProb) {
        int[] guess = new int[2];                           //int array to indicate cell in row and col
        int minDisFromEdge = 100;                           //initial minimum distance of a cell from edge
        int curDisFromEdge = 0;                             //distance of a cell from edge that's currently checking
        ArrayList<Integer> minDisR = new ArrayList<>();     //record all possible target's row # that have same minDisFromEdge
        ArrayList<Integer> minDisC = new ArrayList<>();     //record all possible target's col # that have same minDisFromEdge

        //loop through board to check the distance from edge for each cell
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {

                //condition to be a possible target: 1. status is unchecked and the probability of a ship occuring on that
                //cell isn't 0      2. if computer only shoot even cell, current cell is even   3. if computer only shoot
                //odd cell, current cell is odd
                if(comp.oppGrid.getPositionStatus(i,j) == -1 && totalProb[i][j] != 0) {
                    if(evenParity && (i+j) % 2 == 0) {
                        //distance from edge is calculated by distance from top/bottom * distance from left/right of current cell
                        //depending on the row # and col #, the cell that's close to left edge of board will use col 0 as
                        //reference while close to right edge of board will use col 9 as reference. Similar logic applied to
                        //close to top edge and bottom edge condition
                        int tempI; int tempJ;
                        if(i <= 4) tempI = i;
                        else tempI = (9-i);
                        if(j <= 4) tempJ = j;
                        else tempJ = (9-j);
                        curDisFromEdge = tempI * tempJ;

                        //When current min distance is < original min distance, replace min distance with current one,
                        //clear all previous possible cells (in terms of row # and col #) and add new row # and col #
                        //into array list
                        if(curDisFromEdge < minDisFromEdge) {
                            minDisR.clear();
                            minDisC.clear();
                            minDisFromEdge = curDisFromEdge;
                            minDisR.add(i);
                            minDisC.add(j);
                        }
                        //When current min distance = original min distance, record current row # and col # in arraylist
                        //to indicate it's a possible target
                        else if(curDisFromEdge == minDisFromEdge) {
                            minDisR.add(i);
                            minDisC.add(j);
                        }
                    }

                    else if(!evenParity && (i+j) % 2 == 1) {
                        int tempI; int tempJ;
                        if(i <= 4) tempI = i;
                        else tempI = (9-i);
                        if(j <= 4) tempJ = j;
                        else tempJ = (9-j);

                        curDisFromEdge = tempI * tempJ;
                        if(curDisFromEdge < minDisFromEdge) {
                            minDisR.clear();
                            minDisC.clear();
                            minDisFromEdge = curDisFromEdge;
                            minDisR.add(i);
                            minDisC.add(j);
                        } else if(curDisFromEdge == minDisFromEdge) {
                            minDisR.add(i);
                            minDisC.add(j);
                        }
                    }
                }
            }
        }

        //randomly choosing a cell as target from all possible cells with same min distance from edge and return that cell
        int randInx = Randomizer.nextInt(0,minDisR.size()-1);
        guess[0] = minDisR.get(randInx);
        guess[1] = minDisC.get(randInx);
        return guess;
    }




    //This method takes Player comp as parameter and will calculate the computer's guess in easy mode
    //then return an integer array as computer's guess
    public static int[] compCalculateEasyGuess(Player comp) {
        int[] guess = new int[2];       //an integer array used to store computer's guess
        int r, c;                       //two integers rep. the row # and column # of computer's guess

        //check if we need to implement hunt & target strategy if there's "hit" exist on oppGrid
        String shipBeHit = checkContainHit(comp);
        if(!shipBeHit.equals("NULL")) {
            int shipInx = Arrays.asList(comp.SHIP_NAME).indexOf(shipBeHit);             //get the index of the ship being hit based on SHIP_NAME in Player class
            guess = huntAndTarget(comp, comp.ships[shipInx].getLength(), shipBeHit);    //implement huntAndTarget method to obtain the computer's guess by tracing the hits until a ship is sunk
        }

        //when there's no hit on grid, randomly choosing the target
        else {
            while(true) {
                r = Randomizer.nextInt(0, 9);
                c = Randomizer.nextInt(0, 9);
                //make sure computer isn't choosing the target that had already selected before
                if(!comp.oppGrid.isAlreadyGuessed(r, c)) {break;}
            }

            //assign row # and column # to computer's guess
            guess[0] = r;
            guess[1] = c;
        }
        return guess;
    }


    //This method takes Player comp and shipLength as parameters to calculate the probability distributions for the computer's
    //target grid. It will loop though all possible location of a ship based on its length and return a 2D integer array list
    //to indicate the number of possible occurences on each cell
    public static int[][] probabilityDistributionForEachShip(Player comp, int shipLength) {
        int[][] prob = new int[10][10];     //a 2D integer array used to record the probability distribution for a ship

        //assume ship is aligned horizontally and loop through all possible arrangements in computer's target grid
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c + shipLength <= 10; c++) {
                boolean canPlace = true;

                //loop through every single possible placement of a ship on grid (horizontally) to check if the ship can be placed
                //when current placement of ship overlaps with position status that's not unguessed (meaning it's either
                //miss/sunk since grid with "hit" is dealt in other method), we cannot place a ship there
                for(int k = c; k < c + shipLength; k++) {
                    if(comp.oppGrid.getPositionStatus(r, k) != -1) {
                        canPlace = false;
                        break;
                    }
                }

                //if we can place the ship on that location, add 1 on all cells current placement of ship occupies
                if(canPlace) {
                    for(int k = c; k < c + shipLength; k++) {prob[r][k]++;}
                }
            }
        }

        //assume ship is aligned vertically and loop through all possible arrangements in computer's target grid
        for(int c = 0; c < 10; c++) {
            for(int r = 0; r + shipLength <= 10; r++) {
                boolean canPlace = true;

                //loop through every single possible placement of a ship on grid (vertically) to check if the ship can be placed
                //when current placement of ship overlaps with position status that's not unguessed (meaning it's either
                //miss/sunk since grid with "hit" is dealt in other method), we cannot place a ship there
                for(int k = r; k < r + shipLength; k++) {
                    if(comp.oppGrid.getPositionStatus(k, c) != -1) {
                        canPlace = false;
                        break;
                    }
                }

                //if we can place the ship on that location, add 1 on all cells current placement of ship occupies
                if(canPlace) {
                    for(int k = r; k < r + shipLength; k++) {prob[k][c]++;}
                }
            }
        }
        //return the probability distribution for a specific ship in terms of a 2D array
        return prob;
    }


    //This method takes a 2D integer array as parameter and will find the max value in 2D array and return it
    public static int findMaxProb(int[][] total) {
        int max = -1;
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(total[i][j] > max) {max = total[i][j];}
            }
        }
        return max;
    }


    //This method takes two 2D integer arrays as parameter and will sum the corresponding elements up in order to return
    //one 2D integer array as result
    public static int[][] sumTwoProbability(int[][] total, int[][] temp) {
        int[][] temp2 = new int[10][10];
        for(int i = 0; i < 10; i++) {
            //sum each corresponding elements in two arrays up
            for(int j = 0; j < 10; j++) {temp2[i][j] = total[i][j] + temp[i][j];}
        }
        return temp2;
    }


    //This method takes Player comp, length of ship and name of ship being hit as parameters in order to trace the cells around
    //the "hit" mark(s) and return an integer array with length 2 as the computer's guess
    public static int[] huntAndTarget(Player comp, int shipLength, String shipName) {
        int[] guess = new int[2];                           //computer's guess stored in row # column # form
        int targetR;                                        //computer's row # guess
        int targetC;                                        //computer's column # guess
        ArrayList<Integer> allHitR = new ArrayList<>();     //when we assume ship is placing vertically, this arrayList will record all row # that contain hit marks for each placement of ship (the placement must be valid)
        ArrayList<Integer> allHitC = new ArrayList<>();     //when we assume ship is placing horizontally, this arrayList will record all column # that contain hit marks for each placement of ship (the placement must be valid)

        //first assume ship is aligned horizontally and loop through all possible arrangements on grid
        for(int r = 0; r < 10 ; r++) {
            for(int c = 0; c + shipLength <= 10; c++) {
                boolean canPlace = true;

                //loop through every single possible placement of a ship on grid (horizontally) to check if the ship can be placed
                for(int k = c; k < c + shipLength; k++) {
                    //there two several cases that ship cannot be placed around hit: 1. the current checking cell had status miss or sunk
                    //2. the current checking cell is a "hit", however it's not the same ship the program traced (meaning two hits touch each other)
                    //if any cases above is fulfilled, the ship cannot be placed around hit mark
                    if(comp.oppGrid.getPositionStatus(r, k) == 0 || comp.oppGrid.getPositionStatus(r, k) == 2 || (!comp.oppGrid.getShipName(r,k).equals(shipName) && comp.oppGrid.getPositionStatus(r, k) == 1)) {
                        canPlace = false;
                        break;
                    }
                }

                //if ship can be placed on the location the program has checked containing the hit mark
                if(canPlace) {
                    for(int k = c; k < c + shipLength; k++) {
                        //record all column # for every hit that current ship placement occupies
                        if(comp.oppGrid.getPositionStatus(r, k) == 1 && comp.oppGrid.getShipName(r,k).equals(shipName)) {
                            allHitC.add(k);
                        }
                    }

                    //when we recognize current placement of ship contain two or more hits in a row, we can determine our next target because ship might align horizontally
                    if(allHitC.size() >= 2) {
                        targetR = r;            //ship must align horizontally, therefore the computer's row # guess is the same as row # the program is currently checking

                        //to determine the computer's column # guess, the program will check if the cell to the left of all hits is:
                        //1. the cell to the left of all hits exist     2. the cell to the left of all hits is in unguessed status
                        //if both condition is satisfied, then the cell to the left of all hits is the possible target column #
                        //otherwise, the cell to the right of all hits must be the target column #
                        if(allHitC.get(0) - 1 >= 0 && comp.oppGrid.getPositionStatus(targetR, allHitC.get(0) - 1) == -1) {
                            targetC = allHitC.get(0) - 1 ;
                        } else {
                            targetC = allHitC.get(allHitC.size()-1) +1;
                        }

                        //assign computer's row # and column # guesses into integer array and return the guess
                        guess[0] = targetR;
                        guess[1] = targetC;
                        return guess;
                    }

                    //for every location a ship can be placed around hits, allHitR and allHitC are different therefore need to
                    //remove all elements
                    allHitR.clear();
                    allHitC.clear();
                }
            }
        }

        //remove all elements from allHitR and allHitC (again) because if previous code (two hits in a row) doesn't match the
        //requirement to get computer's guess, we need to continue use these two var.
        allHitC.clear();
        allHitR.clear();


        //second assume ship is aligned vertically and loop through all possible arrangements on grid, the logic is quite similar
        //compared to aligned horizontally but just keep chancing variable row instead of column
        for(int c = 0; c < 10; c++) {
            for(int r = 0; r + shipLength <= 10; r++) {
                boolean canPlace = true;

                //loop through every single possible placement of a ship on grid (vertically) to check if the ship can be placed
                for(int k = r; k < r + shipLength; k++) {
                    //there two several cases that ship cannot be placed around hit: 1. the current checking cell had status miss or sunk
                    //2. the current checking cell is a "hit", however it's not the same ship the program traced (meaning two hits touch each other)
                    //if any cases above is fulfilled, the ship cannot be placed around hit mark
                    if(comp.oppGrid.getPositionStatus(k, c) == 0 || comp.oppGrid.getPositionStatus(k, c) == 2 || (!comp.oppGrid.getShipName(k, c).equals(shipName) && comp.oppGrid.getPositionStatus(k,c) == 1)) {
                        canPlace = false;
                        break;
                    }
                }

                //if ship can be placed on the location the program has checked containing the hit mark
                if(canPlace) {
                    for(int k = r; k < r + shipLength; k++) {
                        //record all row # for every hit that current ship placement occupies
                        if(comp.oppGrid.getPositionStatus(k, c) == 1 && comp.oppGrid.getShipName(k,c).equals(shipName)) {
                            allHitR.add(k);
                        }
                    }

                    //when we recognize current placement of ship contain two or more hits in a column, we can determine our next target because ship might align vertically
                    if(allHitR.size() >= 2) {
                        targetC = c;            //ship must align vertically, therefore the computer's column # guess is the same as the column # the program is currently checking

                        //to determine the computer's row # guess, the program will check if the cell to the top of all hits is:
                        //1. the cell to the top of all hits exist     2. the cell to the top of all hits is in unguessed status
                        //if both condition is satisfied, then the cell to the top of all hits is the possible target row #
                        //otherwise, the cell to the bottom of all hits must be the target row #
                        if(allHitR.get(0)-1 >= 0 && comp.oppGrid.getPositionStatus(allHitR.get(0) - 1, targetC) == -1) {
                            targetR = allHitR.get(0) - 1;
                        } else {
                            targetR = allHitR.get(allHitR.size()-1) + 1;
                        }

                        //assign computer's row # and column # guesses into integer array and return the guess
                        guess[0] = targetR;
                        guess[1] = targetC;
                        return guess;
                    }
                    allHitR.clear();
                    allHitC.clear();
                }
            }
        }



        //When code reach at this point and still didn't return any computer guess, it means there's only 1 hit (with same shipName)
        //appear on the grid. Below code will then find out which cell is possible to select as target around the current hit (top/bottom/left/right?)
        int[] p = returnFirstHitPosition(comp);     //get location of the first hit in grid (multiple hits may exist but with different shipName)
        int hitR = p[0];                            //assign row # of the first hit mark to hitR
        int hitC = p[1];                            //assign column # of the first hit mark to hitC
        boolean canPlace = true;                    //if the ship can be placed under specific condition

        int[] guessLeft = {-1,-1};                  //left cell of current "hit" cell
        int[] guessRight = {-1,-1};                 //right cell of current "hit" cell
        int[] guessTop = {-1,-1};                   //top cell of current "hit" cell
        int[] guessBottom = {-1,-1};                //bottom cell of current "hit" cell


        //can ship be placed horizontally with portion of ship occupies the hit label?
        //the reason we loop from 0 to shipLength is we don't know the "hit" we have is the first/second/third... portion of ship
        //therefore we need to check all possibility to ensure a valid placement
        for(int i = 0; i < shipLength; i++) {
            //for a placement to be invalid: 1. the placement is out of bound of the board
            //2. any cell of current placement had status miss or sunk  3. any cell of current placement is a "hit", AND it's not the same ship the program traced
            //if any above condition is fulfilled for any cell in a placement of ship, it's an invalid placement
            if(hitC - i >= 0 && hitC - i + shipLength <= 10) {
                for(int start = hitC - i; start < hitC - i + shipLength; start++) {
                    if(comp.oppGrid.getPositionStatus(hitR, start) == 0 || comp.oppGrid.getPositionStatus(hitR, start) == 2 || (!comp.oppGrid.getShipName(hitR, start).equals(shipName) && comp.oppGrid.getPositionStatus(hitR, start) == 1)) {
                        canPlace = false;
                        break;
                    }

                    //the placement is valid when reach the end of the loop and still didn't satisfy to any above invalid conditions
                    if(start == hitC - i + shipLength - 1) {
                        canPlace = true;
                        break;
                    }
                }


                //if can be placed horizontally on the location currently checking, the computer's row # guess is the same as row # the program is currently checking
                //to determine the computer's column # guess, the program will check the cell to the left of "hit" is:
                //1. cell to the left of "hit" exist        2. cell to the left of "hit" is in unguessed status
                //if both condition is satisfied, then the cell to the left of "hit" is the possible target column #
                //similar logic apply to checking if right cell of current "hit" is a possible target
                if(canPlace) {
                    if(hitC - 1 >= 0 && comp.oppGrid.getPositionStatus(hitR, hitC-1) == -1 ) {
                        guessLeft[0] = hitR;
                        guessLeft[1] = hitC - 1;
                    }
                    if(hitC + 1 <= 9 && comp.oppGrid.getPositionStatus(hitR, hitC+1) == -1) {
                        guessRight[0] = hitR;
                        guessRight[1] = hitC + 1;
                    }
                }
            }
        }

        canPlace = true;

        //can ship be placed vertically with portion of ship occupies the hit label? (the logic is similar for checking
        // horizontal placement of ships, but just keep changing the row # instead of column #)
        for(int j = 0; j < shipLength; j++) {
            //for a placement to be invalid: 1. the placement is out of bound of the board
            //2. any cell of current placement had status miss or sunk  3. any cell of current placement is a "hit", AND it's not the same ship the program traced
            //if any above condition is fulfilled for any cell in a placement of ship, it's an invalid placement
            if(hitR - j >= 0 && hitR - j + shipLength <= 10) {
                for(int start = hitR - j; start < hitR - j + shipLength; start++) {
                    if(comp.oppGrid.getPositionStatus(start, hitC) == 0 || comp.oppGrid.getPositionStatus(start, hitC) == 2 || (!comp.oppGrid.getShipName(start, hitC).equals(shipName) && comp.oppGrid.getPositionStatus(start, hitC) == 1)) {
                        canPlace = false;
                        break;
                    }

                    //the placement is valid when reach the end of the loop and still didn't satisfy to any above invalid conditions
                    if(start == hitR - j + shipLength - 1) {
                        canPlace = true;
                        break;
                    }
                }

                //if can be placed vertically on the location currently checking, the computer's column # guess is the same as column # the program is currently checking
                //to determine the computer's row # guess, the program will check the cell to the top of "hit" is:
                //1. cell to the top of "hit" exists        2. cell to the top of "hit" is in unguessed status
                //if both condition is satisfied, then the cell to the top of "hit" is the possible target row #
                //similar logic applied to checking if bottom cell is a possible target
                if(canPlace) {
                    if(hitR - 1 >= 0 && comp.oppGrid.getPositionStatus(hitR-1, hitC) == -1) {
                        guessTop[0] = hitR-1;
                        guessTop[1] = hitC;
                    }
                    if(hitR + 1 <= 9 && comp.oppGrid.getPositionStatus(hitR+1, hitC) == -1) {
                        guessBottom[0] = hitR+1;
                        guessBottom[1] = hitC;
                    }
                }
            }
        }

        //selecting target in the clockwise order: left, top, right, bottom
        if(guessLeft[0] != -1) return guessLeft;
        else if(guessTop[0] != -1) return guessTop;
        else if(guessRight[0] != -1) return guessRight;
        else return guessBottom;
    }


    //This method takes Player comp as parameter and will loop through whole comp.oppGrid to get the location where first "hit"
    //appear, then return an integer array with length 2 to indicate "hit" position
    public static int[] returnFirstHitPosition(Player comp) {
        int[] pos = new int[2];     //an integer array to store the position of first "hit"
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {
                if(comp.oppGrid.getPositionStatus(r, c) == 1) {
                    pos[0] = r;
                    pos[1] = c;
                    return pos;
                }
            }
        }
        return pos;                 //must have this statement to make sure an integer array is returned, although it's useless because the program will only call this method when it detects "hit" on label
    }


    //This method takes Player comp as parameter to loop through comp.oppGrid to see if "hit" exists. If exists, method will
    //return the ship being hit and if not, method will return String "NULL" to indicate no "hit"
    public static String checkContainHit(Player comp) {
        String shipBeHit = "NULL";      //a String type var used to record the name of the ship being hit on grid (if there's any)
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {
                if(comp.oppGrid.getPositionStatus(r, c) == 1) {
                    return comp.oppGrid.getShipName(r, c).toLowerCase();
                }
            }
        }
        return shipBeHit;
    }


    //This method takes Player user, comp, user's target grid, user's score and the Position object user clicked as the target
    //as parameters and return nothing. But will do following actions: 1. does user's selected target contain any ship?
    //2. is there any computer's ship sunk after user's selection?
    //3. update output text (user miss/hit/sunk what ship) and user's score based on result of selecting target
    //4. update color of cells in GUI
    public static void userGuess(Player user, Player comp, Grids oppGrid, Score userScore, Position target) {
        int r = target.getR();                              //the row # of user's selected target
        int c = target.getC();                              //the column # of user's selected target
        String outputR = convertIndexToStringRow(r);        //convert row # 0 - 9 to A - J
        String outputC = convertIndexToStringCol(c) + "";   //convert column # 0 - 9 to 1 - 10 (since the convertIndexToStringCol return an int, need to manually convert to String type)

        //if user's target contain any computer's ship
        if(comp.playerGrid.getHasShip(r, c)) {
            hitSound();

            //mark hit on user.oppGrid, comp.playerGrid and update target color on GUI
            //update user's score and display information based on user's target selection and current user's score on GUI
            user.oppGrid.markHit(r,c);
            comp.playerGrid.markHit(r,c);
            target.markHitGUI();
            String shipBeHit = comp.playerGrid.getShipName(r, c);
            statusOutput.setText("-- USER HIT COMPUTER'S " + shipBeHit.toUpperCase() + " AT ROW '" + outputR + "' COLUMN '" + outputC + "' --");
            userScore.hitShipUpdateScore();
            userScoreOutput.setText("User's Score: " + userScore.getScore());

            //an integer type var used to determine the index of ship if being hit based on the order in Player String[] SHIP_NAME
            //order: {"carrier", "battleship", "cruiser", "submarine", "destroyer"}
            int shipInx = Arrays.asList(comp.SHIP_NAME).indexOf(shipBeHit);
            //reduce the life of ship being hit by 1
            comp.ships[shipInx].reduceLife();

            //when any computer's ship is sunk after current user's hit
            if(comp.ships[shipInx].isSunk()) {
                //update the text displaying to user, change from hit info to sunk info
                //update user's score by providing bonus score for sunk a ship and display new user score info
                //call changeHitToSunk method to convert all hit status to sunk for the ship that's already sunk and change
                //color from red into black on GUI
                sunkOutput.setText("-- COMPUTER'S '" + shipBeHit.toUpperCase() + "' IS SUNK! --");
                userScore.sunkShipUpdateScore();
                userScoreOutput.setText("User's Score: " + userScore.getScore());
                changeHitToSunk(user, comp, oppGrid, shipBeHit);
            }
        }

        //when user miss
        else {
            missSound();

            //mark miss on user.oppGrid, comp.playerGrid and update target color on GUI
            //update and display information based on user's target selection
            user.oppGrid.markMiss(r,c);
            comp.playerGrid.markMiss(r,c);
            oppGrid.markMiss(r,c);
            target.markMissGUI();
            statusOutput.setText("-- USER MISS AT ROW '" + outputR + "' COLUMN '" + outputC + "' --");
        }
    }


    //This method takes two Player, opponent's grid and ship being sunk as parameters and change all hit status to sunk status
    //for a ship that's already sunk.
    //Notice that the currentP, currentOpp and oppGrid parameters are not determined. If currentP = user, then currentOpp = comp
    //and oppGrid = oppGrid. However if currentP = comp, then currentOpp = user and oppGrid = userGrid
    public static void changeHitToSunk(Player currentP, Player currentOpp, Grids oppGrid, String sunkShip) {
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {
                //To check if current cell is the cell we want to change: 1. current cell is in hit status
                //2. the shipName on current cell is the sunk ship the program had confirmed
                //both condition need to satisfy in order to modify oppGrid, currentP.oppGrid and currentOpp.playerGrid
                if(currentP.oppGrid.getPositionStatus(r, c) == 1 && currentOpp.playerGrid.getShipName(r,c).equals(sunkShip)) {
                    oppGrid.getOnePosition(r,c).markSunkGUI();
                    currentP.oppGrid.markSunk(r,c);
                    currentOpp.playerGrid.markSunk(r,c);
                }
            }
        }
    }


    //This method takes Player comp and computer's grid for GUI as parameters in order to set up the initial ship placements
    //for computer and return nothing
    public static void compSetUp(Player comp, Grids oppGrid) {
        //count is an integer var used to locate the ship that's currently placing as index (order "carrier", "battleship", "cruiser", "submarine", "destroyer" in comp.SHIP_NAME)
        //r is an integer var indicate the location of row #
        //c is an integer var indicate the location of column #
        //dire is an integer var indicate the direction of ship placement
        //edgePlaced is an Integer ArrayList that record number 1 to 4 to indicate if a ship is already placed on one edge of board
        int count = 4, r = -1, c = -1, dire = -1;
        ArrayList<Integer> edgePlaced = new ArrayList<>();

        //place the destroyer, submarine and cruiser
        for(int i = 0; i < 3; i++) {
            int edge = Randomizer.nextInt(1, 4);        //get a random edge to place a ship
            while(edgePlaced.contains(edge)) {          //ensure placing ships on different edges
                edge = Randomizer.nextInt(1, 4);
            }

            //place current ship on upper location of the board
            if(edge == 1) {
                if(count != 3) edgePlaced.add(1);
                while(true) {
                    //through tournament, the team discover lots of AI put less focus on central area, therefore the team
                    //decided to put submarine in the central 4x4 cells
                    if(count == 3) {
                        r = Randomizer.nextInt(3,6);
                        c = Randomizer.nextInt(3,4);
                    } else {
                        r = Randomizer.nextInt(0,1);    //place at top edge of grid (either row 1 or row 2)
                        c = Randomizer.nextInt(0,8);    //column # of the ship can be any value (from column 1 to column 9)
                    }
                    dire = 0;                           //direction is set to align horizontally (cuz only destroyer can be placed vertically if we only provide row 1 and 2 to top edge arrangement)

                    //make sure each ship won't overlap with each other, cross the board or doesn't touch each other
                    if(!compHasPlacementError(r, c, dire, comp, count)) {
                        if(!compHasPlacementAdjacent(r, c, dire, comp, count)) {break;}
                    }
                }
            }

            //place current ship on lower location of the grid
            else if(edge == 2) {
                if(count != 3) edgePlaced.add(2);
                while(true) {
                    if(count == 3) {                    //force submarine to place in central 4x4 cells
                        r = Randomizer.nextInt(3,6);
                        c = Randomizer.nextInt(3,4);
                    } else {
                        r = Randomizer.nextInt(8,9);    //place at bottom edge of grid (either row 8 or row 9)
                        c = Randomizer.nextInt(0,8);    //column # of the ship can be any value (from column 1 to column 9)
                    }
                    dire = 0;                           //direction is set to align horizontally (cuz only destroyer can be placed vertically if we only provide row 8 and 9 to bottom edge arrangement)

                    //make sure each ship won't overlap with each other, cross the board or doesn't touch each other
                    if(!compHasPlacementError(r, c, dire, comp, count)) {
                        if(!compHasPlacementAdjacent(r, c, dire, comp, count)) {break;}
                    }
                }
            }

            //place current ship on left location of the grid
            else if(edge == 3) {
                if(count != 3) edgePlaced.add(3);
                while(true) {
                    if(count == 3) {                    //force submarine to place in central 4x4 cells
                        r = Randomizer.nextInt(3,4);
                        c = Randomizer.nextInt(3,6);
                    }
                    else {
                        r = Randomizer.nextInt(0,8);    //row # of the ship can be any value (from row 1 to row 9)
                        c = Randomizer.nextInt(0,1);    //place at left edge of grid (either column 1 or column 2)
                    }
                    dire = 1;                           //direction is set to align vertically (cuz only destroyer can be placed horizontally if we only provide column 1 and 2 to left edge arrangement)

                    //make sure each ship won't overlap with each other, cross the board or doesn't touch each other
                    if(!compHasPlacementError(r, c, dire, comp, count)) {
                        if(!compHasPlacementAdjacent(r, c, dire, comp, count)) {break;}
                    }
                }
            }

            //place current ship on right location of the grid
            else if(edge == 4) {
                if(count != 3) edgePlaced.add(4);
                while(true) {
                    if(count == 3) {                    //force submarine to place in central 4x4 cells
                        r = Randomizer.nextInt(3,4);
                        c = Randomizer.nextInt(3,6);
                    }
                    else {
                        r = Randomizer.nextInt(0,8);    //row # of the ship can be any value (from row 1 to row 9)
                        c = Randomizer.nextInt(8,9);    //place at right edge of grid (either column 8 or column 9)
                    }
                    dire = 1;                           //direction is set to align vertically (cuz only destroyer can be placed horizontally if we only provide column 8 and 9 to right edge arrangement)

                    //make sure each ship won't overlap with each other, cross the board or doesn't touch each other
                    if(!compHasPlacementError(r, c, dire, comp, count)) {
                        if(!compHasPlacementAdjacent(r, c, dire, comp, count)) {break;}
                    }
                }
            }

            //After confirming the location of ship, set Location and Direction of current placing ship and add it into
            //comp and oppGrid
            comp.ships[count].setLocation(r, c);
            comp.ships[count].setDirection(dire);
            comp.playerGrid.addShip(comp.ships[count]);
            oppGrid.addShip(comp.ships[count]);
            //move to place next ship
            count--;
        }


        //place battleship and carrier
        for(int i = 0; i < 2; i++) {
            while(true) {
                //getting random location and direction first
                r = Randomizer.nextInt(0, 9);
                c = Randomizer.nextInt(0, 9);
                dire = Randomizer.nextInt(0, 1);

                //In order for the random location being accepted: 1. placement cannot overlap with other ships or exceed board
                //2. placement of ship cannot touch any other ships   3. placement of ship cannot be placed in middle 4x4 cells
                //if all three condition are satisfied, the random location is acceptable, else get another random location
                if(!compHasPlacementError(r, c, dire, comp, count)) {
                    if(!compHasPlacementAdjacent(r, c, dire, comp, count)) {
                        if(!compHasPlacementCentral(r, c, dire, comp, count)) {break;}
                    }
                }
            }

            //After confirming the location of ship, set Location and Direction of current placing ship and add it into
            //comp and oppGrid
            comp.ships[count].setLocation(r, c);
            comp.ships[count].setDirection(dire);
            comp.playerGrid.addShip(comp.ships[count]);
            oppGrid.addShip(comp.ships[count]);
            //move to place next ship
            count--;
        }
    }


    //This method will take row #, column # and direction of a ship's location, the current placing ship rep. by index order
    //and Player comp to determine if the ship is placed in the middle 4x4 cells (row: D-G, col: 4-7) and return a boolean
    //type var to rep. the result (place central or not central)
    public static boolean compHasPlacementCentral(int r, int c, int dire, Player comp, int count) {
        int length = comp.ships[count].getLength();     //get length of current placing ship

        //if ship is placing horizontally
        if(dire == 0) {
            //checking all cells of a placement of ship. If any cell had column # between 3 and 6 AND row # between 3 and 6,
            //it's considered placing in center which violate the rule of placing ships. Need to re-choose location
            for(int i = c; i < c + length; i++) {
                if(i >= 3 && i <= 6 && r >= 3 && r <= 6) {return true;}
            }
            return false;           //return false when all portion of ship stays in "not centered" area
        }

        //if ship is placing vertically
        else {
            //checking all cells of a placement of ship. If any cell had column # between 3 and 6 AND row # between 3 and 6,
            //it's considered placing in center which violate the rule of placing ships. Need to re-choose location
            for(int i = r; i < r + length; i++) {
                if(i >= 3 && i <= 6 && c >= 3 && c <= 6) {return true;}
            }
            return false;           //return false when all portion of ship stays in "not centered" area
        }
    }


    //This method will take row #, column # and direction of a ship's location, the current placing ship rep. by index order
    //and Player comp to determine if one ship's placement is touching another ship, then return a boolean type var. to rep.
    //the resul (touch other ships or doesn't touch)
    public static boolean compHasPlacementAdjacent(int r, int c, int dire, Player comp, int count) {
        int length = comp.ships[count].getLength();     //get length of current placing ship

        //if horizontally place the ship
        if(dire == 0) {
            //First the program will check the above and below cells of current placement when placing horizontally
            //When row # is 0, there's no cell exist above current placement thus only check cells below current placement
            //When row # is 9, there's no cell exist below current placement thus only check cells above current placement
            //When row # is any number between 1 and 8, need to check both above and below cells of current placement
            for(int i = c; i < c + length; i++) {
                if(r == 0) {
                    if(comp.playerGrid.getHasShip(r+1, i)) {return true;}
                } else if(r == 9) {
                    if(comp.playerGrid.getHasShip(r-1, i)) {return true;}
                } else {
                    if(comp.playerGrid.getHasShip(r-1, i) || comp.playerGrid.getHasShip(r+1, i)) {return true;}
                }
            }

            //Then check the front and back cells of current placement

            //When column # is 0:
            //if row # is 0, only need to check the back center cell and back bottom cell, since front cells and back top cell don't exist
            //if row # is 9, only need to check the back center cell and back top cell, since front cells and back bottom cell don't exist
            //if row # is any number between 1 and 8, need to check all back center, back top and back bottom cell. Still front cells don't exist
            if(c == 0) {
                if(r == 0) {
                    return comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r + 1, c + length);
                } else if(r == 9) {
                    return comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r - 1, c + length);
                } else {
                    return comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r - 1, c + length) || comp.playerGrid.getHasShip(r + 1, c + length);
                }
            }

            //When column # + length of ship is >= 10 (actually == 10 would also work), meaning the right most cell of a ship locates on column 10 (final column)
            //if row # is 0, only need to check the front center cell and front bottom cell, since back cells and front top cell don't exist
            //if row # is 9, only need to check the front center cell and front top cell, since back cells and front bottom cell don't exist
            //if row # is any number between 1 and 8, need to check all front center, front top and front bottom cell. Still back cells don't exist
            else if(c + length >= 10) {
                if(r == 0) {
                    return comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r + 1, c - 1);
                } else if(r == 9) {     //placing on bottom row
                    return comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r - 1, c - 1);
                } else {           //placing in middle row
                    return comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r - 1, c - 1) || comp.playerGrid.getHasShip(r + 1, c - 1);
                }
            }

            //When column # is any number that made the placement of ship locate not in column 1 or column 10:
            //if row # is 0, need to check front center, front bottom, back center and back bottom cells, since no cells exist above row 0
            //if row # is 9, need to check front center, front top, back center and back top cells, since no cells exist below row 9
            //if row # is any number between 1 and 8, need to check all 6 cells (front top/center/bottom, back top/center/bottom)
            else {
                if(r == 0) {
                    return comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r + 1, c - 1) || comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r + 1, c + length);
                } else if(r == 9) {
                    return comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r - 1, c - 1) || comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r - 1, c + length);
                } else {
                    return comp.playerGrid.getHasShip(r, c + length) || comp.playerGrid.getHasShip(r - 1, c + length) || comp.playerGrid.getHasShip(r + 1, c + length) || comp.playerGrid.getHasShip(r, c - 1) || comp.playerGrid.getHasShip(r - 1, c - 1) || comp.playerGrid.getHasShip(r + 1, c - 1);
                }
            }

            //after checking all cells around the current placement of ship and confirmed current placement doesn't touch
            //any other ships, return false to indicate doesn't have any "touching" error
        }


        //if vertically place the ship
        else {
            //First the program will check the left and right cells of current placement when placing vertically
            //When column # is 0, there's no cell exist to the left of current placement thus only check cells to the right of current placement
            //When column # is 9, there's no cell exist to the right of current placement thus only check cells to the left of current placement
            //When column # is any number between 1 and 8, need to check both left and right cells of current placement
            for(int i = r; i < r + length; i++) {
                if(c == 0) {
                    if(comp.playerGrid.getHasShip(i, c+1)) {return true;}
                } else if(c == 9) {
                    if(comp.playerGrid.getHasShip(i, c-1)) {return true;}
                } else {
                    if(comp.playerGrid.getHasShip(i, c+1) || comp.playerGrid.getHasShip(i, c-1)) {return true;}
                }
            }

            //Then check the top and bottom cells of current placement

            //When row # is 0:
            //if column # is 0, only need to check the bottom center cell and bottom right cell, since top cells and bottom left cell don't exist
            //if column # is 9, only need to check the bottom center cell and bottom left cell, since top cells and bottom right cell don't exist
            //if column # is any number between 1 and 8, need to check all bottom center, bottom left and bottom right cell. Still top cells don't exist
            if(r == 0) {
                if(c == 0) {
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c + 1);
                } else if(c == 9) {     //placing in right most column
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c - 1);
                } else {            //placing in middle column
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c + 1) || comp.playerGrid.getHasShip(r + length, c - 1);
                }
            }

            //When row # + length of ship is >= 10, meaning the bottom most cell of a ship locates on row 10 (final row)
            //if column # is 0, only need to check the top center cell and top right cell, since bottom cells and top left cell don't exist
            //if column # is 9, only need to check the top center cell and top left cell, since bottom cells and top right cell don't exist
            //if column # is any number between 1 and 8, need to check all top center, top left and top right cell. Still bottom cells don't exist
            else if(r + length >= 10) {
                if(c == 0) {
                    return comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c + 1);
                } else if(c == 9) {
                    return comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c - 1);
                } else {
                    return comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c + 1) || comp.playerGrid.getHasShip(r - 1, c - 1);
                }
            }

            //When row # is any number that made the placement of ship locate not in row 1 or row 10:
            //if column # is 0, need to check the top center, top right, bottom center and bottom right cells, since no cells exist to the left of column 0
            //if column # is 9, need to check the top center, top left, bottom center and bottom left cells, since no cells exist to the right of column 9
            //if column # is any number between 1 and 8, need to check all 6 cells (top left/center/right, bottom left/center/right)
            else {
                if(c == 0) {
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c + 1) || comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c + 1);
                } else if(c == 9) {
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c - 1) || comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c - 1);
                } else {
                    return comp.playerGrid.getHasShip(r + length, c) || comp.playerGrid.getHasShip(r + length, c + 1) || comp.playerGrid.getHasShip(r + length, c - 1) || comp.playerGrid.getHasShip(r - 1, c) || comp.playerGrid.getHasShip(r - 1, c + 1) || comp.playerGrid.getHasShip(r - 1, c - 1);
                }
            }

            //after checking all cells around the current placement of ship and confirmed current placement doesn't touch
            //any other ships, return false to indicate doesn't have any "touching" error
        }
    }


    //This method will take row #, column # and direction of a ship's location, the current placing ship rep. by index order
    //and Player comp to determine if one ship's placement 1. exceed the board 2. overlaps with other already placed ships.
    //It will return a boolean type var. to rep. the result (has error or don't have error)
    public static boolean compHasPlacementError(int r, int c, int dire, Player comp, int count) {
        int length = comp.ships[count].getLength();      //get length of current placing ship

        //if ship is placing horizontally, when length of ship + column # is greater than 10, ship is placed out of the board
        if(dire == 0) {
            int check = length + c;
            if(check > 10) {return true;}
        }

        //if ship is placing vertically, when length of ship + row # is greater than 10, ship is placed out of the board
        if(dire == 1) {
            int check = length + r;
            if(check > 10) {return true;}
        }

        //After confirming all ship's cells is in range, check if it overlaps with other ships when placed horizontally
        //by looping through each position ship will occupy and see if a ship is already there
        if(dire == 0) {
            for(int i = c; i < c + length; i++) {
                if(comp.playerGrid.getHasShip(r, i)) {return true;}
            }
        }

        //check if it overlaps with other ships when placed vertically by looping through each position ship will occupy
        //and see if a ship is already there
        if(dire == 1) {
            for(int i = r; i < r + length; i++) {
                if(comp.playerGrid.getHasShip(i, c)) {return true;}
            }
        }

        //after checking current placement isn't out of bound and doesn't overlap with any other ship
        //return false to indicate doesn't have "placement" error
        return false;
    }


    //This method will take user's input status of a cell as parameter to see if user's input is acceptable and return a
    //boolean var to indicate whether the user's input status is valid
    public static boolean inputValidStatus(String status) {
        status = status.toLowerCase();

        //when status input doesn't equal to any miss/hit/sunk, return false to indicate user's invalid input and update
        //label warningOutput to notify user his/her input is invalid
        if(!(status.equals("miss") || status.equals("hit") || status.equals("sunk"))) {
            warningOutput.setText("Invalid Input!");
            return false;
        }
        return true;
    }


    //This method will take user's input shipName and status of a cell as parameters to see if user's inputs are acceptable and
    //return a boolean var to indicate whether the user's inputs are valid
    public static boolean inputValidShipName(String status, String shipName) {
        shipName = shipName.toLowerCase();
        status = status.toLowerCase();

        //a valid shipName input need to qualify the following:
        //1. if input status is miss, there's no need to check shipName input
        if(status.equals("miss")) {return true;}

        //2. if input status is hit/sunk and shipName is one of the name belong to any 5 ships
        if(!(shipName.equals("destroyer") || shipName.equals("submarine") || shipName.equals("cruiser") || shipName.equals("battleship") || shipName.equals("carrier"))) {
            warningOutput.setText("Invalid Input!");
            return false;
        }

        //3. if input status is hit/sunk and the shipName input doesn't belong to any ship that's already sunk (although I doubt why user will input a shipName that's already sunk)
        int shipInx = Arrays.asList(user.SHIP_NAME).indexOf(shipName);
        if(user.ships[shipInx].isSunk()) {
            warningOutput.setText("Invalid Input!");
            return false;
        }

        //if above 3 conditions are all satisfied, return true to indicate shipName input is valid, otherwise update label
        //to notify user's invalid input and return false
        return true;
    }


    //This method takes an integer as parameter and will convert this row # from programming use (0-9) to displaying text use
    //row # (A-J) and return the converted String value
    public static String convertIndexToStringRow(int i) {
        String letter;
        switch(i) {
            case 0: letter = "A";
                break;
            case 1: letter = "B";
                break;
            case 2: letter = "C";
                break;
            case 3: letter = "D";
                break;
            case 4: letter = "E";
                break;
            case 5: letter = "F";
                break;
            case 6: letter = "G";
                break;
            case 7: letter = "H";
                break;
            case 8: letter = "I";
                break;
            case 9: letter = "J";
                break;
            default: letter = "Z";
        }
        return letter;
    }


    //This method takes an integer as parameter and will convert this column # from programming use (0-9) to displaying text use
    //column # (1-10) and return this converted integer value
    public static int convertIndexToStringCol(int i) {
        int count;
        switch(i) {
            case 0: count = 1;
                break;
            case 1: count = 2;
                break;
            case 2: count = 3;
                break;
            case 3: count = 4;
                break;
            case 4: count = 5;
                break;
            case 5: count = 6;
                break;
            case 6: count = 7;
                break;
            case 7: count = 8;
                break;
            case 8: count = 9;
                break;
            case 9: count = 10;
                break;
            default: count = -1;
        }
        return count;
    }


    //This method is programmed for debugging that takes an 2D integer array as parameter and print out every element
    //in this 2D array (mainly use for coding the probabilityDistributionForEachShip method)
    public static void printTotalProbability(int[][] p) {
        for(int i = 0; i < 10 ; i++) {
            for(int j = 0; j < 10; j++) {
                System.out.print(p[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //This method cooperate with StringTokenizer st and BufferedReader br for reading the next token on the file and return that token
    public static String next() throws IOException{
        if(st == null || !st.hasMoreTokens()) {
            st = new StringTokenizer(br.readLine().trim());
        }
        return st.nextToken();
    }

    //This method will use next() method to return the next integer value written on a file, separating by space
    public static int readInteger() throws IOException {
        return Integer.parseInt(next());
    }

    //This method will use next() method to return the next character value written on a file
    public static char readCharacter () throws IOException {
        return next().charAt(0);
    }

    //This method will return the whole line of text on a file as a String
    public static String readLine () throws IOException {
        return br.readLine().trim();
    }
}