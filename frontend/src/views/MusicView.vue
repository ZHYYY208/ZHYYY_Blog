<script setup>
import { onMounted, computed } from 'vue'
import { usePlayer } from '../composables/usePlayer'

const { state, load, toggle, next, prev, seek, fmt } = usePlayer()
const error = ''

const track = computed(() => state.tracks[state.index])

onMounted(() => {
  load().catch(() => {})
})
</script>

<template>
  <div class="hall">
    <div class="hall-head">
      <h1>音乐馆</h1>
      <span class="sub">Music Hall</span>
    </div>
    <p v-if="error" class="hint">{{ error }}</p>

    <!-- 沉浸式播放模块 -->
    <div v-if="state.tracks.length" class="glass player">
      <div class="discwrap">
        <div class="disc" :class="{ spin: state.playing }" @click="toggle(state.index)"></div>
        <span class="eq" :class="{ on: state.playing }"><i></i><i></i><i></i></span>
      </div>

      <div class="meta">
        <strong class="tt">{{ track ? track.title : '未选择' }}</strong>
        <span>{{ track ? track.artist : '请从下方选择一首歌' }}</span>
      </div>

      <div class="ctrl">
        <button class="ctrl-btn" @click="prev" title="上一首">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M6 6h2v12H6zm3.5 6l8.5 6V6z"/></svg>
        </button>
        <button class="ctrl-btn big" @click="toggle(state.index)" :title="state.playing ? '暂停' : '播放'">
          <svg v-if="state.playing" viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M6 5h4v14H6zm8 0h4v14h-4z"/></svg>
          <svg v-else viewBox="0 0 24 24" width="22" height="22" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
        </button>
        <button class="ctrl-btn" @click="next" title="下一首">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M16 6h2v12h-2zM6 18l8.5-6L6 6z"/></svg>
        </button>
      </div>

      <div class="prog" @click="seek">
        <div class="prog-track">
          <div class="bar" :style="{ width: (state.duration ? (state.time / state.duration) * 100 : 0) + '%' }">
            <div class="thumb"></div>
          </div>
        </div>
      </div>
      <div class="times">
        <span>{{ fmt(state.time) }}</span>
        <span>{{ fmt(state.duration) }}</span>
      </div>
    </div>
    <p v-if="!state.tracks.length" class="hint">还没有音乐</p>

    <!-- 歌单 -->
    <div class="glass list-card">
      <div class="list-head">播放列表 <span v-if="state.tracks.length">· {{ state.tracks.length }} 首</span></div>
      <div
        v-for="(t, i) in state.tracks"
        :key="t.id"
        class="row-item"
        :class="{ active: i === state.index }"
        @click="toggle(i)"
      >
        <span class="idx">
          <template v-if="i === state.index">
            <svg v-if="state.playing" viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"/></svg>
            <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M6 5h4v14H6zm8 0h4v14h-4z"/></svg>
          </template>
          <template v-else>{{ i + 1 }}</template>
        </span>
        <div class="info">
          <strong>{{ t.title }}</strong>
          <span class="meta">{{ t.artist }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.hall-head { display: flex; align-items: baseline; gap: 14px; margin: 8px 0 26px; }
.hall-head h1 { margin: 0; color: var(--text-h); font-size: 30px; }
.hall-head .sub { color: var(--text-muted); letter-spacing: 3px; font-size: 13px; }
.hint { color: var(--text-muted); }

/* ===== 沉浸播放模块 ===== */
.player {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 34px 26px 26px;
  margin-bottom: 22px;
  text-align: center;
}

.discwrap { position: relative; width: 150px; height: 150px; }
.disc {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  cursor: pointer;
  background:
    radial-gradient(circle at 50% 50%, #1e293b 0 18%, transparent 18.5%),
    conic-gradient(from 0deg, var(--accent), var(--accent-2), #ec4899, #f59e0b, var(--accent));
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
  position: relative;
}
.disc::after {
  content: '';
  position: absolute;
  inset: 26%;
  border-radius: 50%;
  background: radial-gradient(circle, #fff 0 22%, #e2e8f0 22.5% 100%);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
}
.disc.spin { animation: spin 8s linear infinite; }
.discwrap:hover .disc { transform: scale(1.04); }
@keyframes spin { to { transform: rotate(360deg); } }

.eq {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: flex-end;
  gap: 3px;
  height: 30px;
  z-index: 2;
  cursor: pointer;
}
.eq i { width: 4px; height: 6px; border-radius: 2px; background: #fff; box-shadow: 0 1px 3px rgba(0,0,0,.2); }
.eq.on i { animation: eq 0.7s ease-in-out infinite; }
.eq.on i:nth-child(2) { animation-delay: 0.15s; }
.eq.on i:nth-child(3) { animation-delay: 0.3s; }
@keyframes eq { 0%, 100% { height: 6px; } 50% { height: 24px; } }

.meta { display: flex; flex-direction: column; gap: 4px; }
.meta .tt { color: var(--text-h); font-size: 22px; }
.meta span { color: var(--text-muted); font-size: 14px; }

.ctrl { display: flex; align-items: center; gap: 12px; margin-top: 2px; }
.ctrl-btn {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: var(--accent-bg);
  color: var(--accent);
  display: grid;
  place-items: center;
  transition: transform 0.15s;
}
.ctrl-btn:hover { transform: scale(1.1); }
.ctrl-btn.big {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
}

.prog {
  width: 100%;
  max-width: 520px;
  position: relative;
  height: 16px;
  border-radius: 999px;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.prog-track { width: 100%; height: 6px; border-radius: 999px; background: rgba(100, 116, 139, 0.35); }
.bar {
  position: relative;
  height: 100%;
  background: linear-gradient(90deg, var(--accent), var(--accent-2), #ec4899, #f59e0b);
  border-radius: 999px;
  transition: width 0.1s linear;
}
.thumb {
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid var(--accent);
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.2s;
}
.prog:hover .thumb { opacity: 1; }
.times {
  width: 100%;
  max-width: 520px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: -6px;
}

/* ===== 歌单模块 ===== */
.list-card { padding: 8px; }
.list-head { padding: 10px 16px; font-weight: 800; color: var(--text-h); font-size: 15px; }
.row-item {
  display: flex;
  gap: 14px;
  align-items: center;
  cursor: pointer;
  padding: 10px 16px;
  border-radius: 12px;
  transition: background 0.2s;
}
.row-item:hover { background: var(--accent-bg); }
.row-item.active { background: var(--accent-bg); }
.idx {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: var(--accent-bg);
  color: var(--accent);
  font-weight: 700;
  font-size: 13px;
  flex-shrink: 0;
}
.info { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.info strong { color: var(--text-h); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.info .meta { color: var(--text-muted); font-size: 12px; }

/* 手机适配 */
@media (max-width: 640px) {
  .player { padding: 26px 16px 20px; }
  .discwrap, .disc { width: 120px; height: 120px; }
  .meta .tt { font-size: 18px; }
  .ctrl-btn { width: 40px; height: 40px; }
  .ctrl-btn.big { width: 56px; height: 56px; }
  .list-card { padding: 6px; }
  .row-item { padding: 10px 12px; }
}
</style>
