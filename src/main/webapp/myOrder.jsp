<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 直接访问本页时，先交给 MyOrderServlet 查询数据
    if (request.getAttribute("orders") == null) {
        request.getRequestDispatcher("/myOrders").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的订单 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <h5 class="section-title mb-3">我的订单</h5>

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
                    <th style="width: 70px;">订单号</th>
                    <th>教材</th>
                    <th style="width: 130px;">单价 × 数量</th>
                    <th style="width: 100px;">总金额</th>
                    <th style="width: 150px;">下单时间</th>
                    <th style="width: 90px;">状态</th>
                    <th style="width: 200px;" class="text-end">操作</th>
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
                        <td class="small text-muted">
                            ￥<fmt:formatNumber value="${o.price}" pattern="0.00"/> × ${o.quantity}
                        </td>
                        <td class="price-text">
                            ￥<fmt:formatNumber value="${o.totalPrice}" pattern="0.00"/>
                        </td>
                        <td class="small text-muted">
                            <fmt:formatDate value="${o.createTime}" pattern="yyyy-MM-dd HH:mm"/>
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
                        <td class="text-end">
                            <c:choose>
                                <c:when test="${o.status == 0}">
                                    <a href="${pageContext.request.contextPath}/updateOrderStatus?orderId=${o.id}&status=1"
                                       class="btn btn-sm btn-success"
                                       onclick="return confirm('确认这本教材已经交易完成了吗？');">确认完成</a>
                                    <a href="${pageContext.request.contextPath}/updateOrderStatus?orderId=${o.id}&status=2"
                                       class="btn btn-sm btn-outline-secondary"
                                       onclick="return confirm('确定取消该订单吗？');">取消订单</a>
                                </c:when>
                                <c:when test="${o.status == 1}">
                                    <%-- 只有已完成的订单才显示评价入口，跳转到详情页的评价表单 --%>
                                    <a href="${pageContext.request.contextPath}/bookDetail?id=${o.bookId}#commentForm"
                                       class="btn btn-sm btn-outline-primary">评价教材</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">无操作</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${empty orders}">
            <div class="empty-box">
                你还没有订单，<a href="${pageContext.request.contextPath}/index">去首页逛逛</a>
            </div>
        </c:if>
    </div>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
