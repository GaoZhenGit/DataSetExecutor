package com.company.mf;

import com.company.lda.LdaConstant;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/2/4.
 */
public class MatrixReader {
    private Map<String, List<String>> followRelation = new LinkedHashMap<>();

    private List<PrintWriter> matrixWriters;

    private IOutput mOutput;

    private ExecutorService service = Executors.newFixedThreadPool(LdaConstant.TopicCount);

    public static void main(String[] args) throws IOException {
        MatrixReader matrixReader = new MatrixReader();
        matrixReader.mOutput = matrixReader.new SimpleOutImpl();
        matrixReader.prepareRelation();
        matrixReader.prepareWriter();
        matrixReader.printMatrix();
        System.currentTimeMillis();
    }

    private void prepareRelation() {
        File followFile = new File(LdaConstant.dataDir + File.separator + LdaConstant.DataInputNamed);
        try {
            Scanner scanner = new Scanner(followFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] idAndFollowees = line.split(":");
                String followerId = idAndFollowees[0];
                List<String> followeeList = new LinkedList<>();
                if (idAndFollowees.length > 1) {
                    String[] followees = idAndFollowees[1].split(" ");
                    for (String followee : followees) {
                        followeeList.add(followee);
                    }
                }
                followRelation.put(followerId, followeeList);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareWriter() throws IOException {
        File outputDir = new File(LdaConstant.MfMatrixDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        matrixWriters = new ArrayList<>();
        for (int i = 0; i < LdaConstant.TopicCount; i++) {
            File output = mOutput.getPrintWriter(i);
            if (!output.exists()) {
                output.createNewFile();
            }
            PrintWriter printWriter = new PrintWriter(output);
            matrixWriters.add(printWriter);
        }
    }

    private PrintWriter getWriter(int index) {
        return matrixWriters.get(index);
    }

    private void printMatrix() {
        for (int i = 0; i < LdaConstant.TopicCount; i++) {
            final int fIndex = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    printMatrix(fIndex);
                }
            });
        }
        service.shutdown();
    }

    private void printMatrix(int index) {
        System.out.println("--------" + index + " start-------");
        File f_c_n = new File(LdaConstant.TopicMatrixDir + File.separator + "f_c_" + index);
        File g_c_n = new File(LdaConstant.TopicMatrixDir + File.separator + "g_c_" + index);

        List<String> followerList = new ArrayList<>();
        List<String> followeeList = new ArrayList<>();
        try {
            Scanner f_c_nScanner = new Scanner(f_c_n);
            while (f_c_nScanner.hasNextLine()) {
                String follower = f_c_nScanner.nextLine().split(" ")[0];//从f_c_n中提取所有的follower
                followerList.add(follower);
            }

            Scanner g_c_nScanner = new Scanner(g_c_n);
            while (g_c_nScanner.hasNextLine()) {
                String followee = g_c_nScanner.nextLine();
                followeeList.add(followee);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mOutput.makeMatrix(followerList, followeeList, index);
        System.out.println("--------" + index + " end---------");
    }

    private interface IOutput {
        void makeMatrix(List<String> followerList, List<String> followeeList, int matrixIndex);
        File getPrintWriter(int i);
    }

    private class FullOutputImpl implements IOutput {

        @Override
        public void makeMatrix(List<String> followerList, List<String> followeeList, int index) {

            int followerCount = followerList.size();
            int followeeCount = followeeList.size();
            for (int i = 0; i < followerCount; i++) {

                String followerId = followerList.get(i);
                List<String> curUserFollowees = followRelation.get(followerId);
                for (int j = 0; j < followeeCount; j++) {
                    String followeeId = followeeList.get(j);
                    if (curUserFollowees.contains(followeeId)) {
                        getWriter(index).print(1);
                    } else {
                        getWriter(index).print(0);
                    }
                }
                getWriter(index).print("\n");
            }
            getWriter(index).flush();
        }

        @Override
        public File getPrintWriter(int i) {
            return new File(LdaConstant.MfMatrixDir + File.separator + "zM" + i);
        }
    }

    private class SimpleOutImpl implements IOutput {

        @Override
        public void makeMatrix(List<String> followerList, List<String> followeeList, int index) {
            int followerCount = followerList.size();
            int followeeCount = followeeList.size();
            getWriter(index).print(followerCount + "*" + followeeCount + '\n');
            for (int i = 0; i < followerCount; i++) {
                String followerId = followerList.get(i);
                List<String> curUserFollowees = followRelation.get(followerId);
//                for (int j = 0; j < curUserFollowees.size(); j++) {
//                    String followeeId = followeeList.get(j);
//                    if (curUserFollowees.contains(followeeId)) {
//                        getWriter(index).print(curUserFollowees.get(j) + " ");
//                    }
//                }
                for (int j = 0; j < followeeCount; j++) {
                    String followeeId = followeeList.get(j);
                    if (curUserFollowees.contains(followeeId)) {
                        getWriter(index).print(j);
                        getWriter(index).print(" ");
                    }
                }
                getWriter(index).print("\n");
            }
            getWriter(index).flush();
        }

        @Override
        public File getPrintWriter(int i) {
            return new File(LdaConstant.MfMatrixDir + File.separator + "zMsimple" + i);
        }
    }
}
