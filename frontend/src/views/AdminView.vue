<script setup>
import { computed, ref } from 'vue'
import { marked } from 'marked'
import { api, upload as apiUpload, setAdminToken, clearAdminToken } from '../api'
import TechBranch from '../components/TechBranch.vue'

const authed = ref(false)
const pwd = ref('')
const err = ref('')
const tab = ref('posts')
const settings = ref({ motto: '', cfHandle: '', nowcoder: '', luogu: '' })

// ---------- 通用反馈 ----------
const toast = ref('')
const toastErr = ref(false)
let toastTimer = null
function showToast(msg, isErr = false) {
  toast.value = msg
  toastErr.value = isErr
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 2200)
}

const confirmBox = ref(null)
function askConfirm(title, text, run) {
  confirmBox.value = { title, text, run }
}
function cancelConfirm() {
  confirmBox.value = null
}
async function okConfirm() {
  const cb = confirmBox.value && confirmBox.value.run
  confirmBox.value = null
  if (cb) {
    try {
      await cb()
    } catch (e) {
      showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '操作失败', true)
    }
  }
}

// ---------- 登录 ----------
async function login() {
  setAdminToken(pwd.value.trim())
  try {
    await api.post('/auth/login', {})
    authed.value = true
    err.value = ''
    loadAll()
  } catch (e) {
    clearAdminToken()
    err.value = e.message === 'UNAUTHORIZED' ? '密码错误' : '连接失败，请重试'
  }
}

function logout() {
  clearAdminToken()
  authed.value = false
}

// ---------- 文章 ----------
const posts = ref([])
const post = ref({ title: '', summary: '', content: '' })
const previewHtml = computed(() => marked.parse(post.value.content))

async function loadPosts() {
  try { posts.value = await api.get('/posts') } catch {}
}

async function publishPost() {
  if (!post.value.title.trim() || !post.value.content.trim()) {
    showToast('标题和内容不能为空', true)
    return
  }
  try {
    await api.post('/posts', post.value)
    post.value = { title: '', summary: '', content: '' }
    showToast('发布成功')
    loadPosts()
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '发布失败', true)
  }
}

async function delPost(id) {
  await api.delete(`/posts/${id}`)
  showToast('已删除')
  loadPosts()
}

// ---------- 说说 ----------
const shuoshuo = ref([])
const shuoText = ref('')

async function loadShuoshuo() {
  try { shuoshuo.value = await api.get('/shuoshuo') } catch {}
}

async function addShuo() {
  if (!shuoText.value.trim()) {
    showToast('请先写点内容', true)
    return
  }
  try {
    await api.post('/shuoshuo', { content: shuoText.value })
    shuoText.value = ''
    showToast('发布成功')
    loadShuoshuo()
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '发布失败', true)
  }
}

async function delShuo(id) {
  await api.delete(`/shuoshuo/${id}`)
  showToast('已删除')
  loadShuoshuo()
}

// ---------- 音乐 ----------
const musics = ref([])
const newTrack = ref({ title: '', artist: '', file: null })

async function onMusicFile(e) {
  const file = e.target.files[0]
  if (!file) return
  newTrack.value.file = file
  newTrack.value.title = file.name.replace(/\.[^.]+$/, '')
  showToast(`已选择: ${file.name}`)
}

async function addMusic() {
  if (!newTrack.value.file) {
    showToast('请先选择音频文件', true)
    return
  }
  if (!newTrack.value.title.trim()) {
    showToast('请填写歌曲名', true)
    return
  }
  try {
    const up = await apiUpload('/upload', newTrack.value.file, 'file', { type: 'music' })
    await api.post('/music', { title: newTrack.value.title, artist: newTrack.value.artist, url: up.url })
    newTrack.value = { title: '', artist: '', file: null }
    document.getElementById('musicFile').value = ''
    showToast('添加成功')
    loadMusic()
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '添加失败', true)
  }
}

async function delMusic(id) {
  await api.delete(`/music/${id}`)
  showToast('已删除')
  loadMusic()
}

async function loadMusic() {
  try { musics.value = await api.get('/music') } catch {}
}

// ---------- 相册 ----------
const photos = ref([])
const newPhoto = ref({ title: '', file: null })

