package com.company.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by host on 2017/1/31.
 */
public class TextReader {
    public static void main(String[] args) {
        File text = new File("../links-anon.txt");
        try {
            PrintWriter writer = new PrintWriter("../relation0.txt");
            Scanner scanner = new Scanner(new FileInputStream(text));
            String lastId = "";
            scanner.nextLine();
            int lineCount = 0;
            int fileCount = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] relation = line.split(" ");
                String follower = relation[0];
                String followee = relation[1];
                if (!follower.equals(lastId)) {
                    writer.print("\n");
                    if (lineCount % 500000 == 0) {
                        writer.flush();
                        writer.close();
                        writer = new PrintWriter("../relation"+ fileCount +".txt");
                        fileCount++;
                    }
                    writer.print(follower);
                    writer.print(":");
                    lineCount++;
                }
                writer.print(followee);
                writer.print(" ");
                lastId = follower;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
