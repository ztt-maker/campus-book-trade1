package com.campus.servlet;

import com.campus.dao.OrderDao;
import com.campus.entity.Order;
import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单 Servlet
 * 查询当前用户作为买家的全部订单，转发到 myOrder.jsp
 */
@WebServlet("/myOrders")
public class MyOrderServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Order> orders = new ArrayList<>();
        try {
            User loginUser = (User) request.getSession().getAttribute("user");
            if (loginUser != null) {
                orders = orderDao.findByBuyerId(loginUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "订单加载失败，请刷新重试");
        }
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/myOrder.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
