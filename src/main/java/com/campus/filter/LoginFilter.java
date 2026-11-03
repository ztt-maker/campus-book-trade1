package com.campus.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录拦截器
 * 未登录用户只能访问：首页、教材浏览、登录、注册和静态资源，
 * 其他请求（发布、下单、评价、我的、后台等）一律跳转到登录页。
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

    /** 无需登录即可访问的路径（精确匹配） */
    private static final String[] PUBLIC_PATHS = {
            "/index", "/index.jsp",
            "/login", "/login.jsp",
            "/register", "/register.jsp",
            "/logout",
            "/book",          // 教材列表、搜索允许匿名浏览
            "/bookDetail"     // 教材详情页允许匿名浏览
    };

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 去掉项目名，得到 /login.jsp 这样的路径
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublic(path) || request.getSession().getAttribute("user") != null) {
            chain.doFilter(req, resp); // 放行
        } else {
            // 未登录：记住提示信息，跳转登录页
            request.getSession().setAttribute("msg", "请先登录后再操作");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    /** 判断是否为公开路径 */
    private boolean isPublic(String path) {
        if (path.isEmpty() || "/".equals(path)) {
            return true; // 项目根路径
        }
        // 静态资源放行（样式、脚本、图片、上传的封面图）
        if (path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/") || path.startsWith("/upload/")
                || path.equals("/favicon.ico")) {
            return true;
        }
        for (String publicPath : PUBLIC_PATHS) {
            if (publicPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
