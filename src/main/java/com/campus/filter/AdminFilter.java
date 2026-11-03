package com.campus.filter;

import com.campus.entity.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员权限过滤器
 * 拦截 /admin/* 下的所有请求：
 * 1. 未登录 → 跳登录页
 * 2. 登录了但不是 admin → 回首页并提示
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("msg", "请先登录后再访问管理后台");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!"admin".equals(user.getRole())) {
            request.getSession().setAttribute("msg", "只有管理员才能访问后台");
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
