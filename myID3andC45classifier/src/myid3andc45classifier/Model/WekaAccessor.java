package myid3andc45classifier.Model;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.*;
import java.util.Random;

/**
 * Created by Julio Savigny on 10/5/2016.
 */
public class WekaAccessor {

    public Instances readARFF(String filepath) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
        Instances instances = source.getDataSet();
        // setting class attribute if the data format does not provide this information
        // For example, the XRFF format saves the class attribute information as well
        if (instances.classIndex() == -1)
            instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    public Instances removeAttr(Instances dataset, int attr_number) throws Exception {
        String[] options = new String[2];
        options[0] = "-R";                                    // "range"
        options[1] = Integer.toString(attr_number);           // the attribute index
        Remove remove = new Remove();                         // new instance of filter
        remove.setOptions(options);                           // set options
        remove.setInputFormat(dataset);                          // inform filter about dataset **AFTER** setting options
        dataset = Filter.useFilter(dataset, remove);   // apply filter
        return dataset;
    }

    public Instances resample(Instances dataset) throws Exception {
        weka.filters.unsupervised.instance.Resample sampler = new weka.filters.unsupervised.instance.Resample();
        sampler.setRandomSeed((int)System.currentTimeMillis());
        sampler.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset,sampler);
        return  dataset;
    }

    public Classifier train(Instances dataset, Classifier cls) throws Exception {
        // train
        if (dataset.classIndex() == -1)
            dataset.setClassIndex(dataset.numAttributes() - 1);
        cls.buildClassifier(dataset);
        return cls;
    }
    public void saveModel(Classifier cls, String filepath) throws Exception {

        // serialize model
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File(filepath)));
        oos.writeObject(cls);
        oos.flush();
        oos.close();
    }

    public Classifier loadModel(String filepath) throws Exception {
        //return (Classifier) weka.core.SerializationHelper.read(filepath);
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath));
        Classifier cls = (Classifier) ois.readObject();
        ois.close();
        return cls;
    }

    public Evaluation tenFoldCrossValidation(Instances dataset, Classifier cls) throws Exception {
        Evaluation eval = new Evaluation(dataset);
        eval.crossValidateModel(cls, dataset, 10, new Random(1));
        return eval;
    }

    public Evaluation percentageSplit(Instances dataset, Classifier cls, double percentage) throws Exception {
        Instances instances = new Instances(dataset);
        instances.randomize(new Random(1));
        System.out.println(percentage);
        int trainSize = (int) Math.round(instances.numInstances() * percentage / 100);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        cls.buildClassifier(train);

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
