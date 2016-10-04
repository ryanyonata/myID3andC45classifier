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
        makeMyTree(data);
    }
    
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
    
    public void makeMyTree(Instances data) throws Exception {
        
    }
    
    public double computeEntropy(Instances data) throws Exception {
        
        double[] classCounts = new double[data.numClasses()]; //array untuk menyimpan value kelas sesuai jumlah kelas
        Enumeration instanceEnum = data.enumerateInstances();
        
        //Masukkan data ke array
        while (instanceEnum.hasMoreElements()) {
            Instance inst = (Instance) instanceEnum.nextElement();
            classCounts[(int)inst.classValue()]++;
        }
        
        double entropy = 0;
        for (int i = 0; i < data.numClasses(); i++) {
            if (classCounts[i] > 0) {
                entropy -=  (double)(classCounts[i]/data.numInstances()) * (Utils.log2(classCounts[i])/data.numInstances());
            }
        }
        
        return entropy;
    
    }
    
    public double computeInfoGain(Instances data, Attribute attr) {
        return 0;
    }
    
}
