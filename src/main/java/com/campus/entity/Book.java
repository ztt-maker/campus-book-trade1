package com.campus.entity;

import java.util.Date;

/**
 * 教材实体类
 * 对应数据库表：book
 */
public class Book {

    private Integer id;             // 教材ID
    private String name;            // 教材名称
    private String author;          // 作者
    private String publisher;       // 出版社
    private String grade;           // 适用年级，如：大一
    private String subject;         // 科目，如：高等数学
    private String bookCondition;   // 新旧程度，如：九成新（对应字段 book_condition）
    private Double price;           // 价格
    private Integer stock;          // 库存
    private String image;           // 封面图路径，如：upload/xxx.jpg
    private String description;     // 教材描述
    private Integer userId;         // 发布人ID（卖家）
    private Date createTime;        // 发布时间
    private Integer status;         // 状态：1上架 0下架

    /** 非数据库字段：列表页联表查询时带出的发布人姓名 */
    private String sellerName;

    public Book() {
    }

    /** 有参构造（只包含数据库字段，sellerName 通过 setter 赋值） */
    public Book(Integer id, String name, String author, String publisher, String grade,
                String subject, String bookCondition, Double price, Integer stock,
                String image, String description, Integer userId, Date createTime, Integer status) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.grade = grade;
        this.subject = subject;
        this.bookCondition = bookCondition;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.description = description;
        this.userId = userId;
        this.createTime = createTime;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * 非数据库字段：教材封面的相对路径
     * 1. 发布时上传了图片 → 返回 images/xxx.jpg
     * 2. 没有上传图片 → 按书名匹配 images/covers 下的本地默认封面
     * 3. 都匹配不到 → 返回 null，页面显示“暂无封面”
     */
    public String getCoverUrl() {
        if (image != null && !image.trim().isEmpty()) {
            return image;
        }
        String n = name == null ? "" : name;
        if (n.contains("高等数学")) return "images/covers/gaoshu.jpg";
        if (n.contains("数据结构")) return "images/covers/data-structure.jpg";
        if (n.contains("英语")) return "images/covers/college-english.jpg";
        if (n.contains("计算机网络")) return "images/covers/computer-network.jpg";
        if (n.contains("线性代数")) return "images/covers/linear-algebra.jpg";
        if (n.contains("物理")) return "images/covers/physics.jpg";
        if (n.contains("操作系统")) return "images/covers/os.jpg";
        if (n.contains("Java")) return "images/covers/java-web.jpg";
        if (n.contains("概率")) return "images/covers/probability.jpg";
        if (n.contains("离散")) return "images/covers/discrete-math.jpg";
        if (n.contains("软件工程")) return "images/covers/software-engineering.jpg";
        if (n.contains("马克思")) return "images/covers/marxism.jpg";
        return null;
    }
}
