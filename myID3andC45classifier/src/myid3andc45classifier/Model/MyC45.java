/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier.Model;

import java.util.ArrayList;
import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

/**
 *
 * @author ryanyonata
 */
public class MyC45 extends Classifier {

    
    private MyC45[] successors; 
    private Attribute attribute;
    private double label;
    private double[] distribution;
    private Attribute classAttribute;
    private static final double epsilon = 1e-6;
    
    @Override
    public void buildClassifier(Instances data) throws Exception {
        getCapabilities().testWithFail(data);
        
        data = new Instances(data);
        data.deleteWithMissingClass();
        
        Enumeration enumAtt = data.enumerateAttributes();
        while (enumAtt.hasMoreElements()) {
            Attribute attr = (Attribute) enumAtt.nextElement();
            if (attr.isNumeric()) {
                ArrayList<Double> mid = new ArrayList<Double>();
                Instances savedData = null;
                double temp, max = Double.NEGATIVE_INFINITY;
                // TODO: split nominal
                data.sort(attr);
                for (int i = 0; i < data.numInstances()-1; i++) {
                    if (data.instance(i).classValue() != data.instance(i+1).classValue()) {
                        Instances newData = convertInstances(data, attr, (data.instance(i+1).value(attr)-data.instance(i).value(attr))/2);
                        temp = computeInfoGainRatio(newData, newData.attribute(newData.numAttributes()-1));
                        if (temp > max) {
                            max = temp;
                            savedData = newData;
                        }
                    }
                }
                
                data = savedData;
            }
        }
        
        
    }
    
    public void makeMyC45Tree(Instances data) throws Exception {
        
        double[] infoGainRatios = new double[data.numAttributes()];
        Enumeration attEnum = data.enumerateAttributes();
        while (attEnum.hasMoreElements()) {
            Attribute att = (Attribute) attEnum.nextElement();
            infoGainRatios[att.index()] = computeInfoGainRatio(data, att);
        }
        
        // TODO: build the tree
        attribute = data.attribute(maxIndex(infoGainRatios));

        // Make leaf if information gain is zero. 
        // Otherwise create successors.
        if (isDoubleEqual(infoGainRatios[attribute.index()], 0)) {
            attribute = null;
            double[] numClasses = new double[data.numClasses()];
            
            Enumeration instEnum = data.enumerateInstances();
            while (instEnum.hasMoreElements()) {
                Instance inst = (Instance) instEnum.nextElement();
                numClasses[(int) inst.classValue()]++;
            }

            label = maxIndex(numClasses);
            classAttribute = data.classAttribute();
        } else {
            Instances[] splitData = splitInstancesByAttribute(data, attribute);
            successors = new MyC45[attribute.numValues()];
            for (int j = 0; j < attribute.numValues(); j++) {
                successors[j] = new MyC45();
                successors[j].buildClassifier(splitData[j]);
            }
        }
        // TODO: prune
    }
    
    public double[] listClassCountsValues(Instances data) throws Exception {
        
        double[] classCounts = new double[data.numClasses()]; //array untuk menyimpan value kelas sesuai jumlah kelas
        Enumeration instanceEnum = data.enumerateInstances();
        
        //Masukkan data ke array
        while (instanceEnum.hasMoreElements()) {
            Instance inst = (Instance) instanceEnum.nextElement();
            classCounts[(int)inst.classValue()]++;
        }
        
        return classCounts;
    }
    
    public double computeEntropy(Instances data) throws Exception {
        
        double entropy = 0;
        
        double[] classCounts = listClassCountsValues(data);
        for (int i = 0; i < data.numClasses(); i++) {
            if (classCounts[i] > 0) {
                double p = classCounts[i]/(double)data.numInstances(); 
                entropy -=  p * (Utils.log2(p));
            }
        }
        
        return entropy;
    }
    
    public Instances[] splitInstancesByAttribute(Instances data, Attribute attr) throws Exception {
        //Split data menjadi beberapa instances sesuai dengan jumlah jenis data pada atribut
        Instances[] splitData = new Instances[attr.numValues()];
        
        for (int i = 0; i < attr. numValues(); i++) {
            splitData[i] = new Instances(data, data.numInstances());
        }
        
        Enumeration instanceEnum = data.enumerateInstances();
        while(instanceEnum.hasMoreElements()) {
            Instance inst = (Instance) instanceEnum.nextElement();
            splitData[(int) inst.value(attr)].add(inst);
        }
        
        for (int i = 0; i < splitData.length; i++) {
            splitData[i].compactify();
        }
        
        return splitData;
    }
    
    public double computeInfoGainRatio(Instances data, Attribute attr) throws Exception {
        double attributeEntropy = 0;
        double attributeSplitInfo = 0;
        
        Instances[] splitData = splitInstancesByAttribute(data, attr);
        for (int i = 0; i < splitData.length; i++) {
            double p = splitData[i].numInstances()/(double)data.numInstances();
            attributeEntropy += p * computeEntropy(splitData[i]);
            attributeSplitInfo -= p * Utils.log2(p);
        }
        return (computeEntropy(data) - attributeEntropy)/attributeSplitInfo;
        
    }
    
    public Capabilities getCapabilities() {

        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enable(Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capability.DATE_ATTRIBUTES);
        result.enable(Capability.MISSING_VALUES);

        // class
        result.enable(Capability.NOMINAL_CLASS);
        result.enable(Capability.MISSING_CLASS_VALUES);

        // instances
        result.setMinimumNumberInstances(0);

        return result;

    }
    
    private Instances convertInstances(Instances data, Attribute att, double threshold) {
        Instances newData = new Instances(data);

        try {
            Add filter = new Add();
            filter.setNominalLabels("<=" + threshold + ",>" + threshold);
            filter.setAttributeName(att.name() + " NOM");
            filter.setInputFormat(newData);
            newData = Filter.useFilter(newData, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < newData.numInstances(); ++i) {
            if ((double) newData.instance(i).value(newData.attribute(att.name())) <= threshold) {
                newData.instance(i).setValue(newData.attribute(att.name() + " NOM"), "<=" + threshold);
            } else {
                newData.instance(i).setValue(newData.attribute(att.name() + " NOM"), ">" + threshold);
            }
        }
        
        newData.deleteAttributeAt(att.index());

        return newData;
    }
    
    private boolean isDoubleEqual(double a, double b) {
        return (a == b) || Math.abs(a-b) < epsilon;
    }
    
    private int maxIndex(double[] array) {
        double max = 0;
        int index = 0;

        if (array.length > 0) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] > max) {
                    max = array[i];
                    index = i;
                }
            }
            return index;
        } else {
            return -1;
        }
    }
    
    public String toString(int level) {
        
        StringBuffer text = new StringBuffer();
        
        if (attribute == null) {
            if (Instance.isMissingValue(label)) {
                text.append(": null");
            } else {
                text.append(": " + classAttribute.value((int) label));
            }
        } else {
            for (int i = 0; i < attribute.numValues(); i++) {
                text.append("\n");
                for (int j = 0; j < level; j++) {
                    text.append("|  ");
                }
                text.append(attribute.name() + " = " + attribute.value(i));
                text.append(successors[i].toString(level + 1));
            }
        }
        
        return text.toString();
    }
    
    public String toString() {
        if ((distribution == null) && (successors == null)) {
            return "C45: No model built yet.";
        }
        
        return "C45\n\n" + toString(0);
    }
}
