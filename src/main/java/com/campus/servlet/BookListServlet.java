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
import java.util.ArrayList;
import java.util.List;

/**
 * 我的发布 Servlet
 * 查询当前登录用户发布的所有教材（包含已下架），转发到 myBook.jsp
 */
@WebServlet("/myBooks")
public class BookListServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Book> books = new ArrayList<>();
        try {
            User loginUser = (User) request.getSession().getAttribute("user");
            if (loginUser != null) {
                books = bookDao.findByUserId(loginUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "教材加载失败，请刷新重试");
        }
        request.setAttribute("books", books);
        request.getRequestDispatcher("/myBook.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
