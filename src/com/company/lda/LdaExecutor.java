package com.company.lda;

import core.algorithm.lda.Estimator;
import core.algorithm.lda.LDAOption;

/**
 * Created by host on 2017/1/26.
 */
public class LdaExecutor {

    public static void main(String[] arg) {
        LDAOption option = new LDAOption();
        option.dir = LdaConstant.LdaDir;
        option.dfile = LdaConstant.DataInputUnnamed;
        option.est = true;  /////
        option.estc = false;
        option.inf = false;
        option.modelName = "model-final";
        option.niters = 100;
        option.savestep = 100;
        option.K = LdaConstant.TopicCount;
        Estimator estimator = new Estimator();
        estimator.init(option);
        estimator.estimate();
    }
}
