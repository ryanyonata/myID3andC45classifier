/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier;

import java.util.Enumeration;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;

/**
 *
 * @author ryanyonata
 */
public class myID3 extends Classifier {
    
    //Daftar atribut kelas
    private myID3[] successors; 
    private Attribute attribute;
    private double classValue;
    private double[] distribution;
    private Attribute classAttribute;
    
    
    //Methods

    @Override
    public void buildClassifier(Instances data) throws Exception {        
        if (!data.classAttribute().isNominal()) {
            throw new Exception("Id3: nominal class, please.");
        }
        Enumeration enumAtt = data.enumerateAttributes();
        while (enumAtt.hasMoreElements()) {
            Attribute attr = (Attribute) enumAtt.nextElement();
            if (!attr.isNominal()) {
                throw new Exception("Id3: only nominal attributes, please.");
            }
            Enumeration enumInstance = data.enumerateInstances();
            while (enumInstance.hasMoreElements()) {
                if (((Instance) enumInstance.nextElement()).isMissing(attr)) {
                    throw new Exception("Id3: no missing values, please.");
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
            throw new NoSupportForMissingValuesException("Id3: no missing values, please");
        }
        
        if (attribute == null) {
            return classValue;
        } else {
            return successors[(int)instance.value(attribute)].classifyInstance(instance);
        }
  
    }
    
    public void makeMyID3Tree(Instances data) throws Exception {
        
        // Compute attribute with maximum information gain.
        double[] infoGains = new double[data.numAttributes()];
        Enumeration attEnum = data.enumerateAttributes();
        while (attEnum.hasMoreElements()) {
            Attribute att = (Attribute) attEnum.nextElement();
            infoGains[att.index()] = computeInfoGain(data, att);
        }
    
        attribute = data.attribute(Utils.maxIndex(infoGains));

        // Make leaf if information gain is zero. 
        // Otherwise create successors.
        if (Utils.eq(infoGains[attribute.index()], 0)) {
            attribute = null;
            distribution = new double[data.numClasses()];
            
            Enumeration instEnum = data.enumerateInstances();
            while (instEnum.hasMoreElements()) {
                Instance inst = (Instance) instEnum.nextElement();
                distribution[(int) inst.classValue()]++;
            }

            Utils.normalize(distribution);
            classValue = Utils.maxIndex(distribution);
            classAttribute = data.classAttribute();
        } else {
            Instances[] splitData = splitData(data, attribute);
            successors = new myID3[attribute.numValues()];
            for (int j = 0; j < attribute.numValues(); j++) {
                successors[j] = new myID3();
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
    
    public double computeAttributeEntropy(Instances data, Attribute attr) throws Exception {
        double attributeEntropy = 0;
        
        Instances[] splitData = splitData(data, attr);
        for (int i = 0; i < splitData.length; i++) {
            double p = splitData[i].numInstances()/(double)data.numInstances();
            attributeEntropy += p * computeEntropy(splitData[i]);
        }
        
        return attributeEntropy;
    }
    
    public double computeInfoGain(Instances data, Attribute attr) throws Exception {
        
        return computeEntropy(data) - computeAttributeEntropy(data, attr);
        
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
            return "Id3: No model built yet.";
        }
        
        return "Id3\n\n" + toString(0);
    }
    
}
