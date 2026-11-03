package com.campus.dao;

import com.campus.entity.User;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问类
 * 全部使用原生 JDBC + DBUtil，不依赖任何框架
 */
public class UserDao {

    /**
     * 登录：根据用户名和密码查询
     * 注意：传入的 password 必须是 MD5 加密后的值
     *
     * @return 查到返回 User 对象，查不到返回 null
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM `user` WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 简单处理：打印日志，返回 null，避免页面 500
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    /**
     * 注册：新增用户，角色默认为 student
     * 这里把 SQLException 抛给 Servlet 处理，便于区分“用户名重复 / 数据库连不上”等原因
     *
     * @return 成功返回 true
     */
    public boolean add(User user) throws SQLException {
        // 字段顺序：username, password, name, role, phone 与下面的 1~5 号参数一一对应
        String sql = "INSERT INTO `user` (username, password, name, role, phone) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());   // 用户名
            ps.setString(2, user.getPassword());   // MD5 后的密码
            ps.setString(3, user.getName());       // 姓名
            ps.setString(4, user.getRole() == null ? "student" : user.getRole()); // 角色
            ps.setString(5, user.getPhone());      // 手机号
            return ps.executeUpdate() > 0;
        } finally {
            DBUtil.close(conn, ps);
        }
    }

    /** 查询全部用户（管理员后台使用） */
    public List<User> findAll() {
        String sql = "SELECT * FROM `user` ORDER BY id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 根据主键查询用户 */
    public User findById(int id) {
        String sql = "SELECT * FROM `user` WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    /** 根据用户名查询（注册时判断用户名是否已被占用） */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM `user` WHERE username = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    /** 把结果集当前行封装成 User 对象 */
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
}
