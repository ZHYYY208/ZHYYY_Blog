# ZHYYY 算法小屋 · 个人博客

一个带毛玻璃 UI 的现代个人博客，基于 **Vue 3 + Spring Boot + SQLite** 实现，包含博客、说说、相册、音乐、知识树与算法战绩等模块。

## ✨ 功能特性

- **首页**：个人信息卡 + 音乐模块 + 算法战绩 + 功能面板拼块；底部信息栏（北京时间 / 建站技术栈 logo / 运行时间）
- **博客**：后台以 Markdown 撰写、实时预览，前台渲染排版（含宽表格横向滚动适配）
- **说说**：后台发布 / 删除，支持**一次附带多张图片**，前台灯箱放大查看
- **相册**：后台先**创建文件夹**再进文件夹内上传图片，前台按文件夹筛选；未归类照片可随时移动到对应文件夹
- **音乐**：后台上传音频入库；首页音乐模块（旋转唱片 + 歌单弹窗）+ 音乐页「音乐馆」沉浸式播放；后台可设置**进站自动播放**某首歌（受浏览器策略限制需首次点击触发）
- **知识树**：以后台维护的树形「所学技术」展示，前台点击大类展开/收起
- **算法战绩**：后台填 CF handle 自动拉取 Codeforces Rating/段位（带段位色），可挂洛谷 / 牛客主页入口
- **关于页**：自我介绍式大卡（头像 + 简介 + Markdown 正文）+ 最近动态，正文后台可编辑
- **虚拟宠物**：右下角会敲代码的小猫（多排键帽 + 左右手随机敲击，随打字节奏同步）
- **管理后台**：`/admin` 每次访问需登录，统一管理文章 / 说说 / 相册 / 音乐 / 技术栈 / 留言 / 账号 / 网站设置，删除带二次确认与 toast 反馈
- **留言板**：访客注册登录后留言（纯文字，登录态保持 5 天），附《留言规范》超链接弹窗与免责声明；站长留言带「站长」徽章；站长可在后台删除留言、重置/删除注册账号
- 站点名称、首页简介(motto)、关于页正文、算法战绩账号、自动播放曲目等均可后台配置

## 🧱 技术栈

| 端 | 技术 |
|----|------|
| 前端 | Vue 3 (Composition API) · Vite · Vue Router · marked |
| 后端 | Spring Boot 3+/4 (Java 17+) · Spring Web · JDBC · SQLite |
| 部署 | Nginx 反向代理 + Systemd/后台进程（可选） |

## 📁 目录结构

```
.
├── backend/            # Spring Boot API 服务
│   ├── src/main/java/com/blog
│   │   ├── config/     # 拦截器 / 启动器 / 建库迁移 / Jackson
│   │   ├── controller/ # REST 接口
│   │   ├── model/      # 实体
│   │   ├── repository/ # JdbcTemplate 数据访问
│   │   └── util/
│   └── src/main/resources/
│       ├── schema.sql               # SQLite 建表
│       └── application.properties
└── frontend/           # Vue 3 前端
    ├── public/         # 静态资源(背景/头像/技术栈 logo)
    └── src/
        ├── api/        # 请求封装（含 token、上传）
        ├── components/ # 音乐模块 / 算法战绩 / 宠物 / 播放器
        ├── composables/# 全局播放器状态
        ├── views/      # 各页面
        └── config.js   # 站点基础配置
```

## 🚀 本地开发

要求：Node ≥ 18、JDK 17+（建议 21/24）

### 1. 启动后端（端口 8080）

```bash
cd backend
# Windows: mvnw.cmd   Linux/macOS: ./mvnw
./mvnw spring-boot:run
```

首次启动会自动在 `backend/data/blog.db` 创建 SQLite 数据库与表，并向 `music` 表写入 3 首示例曲目（便于体验播放器，可在后台删除）。

> 后台管理密码默认读取环境变量 `BLOG_ADMIN_TOKEN`，未设置时回退到默认值（本地开发可忽略；生产务必设置）。

### 2. 启动前端（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

打开 http://localhost:5173 。Vite 已配置将 `/api` 与 `/files` 代理到后端 8080。

### 3. 后台管理

浏览器访问 http://localhost:5173/admin ，输入密码进入后台，可管理文章 / 说说 / 相册 / 音乐 / 技术栈 / 网站设置。

## 📡 API 一览

| 模块 | 接口 |
|------|------|
| 鉴权 | `POST /api/auth/login`（校验 token） |
| 文章 | `GET/POST /api/posts` · `GET/DELETE /api/posts/{id}` |
| 说说 | `GET/POST /api/shuoshuo`（支持 `images[]`）· `DELETE /api/shuoshuo/{id}` |
| 相册 | `GET/POST /api/photos`（支持 `categoryId`）· `PUT /api/photos/{id}/category` · `DELETE /api/photos/{id}` |
| 相册文件夹 | `GET/POST /api/photo-categories` · `DELETE /api/photo-categories/{id}` |
| 音乐 | `GET/POST /api/music` · `DELETE /api/music/{id}` |
| 技术栈 | `GET/POST /api/tech` · `DELETE /api/tech/{id}` |
| 上传 | `POST /api/upload`（`type=music/image`，文件存 `uploads/`） |
| 站点配置 | `GET /api/site` · `PUT /api/site`（motto / 算法战绩 / 关于页正文等） |
| 留言用户 | `POST /api/user/register` · `POST /api/user/login` · `GET /api/user/me` · 站长 `GET /api/user/admin/list` `PUT /api/user/admin/{id}/profile|password` `DELETE /api/user/admin/{id}` |
| 留言板 | `GET/POST /api/messages` · `DELETE /api/messages/{id}`（站长） |
| 运行时间 | `GET /api/uptime` |

> 除 `GET` 外的写接口均在拦截器中校验请求头 `X-Admin-Token`；上传文件经 `/files/**` 静态访问。
> 数据库为 SQLite（`data/blog.db`），新增字段由启动时 `SchemaMigrator` 自动迁移，旧库无需手动处理。

## ☁️ 部署（Ubuntu + Nginx）

1. 构建前端产物：`cd frontend && npm run build` → `frontend/dist`
2. 打包后端：`cd backend && ./mvnw -DskipTests package` → `target/*.jar`
3. 服务器放置：
   - 前端 `dist` 内容 → Nginx 站点根目录（如 `/var/www/html`）
   - 后端 jar → `/opt/app/app.jar`，`uploads/` 与 `data/` 随 jar 运行目录生成
4. Nginx 配置示例：

```nginx
server {
    listen 80;
    root /var/www/html;
    index index.html;
    client_max_body_size 50m;

    location / { try_files $uri $uri/ /index.html; }
    location /api/  { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
    location /files/ { proxy_pass http://127.0.0.1:8080; }
}
```

5. 启动后端并注入管理密码环境变量：

```bash
export BLOG_ADMIN_TOKEN='你的后台密码'
java -Xmx512m -jar /opt/app/app.jar
```

> 国内云服务器绑定域名需完成 ICP 备案；备案通过前可用 `http://服务器IP` 访问。

## ⚠️ 安全提示

- `BLOG_ADMIN_TOKEN` 请务必通过**环境变量**注入，不要提交到仓库
- 写接口均需校验 token；公开站点建议保持前端只读（发布走 `/admin`）

## 📄 License

MIT License —— 供学习参考，自由使用。
