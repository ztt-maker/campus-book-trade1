<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${empty book.id ? '发布教材' : '编辑教材'} - 校园二手教材交易系统</title>
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<jsp:include page="/common/header.jsp"/>

<%-- 新增走 /addBook，编辑走 /editBook --%>
<c:set var="formAction" value="${empty book.id ? 'addBook' : 'editBook'}"/>

<div class="container mt-4 mb-4" style="max-width: 820px;">
    <div class="card border-0 shadow-sm">
        <div class="card-header py-3">
            <span class="section-title">${empty book.id ? '发布二手教材' : '编辑教材信息'}</span>
        </div>

        <div class="card-body p-4">
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2 small">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/${formAction}" method="post"
                  enctype="multipart/form-data">
                <c:if test="${not empty book.id}">
                    <input type="hidden" name="id" value="${book.id}">
                </c:if>

                <div class="row g-3">
                    <div class="col-12">
                        <label class="form-label">教材名称 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="name" value="${book.name}"
                               maxlength="100" required placeholder="例如：高等数学（第七版）上册">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">作者</label>
                        <input type="text" class="form-control" name="author" value="${book.author}" maxlength="100">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">出版社</label>
                        <input type="text" class="form-control" name="publisher" value="${book.publisher}" maxlength="100">
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">适用年级</label>
                        <select class="form-select" name="grade">
                            <option value="大一" ${book.grade == '大一' ? 'selected' : ''}>大一</option>
                            <option value="大二" ${book.grade == '大二' ? 'selected' : ''}>大二</option>
                            <option value="大三" ${book.grade == '大三' ? 'selected' : ''}>大三</option>
                            <option value="大四" ${book.grade == '大四' ? 'selected' : ''}>大四</option>
                            <option value="研究生" ${book.grade == '研究生' ? 'selected' : ''}>研究生</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">科目</label>
                        <input type="text" class="form-control" name="subject" value="${book.subject}"
                               maxlength="50" placeholder="例如：高等数学">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">新旧程度</label>
                        <select class="form-select" name="bookCondition">
                            <option value="全新" ${book.bookCondition == '全新' ? 'selected' : ''}>全新</option>
                            <option value="九成新" ${book.bookCondition == '九成新' ? 'selected' : ''}>九成新</option>
                            <option value="八成新" ${book.bookCondition == '八成新' ? 'selected' : ''}>八成新</option>
                            <option value="七成新" ${book.bookCondition == '七成新' ? 'selected' : ''}>七成新</option>
                            <option value="五成新" ${book.bookCondition == '五成新' ? 'selected' : ''}>五成新</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">价格（元） <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" name="price" value="${book.price}"
                               step="0.01" min="0.01" required placeholder="例如：25.00">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">库存 <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" name="stock" value="${empty book.stock ? 1 : book.stock}"
                               min="1" required>
                    </div>

                    <div class="col-12">
                        <label class="form-label">封面图片</label>
                        <input type="file" class="form-control" name="image" id="imageInput"
                               accept="image/jpeg,image/png,image/gif,image/bmp,image/webp"
                               onchange="previewImage(this)">
                        <div class="form-text">支持 jpg / png / gif / bmp / webp，大小不超过 5MB；编辑时不选则保留原图</div>

                        <div class="mt-2">
                            <%-- 新选择的图片预览 --%>
                            <img id="imagePreview" src="" alt="预览"
                                 class="rounded border d-none" style="height: 100px; object-fit: cover;">
                            <%-- 编辑时的原封面 --%>
                            <c:if test="${not empty book.image}">
                                <img id="oldCover" src="${pageContext.request.contextPath}/${book.image}"
                                     alt="当前封面" class="rounded border"
                                     style="height: 100px; object-fit: cover;">
                                <div class="form-text">当前封面（重新选择图片后会替换）</div>
                            </c:if>
                        </div>
                    </div>

                    <div class="col-12">
                        <label class="form-label">教材描述</label>
                        <textarea class="form-control" name="description" rows="4" maxlength="1000"
                                  placeholder="可以写书籍成色、笔记情况、交易地点等">${book.description}</textarea>
                    </div>
                </div>

                <div class="mt-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary px-4">保存</button>
                    <a href="${pageContext.request.contextPath}/myBooks" class="btn btn-outline-secondary">返回我的发布</a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp"/>

<script>
    // 选择图片后本地预览，并做大小、格式的前端校验
    function previewImage(input) {
        var preview = document.getElementById("imagePreview");
        var oldCover = document.getElementById("oldCover");

        if (!input.files || !input.files[0]) {
            preview.classList.add("d-none");
            return;
        }

        var file = input.files[0];

        if (file.size > 5 * 1024 * 1024) {
            alert("图片大小不能超过 5MB，请压缩后重新选择");
            input.value = "";
            preview.classList.add("d-none");
            return;
        }
        if (!/\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(file.name)) {
            alert("只支持 jpg、png、gif、bmp、webp 格式的图片");
            input.value = "";
            preview.classList.add("d-none");
            return;
        }

        var reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.classList.remove("d-none");
            if (oldCover) {
                oldCover.classList.add("d-none");
            }
        };
        reader.readAsDataURL(file);
    }
</script>
</body>
</html>
