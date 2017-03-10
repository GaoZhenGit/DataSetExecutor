package com.company;

import java.io.File;

/**
 * Created by host on 2017/3/8.
 */
public class Clean {
    public static void main(String[] arg) {
        delete("dataDir");
        delete("ldaDir");
        delete("mfDir/matrix");
        delete("mfDir/PQ");
        delete("mfDir/score");
    }

    private static void delete(String file) {
        delete(new File(file));
    }

    private static void delete(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File subFile : files) {
                    delete(subFile);
                }
            } else {
                file.delete();
            }
        }
    }
}
