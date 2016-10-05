/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

/**
 *
 * @author ryanyonata
 */
public class myC45 extends Classifier {

    
    private myC45[] successors; 
    private Attribute attribute;
    private double classValue;
    private double[] distribution;
    private Attribute classAttribute;
    
    @Override
    public void buildClassifier(Instances data) throws Exception {
        getCapabilities().testWithFail(data);
        
        Enumeration enumAtt = data.enumerateAttributes();
        while (enumAtt.hasMoreElements()) {
            Attribute attr = (Attribute) enumAtt.nextElement();
            if (!attr.isNominal()) {
                // TODO: split nominal
            }
        }
        
        data = new Instances(data);
        data.deleteWithMissingClass();
        
    }
    
    public void makeMyC45Tree(Instances data) throws Exception {
        
        double[] infoGainRatios = new double[data.numAttributes()];
        Enumeration attEnum = data.enumerateAttributes();
        while (attEnum.hasMoreElements()) {
            Attribute att = (Attribute) attEnum.nextElement();
            infoGainRatios[att.index()] = computeInfoGainRatio(data, att);
        }
        
        // TODO: build the tree
        
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
    
    public Instances[] splitData(Instances data, Attribute attr) throws Exception {
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
        
        Instances[] splitData = splitData(data, attr);
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
    
    public String toString(int level) {
        
        StringBuffer text = new StringBuffer();
        
        if (attribute == null) {
            if (Instance.isMissingValue(classValue)) {
                text.append(": null");
            } else {
                text.append(": " + classAttribute.value((int) classValue));
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
