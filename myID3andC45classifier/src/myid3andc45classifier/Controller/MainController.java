package myid3andc45classifier.Controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import myid3andc45classifier.Model.PreprocessRow;
import myid3andc45classifier.Model.WekaAccessor;
import weka.core.Instances;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Tab tab_preprocess;

    @FXML
    private TableView<PreprocessRow> table_preprocess;

    @FXML
    private TableColumn<PreprocessRow, Integer> table_preprocessNumbers;

    @FXML
    private TableColumn<PreprocessRow, Boolean> table_preprocessCheckboxes;

    @FXML
    private TableColumn<PreprocessRow, String> table_preprocessAttributes;

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

    Instances trainset;
    ArrayList<PreprocessRow> preprocessRows;
    ObservableList<PreprocessRow> oPreprocessRows;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final WekaAccessor accessor = new WekaAccessor();
        preprocessRows = new ArrayList<PreprocessRow>();
        tab_classify.setDisable(true);
        tab_predict.setDisable(true);
        btn_browseArff.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Weka Data Files", "*.arff", "*.xrff", "*.csv"));
                File selectedFile = fileChooser.showOpenDialog(new Stage());
                if (selectedFile != null) {
                    tfield_arffPath.setText(selectedFile.getAbsolutePath());
                    try {
                        trainset = accessor.readARFF(selectedFile.getAbsolutePath());
                        tab_classify.setDisable(false);
                        System.out.println(trainset);
                        System.out.println(trainset.numAttributes());
                        for (int i=0; i < trainset.numAttributes(); i++){
                            preprocessRows.add(new PreprocessRow(i+1,trainset.attribute(i).name()));
                        }
                        oPreprocessRows = FXCollections.observableArrayList(preprocessRows);
                        table_preprocessNumbers.setCellValueFactory(
                                new PropertyValueFactory<PreprocessRow,Integer>("number")
                        );
                        table_preprocessAttributes.setCellValueFactory(
                                new PropertyValueFactory<PreprocessRow, String>("attribute")
                        );
                        table_preprocessCheckboxes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreprocessRow, Boolean>, ObservableValue<Boolean>>() {
                            @Override
                            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreprocessRow, Boolean> param) {
                                return param.getValue().isSelected();
                            }
                        });
                        table_preprocessCheckboxes.setCellFactory(CheckBoxTableCell.forTableColumn(table_preprocessCheckboxes));
                        table_preprocessCheckboxes.setEditable(true);
                        table_preprocess.setItems(oPreprocessRows);
                        table_preprocess.setEditable(true);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        btn_removeAttribute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(oPreprocessRows.toString());
                int isSelectedCount = 0;
                int selectedRow = -1;
                for (PreprocessRow row : oPreprocessRows) {
                    if (row.isSelected().getValue()) {
                        isSelectedCount++;
                        selectedRow = row.getNumber();
                    }
                }
                if (isSelectedCount == 1){
                    try {
                        trainset = accessor.removeAttr(trainset, selectedRow);
                        System.out.println(trainset);
                        preprocessRows.clear();
                        for (int i=0; i < trainset.numAttributes(); i++){
                            preprocessRows.add(new PreprocessRow(i+1,trainset.attribute(i).name()));
                        }
                        oPreprocessRows = FXCollections.observableArrayList(preprocessRows);
                        table_preprocessNumbers.setCellValueFactory(
                                new PropertyValueFactory<PreprocessRow,Integer>("number")
                        );
                        table_preprocessAttributes.setCellValueFactory(
                                new PropertyValueFactory<PreprocessRow, String>("attribute")
                        );
                        table_preprocessCheckboxes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreprocessRow, Boolean>, ObservableValue<Boolean>>() {
                            @Override
                            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreprocessRow, Boolean> param) {
                                return param.getValue().isSelected();
                            }
                        });
                        table_preprocessCheckboxes.setCellFactory(CheckBoxTableCell.forTableColumn(table_preprocessCheckboxes));
                        table_preprocessCheckboxes.setEditable(true);
                        table_preprocess.setItems(oPreprocessRows);
                        table_preprocess.setEditable(true);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}