async function onPhotoFile(e) {
  const file = e.target.files[0]
  if (!file) return
  newPhoto.value.file = file
  newPhoto.value.title = file.name.replace(/\.[^.]+$/, '')
  showToast(`已选择: ${file.name}`)
}

async function addPhoto() {
  if (!newPhoto.value.file) {
    showToast('请先选择图片', true)
    return
  }
  try {
    const up = await apiUpload('/upload', newPhoto.value.file, 'file', { type: 'image' })
    await api.post('/photos', { title: newPhoto.value.title, url: up.url })
    newPhoto.value = { title: '', file: null }
    document.getElementById('photoFile').value = ''
    showToast('上传成功')
    loadPhotos()
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '上传失败', true)
  }
}

async function delPhoto(id) {
  await api.delete(`/photos/${id}`)
  showToast('已删除')
  loadPhotos()
}

async function loadPhotos() {
  try { photos.value = await api.get('/photos') } catch {}
}

// ---------- 技术栈 ----------
const techs = ref([])
const showTechModal = ref(false)
const techForm = ref({ name: '', color: '', parentId: 0 })
const activeParent = ref(null)

const techTree = computed(() => {
  const byParent = {}
  for (const n of techs.value) {
    const key = n.parentId || 0
    if (!byParent[key]) byParent[key] = []
    byParent[key].push(n)
  }
  const walk = (pid, depth) =>
    (byParent[pid] || []).map((n) => ({ ...n, depth, children: walk(n.id, depth + 1) }))
  return walk(0, 0)
})

function openTechModal(parent) {
  activeParent.value = parent || null
  techForm.value = { name: '', color: '', parentId: parent ? parent.id : 0 }
  showTechModal.value = true
}

function closeTechModal() {
  showTechModal.value = false
  activeParent.value = null
}

async function submitTech() {
  if (!techForm.value.name.trim()) {
    showToast('请填写名称', true)
    return
  }
  try {
    await api.post('/tech', {
      name: techForm.value.name,
      color: techForm.value.color || '',
      parentId: techForm.value.parentId,
    })
    showToast('添加成功')
    closeTechModal()
    loadTechs()
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '添加失败', true)
  }
}

async function delTech(id) {
  await api.delete(`/tech/${id}`)
  showToast('已删除')
  loadTechs()
}

function askTechDelete(n) {
  askConfirm('删除技术节点', `确定删除「${n.name}」吗？其下所有子节点也会一并删除。`, () => delTech(n.id))
}

async function loadTechs() {
  try { techs.value = await api.get('/tech') } catch {}
}

// ---------- 网站设置 ----------
async function loadSettings() {
  try {
    const cfg = await api.get('/site')
    settings.value.motto = cfg.motto || ''
    settings.value.cfHandle = cfg.cfHandle || ''
    settings.value.nowcoder = cfg.nowcoder || ''
    settings.value.luogu = cfg.luogu || ''
  } catch {}
}

async function saveSettings() {
  try {
    await api.put('/site', {
      motto: settings.value.motto,
      cfHandle: settings.value.cfHandle,
      nowcoder: settings.value.nowcoder,
      luogu: settings.value.luogu,
    })
    showToast('保存成功')
  } catch (e) {
    showToast(e.message === 'UNAUTHORIZED' ? '登录已过期，请重新登录' : '保存失败', true)
  }
}

function loadAll() {
  loadPosts()
  loadShuoshuo()
  loadMusic()
  loadPhotos()
  loadTechs()
  loadSettings()
}
</script>

