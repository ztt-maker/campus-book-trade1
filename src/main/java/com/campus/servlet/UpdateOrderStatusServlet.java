package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.dao.OrderDao;
import com.campus.entity.Order;
import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 修改订单状态 Servlet
 * 仅买家本人、且订单处于 0-待确认 时才能操作：
 *   status=1 确认完成
 *   status=2 取消订单（同时恢复库存）
 */
@WebServlet("/updateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();
    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute("user");
        String msg;

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int status = Integer.parseInt(request.getParameter("status"));
            Order order = orderDao.findById(orderId);

            if (order == null) {
                msg = "订单不存在";
            } else if (!order.getBuyerUserId().equals(loginUser.getId())) {
                msg = "无权操作该订单";
            } else if (order.getStatus() != 0) {
                msg = "该订单已处理，不能重复操作";
            } else if (status == 1) {
                // 确认完成
                msg = orderDao.updateStatus(orderId, 1) ? "交易已完成，现在可以评价教材啦" : "操作失败，请稍后重试";
            } else if (status == 2) {
                // 取消订单：先改状态，再恢复库存
                if (orderDao.updateStatus(orderId, 2)) {
                    bookDao.addStock(order.getBookId(), order.getQuantity());
                    msg = "订单已取消，库存已恢复";
                } else {
                    msg = "操作失败，请稍后重试";
                }
            } else {
                msg = "不支持的订单状态";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "操作失败，参数错误";
        }

        request.getSession().setAttribute("msg", msg);
        response.sendRedirect(request.getContextPath() + "/myOrders");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
