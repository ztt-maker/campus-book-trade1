package com.campus.servlet;

import com.campus.dao.BookDao;
import com.campus.entity.Book;
import com.campus.entity.User;
import com.campus.util.UploadUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 编辑教材 Servlet
 * GET  /editBook?id=1 → 查询教材并打开编辑页（只能编辑自己发布的）
 * POST /editBook      → 保存修改；未重新选择图片时保留原图
 */
@WebServlet("/editBook")
public class EditBookServlet extends HttpServlet {

    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            User loginUser = (User) request.getSession().getAttribute("user");
            Book book = bookDao.findById(id);

            if (book == null || loginUser == null || !book.getUserId().equals(loginUser.getId())) {
                request.getSession().setAttribute("msg", "教材不存在或无权编辑");
                response.sendRedirect(request.getContextPath() + "/myBooks");
                return;
            }
            request.setAttribute("book", book);
            request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("msg", "参数错误，无法编辑");
            response.sendRedirect(request.getContextPath() + "/myBooks");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Map<String, String> data = UploadUtil.parse(request);
            int id = Integer.parseInt(trim(data.get("id")));

            User loginUser = (User) request.getSession().getAttribute("user");
            Book old = bookDao.findById(id);
            if (old == null || loginUser == null || !old.getUserId().equals(loginUser.getId())) {
                request.getSession().setAttribute("msg", "教材不存在或无权编辑");
                response.sendRedirect(request.getContextPath() + "/myBooks");
                return;
            }

            Book book = new Book();
            book.setId(id);
            book.setName(trim(data.get("name")));
            book.setAuthor(trim(data.get("author")));
            book.setPublisher(trim(data.get("publisher")));
            book.setGrade(trim(data.get("grade")));
            book.setSubject(trim(data.get("subject")));
            book.setBookCondition(trim(data.get("bookCondition")));
            book.setDescription(trim(data.get("description")));
            book.setUserId(old.getUserId());

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

            // 没有上传新图片时使用原图
            String image = data.get("image");
            book.setImage(image == null || image.trim().isEmpty() ? old.getImage() : image);

            if (error == null && book.getName().isEmpty()) {
                error = "教材名称不能为空";
            } else if (error == null && price <= 0) {
                error = "价格必须大于 0";
            } else if (error == null && stock < 1) {
                error = "库存至少为 1";
            }

            if (error != null) {
                request.setAttribute("error", error);
                request.setAttribute("book", book);
                request.getRequestDispatcher("/publishBook.jsp").forward(request, response);
                return;
            }

            if (bookDao.update(book)) {
                request.getSession().setAttribute("msg", "修改成功");
            } else {
                request.getSession().setAttribute("msg", "修改失败，请稍后重试");
            }
            response.sendRedirect(request.getContextPath() + "/myBooks");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("msg", "修改失败：图片格式不支持或文件超过 5MB");
            response.sendRedirect(request.getContextPath() + "/myBooks");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
