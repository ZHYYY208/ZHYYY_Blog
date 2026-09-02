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
  <div>
    <h1>音乐</h1>
    <p v-if="error" class="hint">{{ error }}</p>

    <!-- 播放器 -->
    <div v-if="state.tracks.length" class="glass player">
      <div class="art">
        <span class="eq" :class="{ on: state.playing }">
          <i></i><i></i><i></i>
        </span>
      </div>
      <div class="meta">
        <strong>{{ track ? track.title : '' }}</strong>
        <span>{{ track ? track.artist : '' }}</span>
      </div>
      <div class="ctrl">
        <button class="ctrl-btn" @click="prev" title="上一首">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M6 6h2v12H6zm3.5 6l8.5 6V6z"/></svg>
        </button>
        <button class="ctrl-btn big" @click="toggle(state.index)" :title="state.playing ? '暂停' : '播放'">
          <svg v-if="state.playing" viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M6 5h4v14H6zm8 0h4v14h-4z"/></svg>
          <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
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
    <div
      v-for="(t, i) in state.tracks"
      :key="t.id"
      class="glass row-item"
      :class="{ active: i === state.index }"
      @click="toggle(i)"
    >
      <span class="idx">
        <template v-if="i === state.index">
          <svg v-if="state.playing" viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"/></svg>
          <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M6 5h4v14H6zm8 0h4v14h-4z"/></svg>
        </template>
        <template v-else>{{ i + 1 }}</template>
      </span>
      <div class="info">
        <strong>{{ t.title }}</strong>
        <span class="meta">{{ t.artist }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 { margin: 8px 0 24px; color: var(--text-h); }

.player {
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-areas: 'art meta ctrl' 'art progress progress' 'art times times';
  align-items: center;
  gap: 6px 18px;
  padding: 22px 26px;
  margin-bottom: 22px;
}

.art {
  grid-area: art;
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
}
.eq { display: flex; align-items: flex-end; gap: 3px; height: 26px; }
.eq i {
  width: 4px;
  height: 6px;
  border-radius: 2px;
  background: #fff;
}
.eq.on i { animation: eq 0.8s ease-in-out infinite; }
.eq.on i:nth-child(2) { animation-delay: 0.15s; }
.eq.on i:nth-child(3) { animation-delay: 0.3s; }
@keyframes eq { 0%, 100% { height: 6px; } 50% { height: 24px; } }

.meta { grid-area: meta; display: flex; flex-direction: column; gap: 4px; }
.meta strong { color: var(--text-h); font-size: 17px; }
.meta span { color: var(--text-muted); font-size: 13px; }

.ctrl { grid-area: ctrl; display: flex; align-items: center; gap: 8px; }
.ctrl-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 15px;
  transition: transform 0.15s;
}
.ctrl-btn:hover { transform: scale(1.1); }
.ctrl-btn.big {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  font-size: 18px;
}

.prog {
  grid-area: progress;
  position: relative;
  height: 16px;
  border-radius: 999px;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.prog-track {
  width: 100%;
  height: 6px;
  border-radius: 999px;
  background: rgba(100, 116, 139, 0.35);
  overflow: visible;
}
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
  transition: opacity 0.2s, transform 0.15s;
}
.prog:hover .prog-track { height: 8px; }
.prog:hover .thumb { opacity: 1; }
.prog:active .thumb { transform: translateY(-50%) scale(1.25); }
.times {
  grid-area: times;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-muted);
}

.row-item {
  display: flex;
  gap: 16px;
  align-items: center;
  cursor: pointer;
  padding: 14px 20px;
  margin-bottom: 12px;
  transition: transform 0.2s;
}
.row-item:hover { transform: translateX(4px); }
.row-item.active { border-color: var(--accent); }
.idx {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: var(--accent-bg);
  color: var(--accent);
  font-weight: 700;
  flex-shrink: 0;
}
.info { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.info strong { color: var(--text-h); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.meta { color: var(--text-muted); font-size: 13px; }
.hint { color: var(--text-muted); }

/* 手机适配 */
@media (max-width: 640px) {
  .player {
    grid-template-columns: auto 1fr;
    grid-template-areas: 'art meta' 'progress progress' 'times times' 'ctrl ctrl';
    gap: 6px 14px;
    padding: 16px;
  }
  .art { width: 54px; height: 54px; border-radius: 16px; }
  .eq { height: 22px; }
  .meta strong { font-size: 15px; }
  .ctrl {
    justify-content: center;
    padding-top: 6px;
    border-top: 1px solid var(--glass-border);
  }
  .ctrl-btn { width: 36px; height: 36px; }
  .ctrl-btn.big { width: 44px; height: 44px; }
  .row-item { padding: 12px 14px; gap: 12px; }
  .idx { width: 32px; height: 32px; }
}
</style>
