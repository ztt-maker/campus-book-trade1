<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 直接访问 index.jsp 时没有数据，先转发给 IndexServlet 查询后再回到本页
    if (request.getAttribute("books") == null) {
        request.getRequestDispatcher("/index").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>首页 - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <script>
        // 图片加载失败时显示“暂无封面”
        // 必须定义在 img 之前，否则图片 404 时 onerror 先触发，函数还没加载会报错
        function imgFallback(img) {
            img.style.display = 'none';
            var placeholder = img.nextElementSibling;
            if (placeholder && placeholder.classList.contains('book-cover-none')) {
                placeholder.classList.remove('d-none');
            }
        }
    </script>
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<div class="container mt-4">

    <%-- 搜索区 --%>
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-3">
                <div>
                    <h5 class="mb-1">校园二手教材</h5>
                    <p class="text-muted small mb-0">低价淘教材，让闲置的书本流动起来</p>
                </div>
                <small class="text-muted">共 ${fn:length(books)} 本教材</small>
            </div>

            <form class="row g-2" action="${pageContext.request.contextPath}/index" method="get">
                <div class="col-md-8">
                    <input type="text" class="form-control" name="keyword" value="${keyword}"
                           placeholder="输入教材名称搜索，例如：高等数学">
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary flex-fill">搜索</button>
                    <a href="${pageContext.request.contextPath}/index" class="btn btn-outline-secondary">重置</a>
                </div>
            </form>

            <c:if test="${not empty keyword}">
                <div class="small text-muted mt-2">
                    关键词“${keyword}”共找到 ${fn:length(books)} 条教材
                </div>
            </c:if>
        </div>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger py-2 small">${error}</div>
    </c:if>

    <%-- 教材卡片列表：全部来自数据库，一行 4 列 --%>
    <div class="row g-3 g-md-4">
        <c:forEach items="${books}" var="book">

            <%-- 封面：优先发布时上传的图片，没有则使用按书名匹配的本地默认封面 --%>
            <c:set var="coverUrl" value=""/>
            <c:if test="${not empty book.coverUrl}">
                <c:set var="coverUrl" value="${pageContext.request.contextPath}/${book.coverUrl}"/>
            </c:if>

            <div class="col-6 col-md-4 col-lg-3">
                <div class="card h-100 shadow-sm book-card">

                    <c:choose>
                        <c:when test="${not empty coverUrl}">
                            <img src="${coverUrl}" class="card-img-top book-cover"
                                 alt="${book.name}" onerror="imgFallback(this)">
                            <%-- 图片文件不存在或加载失败时显示这个占位块 --%>
                            <div class="book-cover-none d-none">暂无封面</div>
                        </c:when>
                        <c:otherwise>
                            <div class="book-cover-none">暂无封面</div>
                        </c:otherwise>
                    </c:choose>

                    <div class="card-body p-3">
                        <h6 class="card-title text-truncate mb-1" title="${book.name}">${book.name}</h6>
                        <div class="text-muted small text-truncate mb-2">
                            ${empty book.author ? '佚名' : book.author} · ${empty book.publisher ? '未知出版社' : book.publisher}
                        </div>

                        <div class="d-flex flex-wrap gap-1 mb-2">
                            <span class="badge bg-light text-secondary border">${book.bookCondition}</span>
                            <span class="badge bg-light text-secondary border">${book.grade}</span>
                            <span class="badge bg-light text-secondary border">${book.subject}</span>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price-text">
                                ￥<fmt:formatNumber value="${book.price}" pattern="0.00"/>
                            </span>
                            <small class="text-muted">库存 ${book.stock}</small>
                        </div>
                    </div>

                    <div class="card-footer bg-white border-0 pt-0 d-flex justify-content-between align-items-center">
                        <span class="small text-muted text-truncate">${book.sellerName}</span>
                        <a href="${pageContext.request.contextPath}/bookDetail?id=${book.id}"
                           class="btn btn-sm btn-outline-primary">查看</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <%-- 空数据提示 --%>
    <c:if test="${empty books}">
        <div class="card border-0 shadow-sm">
            <div class="card-body empty-box">
                没有找到相关教材，换个关键词试试吧
            </div>
        </div>
    </c:if>

</div>

<jsp:include page="/common/footer.jsp"/>
</body>
</html>
