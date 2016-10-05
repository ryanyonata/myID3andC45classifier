/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myid3andc45classifier;

import weka.core.Instances;
import java.io.BufferedReader;
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
        String filepath = "weather.nominal.arff";
        Instances i = new Instances(readARFF(filepath));
        
        myID3 myTree = new myID3();
        myTree.buildClassifier(i);
        
        Evaluation eval = new Evaluation(i);
        eval.evaluateModel(myTree, i);
        
    }

    public static Instances readARFF(String filepath) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
        Instances instances = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (instances.classIndex() == -1)
            instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    public void removeAttr(int attr_number) throws Exception {
        String[] options = new String[2];
        options[0] = "-R";                                    // "range"
        options[1] = Integer.toString(attr_number);           // the attribute index
        Remove remove = new Remove();                         // new instance of filter
        remove.setOptions(options);                           // set options
        remove.setInputFormat(dataset);                          // inform filter about dataset **AFTER** setting options
        dataset = Filter.useFilter(dataset, remove);   // apply filter
    }

    public void resample() throws Exception {
        weka.filters.unsupervised.instance.Resample sampler = new weka.filters.unsupervised.instance.Resample();
        sampler.setRandomSeed((int)System.currentTimeMillis());
        sampler.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset,sampler);
    }

    public Classifier train(Classifier cls) throws Exception {
        // train
        cls.buildClassifier(dataset);
        return cls;
    }
    public void saveModel(Classifier cls) throws Exception {

        // serialize model
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("/model/some.model"));
        oos.writeObject(cls);
        oos.flush();
        oos.close();
    }

    public Classifier loadModel(String filepath) throws Exception {
        return (Classifier) weka.core.SerializationHelper.read(filepath);
    }

    public Evaluation tenFoldCrossValidation(Classifier cls) throws Exception {
        Evaluation eval = new Evaluation(dataset);
        eval.crossValidateModel(cls, dataset, 10, new Random((int)System.currentTimeMillis()));
        return eval;
    }

    public Evaluation percentageSplit(Classifier cls, double percentage) throws Exception {
        Instances instances = new Instances(dataset);
        instances.randomize(new Random((int)System.currentTimeMillis()));

        int trainSize = (int) Math.round(instances.numInstances() * percentage / 100);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(cls, test);

        return eval;
    }

    public String classifyFromModel(Classifier cls, Instance input_instance) throws Exception {
        double label = cls.classifyInstance(input_instance);
        input_instance.setClassValue(label);
        return input_instance.stringValue(input_instance.numAttributes()-1);
    }
}
