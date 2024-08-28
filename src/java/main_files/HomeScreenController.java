package sample;

//This class is the Controller of the "home-view.fxml" file that control the functions of buttons in that fxml scene
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class HomeScreenController {
    @FXML
    private Stage stage;    //create a new stage for home page
    private Scene scene;    //create a new scene for home page displayed on stage
    private Parent root;    //create a Parent object to build the new scene

    //when "New Game" button is clicked, switch to set up game selection scene
    public void switchToSelectionScene(ActionEvent e) throws IOException {
        Battleship.clickSound();

        //syntax for switching scenes using fxml
        root = FXMLLoader.load(getClass().getResource("setupSelection-view.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //apply css style effects to set up game selection scene
        scene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
        stage.setScene(scene);
        stage.show();
    }


    //when "How to Play" button is clicked, switch to instruction scene
    public void switchToInstructionScene(ActionEvent e) throws IOException {
        Battleship.clickSound();

        //syntax for switching scenes using fxml
        root = FXMLLoader.load(getClass().getResource("instruction-view.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //apply css style effects to instruction scene
        scene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
        stage.setScene(scene);
        stage.show();
    }


    //when "Resume Game" button is clicked
    public void resumeGame(ActionEvent e) throws IOException {
        File f = new File("BattleshipRecordCopy.txt");

        //check whether the BattleshipRecordCopy.txt file is already exist and have records
        //if file exist and have past game records
        if(f.exists() && f.isFile() && f.length() != 0) {
            Battleship.clickSound();

            //syntax for switching scenes
            //call setUpGame function in Battleship class with parameter true, indicating the game is resumed not a new one
            root = Battleship.setUpGame(true);
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            //apply css style effects to the game play scene
            scene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
            stage.setScene(scene);
            stage.show();

            //ensure stage always locates on centre of the screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
        }

        //if the file doesn't exist or doesn't have any records
        else {
            Battleship.clickSound();

            //create an alert box that notify user there isn't any game records and ask user to return to home screen
            Stage alert = new Stage();
            alert.initModality(Modality.APPLICATION_MODAL);             //must deal with current alert box before doing any actions on home screen
            alert.setTitle("Warning");
            alert.setResizable(false);
            Label text = new Label("No Recent Save Records Detect"); //Label with text to deliver message
            Button returnHome = new Button("Return to Home Screen"); //button used to close the alert box

            //manually set the style of alert box instead of using style sheet to keep it simple
            text.setStyle("-fx-font-size: 20");
            returnHome.setStyle("-fx-font-size: 15");

            //close the alert box when "Return to Home Screen" button is clicked
            returnHome.setOnAction(a -> {
                Battleship.clickSound();
                alert.close();
            });

            //program layouts for alert boxes and syntax for displaying it
            HBox buttons = new HBox(80, returnHome);
            buttons.setAlignment(Pos.CENTER);
            VBox layout = new VBox(50, text, buttons);
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout, 500, 200);
            alert.setScene(scene);
            alert.showAndWait();
        }
    }


    //when switch to instruction scene, click on "Return to Home Screen" button and switch back to home screen
    public void returnToHomeScreen(ActionEvent e) throws IOException {
        Battleship.clickSound();

        //syntax for switching scenes using fxml
        root = FXMLLoader.load(getClass().getResource("home-view.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //apply css style effects to home screen
        scene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
        stage.setScene(scene);
        stage.show();
    }
}