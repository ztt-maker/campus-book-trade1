package com.campus.servlet;

import com.campus.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员 - 用户管理
 * GET /admin/user → 查看全部用户
 */
@WebServlet("/admin/user")
public class AdminUserServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("users", userDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "用户加载失败");
        }
        request.setAttribute("activeTab", "user");
        request.getRequestDispatcher("/adminIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
