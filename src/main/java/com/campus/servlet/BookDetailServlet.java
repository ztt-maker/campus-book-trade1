package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.dao.CommentDao;
import com.campus.dao.OrderDao;
import com.campus.entity.Book;
import com.campus.entity.Comment;
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
 * 教材详情 Servlet
 * 未登录也可以浏览；页面下方展示该教材的全部评价
 * 若当前用户有该教材的已完成订单且还没评价过，页面会显示评价表单
 */
@WebServlet("/bookDetail")
public class BookDetailServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();
    private final CommentDao commentDao = new CommentDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Book book = bookDao.findById(id);

            if (book == null) {
                request.getSession().setAttribute("msg", "教材不存在或已被删除");
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }

            // 查询评价并计算平均分
            List<Comment> comments = new ArrayList<>();
            double avgScore = 0;
            try {
                comments = commentDao.findByBookId(id);
                if (!comments.isEmpty()) {
                    int sum = 0;
                    for (Comment c : comments) {
                        sum += c.getScore();
                    }
                    avgScore = (double) sum / comments.size();
                    avgScore = Math.round(avgScore * 10) / 10.0; // 保留一位小数
                }
            } catch (Exception e) {
                e.printStackTrace(); // 评价查询失败不影响教材展示
            }

            // 判断当前用户能否评价：已登录 + 有已完成订单 + 还没评价过
            User loginUser = (User) request.getSession().getAttribute("user");
            boolean canComment = false;
            if (loginUser != null) {
                try {
                    canComment = orderDao.hasFinishedOrder(loginUser.getId(), id)
                            && !commentDao.existsByUserAndBook(loginUser.getId(), id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            request.setAttribute("book", book);
            request.setAttribute("comments", comments);
            request.setAttribute("avgScore", avgScore);
            request.setAttribute("canComment", canComment);
            request.getRequestDispatcher("/bookDetail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("msg", "参数错误，教材不存在");
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
