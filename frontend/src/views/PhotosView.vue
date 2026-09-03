<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '../api'

const photos = ref([])
const cats = ref([])
const error = ref('')

// view: 'home' | { type: 'all' } | { type: 'cat', cat }
const view = ref('home')

const viewer = ref(null)

const catCount = computed(() => {
  const m = {}
  for (const p of photos.value) {
    const k = p.categoryId == null ? 'none' : String(p.categoryId)
    m[k] = (m[k] || 0) + 1
  }
  return m
})

function catPhotos(catId) {
  return catId == null
    ? photos.value.filter((p) => p.categoryId == null)
    : photos.value.filter((p) => p.categoryId === catId)
}

const byGroup = computed(() => {
  const groups = []
  for (const c of cats.value) {
    const list = catPhotos(c.id)
    if (list.length) groups.push({ title: c.name, list })
  }
  const none = catPhotos(null)
  if (none.length) groups.push({ title: '未分类', list: none })
  return groups
})

const allList = computed(() => {
  const list = []
  for (const g of byGroup.value) list.push(...g.list)
  return list
})

function openCat(cat) {
  view.value = { type: 'cat', cat }
  window.scrollTo({ top: 0 })
}

async function load() {
  try {
    const [p, c] = await Promise.all([api.get('/photos'), api.get('/photo-categories')])
    photos.value = p
    cats.value = c
  } catch (e) {
    error.value = '加载失败：' + e.message
  }
}

onMounted(load)
</script>

<template>
  <div>
    <h1>相册</h1>
    <p v-if="error" class="hint">{{ error }}</p>

    <!-- 文件夹首页 -->
    <template v-if="view === 'home'">
      <div class="cat-grid">
        <button v-if="allList.length" class="cat-card all" @click="view = { type: 'all' }">
          <span class="cat-name">全部</span>
          <span class="cat-count">{{ allList.length }} 张</span>
        </button>
        <button v-for="c in cats" :key="c.id" class="cat-card" @click="openCat(c)">
          <span class="cat-name">{{ c.name }}</span>
          <span class="cat-count">{{ catCount[String(c.id)] || 0 }} 张</span>
        </button>
      </div>
      <p v-if="!allList.length && !error" class="hint">还没有照片</p>
    </template>

    <!-- 全部（按文件夹分组） -->
    <template v-else-if="view.type === 'all'">
      <button class="back" @click="view = 'home'">← 文件夹</button>
      <section v-for="g in byGroup" :key="g.title" class="group">
        <h2>{{ g.title }} <span class="gcount">{{ g.list.length }}</span></h2>
        <div class="grid">
          <figure v-for="p in g.list" :key="p.id" class="glass photo" @click="viewer = p.url">
            <img :src="p.url" :alt="p.title" loading="lazy" />
            <figcaption>{{ p.title }}</figcaption>
          </figure>
        </div>
      </section>
    </template>

    <!-- 单文件夹 -->
    <template v-else>
      <button class="back" @click="view = 'home'">← 文件夹</button>
      <h2 class="fname">{{ view.cat.name }}</h2>
      <div class="grid">
        <figure
          v-for="p in catPhotos(view.cat.id)"
          :key="p.id"
          class="glass photo"
          @click="viewer = p.url"
        >
          <img :src="p.url" :alt="p.title" loading="lazy" />
          <figcaption>{{ p.title }}</figcaption>
        </figure>
        <p v-if="!catPhotos(view.cat.id).length" class="hint">这个文件夹还没有照片</p>
      </div>
    </template>

    <!-- 灯箱 -->
    <div v-if="viewer" class="mask" @click="viewer = null">
      <img :src="viewer" alt="" />
    </div>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.hint { color: var(--text-muted); }

/* 文件夹网格 */
.cat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}
.cat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 26px 16px;
  border-radius: 22px;
  cursor: pointer;
  background: linear-gradient(150deg, var(--accent-bg), transparent);
  border: 1px solid var(--glass-border);
  box-shadow: var(--glass-shadow);
  color: var(--text-h);
  transition: transform 0.2s;
}
.cat-card:hover { transform: translateY(-4px); }
.cat-card.all { background: linear-gradient(150deg, rgba(236,72,153,.18), transparent); }
.cat-name { font-size: 18px; font-weight: 800; }
.cat-count { font-size: 12px; color: var(--text-muted); }

.back {
  border: 1px solid var(--glass-border);
  background: rgba(255, 255, 255, 0.4);
  color: var(--text);
  border-radius: 999px;
  padding: 6px 18px;
  cursor: pointer;
  margin-bottom: 16px;
}

.group { margin-bottom: 26px; }
.group h2 { color: var(--text-h); font-size: 20px; display: flex; align-items: center; gap: 8px; }
.gcount {
  font-size: 12px;
  color: var(--accent);
  background: var(--accent-bg);
  border-radius: 999px;
  padding: 1px 10px;
}
.fname { color: var(--text-h); margin: 0 0 16px; font-size: 22px; }

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}
.photo { margin: 0; overflow: hidden; border-radius: 16px; cursor: zoom-in; transition: transform 0.2s; }
.photo:hover { transform: translateY(-3px); }
.photo img { width: 100%; height: 180px; object-fit: cover; display: block; }
.photo figcaption { padding: 10px 14px; font-size: 13px; color: var(--text); }

.mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.8);
  display: grid;
  place-items: center;
  cursor: zoom-out;
}
.mask img { max-width: 92vw; max-height: 92vh; border-radius: 12px; }
</style>
