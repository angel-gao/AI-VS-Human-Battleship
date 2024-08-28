module sample.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens sample.demo1 to javafx.fxml;
    exports sample.demo1;
}