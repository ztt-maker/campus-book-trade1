package com.campus.servlet;

import com.campus.dao.OrderDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员 - 订单管理
 * GET /admin/order → 查看系统内全部订单
 */
@WebServlet("/admin/order")
public class AdminOrderServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("orders", orderDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "订单加载失败");
        }
        request.setAttribute("activeTab", "order");
        request.getRequestDispatcher("/adminIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
