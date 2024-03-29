package sample;

/*
*   Done By:
*   Baraa Atta 1180445
*   Ahmad Hamad 1180060
*/


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Simulation of CPU Scheduling Algorithms");
        primaryStage.setScene(new Scene(root, 1000, 539));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
