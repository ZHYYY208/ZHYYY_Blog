<script setup>
import { onMounted, ref } from 'vue'
import { api } from '../api'

const items = ref([])
const error = ref('')
const viewer = ref(null) // 当前查看的大图 url

async function load() {
  try {
    items.value = await api.get('/shuoshuo')
  } catch (e) {
    error.value = '加载失败：' + e.message
  }
}

onMounted(load)
</script>

<template>
  <div>
    <h1>说说</h1>
    <p v-if="error" class="hint">{{ error }}</p>

    <div v-for="it in items" :key="it.id" class="glass shuo">
      <p>{{ it.content }}</p>
      <div v-if="it.images && it.images.length" class="imgs">
        <img
          v-for="(img, i) in it.images"
          :key="i"
          :src="img"
          alt=""
          loading="lazy"
          @click="viewer = img"
        />
      </div>
      <span class="meta">{{ it.createdAt }}</span>
    </div>
    <p v-if="!items.length && !error" class="hint">还没有说说</p>

    <!-- 灯箱 -->
    <div v-if="viewer" class="mask" @click="viewer = null">
      <img :src="viewer" alt="" />
    </div>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.shuo { padding: 20px 24px; margin-bottom: 14px; }
.meta { color: var(--text-muted); font-size: 12px; }
.shuo p { margin: 0 0 10px; white-space: pre-wrap; color: var(--text-h); }
.imgs { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 10px; }
.imgs img {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 14px;
  cursor: zoom-in;
  transition: transform 0.2s;
  border: 1px solid var(--glass-border);
}
.imgs img:hover { transform: scale(1.03); }
.hint { color: var(--text-muted); }

.mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.8);
  display: grid;
  place-items: center;
  cursor: zoom-out;
}
.mask img {
  max-width: 92vw;
  max-height: 92vh;
  border-radius: 12px;
}
</style>
