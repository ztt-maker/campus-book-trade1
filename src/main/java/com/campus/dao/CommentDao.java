package com.campus.dao;

import com.campus.entity.Comment;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价数据访问类
 * 全部使用原生 JDBC + DBUtil
 */
public class CommentDao {

    /**
     * 新增评价
     *
     * @return 成功返回 true
     */
    public boolean add(Comment comment) {
        String sql = "INSERT INTO `comment` (book_id, user_id, score, content) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, comment.getBookId());
            ps.setInt(2, comment.getUserId());
            ps.setInt(3, comment.getScore() == null ? 5 : comment.getScore());
            ps.setString(4, comment.getContent());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /** 根据教材ID查询该教材的全部评价（详情页使用，带出评价人姓名） */
    public List<Comment> findByBookId(int bookId) {
        String sql = "SELECT c.*, u.name AS user_name FROM `comment` c "
                + "JOIN `user` u ON c.user_id = u.id "
                + "WHERE c.book_id = ? ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Comment> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bookId);
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

    /** 查询某个用户发布的全部评价（我的评价页，带出教材名） */
    public List<Comment> findByUserId(int userId) {
        String sql = "SELECT c.*, u.name AS user_name, b.name AS book_name FROM `comment` c "
                + "JOIN `user` u ON c.user_id = u.id "
                + "JOIN book b ON c.book_id = b.id "
                + "WHERE c.user_id = ? ORDER BY c.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Comment> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
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

    /**
     * 判断某个用户是否已经评价过某本教材
     * 评价模块用来防止重复评价
     */
    public boolean existsByUserAndBook(int userId, int bookId) {
        String sql = "SELECT COUNT(*) FROM `comment` WHERE user_id = ? AND book_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return false;
    }

    /** 把结果集当前行封装成 Comment 对象 */
    private Comment mapRow(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setBookId(rs.getInt("book_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setScore(rs.getInt("score"));
        comment.setContent(rs.getString("content"));
        comment.setCreateTime(rs.getTimestamp("create_time"));
        comment.setUserName(rs.getString("user_name"));
        try {
            // 我的评价列表有教材名，详情页评价列表没有该列，取不到则忽略
            comment.setBookName(rs.getString("book_name"));
        } catch (SQLException ignored) {
        }
        return comment;
    }
}
