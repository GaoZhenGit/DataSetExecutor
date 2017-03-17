package com.company.lda;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by host on 2017/1/28.
 */
public class LdaFilter {

    final static java.text.DecimalFormat df = new DecimalFormat(LdaConstant.filterDecimalFormat);

    public static void main(String[] args) throws FileNotFoundException {
        File theta = new File(LdaConstant.Theta);
        File thetaAfter = new File(LdaConstant.ThetaAfter);
        filterTheta(theta, thetaAfter, 0.05);
        File phi = new File(LdaConstant.Phi);
        File phiAfter = new File(LdaConstant.PhiAfter);
        filterPhi(phi, phiAfter, 500);
    }

    private static void filterTheta(File before, File after, double threshold) throws FileNotFoundException {
        System.out.println("------start filter theta------");
        Scanner scanner = new Scanner(new FileInputStream(before));
        PrintWriter writer = new PrintWriter(new PrintWriter(after));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] topics = line.split(" ");
            for (String topic : topics) {
                double d = Double.valueOf(topic);
                if (d <= threshold) {
                    d = 0;
                }
                writer.print(df.format(d) + " ");
            }
            writer.print("\n");
        }
        writer.flush();
        writer.close();
        scanner.close();
        System.out.println("------end filter theta------");
    }

    private static void filterPhi(File before, File after, int rank) throws FileNotFoundException {
        System.out.println("------start filter phi------");
        Scanner scanner = new Scanner(new FileInputStream(before));
        PrintWriter writer = new PrintWriter(new PrintWriter(after));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] topicsStr = line.split(" ");
            List<Double> topicList = new ArrayList<>(topicsStr.length);
            Set<Double> toplicSet = new HashSet<>();
            for (String topic : topicsStr) {
                topicList.add(Double.valueOf(topic));
                toplicSet.add(Double.valueOf(topic));
            }
            System.out.println(toplicSet.size());
            topicList.sort(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    return o1.compareTo(o2);
                }
            });
            Double rankThreshold = topicList.get(topicsStr.length - rank);
            for (String topic : topicsStr) {
                double d = Double.valueOf(topic);
                if (d < rankThreshold) {
                    d = 0.0;
                }
                writer.print(df.format(d) + " ");
            }
            writer.print("\n");
        }
        writer.flush();
        writer.close();
        scanner.close();
        System.out.println("------end filter phi------");
    }
}
