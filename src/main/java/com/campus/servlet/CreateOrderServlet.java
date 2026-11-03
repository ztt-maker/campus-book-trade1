package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.dao.OrderDao;
import com.campus.entity.Book;
import com.campus.entity.Order;
import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 下单 Servlet
 * POST /createOrder → 校验教材、库存、身份后生成订单，初始状态为 0-待确认
 * 未登录用户会被 LoginFilter 拦截
 */
@WebServlet("/createOrder")
public class CreateOrderServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User loginUser = (User) request.getSession().getAttribute("user");
        int bookId;
        int quantity = 1;

        try {
            bookId = Integer.parseInt(request.getParameter("bookId"));
            String q = request.getParameter("quantity");
            if (q != null && !q.trim().isEmpty()) {
                quantity = Integer.parseInt(q.trim());
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("msg", "参数错误，请重新下单");
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        Book book = bookDao.findById(bookId);

        // 1. 各种校验，失败统一回到详情页提示
        String error = null;
        if (book == null) {
            error = "教材不存在";
        } else if (book.getStatus() == null || book.getStatus() != 1) {
            error = "该教材已下架，无法下单";
        } else if (book.getUserId().equals(loginUser.getId())) {
            error = "不能购买自己发布的教材";
        } else if (quantity < 1) {
            error = "购买数量至少为 1";
        } else if (book.getStock() < quantity) {
            error = "库存不足，当前仅剩 " + book.getStock() + " 本";
        }

        if (error != null) {
            request.getSession().setAttribute("msg", error);
            redirectDetail(response, request, bookId);
            return;
        }

        try {
            // 2. 扣减库存（带条件更新，防止超卖）
            if (!bookDao.reduceStock(bookId, quantity)) {
                request.getSession().setAttribute("msg", "手慢了，库存不足");
                redirectDetail(response, request, bookId);
                return;
            }

            // 3. 生成订单，状态 0-待确认
            Order order = new Order();
            order.setBookId(bookId);
            order.setBuyerUserId(loginUser.getId());
            order.setPrice(book.getPrice());
            order.setQuantity(quantity);

            if (orderDao.add(order)) {
                request.getSession().setAttribute("msg", "下单成功，请在“我的订单”中查看");
                response.sendRedirect(request.getContextPath() + "/myOrders");
            } else {
                bookDao.addStock(bookId, quantity); // 下单失败，恢复库存
                request.getSession().setAttribute("msg", "下单失败，请稍后重试");
                redirectDetail(response, request, bookId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bookDao.addStock(bookId, quantity);
            request.getSession().setAttribute("msg", "下单失败，请稍后重试");
            redirectDetail(response, request, bookId);
        }
    }

    /** 回到教材详情页 */
    private void redirectDetail(HttpServletResponse response, HttpServletRequest request, int bookId)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/bookDetail?id=" + bookId);
    }
}
