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
        // Path to the FXML File
        String fxmlDocPath = "C:/Users/Julio Savigny/Documents/myID3andC45classifier/myID3andC45classifier/src/myid3andc45classifier/View/myID3C45.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        // Create the Pane and all Details
        TabPane root = (TabPane) loader.load(fxmlStream);

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

