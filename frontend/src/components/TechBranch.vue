<script setup>
defineProps({
  nodes: { type: Array, default: () => [] },
})

const emit = defineEmits(['add', 'del'])

function depthStyle(d) {
  return { paddingLeft: (d * 18 + 6) + 'px' }
}
</script>

<template>
  <div v-for="n in nodes" :key="n.id" class="wrap">
    <div class="node" :style="depthStyle(n.depth)">
      <span class="bubble" :style="{ background: n.color || '#94a3b8' }"></span>
      <span class="name" :class="{ root: n.depth === 0 }">{{ n.name }}</span>
      <span class="ops">
        <button class="add-btn" title="添加子节点" @click="emit('add', n)">＋ 子</button>
        <button class="del-btn" @click="emit('del', n)">删除</button>
      </span>
    </div>
    <TechBranch v-if="n.children && n.children.length" :nodes="n.children" @add="emit('add', $event)" @del="emit('del', $event)" />
  </div>
</template>

<script>
export default { name: 'TechBranch' }
</script>

<style scoped>
.wrap { display: flex; flex-direction: column; }
.node {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 10px;
}
.node:hover { background: var(--accent-bg); }
.bubble { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
.name { color: var(--text-h); font-size: 14px; font-weight: 500; }
.name.root { font-weight: 800; font-size: 15px; }
.ops { margin-left: auto; display: flex; gap: 6px; opacity: 0.35; transition: opacity 0.2s; }
.node:hover .ops { opacity: 1; }
.add-btn, .del-btn {
  border: none;
  border-radius: 999px;
  padding: 2px 10px;
  font-size: 12px;
  cursor: pointer;
}
.add-btn { background: var(--accent-bg); color: var(--accent); }
.del-btn { background: rgba(239, 68, 68, 0.12); color: #ef4444; }
</style>
