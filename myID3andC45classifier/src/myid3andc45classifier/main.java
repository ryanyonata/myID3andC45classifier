/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myid3andc45classifier;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author ryanyonata
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        WekaAccessor accessor = new WekaAccessor();
        Instances trainset;
        trainset = accessor.readARFF("C:\\Users\\Julio Savigny\\Desktop\\myID3andC45classifier\\myID3andC45classifier\\resources\\weather.nominal.arff");
        Classifier j48 = new J48();
        Classifier model = accessor.train(trainset, j48);
        accessor.saveModel(model);
        Classifier loadedModel = accessor.loadModel("C:\\Users\\Julio Savigny\\Desktop\\myID3andC45classifier\\myID3andC45classifier\\some.model");
        System.out.println(model);
        System.out.println(loadedModel);
        
    }
    
    public void readARFF(String filepath) throws Exception {
        DataSource source = new DataSource("weather.nominal.arff");
        Instances data = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (data.classIndex() == -1)
          data.setClassIndex(data.numAttributes() - 1);
    }
    
    
}
