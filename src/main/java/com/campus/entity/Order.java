package com.campus.entity;

import java.util.Date;

/**
 * 订单实体类
 * 对应数据库表：orders
 */
public class Order {

    private Integer id;             // 订单ID
    private Integer bookId;         // 教材ID
    private Integer buyerUserId;    // 买家ID
    private Double price;           // 下单时的教材单价
    private Integer quantity;       // 购买数量
    private Double totalPrice;      // 订单总金额
    private Integer status;         // 状态：0待确认 1已完成 2已取消
    private Date createTime;        // 下单时间

    /** 非数据库字段：列表页联表查询时带出的教材名称 */
    private String bookName;
    /** 非数据库字段：列表页联表查询时带出的买家姓名 */
    private String buyerName;

    public Order() {
    }

    /** 有参构造（只包含数据库字段） */
    public Order(Integer id, Integer bookId, Integer buyerUserId, Double price,
                 Integer quantity, Double totalPrice, Integer status, Date createTime) {
        this.id = id;
        this.bookId = bookId;
        this.buyerUserId = buyerUserId;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(Integer buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
}
