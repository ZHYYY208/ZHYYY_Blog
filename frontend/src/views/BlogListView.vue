<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const posts = ref([])
const error = ref('')

onMounted(async () => {
  try {
    posts.value = await api.get('/posts')
  } catch (e) {
    error.value = '后端未连接：' + e.message
  }
})
</script>

<template>
  <div>
    <div class="head">
      <h1>博客文章</h1>
      <span class="count" v-if="posts.length">{{ posts.length }} 篇</span>
    </div>
    <p v-if="error" class="hint">{{ error }}</p>
    <div class="scroll">
      <article v-for="p in posts" :key="p.id" class="glass item">
        <router-link :to="`/post/${p.id}`" class="title">
          <h2>{{ p.title }}</h2>
        </router-link>
        <p class="meta">{{ p.createdAt }}</p>
        <p class="summary">{{ p.summary }}</p>
      </article>
      <p v-if="!posts.length && !error" class="hint">还没有文章</p>
    </div>
  </div>
</template>

<style scoped>
.head { display: flex; align-items: baseline; gap: 12px; margin: 8px 0 16px; }
.head h1 { margin: 0; color: var(--text-h); }
.count { color: var(--text-muted); font-size: 13px; }

.scroll {
  max-height: 64vh;
  overflow-y: auto;
  overscroll-behavior: contain;
  padding-right: 4px;
}
.scroll::-webkit-scrollbar { width: 6px; }
.scroll::-webkit-scrollbar-thumb { background: var(--accent-bg); border-radius: 999px; }
.scroll::-webkit-scrollbar-thumb:hover { background: var(--accent); }

.item { padding: 20px 24px; margin-bottom: 14px; transition: transform 0.25s, box-shadow 0.25s; }
.item:hover { transform: translateY(-2px) scale(1.005); }
.title { text-decoration: none; }
.title h2 { color: var(--text-h); margin: 0 0 6px; font-size: 21px; }
.item:hover .title h2 { color: var(--accent); }
.meta { color: var(--text-muted); font-size: 13px; margin: 0 0 10px; }
.summary { margin: 0; color: var(--text); opacity: 0.85; }
.hint { color: var(--text-muted); }

@media (max-width: 640px) {
  .scroll { max-height: 58vh; }
}
</style>
