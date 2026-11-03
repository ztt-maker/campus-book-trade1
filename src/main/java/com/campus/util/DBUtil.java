package com.campus.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库工具类
 * 1. 启动时读取 db.properties，注册 JDBC 驱动
 * 2. 提供获取连接、关闭资源的方法
 */
public class DBUtil {

    private static String url;
    private static String username;
    private static String password;

    // 静态代码块：类加载时执行一次，读取配置并加载驱动
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("未找到 db.properties 配置文件");
            }
            Properties props = new Properties();
            props.load(in);

            String driver = props.getProperty("driver");
            url = props.getProperty("url");
            username = props.getProperty("username");
            password = props.getProperty("password");

            Class.forName(driver); // 注册驱动
        } catch (Exception e) {
            // 配置或驱动有问题时，直接抛出，方便在启动阶段发现问题
            throw new ExceptionInInitializerError("数据库初始化失败：" + e.getMessage());
        }
    }

    /** 获取数据库连接 */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /** 关闭结果集、语句对象、连接（常用于查询） */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(conn, stmt);
    }

    /** 关闭连接和语句对象（常用于增删改） */
    public static void close(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
