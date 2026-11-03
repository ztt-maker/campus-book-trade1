<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>注册 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<div class="auth-wrap" style="max-width: 470px;">
    <div class="card auth-card">
        <div class="card-body p-4 p-md-5">
            <h5 class="auth-title text-center mb-1">学生注册</h5>
            <p class="text-center text-muted small mb-4">注册后即可发布和购买二手教材</p>

            <%-- 服务端返回的错误提示 --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2 small">${error}</div>
            </c:if>

            <%-- 前端 JS 校验的错误提示（默认隐藏） --%>
            <div id="jsError" class="alert alert-danger py-2 small d-none"></div>

            <%-- novalidate：关闭浏览器自带的“请填写此字段”气泡，改用下面的 JS 统一提示 --%>
            <form action="${pageContext.request.contextPath}/register" method="post"
                  novalidate onsubmit="return checkForm()">

                <div class="mb-3">
                    <label class="form-label">用户名 <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="username" id="username"
                           value="${param.username}" required minlength="3" maxlength="20"
                           placeholder="3~20 位，注册后用于登录">
                </div>

                <div class="mb-3">
                    <label class="form-label">姓名 <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" name="name" id="name"
                           value="${param.name}" required maxlength="20" placeholder="请输入真实姓名">
                </div>

                <div class="mb-3">
                    <label class="form-label">手机号</label>
                    <input type="text" class="form-control" name="phone" id="phone"
                           value="${param.phone}" maxlength="11" placeholder="选填，11 位数字">
                </div>

                <div class="mb-3">
                    <label class="form-label">密码 <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" name="password" id="password"
                           required minlength="6" placeholder="不少于 6 位">
                </div>

                <div class="mb-4">
                    <%-- name 必须与 RegisterServlet 中的 confirmPassword 一致 --%>
                    <label class="form-label">确认密码 <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" name="confirmPassword" id="confirmPassword"
                           required minlength="6" placeholder="请再输入一次密码">
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2">注册</button>
            </form>

            <div class="text-center mt-3 small">
                已有账号？<a href="${pageContext.request.contextPath}/login.jsp">去登录</a>
            </div>
        </div>
    </div>
</div>

<script>
    // 显示错误提示（页面内的红色提示条，代替浏览器默认气泡）
    function showError(msg) {
        var box = document.getElementById("jsError");
        box.textContent = msg;
        box.classList.remove("d-none");
        box.scrollIntoView({behavior: "smooth", block: "center"});
    }

    // 隐藏错误提示
    function hideError() {
        var box = document.getElementById("jsError");
        box.textContent = "";
        box.classList.add("d-none");
    }

    // 提交前的完整校验
    function checkForm() {
        hideError();

        var username = document.getElementById("username").value.trim();
        var name = document.getElementById("name").value.trim();
        var phone = document.getElementById("phone").value.trim();
        var password = document.getElementById("password").value;
        var confirmPassword = document.getElementById("confirmPassword").value;

        if (username === "") {
            showError("请输入用户名");
            return false;
        }
        if (username.length < 3 || username.length > 20) {
            showError("用户名长度需在 3~20 位之间");
            return false;
        }
        if (name === "") {
            showError("请输入姓名");
            return false;
        }
        if (phone !== "" && !/^\d{11}$/.test(phone)) {
            showError("手机号格式不正确，应为 11 位数字");
            return false;
        }
        if (password === "") {
            showError("请输入密码");
            return false;
        }
        if (password.length < 6) {
            showError("密码长度不能少于 6 位");
            return false;
        }
        if (confirmPassword === "") {
            showError("请再次输入密码");
            return false;
        }
        if (password !== confirmPassword) {
            showError("两次输入的密码不一致，请重新输入");
            return false;
        }
        return true;
    }
</script>
<script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
