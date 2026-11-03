```
# 校园二手教材交易系统 部署文档

| 项目 | 内容 |
|---|---|
| 项目名称 | campus-book-trade（校园二手教材交易系统） |
| 项目路径 | `D:\JavaEE1\campus-book-trade` |
| 打包方式 | Maven WAR，`finalName` = `campus-book-trade` |
| 数据库 | MySQL，库名 `campus_book_trade` |
| 访问入口 | `[http://localhost:8080/campus-book-trade/](http://localhost:8080/campus-book-trade/)` |

---

## 1. 系统概述

基于 Java EE（Servlet + JSP + JDBC + MySQL）的传统 Web 项目，无 Spring 框架，Servlet/Filter 全部使用注解注册。

| 模块 | 入口地址 | 说明 |
|---|---|---|
| 首页/搜索 | `/index` | 在售教材列表，`keyword` 模糊搜索，`status=1` 才展示 |
| 教材详情 | `/bookDetail?id=x` | 含评价列表、可下单区域 |
| 登录 | `/login`（页面 `/login.jsp`） | MD5 校验密码，成功写入 session |
| 注册 | `/register` | 用户名唯一校验 |
| 发布教材 | `/addBook`（页面 `/publishBook.jsp`） | multipart 表单，支持封面图上传 |
| 我的发布 | `/myBooks` | 包含已下架教材 |
| 我的订单 | `/myOrders` | 支持取消订单、确认完成 |
| 我的评价 | `/myComments` | 查看自己所有评价 |
| 后台管理 | `/admin/book`、`/admin/user`、`/admin/order` | 仅 `role=admin` 管理员账号可访问 |

权限由两个过滤器控制：`LoginFilter`（拦截 `/*`，内置白名单）与 `AdminFilter`（拦截 `/admin/*`）。会话超时时间 30 分钟，配置在 `web.xml`。

---

## 2. 环境要求（关键，选错版本必失败）

| 组件 | 要求版本 | 说明 |
|---|---|---|
| JDK | **8 / 11 / 17**（推荐 8 或 11） | `pom.xml` 编译级别为 1.8；JDK 24+ 不再支持 `-source 8` |
| Tomcat | **9.x（必须）** | 依赖 `javax.servlet-api 4.0.1`；Tomcat 10/11 使用 `jakarta.*` 包名，部署后会报 `ClassNotFoundException` |
| Maven | 3.6+ | 用于编译打包 |
| MySQL | 5.7 / 8.0 / 9.0 | 脚本兼容 5.7/8.0，9.0 可正常导入；连接串自带 `allowPublicKeyRetrieval=true` |
| 浏览器 | Chrome / Edge 新版 | Bootstrap、jQuery 使用 CDN，**需要联网**；断网会丢失样式，业务功能不受影响 |

> 离线演示方案：将 Bootstrap CSS 下载到 `src/main/webapp/css/`，修改所有 JSP 的 CDN link 为本地路径。

---

## 3. 目录结构说明
```

D:\JavaEE1\campus-book-trade
├── pom.xml                              # 依赖与打包配置
├── sql  
│   ├── campus_book_trade.sql            # 建库建表 + 初始数据（包含 DROP DATABASE）
│   └── add_sample_books.sql             # 补充演示数据，可重复执行
└── src\main  
    ├── java\com\campus  
    │   ├── entity\      Book / User / Order / Comment
    │   ├── dao\         BookDao / UserDao / OrderDao / CommentDao（原生 JDBC）
    │   ├── servlet\     17 个 Servlet（@WebServlet 注解）
    │   ├── filter\      LoginFilter、AdminFilter
    │   └── util\        DBUtil、MD5Util、UploadUtil
    ├── resources\db.properties           # 数据库连接配置，部署前必须修改
    └── webapp  
        ├── WEB-INF\web.xml               # 欢迎页配置、session 超时配置
        ├── common\header.jsp / footer.jsp
        ├── index.jsp / bookDetail.jsp / publishBook.jsp / adminIndex.jsp ...
        ├── css\style.css
        └── images\covers*.jpg           # 内置默认封面图片

```

核心配置文件：`src/main/resources/db.properties`、`src/main/webapp/WEB-INF/web.xml`

---

## 4. 数据库部署

### 4.1 导入主脚本
> ⚠️ 警告：`campus_book_trade.sql` 包含 `DROP DATABASE IF EXISTS campus_book_trade;`，会清空同名数据库，提前做好备份！

Windows 命令行导入：
```bash
chcp 65001
"C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe" -uroot -p123456 --default-character-set=utf8mb4 < "D:\JavaEE1\campus-book-trade\sql\campus_book_trade.sql"
```

图形工具（Navicat / MySQL Workbench）：直接打开 `sql/campus_book_trade.sql` 全部执行，脚本自带建库、切换库语句。

脚本一共创建4张数据表：

| 表名 | 说明 | 外键关联 |
| --- | --- | --- |
| `user` | 用户表，role区分学生/管理员，密码MD5存储 | — |
| `book` | 教材表，status=1上架 0下架，image存图片相对路径 | `user_id → user.id` |
| `orders` | 订单表，status：0待确认、1已完成、2已取消 | `book_id`、`buyer_user_id` |
| `comment` | 评价表，score为1~5星评分 | `book_id`、`user_id` |

> 
> `user`、`comment` 属于MySQL关键字，项目SQL全部添加反引号，避免语法报错。

### 4.2 可选：补充演示数据

基础脚本只有4本教材，执行下面命令可以额外添加8个卖家、8本教材，首页合计12条教材，匹配全部内置封面：

```
"C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe" -uroot -p123456 --default-character-set=utf8mb4 < "D:\JavaEE1\campus-book-trade\sql\add_sample_books.sql"
```

脚本做了幂等判断，多次执行不会重复插入数据。

### 4.3 验证数据

执行SQL查看在售教材：

```
SELECT b.id, b.name, b.price, b.stock, u.name AS seller
FROM book b JOIN `user` u ON b.user_id = u.id
WHERE b.status = 1 ORDER BY b.id;
```

```
SELECT COUNT(*) AS user_count FROM `user`;
```

✅ 导入两个脚本后预期：在售教材12条，用户总数11个。

> 
> 如果首页显示0本教材，说明数据没有导入成功或者教材status=0下架状态。

---

## 5. 数据库连接配置

编辑 `src/main/resources/db.properties`

```
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/campus_book_trade?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
username=root
password=123456
```

修改要点：

1. 一般只需要修改 `username` 和 `password`；MySQL端口不是3306就修改url端口号
2. `characterEncoding=utf8` 不可删除，防止中文乱码
3. `allowPublicKeyRetrieval=true` 不可删除，解决MySQL8/9公钥认证报错
4. `serverTimezone=Asia/Shanghai` 不可删除，防止订单时间时差8小时
5. 修改配置文件后**必须重新打包部署**，修改才会生效

---

## 6. 编译打包

```
cd /d D:\JavaEE1\campus-book-trade
mvn clean package -DskipTests
```

打包产物：`target\campus-book-trade.war`

> 
> 首次下载依赖很慢，可以在maven `settings.xml` 配置阿里云镜像：

```
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>aliyun maven</name>
    <url>[https://maven.aliyun.com/repository/public</url>](https://maven.aliyun.com/repository/public</url>)
  </mirror>
</mirrors>
```

---

## 7. 部署方式一：Tomcat + WAR（作业演示推荐）

```
# 1. 停止Tomcat
%CATALINA_HOME%\bin\shutdown.bat

# 2. 清理旧部署包
rmdir /s /q "%CATALINA_HOME%\webapps\campus-book-trade"
del "%CATALINA_HOME%\webapps\campus-book-trade.war"

# 3. 复制新war包到webapps
copy "D:\JavaEE1\campus-book-trade\target\campus-book-trade.war" "%CATALINA_HOME%\webapps\"

# 4. 启动Tomcat
%CATALINA_HOME%\bin\startup.bat
```

Tomcat自动解压war包，访问地址：

```
[http://localhost:8080/campus-book-trade/](http://localhost:8080/campus-book-trade/)
```

> 
> 项目使用 `${pageContext.request.contextPath}` 拼接路径，上下文路径固定为 `/campus-book-trade`，不用修改页面链接。

---

## 8. 部署方式二：IDEA本地运行（开发调试推荐）

1. `File → Project Structure → Modules`，确认 `src/main/java` 是Sources、`src/main/resources` 是Resources
2. `Run → Edit Configurations → + → Tomcat Server → Local`，选择Tomcat9根目录
3. `Deployment → + → Artifact → campus-book-trade:war exploded`，Application context填写 `/campus-book-trade`
4. 启动Tomcat，自动打开首页

> 
> IDEA exploded模式下，执行 `mvn clean` 会清空target目录，上传的图片会丢失。

---

## 9. 图片上传目录（高频踩坑点）

图片保存逻辑：`request.getServletContext().getRealPath("/" + IMAGE_DIR);`
图片保存在部署目录 `images/` 下，数据库只存储相对路径。

| 部署模式 | 图片位置 | 风险 |
| --- | --- | --- |
| WAR部署 | `%CATALINA_HOME%\webapps\campus-book-trade\images\` | 重新部署war，整个文件夹删除，上传图片丢失 |
| IDEA exploded | `target\campus-book-trade\images\` | `mvn clean` 清空图片 |
| 不自动解压war | `getRealPath` 返回null | 图片上传直接报错 |

备份图片脚本：

```
xcopy "%CATALINA_HOME%\webapps\campus-book-trade\images" "D:\backup\images_%date:~0,4%%date:~5,2%%date:~8,2%" /E /I /Y
```

> 
> 图片加载失败页面会自动展示“暂无封面”占位，不会出现裂图。

---

## 10. 部署后验证清单

| # | 操作 | 预期结果 |
| --- | --- | --- |
| 1 | 访问首页 `[http://localhost:8080/campus-book-trade/](http://localhost:8080/campus-book-trade/)` | 页面正常，提示共12本教材 |
| 2 | 浏览教材卡片 | 全部展示封面，无裂图 |
| 3 | 搜索框输入Java搜索 | 筛选出对应教材 |
| 4 | 点击重置 | 恢复全部12条数据 |
| 5 | 进入教材详情页 | 教材信息、评价列表正常展示 |
| 6 | 未登录访问 `/myBooks` | 自动跳转到登录页面 |
| 7 | student1账号登录 | 导航栏显示用户名 |
| 8 | 发布教材，上传图片 | 发布成功，我的发布列表可见，首页可以刷出新教材 |
| 9 | 上传超过5MB图片 | 提示图片超出大小限制 |
| 10 | student2账号下单 | 下单成功，库存减少，我的订单可以看到 |
| 11 | 普通学生访问后台 `/admin/book` | 无权限，跳转首页提示 |
| 12 | admin账号登录后台 | 教材、用户、订单管理功能正常 |

---

## 11. 默认账号（统一密码：`123456`）

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | admin | 系统管理员 |
| `student1` / `student2` | student | 测试学生账号 |
| `chen` `wang` `liu` `zhao` `sun` `zhou` `wu` `zheng` | student | 演示学生账号 |

> 
> 数据库密码是MD5加密：`123456` 加密结果为 `e10adc3949ba59abbe56e057f20f883e`

---

## 12. 常见问题排查

| 现象 | 原因 | 解决方案 |
| --- | --- | --- |
| 404页面找不到 | war没有部署成功 / 访问路径缺少前缀 | 确认webapps解压目录，地址带上 `/campus-book-trade` |
| 500 `ClassNotFoundException: javax.servlet` | 使用Tomcat10/11 | 更换Tomcat9.x |
| 500 数据库初始化失败 | db.properties缺失或者配置错误 | 检查classes下db.properties，核对账号密码 |
| Communications link failure | MySQL服务未启动 / 端口错误 | 启动MySQL，核对url端口 |
| Access denied for user root | MySQL账号密码错误 | 修改db.properties，重新打包部署 |
| Unknown database | 数据库没有创建 | 重新执行建库sql脚本 |
| 页面中文乱码 | 导入脚本编码不对 | 使用 `--default-character-set=utf8mb4` 导入sql |
| 上传图片提示无法定位目录 | Tomcat关闭自动解压war | server.xml Host标签保持 `unpackWARs="true"` |
| 8080端口占用 | 端口冲突 | 修改server.xml端口或者关闭占用程序 |
| 页面样式全部消失 | Bootstrap CDN无法访问 | 切换国内CDN或者改用本地css文件 |

日志查看位置：`%CATALINA_HOME%\logs\catalina.YYYY-MM-DD.log`

---

## 13. 升级部署与回滚

### 升级（保留业务数据）

```
# 备份数据库
"C:\Program Files\MySQL\MySQL Server 9.0\bin\mysqldump.exe" -uroot -p123456 --default-character-set=utf8mb4 campus_book_trade > "D:\backup\cbt_backup.sql"

# 备份上传图片
xcopy "%CATALINA_HOME%\webapps\campus-book-trade\images" "D:\backup\images" /E /I /Y
```

备份完成后：停Tomcat → 删除旧war和目录 → 放入新war → 启动Tomcat，执行验证清单。

### 回滚方案

保留旧版本war包和数据库备份；停服务、删除部署目录，放回旧war。表结构不变的情况下，只需要回退war，不需要恢复数据库。

---

## 14. 附录

Maven命令

```
mvn clean            # 清理target目录
mvn compile          # 仅编译，快速检查代码报错
mvn clean package    # 打包war
```

Tomcat启停

```
%CATALINA_HOME%\bin\startup.bat
%CATALINA_HOME%\bin\shutdown.bat
```

> 
> ✅ 部署一句话检查清单：Tomcat9 + JDK8/11 + MySQL导入脚本 + db.properties密码正确 + mvn打包成功 + war放入webapps + 访问首页可以看到12本教材

## 获取代码

方式一：下载压缩包

打开仓库页面，点击绿色 Code 按钮，选择下载 ZIP，解压到本地文件夹。

方式二：使用 Git 克隆

```bash
git clone https://github.com/ztt-maker/campus-book-trade1.git
cd campus-book-trade1
