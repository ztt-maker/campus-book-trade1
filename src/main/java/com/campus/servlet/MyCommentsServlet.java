package com.campus.servlet;

import com.campus.dao.CommentDao;
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
 * 我的评价 Servlet
 * GET /myComments → 从 session 取登录用户，查询该用户发布的全部评价
 *
 * 注意：@WebServlet 的路径与导航栏 href、JSP 文件名的大小写必须严格一致
 */
@WebServlet("/myComments")
public class MyCommentsServlet extends HttpServlet {

    private final CommentDao commentDao = new CommentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 登录校验（LoginFilter 已经拦过一层，这里再兜底一次）
        User loginUser = (User) request.getSession().getAttribute("user");
        if (loginUser == null) {
            request.getSession().setAttribute("msg", "请先登录后再查看我的评价");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. 查询当前用户的评价
        List<Comment> comments = new ArrayList<>();
        try {
            comments = commentDao.findByUserId(loginUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "评价加载失败，请刷新重试");
        }

        // 3. 存入 request 域并转发页面
        request.setAttribute("comments", comments);
        request.getRequestDispatcher("/myComments.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
