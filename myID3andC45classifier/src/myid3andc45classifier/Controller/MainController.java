package myid3andc45classifier.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Tab tab_preprocess;

    @FXML
    private TableView<?> table_preprocess;

    @FXML
    private TableColumn<?, ?> table_preprocessNumbers;

    @FXML
    private TableColumn<?, ?> table_preprocessCheckboxes;

    @FXML
    private TableColumn<?, ?> table_preprocessAttributes;

    @FXML
    private Button btn_removeAttribute;

    @FXML
    private Button btn_loadArff;

    @FXML
    private Button btn_browseArff;

    @FXML
    private TextField tfield_arffPath;

    @FXML
    private Button btn_resample;

    @FXML
    private Tab tab_classify;

    @FXML
    private RadioButton radio_wekaId3;

    @FXML
    private ToggleGroup classifierToggle;

    @FXML
    private RadioButton radio_wekaC45;

    @FXML
    private RadioButton radio_myId3;

    @FXML
    private RadioButton radio_myC45;

    @FXML
    private Button btn_trainModel;

    @FXML
    private Button btn_saveModel;

    @FXML
    private Button btn_loadModel;

    @FXML
    private TextField tfield_percentageSplit;

    @FXML
    private RadioButton radio_percentageSplit;

    @FXML
    private ToggleGroup testToggle;

    @FXML
    private RadioButton radio_tenFold;

    @FXML
    private RadioButton radio_testSet;

    @FXML
    private Button button_selectTestArff;

    @FXML
    private Label label_isModel;

    @FXML
    private Tab tab_predict;

    @FXML
    private TableView<?> table_prediction;

    @FXML
    private TableColumn<?, ?> table_predictionNumbers;

    @FXML
    private TableColumn<?, ?> table_predictionAttributes;

    @FXML
    private TableColumn<?, ?> table_predictionValues;

    @FXML
    private Button btn_predict;

    @FXML
    private TextField textarea_predict;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

