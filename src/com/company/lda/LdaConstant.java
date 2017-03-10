package com.company.lda;

import java.io.File;

/**
 * Created by host on 2017/1/29.
 */
public interface LdaConstant {
    String DataInputNamed = "database-result-named.txt";
    String DataInputUnnamed = "database-result-unnamed.txt";
    int UserCount = 4359;
    int TopicCount = 15;
    int WordCount = 5733;//手动填写

    String filterDecimalFormat = "0.000000000000000";//过滤器格式

    String dataDir = "./dataDir";
    String LdaDir = "./ldaDir";
    String ModelName = "model-final";
    String Theta = LdaDir + File.separator + ModelName + ".theta";
    String Phi = LdaDir + File.separator + ModelName + ".phi";
    String WordMap = LdaDir + File.separator + "wordmap.txt";
    String DocMap = LdaDir + File.separator + "database-result-sort.txt";

    String ThetaAfter = Theta + "-after";
    String PhiAfter = Phi + "-after";

    String TopicMatrixDir = LdaDir + File.separator + "topic";
    String MfDir = "./mfDir";
    String MfMatrixDir = MfDir + File.separator + "matrix";
}
