/* =============================================================
 * 校园二手教材交易系统 - 数据库脚本（JavaEE 期末作业版）
 * 数据库：MySQL 5.7 / 8.0
 * 表名：user（用户）、book（教材）、orders（订单）、comment（评价）
 * 说明：整个文件可直接全部执行；表名 user / comment 为关键字，SQL 中统一加反引号
 * ============================================================= */

DROP DATABASE IF EXISTS campus_book_trade;
CREATE DATABASE campus_book_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_book_trade;

/* -------------------------------------------------------------
 * 1. 用户表 user
 * role：student-普通学生 admin-管理员
 * ------------------------------------------------------------- */
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    id          INT         NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    password    VARCHAR(64) NOT NULL COMMENT '密码（MD5加密，32位）',
    name        VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    role        VARCHAR(10) NOT NULL DEFAULT 'student' COMMENT '角色：student学生 admin管理员',
    phone       VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

/* -------------------------------------------------------------
 * 2. 教材表 book
 * user_id：发布人（卖家）
 * status：1-上架 0-下架
 * image：封面图相对路径，如 upload/xxx.jpg
 * ------------------------------------------------------------- */
CREATE TABLE book (
    id             INT           NOT NULL AUTO_INCREMENT COMMENT '教材ID',
    name           VARCHAR(100)  NOT NULL COMMENT '教材名称',
    author         VARCHAR(100)  DEFAULT NULL COMMENT '作者',
    publisher      VARCHAR(100)  DEFAULT NULL COMMENT '出版社',
    grade          VARCHAR(20)   DEFAULT NULL COMMENT '适用年级',
    subject        VARCHAR(50)   DEFAULT NULL COMMENT '科目',
    book_condition VARCHAR(20)   DEFAULT '九成新' COMMENT '新旧程度',
    price          DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格（元）',
    stock          INT           NOT NULL DEFAULT 1 COMMENT '库存',
    image          VARCHAR(255)  DEFAULT NULL COMMENT '图片路径',
    description    VARCHAR(1000) DEFAULT NULL COMMENT '教材描述',
    user_id        INT           NOT NULL COMMENT '发布人ID',
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    status         TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
    PRIMARY KEY (id),
    KEY idx_user (user_id),
    KEY idx_name (name),
    CONSTRAINT fk_book_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='教材表';

/* -------------------------------------------------------------
 * 3. 订单表 orders
 * 一个订单对应一种教材
 * status：0-待确认 1-已完成 2-已取消
 * ------------------------------------------------------------- */
CREATE TABLE orders (
    id            INT           NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    book_id       INT           NOT NULL COMMENT '教材ID',
    buyer_user_id INT           NOT NULL COMMENT '买家ID',
    price         DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity      INT           NOT NULL DEFAULT 1 COMMENT '购买数量',
    total_price   DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status        TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0待确认 1已完成 2已取消',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    KEY idx_buyer (buyer_user_id),
    KEY idx_book (book_id),
    CONSTRAINT fk_orders_book  FOREIGN KEY (book_id)       REFERENCES book (id),
    CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_user_id) REFERENCES `user` (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='订单表';

/* -------------------------------------------------------------
 * 4. 评价表 comment
 * score：1~5 分
 * ------------------------------------------------------------- */
CREATE TABLE `comment` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    book_id     INT          NOT NULL COMMENT '教材ID',
    user_id     INT          NOT NULL COMMENT '评价人ID',
    score       TINYINT      NOT NULL DEFAULT 5 COMMENT '评分1-5',
    content     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (id),
    KEY idx_book (book_id),
    KEY idx_user (user_id),
    CONSTRAINT fk_comment_book FOREIGN KEY (book_id) REFERENCES book (id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='评价表';

/* =============================================================
 * 测试数据
 * 所有账号密码均为 123456（MD5：e10adc3949ba59abbe56e057f20f883e）
 * ============================================================= */
INSERT INTO `user` (username, password, name, role, phone) VALUES
('admin',    MD5('123456'), '系统管理员', 'admin',   '13800000000'),
('student1', MD5('123456'), '张三',       'student', '13800000001'),
('student2', MD5('123456'), '李四',       'student', '13800000002');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status) VALUES
('高等数学（第七版）上册', '同济大学数学系', '高等教育出版社', '大一', '高等数学', '九成新', 25.00, 2, NULL, '书角有轻微磨损，笔记较少，适合大一使用。',   2, 1),
('数据结构（C语言版）',   '严蔚敏',         '清华大学出版社', '大二', '数据结构', '八成新', 18.50, 1, NULL, '考研复习用书，课后习题有少量铅笔答案。',       2, 1),
('大学英语综合教程 1',    '李荫华',         '上海外语教育出版社', '大一', '大学英语', '全新', 30.00, 3, NULL, '买重了，未拆封，全新正版。',                   3, 1),
('计算机网络（第7版）',   '谢希仁',         '电子工业出版社', '大三', '计算机网络', '七成新', 15.00, 1, NULL, '书页完整无缺页，封面有折痕。',                 3, 1);

INSERT INTO orders (book_id, buyer_user_id, price, quantity, total_price, status) VALUES
(2, 3, 18.50, 1, 18.50, 1),   -- 李四买张三的数据结构，已完成
(1, 3, 25.00, 1, 25.00, 0);   -- 李四买张三的高数，待确认

INSERT INTO `comment` (book_id, user_id, score, content) VALUES
(2, 3, 5, '书很新，知识点总结得很全，推荐！'),
(1, 3, 4, '整体不错，就是笔记有点多。');

/* 查询验证（可选）
SELECT b.*, u.name AS seller_name FROM book b JOIN `user` u ON b.user_id = u.id ORDER BY b.create_time DESC;
SELECT o.*, b.name AS book_name, u.name AS buyer_name FROM orders o JOIN book b ON o.book_id = b.id JOIN `user` u ON o.buyer_user_id = u.id ORDER BY o.create_time DESC;
*/
