<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const post = ref(null)
const error = ref('')

const html = computed(() =>
  post.value ? DOMPurify.sanitize(marked.parse(post.value.content)) : ''
)

onMounted(async () => {
  try {
    post.value = await api.get(`/posts/${route.params.id}`)
  } catch (e) {
    error.value = '文章不存在或后端未连接'
  }
})
</script>

<template>
  <div>
    <p v-if="error" class="hint">{{ error }}</p>
    <template v-else-if="post">
      <div class="glass header">
        <h1>{{ post.title }}</h1>
        <p class="meta">{{ post.createdAt }}</p>
      </div>
      <div class="glass body" v-html="html"></div>
    </template>
  </div>
</template>

<style scoped>
.header { padding: 32px 36px; margin-bottom: 20px; }
.header h1 { margin: 0 0 8px; color: var(--text-h); }
.meta { color: var(--text-muted); font-size: 13px; margin: 0; }
.hint { color: var(--text-muted); }
.body { padding: 36px; line-height: 1.8; color: var(--text); }
.body :deep(h1), .body :deep(h2), .body :deep(h3), .body :deep(h4) { color: var(--text-h); }
.body :deep(img) { max-width: 100%; border-radius: 14px; }
.body :deep(pre) { background: rgba(0, 0, 0, 0.35); padding: 16px; overflow-x: auto; border-radius: 14px; color: #e2e8f0; }
.body :deep(code) { font-family: ui-monospace, Consolas, monospace; }
.body :deep(blockquote) { border-left: 4px solid var(--accent); margin: 0; padding: 2px 0 2px 18px; color: var(--text-muted); }
.body :deep(a) { color: var(--accent); }
.body :deep(hr) { border: none; border-top: 1px solid var(--glass-border); }
.body :deep(table) { display: block; width: 100%; overflow-x: auto; border-collapse: collapse; -webkit-overflow-scrolling: touch; }
.body :deep(th), .body :deep(td) { border: 1px solid var(--glass-border); padding: 8px 12px; }
.body :deep(th) { background: var(--accent-bg); color: var(--text-h); white-space: nowrap; }
.body :deep(li) { margin: 4px 0; }

@media (max-width: 640px) {
  .body { padding: 20px 16px; line-height: 1.75; }
  .header { padding: 24px 20px; }
  .body :deep(th), .body :deep(td) { padding: 6px 10px; font-size: 13px; }
}
</style>
