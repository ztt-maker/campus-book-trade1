package com.campus.entity;

import java.util.Date;

/**
 * 评价实体类
 * 对应数据库表：comment
 */
public class Comment {

    private Integer id;         // 评价ID
    private Integer bookId;     // 教材ID
    private Integer userId;     // 评价人ID
    private Integer score;      // 评分：1~5
    private String content;     // 评价内容
    private Date createTime;    // 评价时间

    /** 非数据库字段：详情页展示评价人姓名 */
    private String userName;
    /** 非数据库字段：我的评价列表展示教材名称 */
    private String bookName;

    public Comment() {
    }

    /** 有参构造（只包含数据库字段） */
    public Comment(Integer id, Integer bookId, Integer userId,
                   Integer score, String content, Date createTime) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.score = score;
        this.content = content;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
