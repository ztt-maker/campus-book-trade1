package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页 Servlet
 * 查询上架教材列表；带 keyword 参数时按教材名称模糊搜索
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        List<Book> books;
        try {
            if (keyword.isEmpty()) {
                books = bookDao.findAll();
            } else {
                books = bookDao.search(keyword);
            }
        } catch (Exception e) {
            // 简单处理：查询失败时给空列表，页面不会 500
            e.printStackTrace();
            books = new ArrayList<>();
            request.setAttribute("error", "教材加载失败，请刷新重试");
        }

        request.setAttribute("books", books);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
