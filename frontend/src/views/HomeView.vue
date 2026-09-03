<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'
import { BLOG } from '../config'
import CodeStats from '../components/CodeStats.vue'
import MusicModule from '../components/MusicModule.vue'
import HomeMessages from '../components/HomeMessages.vue'

const counts = ref({ posts: 0, shuoshuo: 0, photos: 0, music: 0 })
const latestPosts = ref([])
const latestShuo = ref([])
const latestPhotos = ref([])
const repos = ref([])
const motto = ref(BLOG.motto)

const socials = [
  { name: 'GitHub', href: `https://github.com/${BLOG.github}`, icon: 'github' },
  { name: 'Gmail', href: 'mailto:zhhhy208@gmail.com', icon: 'google' },
  { name: 'Email', href: 'mailto:zhhhy208@foxmail.com', icon: 'mail' },
]

async function loadContent() {
  const [posts, shuoshuo, photos, music] = await Promise.allSettled([
    api.get('/posts'),
    api.get('/shuoshuo'),
    api.get('/photos'),
    api.get('/music'),
  ])
  latestPosts.value = (posts.value ?? []).slice(0, 3)
  latestShuo.value = (shuoshuo.value ?? []).slice(0, 3)
  latestPhotos.value = (photos.value ?? []).slice(0, 4)
  counts.value = {
    posts: posts.value?.length ?? 0,
    shuoshuo: shuoshuo.value?.length ?? 0,
    photos: photos.value?.length ?? 0,
    music: music.value?.length ?? 0,
  }
}

async function loadRepos() {
  try {
    const res = await fetch(`https://api.github.com/users/${BLOG.github}/repos?sort=updated&per_page=5`)
    if (!res.ok) throw new Error()
    repos.value = (await res.json()).slice(0, 4)
  } catch {
    repos.value = []
  }
}

async function loadMotto() {
  try {
    const cfg = await api.get('/site')
    if (cfg.motto) motto.value = cfg.motto
  } catch {}
}

onMounted(() => {
  loadMotto()
  loadContent()
  loadRepos()
})

const ic = {
  github: 'M12 2C6.48 2 2 6.48 2 12c0 4.42 2.87 8.17 6.84 9.5.5.09.66-.22.66-.48v-1.7c-2.78.6-3.37-1.34-3.37-1.34-.45-1.16-1.11-1.47-1.11-1.47-.91-.62.07-.6.07-.6 1 .07 1.53 1.03 1.53 1.03.9 1.52 2.34 1.08 2.91.83.09-.65.35-1.09.63-1.34-2.22-.25-4.55-1.11-4.55-4.94 0-1.09.39-1.98 1.03-2.68-.1-.25-.45-1.27.1-2.65 0 0 .84-.27 2.75 1.02a9.56 9.56 0 0 1 5 0c1.91-1.3 2.75-1.02 2.75-1.02.55 1.38.2 2.4.1 2.65.64.7 1.03 1.59 1.03 2.68 0 3.84-2.34 4.68-4.57 4.94.36.31.68.92.68 1.85V21c0 .27.16.58.67.48A10 10 0 0 0 22 12c0-5.52-4.48-10-10-10z',
  mail: 'M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z',
}
</script>

<template>
  <div class="dash">
    <!-- 顶部三卡：个人信息 / 音乐 / 算法战绩 -->
    <div class="top-row">
      <section class="glass profile">
        <img class="avatar" src="/avatar.jpg" alt="avatar" />
        <div class="p-name">{{ BLOG.name }}</div>
        <p class="motto">{{ motto }}</p>
        <div class="socials">
          <a v-for="s in socials" :key="s.name" :href="s.href" target="_blank" rel="noopener" :title="s.name">
            <svg v-if="s.icon === 'google'" viewBox="0 0 24 24" width="15" height="15">
              <path fill="#4285F4" d="M23.5 12.27c0-.79-.07-1.55-.2-2.28H12v4.32h6.46a5.5 5.5 0 0 1-2.39 3.61v3h3.87c2.26-2.09 3.56-5.17 3.56-8.65z"/>
              <path fill="#34A853" d="M12 24c3.24 0 5.96-1.08 7.94-2.91l-3.87-3c-1.08.72-2.45 1.15-4.07 1.15-3.13 0-5.78-2.11-6.73-4.96H1.29v3.1A12 12 0 0 0 12 24z"/>
              <path fill="#FBBC05" d="M5.27 14.28a7.2 7.2 0 0 1 0-4.56v-3.1H1.29a12 12 0 0 0 0 10.76l3.98-3.1z"/>
              <path fill="#EA4335" d="M12 4.76c1.76 0 3.35.61 4.6 1.8l3.45-3.45A11.99 11.99 0 0 0 1.29 6.62l3.98 3.1C6.22 6.87 8.87 4.76 12 4.76z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="15" height="15" fill="currentColor"><path :d="ic[s.icon]" /></svg>
          </a>
        </div>
        <div class="stats">
          <router-link to="/posts" class="stat"><b>{{ counts.posts }}</b><span>文章</span></router-link>
          <router-link to="/shuoshuo" class="stat"><b>{{ counts.shuoshuo }}</b><span>说说</span></router-link>
          <router-link to="/photos" class="stat"><b>{{ counts.photos }}</b><span>照片</span></router-link>
          <router-link to="/music" class="stat"><b>{{ counts.music }}</b><span>歌曲</span></router-link>
        </div>
      </section>

      <MusicModule />

      <div class="code-wrap">
        <CodeStats />
      </div>
    </div>

    <!-- 功能面板 -->
    <div class="grid">
      <section class="glass panel">
        <router-link to="/posts" class="panel-head">
          <h2>博客</h2>
          <span class="more">全部 →</span>
        </router-link>
        <router-link v-for="p in latestPosts" :key="p.id" :to="`/post/${p.id}`" class="entry">
          <span class="dot"></span>
          <div>
            <b>{{ p.title }}</b>
            <small>{{ p.createdAt }}</small>
          </div>
        </router-link>
        <p v-if="!latestPosts.length" class="empty">还没有文章</p>
      </section>

      <section class="glass panel">
        <router-link to="/shuoshuo" class="panel-head">
          <h2>说说</h2>
          <span class="more">全部 →</span>
        </router-link>
        <div v-for="s in latestShuo" :key="s.id" class="entry">
          <span class="dot"></span>
          <div>
            <b class="shuo-text">{{ s.content }}</b>
            <small>{{ s.createdAt }}</small>
          </div>
        </div>
        <p v-if="!latestShuo.length" class="empty">还没有说说</p>
      </section>

      <section class="glass panel">
        <router-link to="/projects" class="panel-head">
          <h2>GitHub</h2>
          <span class="more">全部 →</span>
        </router-link>
        <a v-for="r in repos" :key="r.id" :href="r.html_url" target="_blank" rel="noopener" class="entry">
          <span class="dot"></span>
          <div>
            <b>{{ r.name }}</b>
            <small>⭐ {{ r.stargazers_count }} · {{ r.language }}</small>
          </div>
        </a>
        <p v-if="!repos.length" class="empty">填好 GitHub 用户名后展示</p>
      </section>
    </div>

    <!-- 留言板 + 相册 并排 -->
    <div class="media-row">
      <HomeMessages class="media-cell" />
      <section class="glass panel media-cell">
        <router-link to="/photos" class="panel-head">
          <h2>相册</h2>
          <span class="more">进入 →</span>
        </router-link>
        <div class="ph-grid">
          <img
            v-for="ph in latestPhotos"
            :key="ph.id"
            class="ph"
            :src="ph.url"
            :alt="ph.title"
            loading="lazy"
          />
          <div v-if="!latestPhotos.length" class="ph-empty">还没有照片</div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.dash { display: flex; flex-direction: column; gap: 8px; }
