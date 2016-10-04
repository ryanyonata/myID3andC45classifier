/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier;

import java.util.Enumeration;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;
import weka.gui.beans.Classifier;

/**
 *
 * @author ryanyonata
 */
public class myID3 extends AbstractClassifier {
    //Daftar atribut kelas
    private myID3[] successors; 
    private Attribute attribute;
    private double classValue;
    
    //Methods

    @Override
    public void buildClassifier(Instances data) throws Exception {        
        //Hapus data yang tidak ada kelasnya
        data = new Instances(data);
        data.deleteWithMissingClass();
        
        //Buat pohon
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
        
        //
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
}
