/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier.Model;

import java.util.ArrayList;
import static java.util.Collections.copy;
import java.util.Enumeration;
import static javafx.collections.FXCollections.copy;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.AttributeStats;
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
    private Attribute splittedAttribute;
    private static final double epsilon = 1e-6;
    private boolean pruned = false;
    
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
                        if (data.attribute(attr.name() + " " + (data.instance(i+1).value(attr)+data.instance(i).value(attr))/2) == null) {
                        data = convertInstances(data, attr, (data.instance(i+1).value(attr)+data.instance(i).value(attr))/2);
                        //temp = computeInfoGainRatio(newData, newData.attribute(newData.numAttributes()-1));
                        //System.out.println("attribute "+newData.attribute(newData.numAttributes()-1).name());
                        //if (temp > max) {
                        //    max = temp;
                        //    savedData = newData;
                        //}
                        }
                    }
                }
                
                //Penanganan Missing Value
                AttributeStats attributeStats = data.attributeStats(attr.index());
                double mean = attributeStats.numericStats.mean;
                if (Double.isNaN(mean)) mean = 0;
                // Replace missing value with mean
                Enumeration instEnumerate = data.enumerateInstances();
                while(instEnumerate.hasMoreElements()){
                    Instance instance = (Instance)instEnumerate.nextElement();
                    if(instance.isMissing(attr.index())){
                        instance.setValue(attr.index(),mean);
                    }
                }
                
                //data = new Instances(savedData);
            } else {
                //Penanganan Missing Value
                AttributeStats attributeStats = data.attributeStats(attr.index());
                int maxIndex = 0;
                for(int i=1; i<attr.numValues(); i++){
                    if(attributeStats.nominalCounts[maxIndex] < attributeStats.nominalCounts[i]){
                        maxIndex = i;
                    }
                }
                // Replace missing value with max index
                Enumeration instEnumerate = data.enumerateInstances();
                while(instEnumerate.hasMoreElements()){
                    Instance instance = (Instance)instEnumerate.nextElement();
                    if(instance.isMissing(attr.index())){
                        instance.setValue(attr.index(),maxIndex);
                }
}
            }
        }
        makeMyC45Tree(data);
        
    }
    
    @Override
    public double classifyInstance(Instance instance) {
        int i = 0;
        if (attribute == null) {
            return label;
        } else {
            boolean numeric = false;
            for(int j = 0; j < instance.numAttributes(); j++) {
                if(instance.attribute(j).isNumeric()) {
                    if(instance.attribute(j).name().equalsIgnoreCase(attribute.name().split(" ")[0])) {
                        numeric = true;
                        break;
                    }
                    i++;
                }
            }
            if (numeric) {
                double threshold = Double.parseDouble(attribute.name().split(" ")[1]);
                //System.out.println("WOWW!!! " + attribute.name() + " threshold is " + threshold);
                double val = (double) instance.value(i);
                if (val <= threshold) {
                    return successors[(int) attribute.indexOfValue("<="+threshold)].classifyInstance(instance);
                    //instance.setValue(attribute, "<="+threshold);
                } else {
                    return successors[(int) attribute.indexOfValue(">"+threshold)].classifyInstance(instance);
                    //instance.setValue(attribute, ">"+threshold);
                }
            }
            
            return successors[(int)instance.value(attribute)].classifyInstance(instance);
        }
  
    }
    
    public void makeMyC45Tree(Instances data) throws Exception {
        if (data.numInstances() == 0) {
            attribute = null;
            label = Instance.missingValue();
            return;
        }
        //System.out.println("NEW");
        double[] infoGainRatios = new double[data.numAttributes()];
        Enumeration attEnum = data.enumerateAttributes();
        while (attEnum.hasMoreElements()) {
            Attribute att = (Attribute) attEnum.nextElement();
            if (!att.isNumeric())
                infoGainRatios[att.index()] = computeInfoGainRatio(data, att);
            else
                infoGainRatios[att.index()] = Double.NEGATIVE_INFINITY;
            //System.out.println(att.name() + " " + infoGainRatios[att.index()]);
        }
        
        // TODO: build the tree
        attribute = data.attribute(maxIndex(infoGainRatios));
        //System.out.println(infoGainRatios[maxIndex(infoGainRatios)]);
        // Make leaf if information gain is zero. 
        // Otherwise create successors.
        if (infoGainRatios[maxIndex(infoGainRatios)] <= epsilon || Double.isNaN(infoGainRatios[maxIndex(infoGainRatios)])) {
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
        //pruneTree(data);
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
    
    public double computeInfoGain(Instances data, Attribute attr) throws Exception {
        
        double attributeEntropy = 0;
        
        Instances[] splitData = splitInstancesByAttribute(data, attr);
        for (int i = 0; i < splitData.length; i++) {
            double p = splitData[i].numInstances()/(double)data.numInstances();
            attributeEntropy += p * computeEntropy(splitData[i]);
        }
        
        return computeEntropy(data) - attributeEntropy;
        
    }
    
    @Override
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
        int idx = att.index();
        String name = att.name();

        try {
            Add filter = new Add();
            //filter.setAttributeIndex((idx + 2) + "");
            filter.setNominalLabels("<=" + threshold + ",>" + threshold);
            filter.setAttributeName(name + " " + threshold);
            filter.setInputFormat(newData);
            newData = Filter.useFilter(newData, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Base attribute "+name+" index "+newData.attribute(name).index());
        //System.out.println("New attribute "+newData.attribute(name + " " + threshold).name()+" index "+newData.attribute(name + " " + threshold).index());
        for (int i = 0; i < newData.numInstances(); ++i) {
            if ((double) newData.instance(i).value(newData.attribute(idx)) <= threshold) {
                newData.instance(i).setValue(newData.attribute(name + " " + threshold), "<=" + threshold);
            } else {
                newData.instance(i).setValue(newData.attribute(name + " " + threshold), ">" + threshold);
            }
        }
        
        //newData.deleteAttributeAt(att.index());

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
        if (successors == null) {
            return "C45: No model built yet.";
        }
        
        return "C45\n\n" + toString(0);
    }

    private double getThreshold(Attribute attr) {
        return Double.parseDouble(attr.value(0).replace("<=", ""));
    }
    
    public boolean checkInstance (Instance instance) {
        double cv = instance.classValue();
        return isDoubleEqual(cv, classifyInstance(instance));
    }
    
    public double countError (Instances instances) {
        int ctrFalse = 0;
        int ctr = 0;
        Enumeration enumeration = instances.enumerateInstances();
        while (enumeration.hasMoreElements()) {
            Instance instance = (Instance) enumeration.nextElement();
            if (!checkInstance(instance)) {
                ctrFalse++;
            }
            ctr++;
        }
        return (double) ctrFalse/ (double) (ctr);
    }
    
    
    
    public void pruneTree(Instances data) throws Exception {
        
        //Pruning jika successor != 0
        if (successors != null) {
            for (int i = 0; i < successors.length; i++) {
                double error = countError(data);
        
                MyC45 temp = this.successors[i]; //save children
                this.successors[i] = null; //pruning
                double prunedError = countError(data);

                if (error < prunedError) {
                    //Cancel Pruning
                    this.successors[i] = temp;
                }
            }
        }
        
    }
}
