<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 必须经过 BookDetailServlet 才能访问
    if (request.getAttribute("book") == null) {
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${book.name} - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <script>
        // 封面图片加载失败时显示“暂无封面”，必须定义在 img 之前
        function coverFallback(img) {
            img.style.display = 'none';
            var placeholder = img.nextElementSibling;
            if (placeholder && placeholder.classList.contains('detail-none')) {
                placeholder.classList.remove('d-none');
            }
        }
    </script>
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <%-- 下单结果 / 错误提示 --%>
    <c:if test="${not empty sessionScope.msg}">
        <div class="alert alert-info py-2 small">${sessionScope.msg}</div>
        <c:remove var="msg" scope="session"/>
    </c:if>

    <%-- 教材基本信息 --%>
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <div class="row g-4">

                <div class="col-md-4">
                    <c:choose>
                        <c:when test="${not empty book.coverUrl}">
                            <img src="${pageContext.request.contextPath}/${book.coverUrl}"
                                 class="detail-cover" alt="封面" onerror="coverFallback(this)">
                            <%-- 图片文件丢失时显示这个占位块 --%>
                            <div class="detail-none d-none">暂无封面</div>
                        </c:when>
                        <c:otherwise>
                            <div class="detail-none">暂无封面</div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="col-md-8">
                    <h4 class="mb-2">${book.name}</h4>
                    <div class="text-muted small mb-3">
                        卖家：${book.sellerName} ·
                        发布于 <fmt:formatDate value="${book.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                    </div>

                    <div class="d-flex align-items-center flex-wrap gap-2 mb-3">
                        <span class="price-lg">
                            ￥<fmt:formatNumber value="${book.price}" pattern="0.00"/>
                        </span>
                        <span class="badge bg-light text-secondary border">${book.bookCondition}</span>
                        <span class="badge bg-light text-secondary border">${book.grade}</span>
                        <span class="badge bg-light text-secondary border">${book.subject}</span>
                    </div>

                    <div class="row g-2 small mb-3">
                        <div class="col-sm-6">
                            <span class="text-muted">作者：</span>${empty book.author ? '未填写' : book.author}
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">出版社：</span>${empty book.publisher ? '未填写' : book.publisher}
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">库存：</span>${book.stock} 本
                        </div>
                        <div class="col-sm-6">
                            <span class="text-muted">状态：</span>
                            <c:choose>
                                <c:when test="${book.status == 1}">
                                    <span class="badge bg-success">在售</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">已下架</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="desc-box mb-4">${book.description}</div>

                    <%-- 下单区域：根据登录状态、是否本人、库存显示不同按钮 --%>
                    <div class="bg-light rounded p-3">
                        <c:choose>
                            <c:when test="${empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">
                                    登录后下单
                                </a>
                            </c:when>
                            <c:when test="${sessionScope.user.id == book.userId}">
                                <span class="text-muted small">这是你发布的教材，不能购买自己的书</span>
                            </c:when>
                            <c:when test="${book.status != 1}">
                                <span class="text-muted small">该教材已下架，暂不可购买</span>
                            </c:when>
                            <c:when test="${book.stock lt 1}">
                                <span class="text-muted small">库存不足</span>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/createOrder" method="post"
                                      class="d-flex gap-2 align-items-center flex-wrap">
                                    <input type="hidden" name="bookId" value="${book.id}">
                                    <label class="form-label mb-0">数量</label>
                                    <input type="number" class="form-control" name="quantity" value="1"
                                           min="1" max="${book.stock}" style="width: 90px;">
                                    <button type="submit" class="btn btn-primary">立即下单</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- 评价区 --%>
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header d-flex justify-content-between align-items-center py-3">
            <span class="section-title">教材评价</span>
            <span class="small text-muted">
                <c:choose>
                    <c:when test="${empty comments}">暂无评价</c:when>
                    <c:otherwise>
                        平均 ${avgScore} 分 · 共 ${fn:length(comments)} 条评价
                    </c:otherwise>
                </c:choose>
            </span>
        </div>

        <div class="card-body p-4">

            <%-- 评价表单：只有“有已完成订单且未评价过”的登录用户才能看到 --%>
            <c:if test="${canComment}">
                <div class="bg-light rounded p-3 mb-4" id="commentForm">
                    <h6 class="mb-3">评价这本教材</h6>
                    <form action="${pageContext.request.contextPath}/addComment" method="post">
                        <input type="hidden" name="bookId" value="${book.id}">

                        <div class="mb-3">
                            <label class="form-label">评分</label>
                            <div>
                                <c:forEach begin="1" end="5" var="i">
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="score"
                                               id="score${i}" value="${i}" ${i == 5 ? 'checked' : ''}>
                                        <label class="form-check-label" for="score${i}">${i} 星</label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">评价内容</label>
                            <textarea class="form-control" name="content" rows="3" maxlength="500" required
                                      placeholder="说说书本的成色、内容以及交易体验（不超过 500 字）"></textarea>
                        </div>

                        <button type="submit" class="btn btn-primary btn-sm px-3">提交评价</button>
                    </form>
                </div>
            </c:if>

            <c:forEach items="${comments}" var="c">
                <div class="review-item">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <div>
                            <strong class="small">${c.userName}</strong>
                            <span class="stars ms-2">
                                <c:forEach begin="1" end="5" var="i">${i le c.score ? '★' : '☆'}</c:forEach>
                            </span>
                        </div>
                        <small class="text-muted">
                            <fmt:formatDate value="${c.createTime}" pattern="yyyy-MM-dd HH:mm"/>
                        </small>
                    </div>
                    <div class="small text-secondary">${c.content}</div>
                </div>
            </c:forEach>

            <c:if test="${empty comments}">
                <div class="empty-box">
                    还没有人评价这本教材，完成交易后可以在“我的订单”中评价。
                </div>
            </c:if>
        </div>
    </div>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
