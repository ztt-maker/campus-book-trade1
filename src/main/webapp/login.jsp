<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<div class="auth-wrap">
    <div class="card auth-card">
        <div class="card-body p-4 p-md-5">
            <h5 class="auth-title text-center mb-1">欢迎回来</h5>
            <p class="text-center text-muted small mb-4">登录校园二手教材交易系统</p>

            <%-- 提示信息：拦截器跳转提示、注册成功提示 --%>
            <c:if test="${not empty sessionScope.msg}">
                <div class="alert alert-warning py-2 small">${sessionScope.msg}</div>
                <c:remove var="msg" scope="session"/>
            </c:if>
            <%-- 错误提示 --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2 small">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label class="form-label">用户名</label>
                    <input type="text" class="form-control" name="username"
                           value="${param.username}" placeholder="请输入用户名" required autofocus>
                </div>
                <div class="mb-4">
                    <label class="form-label">密码</label>
                    <input type="password" class="form-control" name="password"
                           placeholder="请输入密码" required>
                </div>
                <button type="submit" class="btn btn-primary w-100 py-2">登录</button>
            </form>

            <div class="text-center mt-3 small">
                还没有账号？<a href="${pageContext.request.contextPath}/register.jsp">立即注册</a>
            </div>
        </div>
    </div>

    <div class="text-center mt-3 small text-muted">
        测试账号：admin / student1，密码都是 123456
    </div>
</div>

<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
