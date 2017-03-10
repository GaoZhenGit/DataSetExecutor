package com.company.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadExecutor {

    private final static String url = "jdbc:mysql://localhost:3306/weibo";
    private final static String username = "root";
    private final static String password = "123456";
    private final static String named = "./dataDir/database-result-named.txt";
    private final static String unnamed = "./dataDir/database-result-unnamed.txt";
    private final static String sort = "./dataDir/database-result-sort.txt";
    private final static String output = named;

    private final static boolean PRINT_TO_FILE = true;
    private PrintWriter printWriter;

    private static Connection mConnection;
    public static void main(String arg[]) {

        try {
            // 加载MySql的驱动类
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace();
        }

        try {
            mConnection = DriverManager.getConnection(url, username, password);
            ReadExecutor readExecutor = new ReadExecutor();
            readExecutor.start();
            if (PRINT_TO_FILE) {
                readExecutor.getPrintWriter().flush();
                readExecutor.getPrintWriter().close();
            }
        } catch (Exception se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace();
        }finally{
            if (mConnection != null) {
                try {
                    mConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void start() {
//		String sql =
//				"SELECT `follower`. uid , `followee`.`uid`"
//				+ "FROM `user` `follower`,`userrelation` , `user`  `followee`"
//				+ "WHERE `follower`.`uid` = `userrelation`.`suid`"
//				+ "and `followee`.`uid` = `userrelation`.tuid LIMIT 1,20";
//        String sql = "SELECT `user`.`uid`, `userrelation`.`tuid` FROM `user` left join `userrelation` on `user`.`uid` = `userrelation`.`suid`";
        String sql = "select * from `userrelation`";
        PreparedStatement stmt;
        try {
            stmt = mConnection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            printResult(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void printResult(ResultSet resultSet) throws SQLException {
        String lastId = "";
        while (resultSet.next()) {
            if (!lastId.equals(resultSet.getString(1))) {
                if (lastId.length() != 0) {
                    outln();
                }
                if (!output.equals(unnamed)) {
                    out(resultSet.getString(1));
                    if (output.equals(named)) {
                        out(":");
                    }
                }
            }
            if (!output.equals(sort)) {
                out(resultSet.getString(2) + " ");
            }
            lastId = resultSet.getString(1);
        }
    }

    private void out(String s) {
        if (s == null || s.length() == 0 || s.startsWith("null")) {
            return;
        }
        if (PRINT_TO_FILE) {
            try {
                getPrintWriter().append(s);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.print(s);
        }
    }

    private void outln(String s) {
        if (s == null || s.length() == 0 || s.equals("null")) {
            outln();
        }
        if (PRINT_TO_FILE) {
            try {
                getPrintWriter().append(s).append("\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.print(s);
        }
    }
    private void outln() {
        if (PRINT_TO_FILE) {
            try {
                getPrintWriter().append("\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println();
        }
    }

    private PrintWriter getPrintWriter() throws FileNotFoundException {
        if (printWriter == null) {
            File outFile = new File(output);
            if (outFile.exists()) {
                outFile.delete();
            }
            printWriter = new PrintWriter(outFile);
        }
        return printWriter;
    }
}
