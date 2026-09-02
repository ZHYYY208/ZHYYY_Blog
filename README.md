# Personal Blog

带音乐、简易宠物、相册、GitHub 项目展示与「说说」的个人博客。

- **后端**: Spring Boot 4.1 (Java 24) + SQLite
- **前端**: Vue 3 + Vite + Vue Router
- **目录**: `backend/` API 服务 · `frontend/` Web 前端

## 本地开发

### 1. 启动后端 (端口 8080)

```bash
cd backend
# Windows 用 mvnw.cmd；Linux/macOS 用 ./mvnw
mvnw.cmd spring-boot:run
```

首次启动会自动在 `backend/data/blog.db` 创建 SQLite 数据库与表。

### 2. 启动前端 (端口 5173)

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173 。Vite 已配置将 `/api` 代理到后端 8080。

## 功能 API

| 功能 | 接口 |
|------|------|
| 博客文章 | `GET/POST /api/posts`, `GET/DELETE /api/posts/{id}` |
| 说说 | `GET/POST /api/shuoshuo`, `DELETE /api/shuoshuo/{id}` |
| 相册 | `GET/POST /api/photos`, `DELETE /api/photos/{id}` |
| 音乐 | `GET/POST /api/music`, `DELETE /api/music/{id}` |
| GitHub 项目 | 前端直接调 GitHub API（在 `ProjectsView.vue` 填入用户名） |

## 配置待办

- `frontend/src/views/ProjectsView.vue`: 填写你的 GitHub 用户名
- 图片/音频上传: 后端提供 `/api/upload`（Multipart）后，照片和音乐 URL 指向 `/files/...`

## 部署 (Ubuntu + Nginx)

1. 构建: `cd frontend && npm run build`，产物在 `frontend/dist`
2. 后端打包: `cd backend && mvnw.cmd -DskipTests package`
3. 服务器上: Nginx 托管 `frontend/dist` 静态文件，`/api` 和 `/files` 反代到 `localhost:8080`

详见后续部署文档。
