<script setup>
import { computed, onMounted, ref } from 'vue'
import { usePlayer } from '../composables/usePlayer'

const { state, load, toggle, next, prev, seek, fmt, playAt } = usePlayer()

const track = computed(() => state.tracks[state.index])
const hasSelection = computed(() => state.index >= 0)
const visible = computed(() => state.loaded && state.tracks.length > 0)

// 默认收起为律动条（PC + 手机一致）
const collapsed = ref(true)
const listOpen = ref(false)

function openList() {
  listOpen.value = true
}

function pick(i) {
  playAt(i)
  listOpen.value = false
}

function expand() {
  collapsed.value = false
}

function collapse() {
  collapsed.value = true
  listOpen.value = false
}

// 点律动条 = 播放开关；无曲目则先展开歌单
function onFabTap() {
  if (hasSelection.value) {
    toggle(state.index)
  } else {
    expand()
    openList()
  }
}

function onArtTap() {
  if (!hasSelection.value) {
    openList()
    return
  }
  toggle(state.index)
}

function onNav(dir) {
  if (!hasSelection.value) {
    openList()
    return
  }
  dir === 'next' ? next() : prev()
}

onMounted(() => {
  load()
})
</script>

<template>
  <button v-if="visible && collapsed" class="ripple" title="播放 / 暂停" @click="onFabTap">
    <span class="bars" :class="{ idle: !hasSelection || !state.playing }"><i></i><i></i><i></i><i></i></span>
    <span class="expand-btn" title="展开" @click.stop="expand">
      <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor"><path d="M12 16l-6-6 1.4-1.4 4.6 4.6 4.6-4.6L18 10z"/></svg>
    </span>
  </button>

  <!-- 展开面板 -->
  <div v-if="visible && !collapsed" class="panel glass">
    <div class="head">
      <div class="art" :class="{ idle: !hasSelection }" @click="onArtTap">
        <span class="eq" :class="{ on: hasSelection && state.playing }"><i></i><i></i><i></i></span>
      </div>
      <div class="info" @click="onArtTap">
        <template v-if="hasSelection">
          <strong>{{ track.title }}</strong>
          <span>{{ track.artist }}</span>
        </template>
        <template v-else>
          <strong class="placeholder">点播放或选歌开始</strong>
          <span>▾</span>
        </template>
      </div>
      <button class="mini-btn" title="选歌" :class="{ active: listOpen }" @click="listOpen = !listOpen">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
          <path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z" />
        </svg>
      </button>
      <button class="mini-btn" title="收起" @click="collapse">
        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor"><path d="M12 8l-6 6 1.4 1.4L12 10.8l4.6 4.6L18 14z"/></svg>
      </button>
    </div>

    <div class="nav">
      <button class="mini-btn" title="上一首" @click="onNav('prev')">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M6 6h2v12H6zm3.5 6l8.5 6V6z"/></svg>
      </button>
      <div class="prog" @click="seek">
        <div class="prog-track">
          <div class="bar" :style="{ width: (hasSelection && state.duration ? (state.time / state.duration) * 100 : 0) + '%' }">
            <div class="thumb"></div>
          </div>
        </div>
      </div>
      <span class="time">{{ fmt(state.time) }} / {{ fmt(state.duration) }}</span>
      <button class="mini-btn" title="下一首" @click="onNav('next')">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M16 6h2v12h-2zM6 18l8.5-6L6 6z"/></svg>
      </button>
    </div>

    <transition name="list">
      <div v-if="listOpen" class="list glass">
        <div class="list-title">选择歌曲</div>
        <div v-for="(t, i) in state.tracks" :key="t.id" class="li" :class="{ cur: i === state.index }" @click="pick(i)">
          <span class="n">
            <svg v-if="i === state.index" viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"/></svg>
            <template v-else>{{ i + 1 }}</template>
          </span>
          <span class="nm">{{ t.title }}</span>
          <span class="ar">{{ t.artist }}</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
