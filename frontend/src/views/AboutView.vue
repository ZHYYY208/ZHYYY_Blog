<script setup>
import { computed, onMounted, ref } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { api } from '../api'
import { BLOG } from '../config'

const motto = ref(BLOG.motto)
const about = ref('')
const posts = ref([])
const shuoshuo = ref([])

const aboutHtml = computed(() =>
  about.value ? DOMPurify.sanitize(marked.parse(about.value)) : ''
)

async function load() {
  try {
    const cfg = await api.get('/site')
    if (cfg.motto) motto.value = cfg.motto
    if (cfg.about) about.value = cfg.about
  } catch {}
  try { posts.value = (await api.get('/posts')).slice(0, 5) } catch {}
  try { shuoshuo.value = (await api.get('/shuoshuo')).slice(0, 5) } catch {}
}

onMounted(load)

const socials = [
  { name: 'GitHub', href: `https://github.com/${BLOG.github}`, icon: 'github' },
  { name: 'Gmail', href: 'mailto:zhhhy208@gmail.com', icon: 'google' },
  { name: 'Email', href: 'mailto:zhhhy208@foxmail.com', icon: 'mail' },
]

const ic = {
  github: 'M12 2C6.48 2 2 6.48 2 12c0 4.42 2.87 8.17 6.84 9.5.5.09.66-.22.66-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.45-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.94 0-1.09.39-1.98 1.03-2.68-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.56 9.56 0 0 1 5 0c1.91-1.3 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.59 1.03 2.68 0 3.84-2.34 4.68-4.57 4.94.36.31.68.92.68 1.85V21c0 .27.16.58.67.48A10 10 0 0 0 22 12c0-5.52-4.48-10-10-10z',
  mail: 'M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z',
}
</script>

<template>
  <div>
    <h1>关于</h1>

    <section class="glass about">
      <div class="aside">
        <img class="avatar" src="/avatar.jpg" alt="avatar" />
        <div class="socials">
          <a v-for="s in socials" :key="s.name" :href="s.href" target="_blank" rel="noopener" :title="s.name">
            <svg v-if="s.icon === 'google'" viewBox="0 0 24 24" width="16" height="16">
              <path fill="#4285F4" d="M23.5 12.27c0-.79-.07-1.55-.2-2.28H12v4.32h6.46a5.5 5.5 0 0 1-2.39 3.61v3h3.87c2.26-2.09 3.56-5.17 3.56-8.65z"/>
              <path fill="#34A853" d="M12 24c3.24 0 5.96-1.08 7.94-2.91l-3.87-3c-1.08.72-2.45 1.15-4.07 1.15-3.13 0-5.78-2.11-6.73-4.96H1.29v3.1A12 12 0 0 0 12 24z"/>
              <path fill="#FBBC05" d="M5.27 14.28a7.2 7.2 0 0 1 0-4.56v-3.1H1.29a12 12 0 0 0 0 10.76l3.98-3.1z"/>
              <path fill="#EA4335" d="M12 4.76c1.76 0 3.35.61 4.6 1.8l3.45-3.45A11.99 11.99 0 0 0 1.29 6.62l3.98 3.1C6.22 6.87 8.87 4.76 12 4.76z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path :d="ic[s.icon]" /></svg>
          </a>
        </div>
      </div>

      <div class="main">
        <h2 class="name">{{ BLOG.name }}</h2>
        <p class="motto">{{ motto }}</p>
        <div v-if="aboutHtml" class="prose" v-html="aboutHtml"></div>
      </div>
    </section>

    <section class="glass card">
      <h3>最近动态</h3>
      <div class="act">
        <router-link v-for="p in posts" :key="p.id" :to="`/post/${p.id}`" class="a">
          <span class="tag">文章</span>
          <span class="tx">{{ p.title }}</span>
          <span class="dt">{{ p.createdAt }}</span>
        </router-link>
        <router-link v-for="s in shuoshuo" :key="s.id" to="/shuoshuo" class="a">
          <span class="tag shuo">说说</span>
          <span class="tx">{{ s.content }}</span>
          <span class="dt">{{ s.createdAt }}</span>
        </router-link>
        <p v-if="!posts.length && !shuoshuo.length" class="empty">还没有动态</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.about {
  display: flex;
  gap: 34px;
  padding: 32px 34px;
  margin-bottom: 18px;
}
.aside {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}
.avatar {
  width: 120px;
  height: 120px;
  border-radius: 30px;
  object-fit: cover;
  border: 1px solid var(--glass-border);
  box-shadow: 0 10px 26px rgba(99, 102, 241, 0.32);
}
.socials { display: flex; gap: 8px; }
.socials a {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 11px;
  color: var(--text);
  border: 1px solid var(--glass-border);
  transition: all 0.25s;
}
.socials a:hover { color: #fff; background: var(--accent); border-color: transparent; }

.main { flex: 1; min-width: 0; }
.name { margin: 0 0 10px; color: var(--text-h); font-size: 30px; }
.motto { margin: 0 0 6px; color: var(--accent); font-weight: 600; font-size: 15px; }

.prose { line-height: 1.9; color: var(--text); margin-top: 12px; border-top: 1px solid var(--glass-border); padding-top: 16px; }
.prose :deep(h1), .prose :deep(h2), .prose :deep(h3) { color: var(--text-h); }
.prose :deep(a) { color: var(--accent); }
.prose :deep(img) { max-width: 100%; border-radius: 12px; }
.prose :deep(pre) { background: rgba(15,23,42,.9); color: #cbd5e1; padding: 14px; border-radius: 12px; overflow-x: auto; }
.prose :deep(blockquote) { border-left: 4px solid var(--accent); margin: 0; padding-left: 14px; color: var(--text-muted); }
.prose :deep(hr) { border: none; border-top: 1px solid var(--glass-border); }
.prose :deep(ul), .prose :deep(ol) { padding-left: 1.2em; }

.card { padding: 26px 30px; margin-bottom: 18px; }
.card h3 { margin: 0 0 14px; color: var(--text-h); }

.act { display: flex; flex-direction: column; }
.a {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 8px;
  border-radius: 10px;
  text-decoration: none;
  border-bottom: 1px dashed var(--glass-border);
}
.a:hover { background: var(--accent-bg); }
.tag {
  flex-shrink: 0;
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 11px;
  padding: 2px 12px;
  border-radius: 999px;
}
.tag.shuo { background: var(--accent-bg); color: var(--accent); }
.tx { flex: 1; color: var(--text-h); font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.dt { color: var(--text-muted); font-size: 12px; flex-shrink: 0; }
.empty { color: var(--text-muted); }

@media (max-width: 700px) {
  .about { flex-direction: column; align-items: center; text-align: center; padding: 24px 20px; }
}
</style>
