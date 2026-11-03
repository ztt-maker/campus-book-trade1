package com.campus.dao;

import com.campus.entity.Book;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 教材数据访问类
 * 全部使用原生 JDBC + DBUtil
 */
public class BookDao {

    /**
     * 发布教材
     *
     * @return 成功返回 true
     */
    public boolean add(Book book) {
        String sql = "INSERT INTO book (name, author, publisher, grade, subject, book_condition, "
                + "price, stock, image, description, user_id, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getGrade());
            ps.setString(5, book.getSubject());
            ps.setString(6, book.getBookCondition());
            ps.setDouble(7, book.getPrice() == null ? 0 : book.getPrice());
            ps.setInt(8, book.getStock() == null ? 1 : book.getStock());
            ps.setString(9, book.getImage());
            ps.setString(10, book.getDescription());
            ps.setInt(11, book.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /**
     * 查询全部在售教材（前台列表 / 首页使用）
     * 按发布时间倒序，并带出发布人姓名
     */
    public List<Book> findAll() {
        String sql = "SELECT b.*, u.name AS seller_name FROM book b "
                + "JOIN `user` u ON b.user_id = u.id "
                + "WHERE b.status = 1 ORDER BY b.create_time DESC";
        return queryList(sql, null);
    }

    /** 管理员查询全部教材（包含已下架） */
    public List<Book> findAllForAdmin() {
        String sql = "SELECT b.*, u.name AS seller_name FROM book b "
                + "JOIN `user` u ON b.user_id = u.id "
                + "ORDER BY b.create_time DESC";
        return queryList(sql, null);
    }

    /**
     * 按教材名称模糊搜索（只搜在售教材）
     *
     * @param keyword 关键词
     */
    public List<Book> search(String keyword) {
        String sql = "SELECT b.*, u.name AS seller_name FROM book b "
                + "JOIN `user` u ON b.user_id = u.id "
                + "WHERE b.status = 1 AND b.name LIKE ? ORDER BY b.create_time DESC";
        return queryList(sql, "%" + keyword + "%");
    }

    /** 查询某个用户发布的教材（我的发布，包含已下架） */
    public List<Book> findByUserId(int userId) {
        String sql = "SELECT b.*, u.name AS seller_name FROM book b "
                + "JOIN `user` u ON b.user_id = u.id "
                + "WHERE b.user_id = ? ORDER BY b.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Book> list = new ArrayList<>();
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

    /** 根据主键查询教材（详情页使用） */
    public Book findById(int id) {
        String sql = "SELECT b.*, u.name AS seller_name FROM book b "
                + "JOIN `user` u ON b.user_id = u.id WHERE b.id = ?";
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

    /**
     * 修改教材信息（不修改上下架状态，状态由 updateStatus 单独控制）
     *
     * @return 成功返回 true
     */
    public boolean update(Book book) {
        String sql = "UPDATE book SET name = ?, author = ?, publisher = ?, grade = ?, subject = ?, "
                + "book_condition = ?, price = ?, stock = ?, image = ?, description = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getGrade());
            ps.setString(5, book.getSubject());
            ps.setString(6, book.getBookCondition());
            ps.setDouble(7, book.getPrice() == null ? 0 : book.getPrice());
            ps.setInt(8, book.getStock() == null ? 1 : book.getStock());
            ps.setString(9, book.getImage());
            ps.setString(10, book.getDescription());
            ps.setInt(11, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /**
     * 删除教材
     * 若该教材已被订单引用，外键会阻止删除，这里返回 false 由页面提示
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM book WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /**
     * 修改上下架状态
     *
     * @param status 1-上架 0-下架
     */
    public boolean updateStatus(int id, int status) {
        String sql = "UPDATE book SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /**
     * 下单时扣减库存
     * SQL 中带 stock >= ? 条件，库存不足时影响行数为 0，可防止超卖
     */
    public boolean reduceStock(int bookId, int quantity) {
        String sql = "UPDATE book SET stock = stock - ? WHERE id = ? AND stock >= ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /** 取消订单时恢复库存 */
    public boolean addStock(int bookId, int quantity) {
        String sql = "UPDATE book SET stock = stock + ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /** 通用列表查询（SQL 中最多一个 ? 参数） */
    private List<Book> queryList(String sql, String param) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Book> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            if (param != null) {
                ps.setString(1, param);
            }
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

    /** 把结果集当前行封装成 Book 对象 */
    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setName(rs.getString("name"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setGrade(rs.getString("grade"));
        book.setSubject(rs.getString("subject"));
        book.setBookCondition(rs.getString("book_condition"));
        book.setPrice(rs.getDouble("price"));
        book.setStock(rs.getInt("stock"));
        book.setImage(rs.getString("image"));
        book.setDescription(rs.getString("description"));
        book.setUserId(rs.getInt("user_id"));
        book.setCreateTime(rs.getTimestamp("create_time"));
        book.setStatus(rs.getInt("status"));
        book.setSellerName(rs.getString("seller_name"));
        return book;
    }
}
