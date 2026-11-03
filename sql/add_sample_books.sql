/* =============================================================
 * 补充演示数据：8 位卖家 + 8 本教材
 * 作用：让首页展示的教材与管理员后台、数据库完全一致
 * 特点：可重复执行，已存在的数据不会重复插入
 * ============================================================= */
USE campus_book_trade;

/* ---------- 1. 新增 8 位卖家（密码都是 123456） ---------- */
INSERT INTO `user` (username, password, name, role, phone)
SELECT 'chen', MD5('123456'), '陈同学', 'student', '13800000003' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'chen');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'wang', MD5('123456'), '王同学', 'student', '13800000004' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'wang');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'liu', MD5('123456'), '刘同学', 'student', '13800000005' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'liu');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'zhao', MD5('123456'), '赵同学', 'student', '13800000006' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'zhao');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'sun', MD5('123456'), '孙同学', 'student', '13800000007' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'sun');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'zhou', MD5('123456'), '周同学', 'student', '13800000008' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'zhou');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'wu', MD5('123456'), '吴同学', 'student', '13800000009' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'wu');

INSERT INTO `user` (username, password, name, role, phone)
SELECT 'zheng', MD5('123456'), '郑同学', 'student', '13800000010' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE username = 'zheng');

/* ---------- 2. 新增 8 本教材，卖家对应上面 8 位用户 ---------- */
INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT 'Java Web程序设计（第2版）', '李兴华', '清华大学出版社', '大三', 'Java', '九成新', 28.00, 2, NULL,
       '书况良好，重点章节有少量笔记，适合 Java Web 课程使用。', u.id, 1
FROM `user` u
WHERE u.username = 'chen'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = 'Java Web程序设计（第2版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '线性代数（第六版）', '同济大学数学系', '高等教育出版社', '大一', '线性代数', '八成新', 12.00, 3, NULL,
       '课后习题做过一部分，答案用铅笔写，可擦除。', u.id, 1
FROM `user` u
WHERE u.username = 'wang'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '线性代数（第六版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '大学物理（上册）', '马文蔚', '高等教育出版社', '大二', '大学物理', '九成新', 20.00, 1, NULL,
       '封面有轻微折痕，内页干净，附赠配套习题册。', u.id, 1
FROM `user` u
WHERE u.username = 'liu'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '大学物理（上册）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '操作系统概念（第九版）', '西尔伯沙茨', '机械工业出版社', '大三', '操作系统', '七成新', 35.00, 1, NULL,
       '专业书，有部分标注，不影响阅读，考研复习很好用。', u.id, 1
FROM `user` u
WHERE u.username = 'zhao'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '操作系统概念（第九版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '概率论与数理统计（第四版）', '盛骤', '高等教育出版社', '大二', '概率论', '九成新', 16.50, 2, NULL,
       '书页完整，笔记较少，适合期末复习。', u.id, 1
FROM `user` u
WHERE u.username = 'sun'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '概率论与数理统计（第四版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '离散数学（第五版）', '耿素云', '清华大学出版社', '大二', '离散数学', '八成新', 14.00, 2, NULL,
       '有少量荧光笔标记，重点内容已整理。', u.id, 1
FROM `user` u
WHERE u.username = 'zhou'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '离散数学（第五版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '软件工程导论（第六版）', '张海藩', '清华大学出版社', '大三', '软件工程', '九成新', 22.00, 1, NULL,
       '几乎全新，只翻过前两章，适合课程设计参考。', u.id, 1
FROM `user` u
WHERE u.username = 'wu'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '软件工程导论（第六版）');

INSERT INTO book (name, author, publisher, grade, subject, book_condition, price, stock, image, description, user_id, status)
SELECT '马克思主义基本原理概论', '本书编写组', '高等教育出版社', '大一', '思政', '全新', 10.00, 4, NULL,
       '买重了，全新未使用，低价出。', u.id, 1
FROM `user` u
WHERE u.username = 'zheng'
  AND NOT EXISTS (SELECT 1 FROM book b WHERE b.name = '马克思主义基本原理概论');
