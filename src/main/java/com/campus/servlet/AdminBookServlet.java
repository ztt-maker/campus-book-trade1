package com.campus.servlet;

import com.campus.dao.BookDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员 - 教材管理
 * GET /admin/book                          查看全部教材（含已下架）
 * GET /admin/book?action=offline&id=1      下架教材
 * GET /admin/book?action=online&id=1       重新上架
 */
@WebServlet("/admin/book")
public class AdminBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // 1. 下架 / 上架操作
        if ("offline".equals(action) || "online".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                int status = "offline".equals(action) ? 0 : 1;
                boolean ok = bookDao.updateStatus(id, status);
                request.getSession().setAttribute("msg",
                        ok ? ("offline".equals(action) ? "教材已下架" : "教材已重新上架")
                           : "操作失败，教材不存在");
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("msg", "操作失败，参数错误");
            }
            response.sendRedirect(request.getContextPath() + "/admin/book");
            return;
        }

        // 2. 查看全部教材
        try {
            request.setAttribute("books", bookDao.findAllForAdmin());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "教材加载失败");
        }
        request.setAttribute("activeTab", "book");
        request.getRequestDispatcher("/adminIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
