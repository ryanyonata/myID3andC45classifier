/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myid3andc45classifier;

import myid3andc45classifier.Model.MyID3;
import myid3andc45classifier.Model.WekaAccessor;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author ryanyonata
 */
public class Main {
    
    public Instances dataset;
    public Instances testset;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        WekaAccessor accessor = new WekaAccessor();
        Instances trainset;
        trainset = accessor.readARFF("C:\\Users\\Julio Savigny\\Documents\\myID3andC45classifier\\myID3andC45classifier\\resources\\weather.nominal.arff");
        Classifier j48 = new J48();
        Classifier model = accessor.train(trainset, j48);
        //accessor.saveModel(model, "C:\\Users\\Julio Savigny\\Desktop\\myID3andC45classifier\\myID3andC45classifier\\some.model");
        //Classifier loadedModel = accessor.loadModel("C:\\Users\\Julio Savigny\\Desktop\\myID3andC45classifier\\myID3andC45classifier\\some.model");
        System.out.println(model);
        //System.out.println(loadedModel);

        // Coba ID3 Apoy
        Classifier customID3 = new MyID3();
        Classifier myId3Model = accessor.train(trainset, customID3);
        Instances resampledTrainset = accessor.resample(trainset);
        System.out.println(myId3Model);
        System.out.println(accessor.tenFoldCrossValidation(resampledTrainset, customID3).toSummaryString());
        Evaluation eval = new Evaluation(trainset);
        eval.evaluateModel(myId3Model, trainset);
        //System.out.println(eval.toSummaryString());

//        System.out.println(trainset);
//        System.out.println(resampledTrainset);

        // Coba C4.5 Bayu
//        Classifier customC45 = new myC45();
//        Classifier myC45Model = accessor.train(trainset, customC45);
//        System.out.println(myC45Model);
    }
}
