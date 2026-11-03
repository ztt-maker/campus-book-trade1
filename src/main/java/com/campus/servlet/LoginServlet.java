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

/**
 * 登录 Servlet
 * GET  /login  → 打开登录页
 * POST /login  → 校验账号密码，成功则把 User 存入 session
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 1. 基本校验
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            request.setAttribute("error", "用户名和密码不能为空");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            // 2. 密码加密后到数据库比对
            User user = userDao.login(username.trim(), MD5Util.md5(password));

            if (user != null) {
                // 3. 登录成功：用户对象存入 session
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/index");
            } else {
                request.setAttribute("error", "用户名或密码错误");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "登录失败，请稍后重试");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
