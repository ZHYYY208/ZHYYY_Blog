<script setup>
import VirtualPet from './components/VirtualPet.vue'
import MiniPlayer from './components/MiniPlayer.vue'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { BLOG } from './config'

const uptime = ref({ days: 0, hours: 0, minutes: 0, seconds: 0 })
const fetched = ref(false)
let timer = null

async function loadUptime() {
  try {
    const base = await (await fetch('/api/uptime')).json()
    fetched.value = true
    const start = Date.now() - base.totalSeconds * 1000
    timer = setInterval(() => {
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

onMounted(loadUptime)
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
      </nav>
    </header>

    <main class="content">
      <router-view />
    </main>

    <footer class="footer">
      <p class="cpy">© {{ new Date().getFullYear() }} {{ BLOG.name }} · Powered by Vue + Spring Boot</p>
      <p v-if="fetched" class="up">
        已运行 <b>{{ uptime.days }}</b> 天 <b>{{ uptime.hours }}</b> 时 <b>{{ uptime.minutes }}</b> 分 <b>{{ uptime.seconds }}</b> 秒
      </p>
    </footer>

    <MiniPlayer />
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
  max-width: 1200px;
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
  max-width: 900px;
  margin: 0 auto;
  padding: 28px 16px 60px;
  box-sizing: border-box;
}

.footer {
  text-align: center;
  padding: 18px 16px 22px;
  font-size: 12px;
  color: var(--text);
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.8);
}
.footer .cpy { margin: 0 0 6px; }
.footer .up { margin: 0; letter-spacing: 0.5px; }
.footer b { color: var(--accent); }

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
