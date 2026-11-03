package com.campus.servlet;

import com.campus.dao.CommentDao;
import com.campus.dao.OrderDao;
import com.campus.entity.Comment;
import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 发布评价 Servlet
 * 规则：
 * 1. 必须登录（LoginFilter 已拦截）
 * 2. 必须在该教材上有“已完成”的订单
 * 3. 同一本教材不能重复评价
 * 评分用 1~5 的数字保存
 */
@WebServlet("/addComment")
public class AddCommentServlet extends HttpServlet {

    private final CommentDao commentDao = new CommentDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User loginUser = (User) request.getSession().getAttribute("user");
        int bookId = 0;
        String msg;

        try {
            bookId = Integer.parseInt(request.getParameter("bookId"));
            int score = Integer.parseInt(request.getParameter("score"));
            String content = request.getParameter("content");
            content = content == null ? "" : content.trim();

            if (score < 1 || score > 5) {
                msg = "评分必须在 1~5 星之间";
            } else if (content.isEmpty()) {
                msg = "评价内容不能为空";
            } else if (content.length() > 500) {
                msg = "评价内容不能超过 500 字";
            } else if (!orderDao.hasFinishedOrder(loginUser.getId(), bookId)) {
                msg = "只有完成交易的教材才能评价";
            } else if (commentDao.existsByUserAndBook(loginUser.getId(), bookId)) {
                msg = "你已经评价过这本教材了";
            } else {
                Comment comment = new Comment();
                comment.setBookId(bookId);
                comment.setUserId(loginUser.getId());
                comment.setScore(score);
                comment.setContent(content);

                msg = commentDao.add(comment) ? "评价成功，感谢你的反馈" : "评价失败，请稍后重试";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "评价失败，参数错误";
        }

        request.getSession().setAttribute("msg", msg);
        if (bookId > 0) {
            response.sendRedirect(request.getContextPath() + "/bookDetail?id=" + bookId);
        } else {
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index");
    }
}
