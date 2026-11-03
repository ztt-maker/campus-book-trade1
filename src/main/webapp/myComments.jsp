<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 直接访问 myComments.jsp 时没有数据，先交给 MyCommentsServlet 查询
    if (request.getAttribute("comments") == null) {
        request.getRequestDispatcher("/myComments").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的评价 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <h5 class="section-title mb-3">我的评价</h5>

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
                    <th style="width: 260px;">教材名称</th>
                    <th style="width: 130px;">评分</th>
                    <th>评价内容</th>
                    <th style="width: 160px;">评价时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${comments}" var="c">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/bookDetail?id=${c.bookId}"
                               class="text-decoration-none">
                                ${empty c.bookName ? '教材已删除' : c.bookName}
                            </a>
                        </td>
                        <td>
                            <span class="stars">
                                <c:forEach begin="1" end="5" var="i">${i le c.score ? '★' : '☆'}</c:forEach>
                            </span>
                            <span class="text-muted small">(${c.score}分)</span>
                        </td>
                        <td class="small">${c.content}</td>
                        <td class="small text-muted">
                            <fmt:formatDate value="${c.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${empty comments}">
            <div class="empty-box">
                你还没有发表过评价，完成交易后可以在“我的订单”中评价教材。
            </div>
        </c:if>
    </div>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
