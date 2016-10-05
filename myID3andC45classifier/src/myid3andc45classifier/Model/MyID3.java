/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier.Model;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;

/**
 *
 * @author ryanyonata
 */
public class MyID3 extends Classifier {
    
    //Daftar atribut kelas
    private MyID3[] successors; 
    private Attribute attribute;
    private double label;
    private Attribute classAttribute;
    private double classValue;
    private static final double epsilon = 1e-6;
    
    //Methods
    @Override
    public void buildClassifier(Instances data) throws Exception {        
        if (!data.classAttribute().isNominal()) {
            throw new Exception("MyID3: nominal class, please.");
        }
        Enumeration enumAtt = data.enumerateAttributes();
        while (enumAtt.hasMoreElements()) {
            Attribute attr = (Attribute) enumAtt.nextElement();
            if (!attr.isNominal()) {
                throw new Exception("MyID3: only nominal attributes, please.");
            }
            Enumeration enumInstance = data.enumerateInstances();
            while (enumInstance.hasMoreElements()) {
                if (((Instance) enumInstance.nextElement()).isMissing(attr)) {
                    throw new Exception("MyID3: no missing values, please.");
                }
            }
        }
        data = new Instances(data);
        data.deleteWithMissingClass(); 
        makeMyID3Tree(data);
    }
    
    @Override
    public double classifyInstance(Instance instance) throws NoSupportForMissingValuesException {
        
        //Periksa apakah instance memiliki missing value
        if (instance.hasMissingValue()) {
            throw new NoSupportForMissingValuesException("MyID3: no missing values, please");
        }
        
        if (attribute == null) {
            return label;
        } else {
            return successors[(int)instance.value(attribute)].classifyInstance(instance);
        }
  
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
    
    public void makeMyID3Tree(Instances data) throws Exception {
        
        // Mengecek apakah tidak terdapat instance yang dalam node ini
        if (data.numInstances() == 0) {
            attribute = null;
            classValue = Instance.missingValue();
            return;
         }
        
        // Compute attribute with maximum information gain.
        double[] infoGains = new double[data.numAttributes()];
        Enumeration attEnum = data.enumerateAttributes();
        while (attEnum.hasMoreElements()) {
            Attribute att = (Attribute) attEnum.nextElement();
            infoGains[att.index()] = computeInfoGain(data, att);
        }
    
        attribute = data.attribute(maxIndex(infoGains));

        // Make leaf if information gain is zero. 
        // Otherwise create successors.
        if (isDoubleEqual(infoGains[attribute.index()], 0)) {
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
            successors = new MyID3[attribute.numValues()];
            for (int j = 0; j < attribute.numValues(); j++) {
                successors[j] = new MyID3();
                successors[j].buildClassifier(splitData[j]);
            }
        }
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
    
    private double log2(double num) {
        return (num == 0) ? 0 : Math.log(num) / Math.log(2);
    }
    
    public double computeEntropy(Instances data) throws Exception {
        
        double entropy = 0;
        
        double[] classCounts = listClassCountsValues(data);
        for (int i = 0; i < data.numClasses(); i++) {
            if (classCounts[i] > 0) {
                double p = classCounts[i]/(double)data.numInstances(); 
                entropy -=  p * (log2(p));
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
    
    public double computeAttributeEntropy(Instances data, Attribute attr) throws Exception {
        double attributeEntropy = 0;
        
        Instances[] splitData = splitInstancesByAttribute(data, attr);
        for (int i = 0; i < splitData.length; i++) {
            double p = splitData[i].numInstances()/(double)data.numInstances();
            attributeEntropy += p * computeEntropy(splitData[i]);
        }
        
        splitData = null;
        
        return attributeEntropy;
    }
    
    public double computeInfoGain(Instances data, Attribute attr) throws Exception {
        
        return computeEntropy(data) - computeAttributeEntropy(data, attr);
        
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
            return "MyID3: No model built yet.";
        }
        
        return "MyID3\n\n" + toString(0);
    }
    
}
