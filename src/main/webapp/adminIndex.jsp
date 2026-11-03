<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.campus.entity.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 本页由 AdminBookServlet / AdminOrderServlet / AdminUserServlet 转发进入
    User loginUser = (User) session.getAttribute("user");
    if (loginUser == null || !"admin".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
    if (request.getAttribute("activeTab") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/book");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员后台 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="section-title mb-0">管理员后台</h5>
        <span class="small text-muted">当前管理员：${sessionScope.user.username}</span>
    </div>

    <c:if test="${not empty sessionScope.msg}">
        <div class="alert alert-info py-2 small">${sessionScope.msg}</div>
        <c:remove var="msg" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger py-2 small">${error}</div>
    </c:if>

    <%-- 三个管理模块的切换入口 --%>
    <ul class="nav nav-pills mb-3">
        <li class="nav-item">
            <a class="nav-link ${activeTab == 'book' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/book">教材管理</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeTab == 'order' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/order">订单管理</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeTab == 'user' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/user">用户管理</a>
        </li>
    </ul>

    <%-- ================= 教材管理 ================= --%>
    <c:if test="${activeTab == 'book'}">
        <div class="card border-0 shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead>
                    <tr>
                        <th style="width: 60px;">ID</th>
                        <th style="width: 70px;">封面</th>
                        <th>教材信息</th>
                        <th style="width: 100px;">卖家</th>
                        <th style="width: 120px;">价格 / 库存</th>
                        <th style="width: 90px;">状态</th>
                        <th style="width: 140px;">发布时间</th>
                        <th style="width: 160px;" class="text-end">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${books}" var="b">
                        <tr>
                            <td class="text-muted">${b.id}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty b.coverUrl}">
                                        <img src="${pageContext.request.contextPath}/${b.coverUrl}"
                                             class="rounded border"
                                             style="width: 48px; height: 48px; object-fit: cover;" alt="封面">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="bg-light rounded border d-flex align-items-center justify-content-center text-muted"
                                             style="width: 48px; height: 48px; font-size: 12px;">无图</div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="fw-semibold">${b.name}</div>
                                <div class="small text-muted">
                                    ${b.author} · ${b.publisher} · ${b.grade} · ${b.subject}
                                </div>
                            </td>
                            <td class="small">${b.sellerName}</td>
                            <td>
                                <span class="price-text">￥<fmt:formatNumber value="${b.price}" pattern="0.00"/></span>
                                <div class="small text-muted">库存 ${b.stock}</div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status == 1}">
                                        <span class="badge bg-success">在售</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">已下架</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small text-muted">
                                <fmt:formatDate value="${b.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                            </td>
                            <td class="text-end">
                                <a href="${pageContext.request.contextPath}/bookDetail?id=${b.id}"
                                   class="btn btn-sm btn-outline-primary">详情</a>
                                <c:choose>
                                    <c:when test="${b.status == 1}">
                                        <a href="${pageContext.request.contextPath}/admin/book?action=offline&id=${b.id}"
                                           class="btn btn-sm btn-outline-danger"
                                           onclick="return confirm('确定下架《${b.name}》吗？下架后学生无法下单。');">下架</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/admin/book?action=online&id=${b.id}"
                                           class="btn btn-sm btn-outline-success">上架</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <c:if test="${empty books}">
                <div class="empty-box">暂无教材</div>
            </c:if>
        </div>
    </c:if>

    <%-- ================= 订单管理 ================= --%>
    <c:if test="${activeTab == 'order'}">
        <div class="card border-0 shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead>
                    <tr>
                        <th style="width: 70px;">订单号</th>
                        <th>教材</th>
                        <th style="width: 100px;">买家</th>
                        <th style="width: 130px;">单价 × 数量</th>
                        <th style="width: 100px;">总金额</th>
                        <th style="width: 90px;">状态</th>
                        <th style="width: 140px;">下单时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orders}" var="o">
                        <tr>
                            <td class="text-muted">#${o.id}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/bookDetail?id=${o.bookId}"
                                   class="text-decoration-none">${o.bookName}</a>
                            </td>
                            <td class="small">${o.buyerName}</td>
                            <td class="small text-muted">
                                ￥<fmt:formatNumber value="${o.price}" pattern="0.00"/> × ${o.quantity}
                            </td>
                            <td class="price-text">
                                ￥<fmt:formatNumber value="${o.totalPrice}" pattern="0.00"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 0}">
                                        <span class="badge bg-warning text-dark">待确认</span>
                                    </c:when>
                                    <c:when test="${o.status == 1}">
                                        <span class="badge bg-success">已完成</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">已取消</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small text-muted">
                                <fmt:formatDate value="${o.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <c:if test="${empty orders}">
                <div class="empty-box">暂无订单</div>
            </c:if>
        </div>
    </c:if>

    <%-- ================= 用户管理 ================= --%>
    <c:if test="${activeTab == 'user'}">
        <div class="card border-0 shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead>
                    <tr>
                        <th style="width: 60px;">ID</th>
                        <th>用户名</th>
                        <th>姓名</th>
                        <th style="width: 100px;">角色</th>
                        <th style="width: 140px;">手机号</th>
                        <th style="width: 160px;">注册时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${users}" var="u">
                        <tr>
                            <td class="text-muted">${u.id}</td>
                            <td>${u.username}</td>
                            <td>${empty u.name ? '未填写' : u.name}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.role eq 'admin'}">
                                        <span class="badge bg-danger">管理员</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-primary">学生</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small">${empty u.phone ? '未填写' : u.phone}</td>
                            <td class="small text-muted">
                                <fmt:formatDate value="${u.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <c:if test="${empty users}">
                <div class="empty-box">暂无用户</div>
            </c:if>
        </div>
    </c:if>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
