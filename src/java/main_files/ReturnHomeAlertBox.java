package sample;

//This class will create alert boxes when "save progress" and "return to home screen" button is clicked during the game play
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class ReturnHomeAlertBox {
    //Taking the title of alert box, stage of main game play and timer as parameters and display the alert box
    //timer will be paused when the alert box shown up, and the stage parameter is used change current stage
    //if user required so (instead of creating a new stage and not closing the old one)
    public static void display(String title, Stage stage, Timeline timer) throws IOException {
        //basic syntax to create a stage with new scene
        Stage alert = new Stage();
        //make sure user must deal with the alert box before continue playing the game
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setResizable(false);
        timer.pause();      //pause the timer when alert boxes shown


        Label text;         //Label that will appear on alert boxes
        //set different text according to the title of the stage (which button clicked)
        if(title.equals("Save Game Progress Confirmation")) {
            text = new Label("Return to Home Screen and Save Game Progress?\n (This action will overwrite any previous records)");
        } else {
            text = new Label("Return to Home Screen and Start a New Game?\n (Save game progress to resume If game is not over)");
        }


        Button yes = new Button("YES");     //yes button to perform actions described with text
        Button no = new Button("NO");       //no button to return to the current game
        //manually setting visual properties of buttons since alert boxes don't use css style sheet (keep alert box looks simple)
        text.setStyle("-fx-font-size: 20");
        yes.setStyle("-fx-font-size: 15");
        no.setStyle("-fx-font-size: 15");


        //when yes button is clicked
        yes.setOnAction(e-> {
            Battleship.clickSound();
            Battleship.backgroundMusicPlayer.play();
            //close the alert box
            alert.close();

            //depending on which button (save progress or return to home screen) user clicked, perform different actions
            //if user confirm to save current game
            if(title.equals("Save Game Progress Confirmation")) {
                //clear the original record file (overwrite) and write new info about current game
                if(Battleship.recordFile.exists() && Battleship.recordFile.isFile()) {
                    try {
                        Battleship.pw = new PrintWriter("BattleshipRecord.txt");
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                Battleship.writeOnFile();
                //make a .txt copy of the current record file, as current file will always be cleared with the game start
                //but this copy file won't (thus we can achieve game saving and resuming function)
                Battleship.makeFileCopy();
            }


            //syntax to show the home screen with applied visual effects
            Parent homeRoot = null;
            try {
                homeRoot = FXMLLoader.load(ReturnHomeAlertBox.class.getResource("home-view.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Scene homeScene = new Scene(homeRoot);
            stage.setTitle("Battleship Game");
            homeScene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
            stage.setScene(homeScene);
            stage.setResizable(false);
            stage.show();

            //ensure stage appeared in center of the entire screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            //clear all information about last round of game and prepare for next round
            Battleship.clearStatus();
        });


        //when no button is clicked
        no.setOnAction(e-> {
            //close the alert box, continue background music and timer (if the game isn't end)
            Battleship.clickSound();
            if(!Battleship.gameEnd) {
                Battleship.backgroundMusicPlayer.play();
                timer.play();
            }
            alert.close();
        });

        //construct the layout of the alert boxes and display on screen
        HBox buttons = new HBox(80, yes, no);
        buttons.setAlignment(Pos.CENTER);
        text.setAlignment(Pos.CENTER);
        VBox layout = new VBox(50, text, buttons);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 550, 200);
        alert.setScene(scene);
        alert.showAndWait();
    }
}