.empty { color: var(--text-muted); font-size: 13px; margin: 4px 0; }

/* 顶部三卡等高 */
.top-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 8px;
  align-items: stretch;
}
.top-row > * { min-width: 0; }
.code-wrap { display: flex; }
.code-wrap :deep(.stats-card) { width: 100%; }

/* 个人信息：居中竖条 */
.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 16px;
  text-align: center;
}
.avatar {
  width: 76px;
  height: 76px;
  border-radius: 22px;
  object-fit: cover;
  border: 1px solid var(--glass-border);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.3);
}
.p-name {
  font-weight: 800;
  color: var(--text-h);
  font-size: 18px;
}
.motto { margin: 0; color: var(--text); font-size: 12px; line-height: 1.5; max-width: 90%; }
.socials { display: flex; gap: 6px; }
.socials a {
  width: 26px; height: 26px;
  display: grid; place-items: center;
  border-radius: 8px;
  color: var(--text);
  border: 1px solid var(--glass-border);
  transition: all 0.25s;
}
.socials a:hover { color: #fff; background: var(--accent); }
.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 4px;
  width: 100%;
  border-top: 1px solid var(--glass-border);
  margin-top: 4px;
  padding-top: 10px;
}
.stat { display: flex; flex-direction: column; align-items: center; gap: 2px; text-decoration: none; color: var(--text); }
.stat b { font-size: 15px; color: var(--text-h); }
.stat span { font-size: 9px; color: var(--text-muted); }
.stat:hover b { color: var(--accent); }

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.panel { padding: 12px 14px; display: flex; flex-direction: column; gap: 6px; transition: transform 0.25s; }
.panel:hover { transform: translateY(-3px); }
.panel.wide { grid-column: 1 / -1; }
.panel-head {
  display: flex; align-items: center; justify-content: space-between;
  text-decoration: none;
}
.panel-head h2 { margin: 0; font-size: 15px; color: var(--text-h); }
.more { font-size: 11px; color: var(--text-muted); }
.panel-head:hover .more { color: var(--accent); }
.panel-head:hover h2 { color: var(--accent); }

.entry {
  display: flex; align-items: center; gap: 8px;
  text-decoration: none; color: var(--text);
  padding: 3px 6px; border-radius: 8px;
  transition: background 0.2s;
}
.entry:hover { background: var(--accent-bg); }
.entry .dot { width: 6px; height: 6px; border-radius: 50%; background: var(--accent); flex-shrink: 0; margin-top: 4px; }
.entry div { display: flex; flex-direction: column; gap: 1px; min-width: 0; }
.entry b { color: var(--text-h); font-size: 13px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.shuo-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.entry small { color: var(--text-muted); font-size: 10px; }

.ph-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; min-height: 90px; }
.ph { width: 100%; height: 110px; object-fit: cover; border-radius: 10px; border: 1px solid var(--glass-border); }
.ph-empty { grid-column: 1 / -1; color: var(--text-muted); font-size: 13px; display: grid; place-items: center; }

/* 留言 + 相册 并排 */
.media-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  align-items: stretch;
}
.media-cell { margin: 0; min-width: 0; height: 100%; box-sizing: border-box; }
.media-row :deep(.msg-mod) { height: 100%; }
.media-row .ph-grid { grid-template-columns: repeat(2, 1fr); min-height: 0; }
.media-row .ph { height: 120px; }

@media (max-width: 900px) {
  .top-row { grid-template-columns: 1fr; }
  .grid { grid-template-columns: 1fr; }
  .panel.wide { grid-column: auto; }
  .profile { flex-direction: column; align-items: flex-start; }
  .media-row { grid-template-columns: 1fr; }
  .ph-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
