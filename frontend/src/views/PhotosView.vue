<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '../api'

const photos = ref([])
const cats = ref([])
const error = ref('')
const cur = ref(0) // 0=全部

const filtered = computed(() =>
  cur.value === 0 ? photos.value : photos.value.filter((p) => p.categoryId === cur.value)
)

async function load() {
  try {
    photos.value = await api.get('/photos')
    cats.value = await api.get('/photo-categories')
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

    <div v-if="cats.length" class="tabs">
      <button class="tab" :class="{ on: cur === 0 }" @click="cur = 0">全部</button>
      <button
        v-for="c in cats"
        :key="c.id"
        class="tab"
        :class="{ on: cur === c.id }"
        @click="cur = c.id"
      >
        {{ c.name }}
      </button>
    </div>

    <div class="grid">
      <figure v-for="p in filtered" :key="p.id" class="glass photo">
        <img :src="p.url" :alt="p.title" loading="lazy" />
        <figcaption>
          {{ p.title }}
          <span v-if="p.categoryName" class="cat">{{ p.categoryName }}</span>
        </figcaption>
      </figure>
    </div>
    <p v-if="!filtered.length && !error" class="hint">这个分类还没有照片</p>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.tabs { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px; }
.tab {
  border: 1px solid var(--glass-border);
  background: rgba(255, 255, 255, 0.5);
  color: var(--text);
  border-radius: 999px;
  padding: 6px 18px;
  cursor: pointer;
  font-weight: 600;
}
.tab.on { background: var(--accent); color: #fff; border-color: transparent; }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}
.photo { margin: 0; overflow: hidden; border-radius: 22px; transition: transform 0.25s; }
.photo:hover { transform: translateY(-3px); }
.photo img { width: 100%; height: 200px; object-fit: cover; display: block; }
.photo figcaption {
  padding: 12px 16px;
  font-size: 13px;
  color: var(--text-h);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.cat {
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 12px;
  border-radius: 999px;
  padding: 2px 12px;
  flex-shrink: 0;
}
.hint { color: var(--text-muted); }
</style>
