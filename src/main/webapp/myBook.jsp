<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的发布 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="section-title mb-0">我的发布</h5>
        <a href="${pageContext.request.contextPath}/publishBook.jsp" class="btn btn-primary btn-sm">
            + 发布新教材
        </a>
    </div>

    <%-- 操作结果提示（只显示一次） --%>
    <c:if test="${not empty sessionScope.msg}">
        <div class="alert alert-info py-2 small">${sessionScope.msg}</div>
        <c:remove var="msg" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger py-2 small">${error}</div>
    </c:if>

    <div class="card border-0 shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th style="width: 80px;">封面</th>
                    <th>教材信息</th>
                    <th style="width: 120px;">价格 / 库存</th>
                    <th style="width: 90px;">状态</th>
                    <th style="width: 150px;">发布时间</th>
                    <th style="width: 150px;" class="text-end">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${books}" var="book">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${not empty book.coverUrl}">
                                    <img src="${pageContext.request.contextPath}/${book.coverUrl}"
                                         class="rounded border" style="width: 56px; height: 56px; object-fit: cover;"
                                         alt="封面">
                                </c:when>
                                <c:otherwise>
                                    <div class="bg-light rounded border d-flex align-items-center justify-content-center text-muted"
                                         style="width: 56px; height: 56px; font-size: 12px;">无图</div>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="fw-semibold">${book.name}</div>
                            <div class="small text-muted">
                                ${book.author} · ${book.publisher} · ${book.grade} · ${book.subject}
                            </div>
                        </td>
                        <td>
                            <span class="price-text">￥<fmt:formatNumber value="${book.price}" pattern="0.00"/></span>
                            <div class="small text-muted">库存 ${book.stock}</div>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${book.status == 1}">
                                    <span class="badge bg-success">在售</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">已下架</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="small text-muted">
                            <fmt:formatDate value="${book.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/editBook?id=${book.id}"
                               class="btn btn-sm btn-outline-primary">编辑</a>
                            <a href="${pageContext.request.contextPath}/deleteBook?id=${book.id}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('确定删除《${book.name}》吗？删除后不可恢复。');">删除</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${empty books}">
            <div class="empty-box">
                你还没有发布过教材，<a href="${pageContext.request.contextPath}/publishBook.jsp">去发布一本</a>
            </div>
        </c:if>
    </div>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
