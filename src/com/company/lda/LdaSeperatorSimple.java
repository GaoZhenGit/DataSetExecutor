package com.company.lda;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by host on 2017/1/29.
 */
@Deprecated
public class LdaSeperatorSimple {
    private Map<String, Integer> wordmap = new HashMap<>();
    private List<String> wordList = new ArrayList<>();
    private List<String> docList = new ArrayList<>();

    private List<List<String>> followRelation = new LinkedList<>();

    private List<Double> docM;
    private List<Double> wordM;

    private List<PrintWriter> resultMatrix;

    ExecutorService service = Executors.newFixedThreadPool(LdaConstant.TopicCount);

    public static void main(String[] args) throws InterruptedException {

        LdaSeperatorSimple ldaSeperator = new LdaSeperatorSimple();
        ldaSeperator.generatorWordMap();
        ldaSeperator.generatorDocMap();
        ldaSeperator.generatorFollowRelation();
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

    private void generatorFollowRelation() {
        File followFile = new File(LdaConstant.LdaDir + File.separator + LdaConstant.DataInputUnnamed);
        try {
            Scanner scanner = new Scanner(followFile);
            int userCount = 0;
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] followees = line.split(" ");
                List<String> followeeList = new LinkedList<>();
                for (String followee : followees) {
                    followeeList.add(followee);
                }
                followRelation.add(followeeList);
                userCount++;
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
                List<String> curDocFollowees = followRelation.get(j);//当前follower所follow的followee组
                for (int k = 0; k < wordList.size(); k++) {
                    String wordId = wordList.get(k);
                    if (word(i, j) != 0.0 && curDocFollowees.contains(wordId)) {//
                        writeTopicM(i).print(k + " ");
                    }
                }
            }
            writeTopicM(i).print("\n");
        }
        writeTopicM(i).flush();
        System.out.println("---------" + topicTh + " end---------");
    }

    private double doc(int row, int col) {
        return docM.get((row) * LdaConstant.TopicCount + col);
    }

    private double word(int row, int col) {
        return wordM.get((row) * LdaConstant.TopicCount + col);
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
        if (resultMatrix == null) {
            resultMatrix = new ArrayList<>();
            File dir = new File(LdaConstant.TopicMatrixDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (int i = 0; i < LdaConstant.TopicCount; i++) {
                File file = new File(LdaConstant.TopicMatrixDir + File.separator + i + ".simple");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    resultMatrix.add(printWriter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PrintWriter writeTopicM(int topicNo) {
        return resultMatrix.get(topicNo);
    }
}