<template>
  <div>
    <h1>管理后台</h1>

    <!-- 登录 -->
    <div v-if="!authed" class="glass login">
      <input v-model="pwd" type="password" placeholder="请输入管理密码" @keyup.enter="login" />
      <button @click="login">进入</button>
      <p v-if="err" class="msg err">{{ err }}</p>
    </div>

    <template v-else>
      <div class="toolbar glass">
        <div class="tabs">
          <button :class="{ on: tab === 'posts' }" @click="tab = 'posts'">写文章</button>
          <button :class="{ on: tab === 'shuoshuo' }" @click="tab = 'shuoshuo'">发说说</button>
          <button :class="{ on: tab === 'photos' }" @click="tab = 'photos'">相册</button>
          <button :class="{ on: tab === 'music' }" @click="tab = 'music'">音乐</button>
          <button :class="{ on: tab === 'tech' }" @click="tab = 'tech'">技术栈</button>
          <button :class="{ on: tab === 'settings' }" @click="tab = 'settings'">网站设置</button>
          <a href="/" class="view">查看前台 →</a>
        </div>
        <button class="logout" @click="logout">退出</button>
      </div>

      <!-- 文章管理 -->
      <section v-if="tab === 'posts'" class="glass card">
        <h2>发布新文章</h2>
        <input v-model="post.title" placeholder="文章标题" class="input title" />
        <input v-model="post.summary" placeholder="摘要（可选，留空自动截取）" class="input" />
        <div class="editor">
          <textarea v-model="post.content" rows="14" placeholder="正文（支持 Markdown）"></textarea>
          <div class="preview" v-html="previewHtml"></div>
        </div>
        <div class="row">
          <button class="primary" @click="publishPost">发布</button>
        </div>
      </section>

      <section v-if="tab === 'posts'" class="list">
        <div v-for="p in posts" :key="p.id" class="glass item">
          <div class="item-info">
            <strong>{{ p.title }}</strong>
            <span>{{ p.createdAt }}</span>
          </div>
          <div class="ops">
            <a :href="`/post/${p.id}`" target="_blank">查看</a>
            <button @click="askConfirm('删除文章', `确定删除「${p.title}」吗？此操作不可恢复。`, () => delPost(p.id))">删除</button>
          </div>
        </div>
      </section>

      <!-- 说说管理 -->
      <section v-if="tab === 'shuoshuo'" class="glass card">
        <h2>发一条说说</h2>
        <textarea v-model="shuoText" rows="3" placeholder="此刻想说点什么…"></textarea>
        <div class="row">
          <button class="primary" @click="addShuo">发布</button>
        </div>
      </section>

      <section v-if="tab === 'shuoshuo'" class="list">
        <div v-for="s in shuoshuo" :key="s.id" class="glass item">
          <div class="item-info">
            <p>{{ s.content }}</p>
            <span>{{ s.createdAt }}</span>
          </div>
          <div class="ops"><button @click="askConfirm('删除说说', '确定删除这条说说吗？', () => delShuo(s.id))">删除</button></div>
        </div>
      </section>

      <!-- 音乐管理 -->
      <section v-if="tab === 'music'" class="glass card">
        <h2>添加音乐</h2>
        <div class="up-row">
          <label class="file-btn">
            选择音频文件
            <input id="musicFile" type="file" accept="audio/*" hidden @change="onMusicFile" />
          </label>
          <input v-model="newTrack.title" placeholder="歌曲名" class="input flex1" />
          <input v-model="newTrack.artist" placeholder="歌手（可选）" class="input flex1" />
        </div>
        <div class="row">
          <button class="primary" @click="addMusic">上传入库</button>
        </div>
      </section>

      <section v-if="tab === 'music'" class="list">
        <div v-for="m in musics" :key="m.id" class="glass item">
          <div class="item-info">
            <strong>{{ m.title }}</strong>
            <span>{{ m.artist }} · <a :href="m.url" target="_blank">试听</a></span>
          </div>
          <div class="ops"><button @click="askConfirm('删除音乐', `确定删除「${m.title}」吗？`, () => delMusic(m.id))">删除</button></div>
        </div>
      </section>

      <!-- 相册管理 -->
      <section v-if="tab === 'photos'" class="glass card">
        <h2>上传照片</h2>
        <div class="up-row">
          <label class="file-btn">
            选择图片
            <input id="photoFile" type="file" accept="image/*" hidden @change="onPhotoFile" />
          </label>
          <input v-model="newPhoto.title" placeholder="照片说明（可选）" class="input flex1" />
        </div>
        <div class="row">
          <button class="primary" @click="addPhoto">上传</button>
        </div>
      </section>

      <section v-if="tab === 'photos'" class="grid-list">
        <div v-for="p in photos" :key="p.id" class="glass ph-card">
          <img :src="p.url" :alt="p.title" loading="lazy" />
          <div class="ph-cap">
            <span>{{ p.title }}</span>
            <button @click="askConfirm('删除照片', `确定删除「${p.title}」吗？`, () => delPhoto(p.id))">删除</button>
          </div>
        </div>
        <p v-if="!photos.length" class="hint">还没有照片</p>
      </section>

      <!-- 技术栈管理 -->
      <section v-if="tab === 'tech'" class="glass card">
        <div class="tech-head">
          <h2>技术栈</h2>
          <button class="primary" @click="openTechModal(null)">＋ 添加大类</button>
        </div>
        <p class="tip">悬停节点显示操作：点「＋子」添加子节点，点「删除」连同子节点一并删除。</p>
        <div v-if="techTree.length" class="tree">
          <TechBranch :nodes="techTree" @add="openTechModal" @del="askTechDelete" />
        </div>
        <p v-else class="hint">还没有节点，先添加一个大类吧</p>
      </section>

      <!-- 网站设置 -->
      <section v-if="tab === 'settings'" class="glass card">
        <h2>网站设置</h2>

        <label class="lab">首页简介（motto）</label>
        <textarea v-model="settings.motto" rows="2" placeholder="一行自我介绍，显示在首页头像旁"></textarea>

        <h3 class="sub-h">算法战绩（首页展示）</h3>
        <div class="set-row">
          <label class="lab">Codeforces Handle（自动拉取 Rating）</label>
          <input v-model="settings.cfHandle" placeholder="如 tourist" class="input" />
        </div>
        <div class="set-row">
          <label class="lab">牛客主页链接（仅跳转用，不公开显示）</label>
          <input v-model="settings.nowcoder" placeholder="如 https://ac.nowcoder.com/acm/contest/profile/123456" class="input" />
        </div>
        <div class="set-row">
          <label class="lab">洛谷主页链接（仅跳转用，不公开显示）</label>
          <input v-model="settings.luogu" placeholder="如 https://www.luogu.com.cn/user/123456" class="input" />
        </div>

        <div class="row">
          <button class="primary" @click="saveSettings">保存</button>
        </div>
      </section>

      <!-- 技术栈添加弹窗 -->
      <div v-if="showTechModal" class="mask" @click.self="closeTechModal">
        <div class="modal glass">
          <h3>{{ activeParent ? `在「${activeParent.name}」下添加子节点` : '添加大类' }}</h3>
          <input v-model="techForm.name" placeholder="名称，如 Java / Spring Boot" class="input" @keyup.enter="submitTech" />
          <input v-model="techForm.color" placeholder="颜色(可选)，如 #6366f1" class="input" @keyup.enter="submitTech" />
          <div class="modal-ops">
            <button class="ghost" @click="closeTechModal">取消</button>
            <button class="primary" @click="submitTech">保存</button>
          </div>
        </div>
      </div>

      <!-- 删除确认弹窗 -->
      <div v-if="confirmBox" class="mask" @click.self="cancelConfirm">
        <div class="modal glass">
          <h3>{{ confirmBox.title }}</h3>
          <p class="confirm-text">{{ confirmBox.text }}</p>
          <div class="modal-ops">
            <button class="ghost" @click="cancelConfirm">取消</button>
            <button class="danger" @click="okConfirm">删除</button>
          </div>
        </div>
      </div>

      <!-- Toast -->
      <transition name="toast">
        <div v-if="toast" class="toast" :class="{ err: toastErr }">{{ toast }}</div>
      </transition>
    </template>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }
.login { max-width: 420px; padding: 30px; display: flex; flex-direction: column; gap: 14px; }
.login input, .input, select, textarea {
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
  color: var(--text-h);
  padding: 10px 14px;
  font: inherit;
  outline: none;
}
.login button, .primary {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  border: none;
  color: #fff;
  border-radius: 999px;
  padding: 9px 26px;
  font-weight: 600;
  cursor: pointer;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 18px;
}
.tabs { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.tabs button {
  border: none;
  background: transparent;
  color: var(--text);
  padding: 7px 14px;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 600;
}
.tabs button.on { background: var(--accent-bg); color: var(--accent); }
.tabs .view { font-size: 13px; margin-left: 8px; }
.logout {
  border: 1px solid var(--glass-border);
  background: transparent;
  color: var(--text);
  padding: 6px 14px;
  border-radius: 999px;
  cursor: pointer;
}

.card { padding: 24px 26px; margin-bottom: 18px; }
.card h2 { margin: 0 0 16px; color: var(--text-h); }
.input { width: 100%; margin-bottom: 12px; }
.input.title { font-size: 18px; font-weight: 700; }

.editor { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px; }
.editor textarea { resize: vertical; font-family: ui-monospace, Consolas, monospace; }
.preview {
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.4);
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.7;
}
.preview :deep(h1), .preview :deep(h2), .preview :deep(h3) { color: var(--text-h); }
.preview :deep(a) { color: var(--accent); }
.preview :deep(code) { background: rgba(0,0,0,0.05); padding: 2px 6px; border-radius: 5px; }