/* 律动条（收起态 / 播放开关） */
.ripple {
  position: fixed;
  left: 20px;
  bottom: 20px;
  z-index: 980;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 6px 8px 16px;
  border-radius: 999px;
  border: 1px solid var(--glass-border);
  cursor: pointer;
  box-shadow: var(--glass-shadow);
  transition: transform 0.2s;
}
.ripple:hover { transform: scale(1.05); }
.bars { display: flex; align-items: center; gap: 3px; height: 22px; padding-right: 8px; border-right: 1px solid var(--glass-border); }
.bars i {
  width: 4px;
  height: 100%;
  border-radius: 2px;
  background: linear-gradient(180deg, var(--accent), var(--accent-2), #ec4899);
}
.bars i { animation: wave 0.8s ease-in-out infinite; }
.bars i:nth-child(2) { animation-delay: 0.15s; }
.bars i:nth-child(3) { animation-delay: 0.3s; }
.bars i:nth-child(4) { animation-delay: 0.45s; }
.bars.idle i { animation: none; height: 40%; }
.bars.idle i:nth-child(2) { height: 65%; }
.bars.idle i:nth-child(3) { height: 50%; }
.bars.idle i:nth-child(4) { height: 80%; }
@keyframes wave { 0%, 100% { height: 30%; } 50% { height: 100%; } }
.expand-btn {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: var(--text-muted);
  background: var(--accent-bg);
  color: var(--accent);
}

/* 展开面板 */
.panel {
  position: fixed;
  left: 20px;
  bottom: 20px;
  z-index: 980;
  width: 330px;
  padding: 12px 14px 10px;
  border-radius: 18px;
  box-sizing: border-box;
}
.head { display: flex; align-items: center; gap: 10px; }
.art {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  cursor: pointer;
  flex-shrink: 0;
}
.art.idle { background: rgba(148, 163, 184, 0.3); color: var(--text-muted); }
.eq { display: flex; align-items: flex-end; gap: 2.5px; height: 18px; }
.eq i { width: 3px; height: 5px; border-radius: 2px; background: #fff; }
.eq.on i { animation: waveEq 0.8s ease-in-out infinite; }
.eq.on i:nth-child(2) { animation-delay: 0.15s; }
.eq.on i:nth-child(3) { animation-delay: 0.3s; }
@keyframes waveEq { 0%, 100% { height: 5px; } 50% { height: 16px; } }

.info {
  flex: 1;
  min-width: 0;
  cursor: pointer;
  overflow: hidden;
}
.info strong { color: var(--text-h); font-size: 13.5px; display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.info strong.placeholder { color: var(--text-muted); font-weight: 500; }
.info span { color: var(--text-muted); font-size: 11px; }

.nav { display: flex; align-items: center; gap: 10px; margin-top: 10px; }
.mini-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: var(--accent-bg);
  color: var(--accent);
  display: grid;
  place-items: center;
  flex-shrink: 0;
  transition: transform 0.15s;
}
.mini-btn:hover { transform: scale(1.1); }
.mini-btn.active { background: var(--accent); color: #fff; }
.time { font-size: 11px; color: var(--text-muted); white-space: nowrap; flex-shrink: 0; }

.prog {
  flex: 1;
  position: relative;
  height: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.prog-track { width: 100%; height: 5px; border-radius: 999px; background: rgba(100, 116, 139, 0.35); }
.bar { position: relative; height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2), #ec4899, #f59e0b); border-radius: 999px; }
.thumb {
  position: absolute;
  right: -7px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid var(--accent);
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.2s;
}
.prog:hover .thumb { opacity: 1; }

.list {
  position: absolute;
  left: 0;
  right: 0;
  bottom: calc(100% + 8px);
  max-height: 260px;
  overflow-y: auto;
  padding: 6px;
  border-radius: 16px;
  z-index: 990;
}
.list-title { padding: 6px 12px 8px; font-size: 12px; color: var(--text-muted); }
.li { display: flex; align-items: center; gap: 10px; padding: 8px 12px; border-radius: 10px; cursor: pointer; font-size: 13px; }
.li:hover { background: var(--accent-bg); }
.li.cur { color: var(--accent); }
.n { width: 22px; display: flex; align-items: center; justify-content: center; color: var(--text-muted); flex-shrink: 0; }
.cur .n { color: var(--accent); }
.nm { color: var(--text-h); font-weight: 600; min-width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; }
.ar { color: var(--text-muted); font-size: 12px; flex-shrink: 0; }

.list-enter-active, .list-leave-active { transition: all 0.2s; }
.list-enter-from, .list-leave-to { opacity: 0; transform: translateY(8px); }

@media (max-width: 760px) {
  .panel { width: calc(100vw - 24px); left: 12px; bottom: 12px; }
  .ripple { left: 12px; bottom: 12px; }
}
</style>
