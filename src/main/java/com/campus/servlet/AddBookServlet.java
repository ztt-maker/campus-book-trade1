package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.entity.Book;
import com.campus.entity.User;
import com.campus.util.UploadUtil;
import org.apache.commons.fileupload.FileUploadBase;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 发布教材 Servlet
 * GET  /addBook → 打开发布页面
 * POST /addBook → 接收表单 + 图片，发布人自动取 session 中的登录用户
 *
 * 图片由 UploadUtil 保存到项目 images 目录，数据库只存相对路径
 */
@WebServlet("/addBook")
public class AddBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 解析 multipart 表单（含图片上传），失败时给出具体原因
        Map<String, String> data;
        try {
            data = UploadUtil.parse(request);
        } catch (FileUploadBase.FileSizeLimitExceededException | FileUploadBase.SizeLimitExceededException e) {
            request.setAttribute("error", "图片大小不能超过 5MB，请压缩后重新上传");
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
            return;
        } catch (IllegalArgumentException e) {
            // 非图片格式
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "图片上传失败：" + e.getMessage());
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
            return;
        }

        // 2. 组装教材对象
        Book book = new Book();
        book.setName(trim(data.get("name")));
        book.setAuthor(trim(data.get("author")));
        book.setPublisher(trim(data.get("publisher")));
        book.setGrade(trim(data.get("grade")));
        book.setSubject(trim(data.get("subject")));
        book.setBookCondition(trim(data.get("bookCondition")));
        book.setDescription(trim(data.get("description")));
        book.setImage(data.get("image")); // 没传图片时为 null，页面会显示“暂无封面”

        String error = null;
        double price = 0;
        int stock = 1;
        try {
            price = Double.parseDouble(trim(data.get("price")));
            stock = Integer.parseInt(trim(data.get("stock")));
        } catch (NumberFormatException e) {
            error = "价格和库存必须填写数字";
        }
        book.setPrice(price);
        book.setStock(stock);

        if (error == null && book.getName().isEmpty()) {
            error = "教材名称不能为空";
        } else if (error == null && price <= 0) {
            error = "价格必须大于 0";
        } else if (error == null && stock < 1) {
            error = "库存至少为 1";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("book", book); // 回显已填内容
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
            return;
        }

        // 3. 发布人取当前登录用户（权限校验：未登录会被 LoginFilter 拦截）
        User loginUser = (User) request.getSession().getAttribute("user");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        book.setUserId(loginUser.getId());

        // 4. 入库
        try {
            if (bookDao.add(book)) {
                request.getSession().setAttribute("msg", "教材发布成功");
                response.sendRedirect(request.getContextPath() + "/myBooks");
            } else {
                request.setAttribute("error", "发布失败，请稍后重试");
                request.setAttribute("book", book);
                request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "发布失败：" + e.getMessage());
            request.setAttribute("book", book);
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
