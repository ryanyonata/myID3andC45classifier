package myid3andc45classifier.Model;

/**
 * Created by Julio Savigny on 10/6/2016.
 */

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class PredictRow {

    public PredictRow(int number, String attribute, Boolean isNominal, Boolean isNumeric, List<String> nominalList){
        this.number = number;
        this.attribute = attribute;
        this.isSelected = new SimpleBooleanProperty(false);
        this.isNominal = isNominal;
        this.isNumeric = isNumeric;
        this.nominalList = nominalList;
    }

    public  PredictRow(int number, String attribute, String value){
        this.number = number;
        this.attribute = attribute;
        this.value = value;
    }

    public String toString(){
        return Integer.toString(number) + ' ' + isSelected + ' ' + attribute;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public BooleanProperty isSelected() {
        return isSelected;
    }

    public void setSelected(BooleanProperty selected) {
        isSelected = selected;
    }

    public Boolean getNominal() {
        return isNominal;
    }

    public void setNominal(Boolean nominal) {
        isNominal = nominal;
    }

    public Boolean getNumeric() {
        return isNumeric;
    }

    public void setNumeric(Boolean numeric) {
        isNumeric = numeric;
    }

    public List<String> getNominalList() {
        return nominalList;
    }

    public void setNominalList(List<String> nominalList) {
        this.nominalList = nominalList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private int number;
    private String attribute;
    private BooleanProperty isSelected;
    private Boolean isNominal;
    private Boolean isNumeric;
    private List<String> nominalList;
    private String value;
}

