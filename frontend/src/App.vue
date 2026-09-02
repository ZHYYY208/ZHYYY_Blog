<script setup>
import VirtualPet from './components/VirtualPet.vue'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { BLOG } from './config'

// 建站使用的技术栈（非知识树）
const techStack = [
  { name: 'Vue', logo: '/logos/vue.svg', color: '#42b883' },
  { name: 'Spring Boot', logo: '/logos/spring.svg', color: '#6db33f' },
  { name: 'SQLite', logo: '/logos/sqlite.svg', color: '#2f7fc1' },
  { name: 'Nginx', logo: '/logos/nginx.svg', color: '#009639' },
]

const uptime = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 })
const fetched = ref(false)
const clock = ref('')
const route = useRoute()
const isHome = computed(() => route.path === '/')
let timer = null

function pad(n) {
  return String(n).padStart(2, '0')
}

async function loadUptime() {
  try {
    const base = await (await fetch('/api/uptime')).json()
    fetched.value = true
    const start = Date.now() - base.totalSeconds * 1000
    timer = setInterval(() => {
      const d = new Date()
      clock.value = `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
      const s = Math.floor((Date.now() - start) / 1000)
      uptime.value = {
        days: Math.floor(s / 86400),
        hours: Math.floor((s % 86400) / 3600),
        minutes: Math.floor((s % 3600) / 60),
        seconds: s % 60,
      }
    }, 1000)
  } catch {}
}

onMounted(() => {
  loadUptime()
})
onBeforeUnmount(() => clearInterval(timer))
</script>

<template>
  <div class="layout">
    <div class="bg-stage" aria-hidden="true">
      <div class="bg-image" style="background-image: url(/bg.jpg)"></div>
      <div class="bg-overlay"></div>
    </div>

    <header class="nav glass">
      <router-link to="/" class="brand">{{ BLOG.name }}</router-link>
      <nav class="links">
        <router-link to="/">首页</router-link>
        <router-link to="/posts">博客</router-link>
        <router-link to="/tech">知识树</router-link>
        <router-link to="/shuoshuo">说说</router-link>
        <router-link to="/photos">相册</router-link>
        <router-link to="/music">音乐</router-link>
        <router-link to="/projects">项目</router-link>
        <router-link to="/about">关于</router-link>
      </nav>
    </header>

    <main class="content">
      <router-view />
    </main>

    <footer class="foot">
      <!-- 底部信息栏：仅首页显示 -->
      <div v-if="isHome" class="footbar glass">
        <div class="fclock">
          <span class="flabel">北京时间</span>
          <span class="fnum">{{ clock || '--:--:--' }}</span>
        </div>
        <div class="ftech">
          <span class="flabel">技术栈</span>
          <span v-for="t in techStack" :key="t.name" class="chip" :style="{ background: t.color }">
            <img class="chip-logo" :src="t.logo" alt="" />
            <span>{{ t.name }}</span>
          </span>
        </div>
        <div class="fup" v-if="fetched">
          <span class="flabel">已运行</span>
          <span class="fnum2">
            <b>{{ uptime.days }}</b>天 <b>{{ uptime.hours }}</b>时 <b>{{ uptime.minutes }}</b>分 <b>{{ uptime.seconds }}</b>秒
          </span>
        </div>
      </div>
      <p class="cpy">© {{ new Date().getFullYear() }} {{ BLOG.name }} · Powered by Vue + Spring Boot</p>
    </footer>

    <VirtualPet />
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.bg-stage {
  position: fixed;
  inset: 0;
  z-index: -1;
  overflow: hidden;
}

.bg-image {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background: var(--overlay);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}

.nav {
  position: sticky;
  top: 8px;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 14px;
  width: calc(100% - 24px);
  max-width: 1100px;
  margin: 8px auto 0;
  padding: 8px 16px;
  border-radius: 18px;
  box-sizing: border-box;
}

.brand {
  font-weight: 800;
  font-size: 18px;
  color: var(--text-h);
  text-decoration: none;
  flex-shrink: 0;
  white-space: nowrap;
}

.links {
  display: flex;
  gap: 2px;
  overflow-x: auto;
  flex: 1;
  justify-content: flex-end;
  scrollbar-width: none;
}
.links::-webkit-scrollbar { display: none; }

.links a {
  position: relative;
  padding: 6px 12px;
  border-radius: 10px;
  text-decoration: none;
  color: var(--text);
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  transition: color 0.2s;
}

.links a:hover { color: var(--accent); }
.links a.router-link-active { color: var(--accent); }
.links a.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--accent);
}

.content {
  flex: 1;
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 18px 16px 10px;
  box-sizing: border-box;
}

.foot {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 16px 18px;
  box-sizing: border-box;
}
.footbar {
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding: 10px 20px;
  border-radius: 16px;
  font-size: 12px;
  color: var(--text);
}
.fclock, .ftech, .fup { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.flabel { color: var(--text-muted); font-size: 11px; }
.fnum {
  font-family: ui-monospace, Consolas, monospace;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 2px;
  color: var(--text-h);
}
.chip {
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 12px 4px 5px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.chip-logo {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fff;
  object-fit: contain;
  padding: 1px;
}
.fup b { color: var(--accent); font-weight: 800; }
.fnum2 { font-family: ui-monospace, Consolas, monospace; color: var(--text-h); }
.cpy {
  margin: 0;
  text-align: center;
  font-size: 12px;
  color: var(--text);
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.8);
}

/* 手机端适配 */
@media (max-width: 760px) {
  .nav {
    position: relative;
    top: 0;
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
    margin-top: 8px;
  }
  .links {
    justify-content: flex-start;
    width: 100%;
    padding-bottom: 2px;
  }
  .content { padding-top: 16px; }
}
</style>
