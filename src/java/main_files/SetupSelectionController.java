package sample;

//This class is the Controller of the "setupSelection-view.fxml" file that control the functions of buttons in that fxml scene
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Paths;

public class SetupSelectionController {
    //Each button in the setup selection scene is assigned with an id in scene builder, although not all ids are used in controller
    @FXML
    private RadioButton easyButton, hardButton, userFirstButton, computerFirstButton;   //ids for radio buttons in setup selection scene
    @FXML
    private Button confirmButton;   //ids for normal button in setup selection scene
    private Stage stage;            //new stage for selection scene
    private Scene scene;            //new scene for selection scene
    private Parent root;            //new Parent to build the scene for selection scene


    //get which level of the game user selects and save it to public static variable level
    public void getLevel() {
        Battleship.clickSound();
        if(easyButton.isSelected()) {
            Battleship.level = "easy";
        } else {
            Battleship.level = "hard";
        }
    }

    //get who shoot first in a round of game and save it to public static variable myTurn
    public void getOrder() {
        Battleship.clickSound();
        Battleship.myTurn = userFirstButton.isSelected();
    }

    //Using the level and myTurn variable to set up one round of game
    public void confirmAndStartGame(ActionEvent e) throws IOException {
        //trigger the getOrder and getLevel function again when the game start to make sure required info is recorded even if
        //user doesn't select any options (by default, easy level and computer first is selected and user cannot cancel the option
        //since they're radio buttons
        getOrder();
        getLevel();
        Battleship.clickSound();

        //basic syntax to create a scene in stage
        //detailed info about how the game is setting up is in Battleship class setUpGame method
        root = Battleship.setUpGame(false);
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //apply css style sheets into the game to achieve specific visual effects for the game
        scene.getStylesheets().add(Paths.get("src/main/resources/sample/demo1/StyleSheet.css").toUri().toString());
        stage.setScene(scene);
        stage.show();

        //ensure stage always locate in center of the entire screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
