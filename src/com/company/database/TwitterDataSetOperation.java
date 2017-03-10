package com.company.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by host on 2017/3/2.
 */
public class TwitterDataSetOperation {
    private final static String url = "jdbc:mysql://localhost:3306/twitter";
    private final static String username = "root";
    private final static String password = "123456";
    private Connection mConnection;

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        TwitterDataSetOperation twitterDataSetOperation = new TwitterDataSetOperation();
        twitterDataSetOperation.prepareDatabase();
        twitterDataSetOperation.insert();
    }

    private void prepareDatabase() {
        try {
            // 加载MySql的驱动类
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace();
        }

        try {
            mConnection = DriverManager.getConnection(url, username, password);
        } catch (Exception se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace();
        }
    }

    private void insert() throws SQLException, FileNotFoundException {
        String sql = "insert into releation values(?,?)";
        PreparedStatement statement = mConnection.prepareStatement(sql);
        File text = new File("../links-anon.txt");
        Scanner scanner = new Scanner(new FileInputStream(text));
        int count = 1;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] pair = line.split(" ");
            if (pair.length < 2)
                continue;
            statement.setString(1,pair[0]);
            statement.setString(2,pair[1]);
            statement.addBatch();
            if (count % 1000 == 0) {
                statement.executeBatch();
            }
            count++;
        }
    }
}
