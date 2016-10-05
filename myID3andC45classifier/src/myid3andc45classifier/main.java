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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author ryanyonata
 */
public class main {
    
    public Instances dataset;
    public Instances testset;

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

        // Coba ID3 Apoy
        Classifier customID3 = new myID3();
        Classifier myId3Model = accessor.train(trainset, customID3);
        System.out.println(myId3Model);

        // Coba C4.5 Bayu
//        Classifier customC45 = new myC45();
//        Classifier myC45Model = accessor.train(trainset, customC45);
//        System.out.println(myC45Model);
    }
}
