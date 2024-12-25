package com.fish.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtil {
    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String usrHome = System.getProperty("user.home");
            c = DriverManager.getConnection(String.format("jdbc:sqlite:%s/.funtool/funtool.db", usrHome));
//            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return c;
    }
}
