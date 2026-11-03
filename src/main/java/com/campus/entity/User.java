package com.campus.entity;

import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表：user
 */
public class User {

    private Integer id;         // 用户ID
    private String username;    // 用户名（登录账号）
    private String password;    // 密码（MD5加密后的32位字符串）
    private String name;        // 姓名
    private String role;        // 角色：student-学生  admin-管理员
    private String phone;       // 手机号
    private Date createTime;    // 注册时间

    /** 无参构造（JavaBean 规范，必须保留） */
    public User() {
    }

    /** 有参构造（只包含数据库字段） */
    public User(Integer id, String username, String password, String name,
                String role, String phone, Date createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
