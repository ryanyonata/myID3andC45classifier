/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myid3andc45classifier;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author ryanyonata
 */
public class myC45 extends Classifier {

    @Override
    public void buildClassifier(Instances data) throws Exception {
        data = new Instances(data);
        data.deleteWithMissingClass();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