.row { display: flex; justify-content: flex-end; align-items: center; gap: 14px; }
.lab { display: block; font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
.sub-h { color: var(--text-h); font-size: 16px; margin: 22px 0 12px; }
.set-row { margin-bottom: 12px; }
.set-row .input { margin-bottom: 0; }
.up-row { display: flex; gap: 10px; margin-bottom: 12px; align-items: center; flex-wrap: wrap; }
.up-row .flex1 { flex: 1; min-width: 120px; margin: 0; }
.file-btn {
  display: inline-block;
  border: 1px dashed var(--accent);
  color: var(--accent);
  border-radius: 12px;
  padding: 11px 18px;
  cursor: pointer;
  font-size: 14px;
  flex-shrink: 0;
  background: var(--accent-bg);
}
.file-btn:hover { background: var(--accent); color: #fff; }

.grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 14px;
}
.ph-card {
  overflow: hidden;
  padding: 0;
  border-radius: 16px;
}
.ph-card img { width: 100%; height: 150px; object-fit: cover; display: block; }
.ph-cap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: var(--text-h);
}
.ph-cap button {
  border: 1px solid rgba(239, 68, 68, 0.4);
  background: transparent;
  color: #ef4444;
  border-radius: 999px;
  padding: 2px 10px;
  cursor: pointer;
  flex-shrink: 0;
}
.hint { color: var(--text-muted); font-size: 14px; }
.msg { margin: 0; font-size: 14px; color: var(--text-muted); }
.msg.err { color: #ef4444; }

.list { display: flex; flex-direction: column; gap: 12px; }
.item { display: flex; justify-content: space-between; align-items: center; gap: 16px; padding: 14px 20px; }
.item-info { min-width: 0; }
.item-info strong { color: var(--text-h); display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.item-info p { margin: 0 0 4px; color: var(--text-h); }
.item-info span { color: var(--text-muted); font-size: 12px; }
.ops { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.ops a { font-size: 13px; }
.ops button {
  border: 1px solid rgba(239, 68, 68, 0.4);
  background: transparent;
  color: #ef4444;
  border-radius: 999px;
  padding: 4px 14px;
  cursor: pointer;
}

.tech-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.tech-head h2 { margin: 0; color: var(--text-h); font-size: 19px; }
.tech-head .primary { padding: 7px 18px; font-size: 14px; }
.tip { color: var(--text-muted); font-size: 13px; margin: 0 0 12px; }
.tree { display: flex; flex-direction: column; }

.mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
  display: grid;
  place-items: center;
  padding: 16px;
}
.modal {
  width: 100%;
  max-width: 460px;
  padding: 24px;
  border-radius: 18px;
  box-sizing: border-box;
}
.modal h3 { margin: 0 0 16px; color: var(--text-h); }
.modal .input { margin-bottom: 12px; }
.confirm-text { color: var(--text); font-size: 15px; margin: 0 0 18px; line-height: 1.6; }
.modal-ops { display: flex; justify-content: flex-end; gap: 10px; }
.ghost {
  border: 1px solid var(--glass-border);
  background: transparent;
  color: var(--text);
  border-radius: 999px;
  padding: 8px 22px;
  cursor: pointer;
}
.danger {
  background: #ef4444;
  border: none;
  color: #fff;
  border-radius: 999px;
  padding: 8px 22px;
  cursor: pointer;
}

.toast {
  position: fixed;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1100;
  background: #16a34a;
  color: #fff;
  padding: 10px 26px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}
.toast.err { background: #dc2626; }
.toast-enter-active, .toast-leave-active { transition: all 0.25s; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, -12px); }

@media (max-width: 700px) {
  .editor { grid-template-columns: 1fr; }
}
</style>
