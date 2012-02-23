package org.nqlinq.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class NQLINQConnection {
    private static Connection Conn;
    private static long LastAccessTime;

    public static long getLastAccessTime() {
        return LastAccessTime;
    }

    public static void setLastAccessTime(long lastAccessTime) {
        LastAccessTime = lastAccessTime;
    }

    public static Connection getConnection() {
        return Conn;
    }

    public static void setConnection(Connection conn) {
        Conn = conn;
    }

    public void open() {

    }

    public void close() {
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return Conn.prepareStatement(sql);
    }

    public Statement createStatement() throws SQLException {
        return Conn.createStatement();
    }
}
