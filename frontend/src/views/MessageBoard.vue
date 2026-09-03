<script setup>
import { onMounted, ref } from 'vue'
import { userApi, setUserSession, clearUserSession, isUserLoggedIn } from '../api'
import RulesCheck from '../components/RulesCheck.vue'

const me = ref(null)
const mode = ref('login') // login | register
const form = ref({ username: '', password: '' })
const err = ref('')
const msgs = ref([])
const content = ref('')
const agreed = ref(false)
const sending = ref(false)

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

function switchMode(m) {
  mode.value = m
  err.value = ''
}

async function submit() {
  err.value = ''
  if (form.value.username.length < 2 || form.value.password.length < 6) {
    err.value = '用户名至少 2 位，密码至少 6 位'
    return
  }
  try {
    const r = mode.value === 'login'
      ? await userApi.login(form.value.username, form.value.password)
      : await userApi.register(form.value.username, form.value.password)
    setUserSession(r.token, r.username)
    me.value = { id: r.id, username: r.username, role: r.role || 'user' }
    form.value = { username: '', password: '' }
  } catch (e) {
    err.value = e.message
  }
}

function logout() {
  userApi.logout().catch(() => {})
  clearUserSession()
  me.value = null
}

async function send() {
  const c = content.value.trim()
  if (!c) return
  if (!agreed.value) {
    err.value = '请先勾选同意留言规范'
    return
  }
  sending.value = true
  try {
    await userApi.postMessage(c, me.value.username)
    content.value = ''
    await loadMsgs()
  } catch (e) {
    err.value = e.message === 'UNAUTHORIZED' ? '请先登录' : e.message
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
  <div>
    <h1>留言板</h1>
    <p class="notice">欢迎留言，一起交流～</p>

    <!-- 未登录：登录/注册 -->
    <section v-if="!me" class="glass auth">
      <div class="tabs">
        <button :class="{ on: mode === 'login' }" @click="switchMode('login')">登录</button>
        <button :class="{ on: mode === 'register' }" @click="switchMode('register')">注册</button>
      </div>
      <div class="field">
        <label>用户名</label>
        <input v-model="form.username" placeholder="2-20 位" @keyup.enter="submit" />
      </div>
      <div class="field">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="至少 6 位" @keyup.enter="submit" />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <button class="primary" @click="submit">{{ mode === 'login' ? '登录' : '注册并登录' }}</button>
      <p class="tip">{{ mode === 'login' ? '还没有账号？' : '已有账号？' }}
        <a @click="switchMode(mode === 'login' ? 'register' : 'login')">
          {{ mode === 'login' ? '注册一个' : '去登录' }}
        </a>
      </p>
    </section>

    <!-- 已登录：发表 -->
    <section v-else class="glass composer">
      <div class="who">
        <b>{{ me.username }}</b>
        <button class="ghost" @click="logout">退出</button>
      </div>
      <textarea v-model="content" rows="3" maxlength="1000" placeholder="想说点什么…"></textarea>
      <div class="agree">
        <RulesCheck v-model="agreed" />
      </div>
      <p v-if="err" class="err">{{ err }}</p>
      <div class="row">
        <button class="primary" :disabled="sending || !content.trim()" @click="send">
          {{ sending ? '发送中…' : '发布留言' }}
        </button>
      </div>
    </section>

    <!-- 留言列表 -->
    <section class="list">
      <div v-for="m in msgs" :key="m.id" class="glass msg">
        <div class="msg-head">
          <b>{{ m.username }}</b>
          <span v-if="m.role === 'owner'" class="badge">站长</span>
          <span class="dt">{{ m.createdAt }}</span>
        </div>
        <p class="msg-body">{{ m.content }}</p>
      </div>
      <p v-if="!msgs.length" class="hint">还没有留言，来抢沙发吧～</p>
    </section>

    <!-- 须知/免责声明 -->
    <footer class="legal">免责声明：请遵守国家法律法规，文明友善发言，违规内容站长有权删除。详细条款见上方「留言规范」。</footer>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 6px; color: var(--text-h); }
.notice { color: var(--text-muted); font-size: 13px; margin: 0 0 18px; }

.auth { max-width: 380px; padding: 24px 28px; display: flex; flex-direction: column; gap: 12px; }
.tabs { display: flex; gap: 10px; margin-bottom: 4px; }
.tabs button { border: none; background: transparent; color: var(--text); font-weight: 700; font-size: 16px; cursor: pointer; padding: 2px 4px; }
.tabs button.on { color: var(--accent); border-bottom: 2px solid var(--accent); }
.field { display: flex; flex-direction: column; gap: 6px; }
.field label { font-size: 13px; color: var(--text-muted); }
.auth input, textarea {
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.5);
  color: var(--text-h);
  padding: 9px 12px;
  font: inherit;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}
.err { color: #ef4444; font-size: 13px; margin: 0; }
.tip { font-size: 13px; color: var(--text-muted); margin: 0; }
.tip a { color: var(--accent); cursor: pointer; }
.primary {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  border: none; color: #fff; border-radius: 999px; padding: 9px 22px; font-weight: 600; cursor: pointer;
}
.primary:disabled { opacity: 0.5; cursor: not-allowed; }

.composer { padding: 20px 24px; margin-bottom: 20px; display: flex; flex-direction: column; gap: 12px; }
.who { display: flex; align-items: center; justify-content: space-between; }
.who b { color: var(--accent); font-size: 16px; }
.ghost { border: 1px solid var(--glass-border); background: transparent; color: var(--text); border-radius: 999px; padding: 4px 14px; cursor: pointer; }
.composer textarea {
  border: 1px solid var(--glass-border); border-radius: 10px; background: rgba(255,255,255,.5);
  color: var(--text-h); padding: 10px 12px; font: inherit; outline: none; resize: vertical;
}
.agree { font-size: 13px; color: var(--text-muted); }
.row { display: flex; justify-content: flex-end; }

.list { display: flex; flex-direction: column; gap: 12px; }
.msg { padding: 16px 20px; }
.msg-head { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.msg-head b { color: var(--accent); }
.badge {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  font-size: 11px;
  border-radius: 999px;
  padding: 1px 10px;
  flex-shrink: 0;
}
.dt { margin-left: auto; color: var(--text-muted); font-size: 12px; flex-shrink: 0; }
.msg-body { margin: 0; color: var(--text-h); white-space: pre-wrap; word-break: break-word; }
.hint { color: var(--text); opacity: 0.85; text-align: center; padding: 8px 0; }

.legal {
  margin-top: 18px;
  font-size: 12px;
  color: var(--text);
  opacity: 0.9;
  line-height: 1.8;
  border-top: 1px solid var(--glass-border);
  padding-top: 10px;
}
</style>
