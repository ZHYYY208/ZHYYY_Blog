import { createRouter, createWebHistory } from 'vue-router'
import { BLOG } from '../config'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue'), meta: { title: '首页' } },
  { path: '/posts', name: 'posts', component: () => import('../views/BlogListView.vue'), meta: { title: '博客' } },
  { path: '/shuoshuo', name: 'shuoshuo', component: () => import('../views/ShuoShuoView.vue'), meta: { title: '说说' } },
  { path: '/photos', name: 'photos', component: () => import('../views/PhotosView.vue'), meta: { title: '相册' } },
  { path: '/music', name: 'music', component: () => import('../views/MusicView.vue'), meta: { title: '音乐' } },
  { path: '/projects', name: 'projects', component: () => import('../views/ProjectsView.vue'), meta: { title: 'GitHub' } },
  { path: '/tech', name: 'tech', component: () => import('../views/TechView.vue'), meta: { title: '知识树' } },
  { path: '/post/:id', name: 'post', component: () => import('../views/PostView.vue'), meta: { title: '文章' } },
  { path: '/admin', name: 'admin', component: () => import('../views/AdminView.vue'), meta: { title: '管理' } },
  { path: '/about', name: 'about', component: () => import('../views/AboutView.vue'), meta: { title: '关于' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.afterEach(() => {
  document.title = BLOG.name
})

export default router
