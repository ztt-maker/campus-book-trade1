package com.campus.servlet;

import com.campus.dao.UserDao;
import com.campus.entity.User;
import com.campus.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 注册 Servlet
 * GET  /register → 打开注册页
 * POST /register → 校验后写入数据库，角色固定为 student
 *
 * 失败时会根据具体原因返回不同提示，方便排查问题
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 必须在 getParameter 之前设置编码，否则中文姓名会乱码
        request.setCharacterEncoding("UTF-8");

        // 1. 按顺序获取表单参数（name 必须与 register.jsp 中一致）
        String username = trim(request.getParameter("username"));          // 用户名
        String name = trim(request.getParameter("name"));                  // 姓名
        String phone = trim(request.getParameter("phone"));                // 手机号
        String password = request.getParameter("password");                // 密码
        String confirmPassword = request.getParameter("confirmPassword");  // 确认密码

        // 2. 服务端校验（前端 JS 可以被绕过，这里必须再校验一次）
        String error = null;

        if (username.isEmpty()) {
            error = "用户名不能为空";
        } else if (username.length() < 3 || username.length() > 20) {
            error = "用户名长度需在 3~20 位之间";
        } else if (password == null || password.isEmpty()) {
            error = "密码不能为空";
        } else if (password.length() < 6) {
            error = "密码长度不能少于 6 位";
        } else if (!password.equals(confirmPassword)) {
            // 先判断两次密码是否一致
            error = "两次输入的密码不一致，请重新输入";
        } else if (name.isEmpty()) {
            error = "请填写姓名";
        } else if (!phone.isEmpty() && !phone.matches("\\d{11}")) {
            error = "手机号格式不正确，应为 11 位数字";
        }

        if (error != null) {
            forwardError(request, response, error);
            return;
        }

        try {
            // 3. 用户名查重
            if (userDao.findByUsername(username) != null) {
                forwardError(request, response, "该用户名已被注册，请更换一个");
                return;
            }

            // 4. 组装用户并保存（密码 MD5 加密）
            User user = new User();
            user.setUsername(username);
            user.setPassword(MD5Util.md5(password));
            user.setName(name);
            user.setPhone(phone);
            user.setRole("student");

            if (userDao.add(user)) {
                request.getSession().setAttribute("msg", "注册成功，请登录");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } else {
                forwardError(request, response, "注册失败：数据库没有写入任何数据，请稍后重试");
            }
        } catch (SQLException e) {
            // 打印真实堆栈，在 IDEA 控制台可以看到具体是连接失败、表不存在还是字段不匹配
            e.printStackTrace();

            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("Duplicate entry")) {
                // 数据库唯一索引拦住了重复用户名
                forwardError(request, response, "用户名已存在，请更换一个");
            } else if (message.contains("Access denied")) {
                forwardError(request, response, "数据库账号或密码错误，请检查 db.properties");
            } else if (message.contains("Unknown database")) {
                forwardError(request, response, "数据库 campus_book_trade 不存在，请先执行建表 SQL");
            } else if (message.contains("doesn't exist") || message.contains("does not exist")) {
                forwardError(request, response, "数据表不存在，请先执行建表 SQL");
            } else {
                forwardError(request, response, "数据库异常：" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            forwardError(request, response, "系统异常：" + e.getMessage());
        }
    }

    /** 校验失败：把错误信息带回注册页 */
    private void forwardError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /** 去空格，null 转为空串 */
    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
