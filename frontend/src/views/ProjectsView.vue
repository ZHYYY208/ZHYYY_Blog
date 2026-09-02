<script setup>
import { onMounted, ref } from 'vue'
import { BLOG } from '../config'

const repos = ref([])
const error = ref('')

onMounted(async () => {
  try {
    const res = await fetch(`https://api.github.com/users/${BLOG.github}/repos?sort=updated&per_page=12`)
    if (!res.ok) throw new Error(`GitHub 返回 ${res.status}`)
    repos.value = await res.json()
  } catch (e) {
    error.value = '请先在 src/config.js 填写你的 GitHub 用户名'
  }
})
</script>

<template>
  <div>
    <h1>GitHub 项目</h1>
    <p v-if="error" class="hint">{{ error }}</p>
    <div class="grid">
      <a
        v-for="r in repos"
        :key="r.id"
        class="glass repo"
        :href="r.html_url"
        target="_blank"
        rel="noopener"
      >
        <strong>{{ r.name }}</strong>
        <p>{{ r.description || '暂无描述' }}</p>
        <span class="meta">⭐ {{ r.stargazers_count }} · {{ r.language }}</span>
      </a>
    </div>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 18px; }
.repo { display: block; text-decoration: none; padding: 20px 24px; transition: transform 0.25s; }
.repo:hover { transform: translateY(-3px); }
.repo strong { color: var(--text-h); }
.repo p { font-size: 14px; color: var(--text); opacity: 0.85; }
.meta { color: var(--text-muted); font-size: 13px; }
.hint { color: var(--text-muted); }
</style>
