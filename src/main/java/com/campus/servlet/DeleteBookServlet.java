package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.entity.Book;
import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 删除教材 Servlet
 * 只能删除自己发布的教材；若该教材已被订单引用，外键会阻止删除，页面给出提示
 */
@WebServlet("/deleteBook")
public class DeleteBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            User loginUser = (User) request.getSession().getAttribute("user");
            Book book = bookDao.findById(id);

            if (book == null || loginUser == null || !book.getUserId().equals(loginUser.getId())) {
                request.getSession().setAttribute("msg", "教材不存在或无权删除");
            } else if (bookDao.delete(id)) {
                request.getSession().setAttribute("msg", "删除成功");
            } else {
                request.getSession().setAttribute("msg", "删除失败：该教材已有订单记录，不能直接删除");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("msg", "删除失败，参数错误");
        }
        response.sendRedirect(request.getContextPath() + "/myBooks");
    }
}
