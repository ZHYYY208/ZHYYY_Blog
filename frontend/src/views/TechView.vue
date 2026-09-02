<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '../api'

const nodes = ref([])
const error = ref('')
const openRoots = ref(new Set())

const PALETTE = ['#6366f1', '#a855f7', '#0ea5e9', '#10b981', '#f59e0b', '#ef4444', '#ec4899']

const tree = computed(() => {
  const byParent = {}
  for (const n of nodes.value) {
    const key = n.parentId || 0
    if (!byParent[key]) byParent[key] = []
    byParent[key].push(n)
  }
  const colorOf = {}
  let di = 0
  const build = (pid, depth) => {
    const kids = byParent[pid] || []
    return kids.map((k) => {
      if (depth === 0) colorOf[k.id] = PALETTE[di++ % PALETTE.length]
      else colorOf[k.id] = colorOf[pid]
      return { ...k, color: colorOf[k.id], children: build(k.id, depth + 1) }
    })
  }
  return build(0, 0)
})

const empty = computed(() => nodes.value.length === 0)

function isOpen(root) {
  return openRoots.value.has(root.id)
}

function toggle(root) {
  const next = new Set(openRoots.value)
  if (next.has(root.id)) next.delete(root.id)
  else next.add(root.id)
  openRoots.value = next
}

const total = computed(() => nodes.value.length)

onMounted(async () => {
  try {
    nodes.value = await api.get('/tech')
  } catch (e) {
    error.value = '加载失败：' + e.message
  }
})
</script>

<template>
  <div>
    <h1>知识树</h1>
    <p class="sub">共 {{ total }} 个节点 · 点击大类可展开/收起</p>
    <p v-if="error" class="hint">{{ error }}</p>

    <div v-if="empty && !error" class="hint">还没有节点，主人正在学习中…</div>

    <div class="tree">
      <div v-for="root in tree" :key="root.id" class="glass branch">
        <div class="rhead" @click="toggle(root)" role="button">
          <span class="rname" :style="{ background: root.color }">{{ root.name }}</span>
          <span v-if="root.children.length" class="rcount">{{ root.children.length }} 项</span>
          <span class="arrow" :class="{ open: isOpen(root) }">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M7 10l5 5 5-5z" /></svg>
          </span>
        </div>

        <div v-if="isOpen(root) && root.children.length" class="kids">
          <div v-for="kid in root.children" :key="kid.id" class="chip" :style="{ borderColor: kid.color }">
            <span class="chip-name">{{ kid.name }}</span>
            <div v-if="kid.children.length" class="kids inline">
              <span v-for="grand in kid.children" :key="grand.id" class="leaf" :style="{ background: kid.color }">{{ grand.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 6px; color: var(--text-h); }
.sub { color: var(--text-muted); font-size: 14px; margin: 0 0 24px; }
.hint { color: var(--text-muted); }
.tree { display: flex; flex-direction: column; gap: 16px; }

.branch { padding: 16px 20px; }
.rhead {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  user-select: none;
}
.rhead:hover .rname { filter: brightness(1.1); }
.rname {
  color: #fff;
  padding: 7px 20px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 16px;
  transition: filter 0.2s;
}
.rcount {
  font-size: 12px;
  color: var(--text-muted);
  background: var(--accent-bg);
  padding: 3px 12px;
  border-radius: 999px;
}
.arrow {
  margin-left: auto;
  display: grid;
  place-items: center;
  color: var(--text-muted);
  transition: transform 0.25s;
}
.arrow.open { transform: rotate(180deg); color: var(--accent); }

.kids {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
  animation: fadeIn 0.25s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
.chip {
  border: 1.5px solid;
  border-radius: 12px;
  padding: 8px 14px;
  color: var(--text-h);
  background: rgba(255, 255, 255, 0.4);
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}
.chip-name { font-size: 14px; font-weight: 600; }
.chip .kids.inline { flex-wrap: wrap; gap: 6px; margin-top: 0; }
.leaf {
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 999px;
}
@media (max-width: 640px) {
  .tree { gap: 12px; }
  .branch { padding: 12px 14px; }
}
</style>
