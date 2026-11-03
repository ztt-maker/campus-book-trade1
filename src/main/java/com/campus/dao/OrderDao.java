package com.campus.dao;

import com.campus.entity.Order;
import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问类
 * 全部使用原生 JDBC + DBUtil
 */
public class OrderDao {

    /**
     * 新增订单（状态默认为 0-待确认）
     * total_price = price * quantity
     *
     * @return 成功返回 true
     */
    public boolean add(Order order) {
        String sql = "INSERT INTO orders (book_id, buyer_user_id, price, quantity, total_price, status) "
                + "VALUES (?, ?, ?, ?, ?, 0)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            int quantity = order.getQuantity() == null ? 1 : order.getQuantity();
            double price = order.getPrice() == null ? 0 : order.getPrice();
            double total = price * quantity;

            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getBookId());
            ps.setInt(2, order.getBuyerUserId());
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setDouble(5, total);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /** 查询某个买家的订单（我的订单页，带出教材名和买家名） */
    public List<Order> findByBuyerId(int buyerUserId) {
        String sql = baseSql() + " WHERE o.buyer_user_id = ? ORDER BY o.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, buyerUserId);
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

    /** 管理员查询全部订单 */
    public List<Order> findAll() {
        String sql = baseSql() + " ORDER BY o.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> list = new ArrayList<>();
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

    /** 根据主键查询订单（取消/完成订单前校验归属使用） */
    public Order findById(int orderId) {
        String sql = baseSql() + " WHERE o.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
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
     * 判断某个买家是否有该教材的已完成订单
     * 评价模块用来控制“完成交易后才能评价”
     */
    public boolean hasFinishedOrder(int buyerUserId, int bookId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE buyer_user_id = ? AND book_id = ? AND status = 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, buyerUserId);
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

    /**
     * 修改订单状态
     *
     * @param status 0-待确认 1-已完成 2-已取消
     */
    public boolean updateStatus(int orderId, int status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps);
        }
        return false;
    }

    /** 联表查询的公共部分：订单 + 教材名 + 买家名 */
    private String baseSql() {
        return "SELECT o.*, b.name AS book_name, u.name AS buyer_name FROM orders o "
                + "JOIN book b ON o.book_id = b.id "
                + "JOIN `user` u ON o.buyer_user_id = u.id";
    }

    /** 把结果集当前行封装成 Order 对象 */
    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setBookId(rs.getInt("book_id"));
        order.setBuyerUserId(rs.getInt("buyer_user_id"));
        order.setPrice(rs.getDouble("price"));
        order.setQuantity(rs.getInt("quantity"));
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setStatus(rs.getInt("status"));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setBookName(rs.getString("book_name"));
        order.setBuyerName(rs.getString("buyer_name"));
        return order;
    }
}
