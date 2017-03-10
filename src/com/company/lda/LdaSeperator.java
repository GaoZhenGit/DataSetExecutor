package com.company.lda;

import core.algorithm.lda.LDA;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by host on 2017/1/29.
 */
public class LdaSeperator {
    private Map<String, Integer> wordmap = new HashMap<>();
    private List<String> wordList = new ArrayList<>();
    private List<String> docList = new ArrayList<>();

//    private List<List<String>> followRelation = new LinkedList<>();

    private List<Double> docM;
    private List<Double> wordM;

    private List<PrintWriter> followerMatrix;
    private List<PrintWriter> followeeMatrix;

    ExecutorService service = Executors.newFixedThreadPool(LdaConstant.TopicCount);
    final static java.text.DecimalFormat df = new DecimalFormat(LdaConstant.filterDecimalFormat);

    public static void main(String[] args) throws InterruptedException {

        LdaSeperator ldaSeperator = new LdaSeperator();
        ldaSeperator.generatorWordMap();
        ldaSeperator.generatorDocMap();
        ldaSeperator.prepareM();
        ldaSeperator.prepareWriter();
        long startTime = System.currentTimeMillis();
        ldaSeperator.printResult();
        while (!ldaSeperator.service.isTerminated()) {
            Thread.sleep(500);
        }
        System.out.print("end" + (System.currentTimeMillis() - startTime) / 1000);
    }

    private void generatorWordMap() {
        File wordMapFile = new File(LdaConstant.WordMap);
        try {
            Scanner wordScanner = new Scanner(new BufferedInputStream(new FileInputStream(wordMapFile)));
            wordScanner.nextLine();
            Map<Integer, String> wordSortMap = new HashMap<>();
            while (wordScanner.hasNextLine()) {
                String[] wordKV = wordScanner.nextLine().split(" ");
                wordmap.put(wordKV[0], Integer.valueOf(wordKV[1]));
                wordSortMap.put(Integer.valueOf(wordKV[1]), wordKV[0]);
            }
            int count = wordSortMap.size();
            for (int i = 0; i < count; i++) {
                wordList.add(wordSortMap.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generatorDocMap() {
        File docMapFile = new File(LdaConstant.DocMap);
        try {
            Scanner docScanner = new Scanner(new BufferedInputStream(new FileInputStream(docMapFile)));
            while (docScanner.hasNextLine()) {
                docList.add(docScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printResult() {
        for (int i = 0; i < LdaConstant.TopicCount; i++) {//一级循环，一次循环写入一个一零矩阵（topic维度区分）
            final int index = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    printResult(index);
                }
            });
        }
        service.shutdown();
    }

    private void printResult(int topicTh) {
        System.out.println("---------" + topicTh + " start---------");
        int i = topicTh;
        for (int j = 0; j < LdaConstant.UserCount; j++) {
            double docValue = doc(j, i);
            if (docValue != 0.0) {
                writeTopicM(i, true).print(docList.get(j));
                writeTopicM(i, true).print(" ");
                writeTopicM(i, true).print(df.format(docValue));
                writeTopicM(i, true).print("\n");
            }
        }
        writeTopicM(i, true).flush();
        for (int j = 0; j < LdaConstant.WordCount; j++) {
            double wordValue = word(i, j);
            if (wordValue != 0.0) {
                writeTopicM(i, false).print(wordList.get(j));
                writeTopicM(i, false).print("\n");
            }
        }
        writeTopicM(i, false).flush();
        System.out.println("---------" + topicTh + " end---------");
    }

    private double doc(int row, int col) {
        return docM.get((row) * LdaConstant.TopicCount + col);
    }

    private double word(int row, int col) {
        return wordM.get((row) * LdaConstant.WordCount + col);
    }

    private void prepareM() {
        docM = new ArrayList<>(LdaConstant.UserCount * LdaConstant.TopicCount);

        File thetaFile = new File(LdaConstant.ThetaAfter);
        try {
            Scanner thetaScanner = new Scanner(thetaFile);
            while (thetaScanner.hasNext()) {
                docM.add(Double.valueOf(thetaScanner.next()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        wordM = new ArrayList<>(LdaConstant.TopicCount * LdaConstant.WordCount);
        File phiFile = new File(LdaConstant.PhiAfter);
        try {
            Scanner phiScanner = new Scanner(phiFile);
            while (phiScanner.hasNext()) {
                wordM.add(Double.valueOf(phiScanner.next()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareWriter() {
        if (followerMatrix == null) {
            followerMatrix = new ArrayList<>();
            File dir = new File(LdaConstant.TopicMatrixDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (int i = 0; i < LdaConstant.TopicCount; i++) {
                File file = new File(LdaConstant.TopicMatrixDir + File.separator + "f_c_" + i);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    followerMatrix.add(printWriter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (followeeMatrix == null) {
            followeeMatrix = new ArrayList<>();
            File dir = new File(LdaConstant.TopicMatrixDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (int i = 0; i < LdaConstant.TopicCount; i++) {
                File file = new File(LdaConstant.TopicMatrixDir + File.separator + "g_c_" + i);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    followeeMatrix.add(printWriter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PrintWriter writeTopicM(int topicNo, boolean isFollower) {
        if (isFollower) {
            return followerMatrix.get(topicNo);
        } else {
            return followeeMatrix.get(topicNo);
        }
    }
}
