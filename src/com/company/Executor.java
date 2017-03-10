package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by host on 2017/3/9.
 */
public class Executor {
    public static void main(String[] args) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("python D:\\JavaProject\\DataSetExecutor\\src\\com\\company\\conclusion\\conculsion_cal.py");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }
}
