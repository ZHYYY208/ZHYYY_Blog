<script setup>
import { onMounted, ref } from 'vue'
import { userApi, isUserLoggedIn } from '../api'
import RulesCheck from './RulesCheck.vue'

const me = ref(null)
const msgs = ref([])
const content = ref('')
const agreed = ref(false)
const sending = ref(false)
const err = ref('')

async function loadMe() {
  if (!isUserLoggedIn()) return
  try {
    me.value = await userApi.me()
  } catch {
    me.value = null
  }
}

async function loadMsgs() {
  try {
    msgs.value = await userApi.messages()
  } catch {}
}

async function send() {
  const c = content.value.trim()
  if (!c) { err.value = '写点内容吧'; return }
  if (!agreed.value) { err.value = '请先勾选同意留言规范'; return }
  sending.value = true
  err.value = ''
  try {
    await userApi.postMessage(c)
    content.value = ''
    agreed.value = false
    await loadMsgs()
  } catch (e) {
    err.value = e.message === 'UNAUTHORIZED' ? '请先登录后再留言' : e.message
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  loadMe()
  loadMsgs()
})
</script>

<template>
  <section class="glass msg-mod">
    <div class="head">
      <h2>留言板</h2>
      <router-link to="/messages" class="more">全部 →</router-link>
    </div>

    <!-- 快捷发言 -->
    <div v-if="me" class="composer">
      <div class="who"><b>{{ me.username }}</b></div>
      <textarea v-model="content" rows="2" maxlength="1000" placeholder="说点什么…"></textarea>
      <div class="agree">
        <RulesCheck v-model="agreed" />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <div class="row">
        <button class="primary" :disabled="sending || !content.trim()" @click="send">
          {{ sending ? '发送中…' : '留言' }}
        </button>
      </div>
    </div>
    <div v-else class="composer login-hint">
      <router-link to="/messages" class="go">登录后可留言 →</router-link>
    </div>

    <!-- 最近留言 -->
    <div class="list">
      <div v-for="m in msgs.slice(0, 4)" :key="m.id" class="msg">
        <div class="msg-head">
          <b>{{ m.username }}</b>
          <span v-if="m.role === 'owner'" class="badge">站长</span>
          <span class="dt">{{ m.createdAt }}</span>
        </div>
        <p class="msg-body">{{ m.content }}</p>
      </div>
      <p v-if="!msgs.length" class="hint">还没有留言，来抢沙发～</p>
    </div>
  </section>
</template>

<style scoped>
.msg-mod { padding: 18px 20px; }
.head { display: flex; align-items: baseline; justify-content: space-between; margin-bottom: 12px; }
.head h2 { margin: 0; font-size: 18px; color: var(--text-h); }
.more { font-size: 13px; color: var(--text-muted); text-decoration: none; }

.composer { display: flex; flex-direction: column; gap: 8px; margin-bottom: 14px; padding-bottom: 14px; border-bottom: 1px dashed var(--glass-border); }
.who b { color: var(--accent); font-size: 14px; }
textarea {
  border: 1px solid var(--glass-border); border-radius: 10px;
  background: rgba(255,255,255,.5); color: var(--text-h);
  padding: 8px 12px; font: inherit; outline: none; resize: vertical;
}
.agree { font-size: 12px; color: var(--text-muted); }
.row { display: flex; justify-content: flex-end; }
.err { margin: 0; color: #ef4444; font-size: 12px; }
.primary {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  border: none; color: #fff; border-radius: 999px; padding: 7px 20px; font-weight: 600; cursor: pointer;
}
.primary:disabled { opacity: 0.5; cursor: not-allowed; }
.login-hint { align-items: center; }
.go { color: var(--accent); text-decoration: none; font-size: 14px; }

.list { display: flex; flex-direction: column; gap: 10px; }
.msg { padding: 10px 12px; background: rgba(255,255,255,.35); border: 1px solid var(--glass-border); border-radius: 12px; }
.msg-head { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.msg-head b { color: var(--accent); font-size: 13px; }
.badge {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  font-size: 10px;
  border-radius: 999px;
  padding: 1px 8px;
  flex-shrink: 0;
}
.dt { margin-left: auto; color: var(--text-muted); font-size: 11px; flex-shrink: 0; }
.msg-body { margin: 0; color: var(--text-h); font-size: 14px; white-space: pre-wrap; word-break: break-word; }
.hint { color: var(--text); font-size: 13px; opacity: 0.85; text-align: center; padding: 6px 0; }
</style>
