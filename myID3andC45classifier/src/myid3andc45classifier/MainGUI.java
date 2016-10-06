package myid3andc45classifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Julio Savigny on 10/5/2016.
 */
public class MainGUI extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    public void start(Stage stage) throws IOException
    {
        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader();

        // Create the Pane and all Details
        TabPane root = (TabPane) loader.load(MainGUI.class.getResource("View/myID3C45.fxml"));

        // Create the Scene
        Scene scene = new Scene(root);
        // Set the Scene to the Stage
        stage.setScene(scene);
        // Set the Title to the Stage
        stage.setTitle("myWekaExplorer");
        // Display the Stage
        stage.show();

    }
}

