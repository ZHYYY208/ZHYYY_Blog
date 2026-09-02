<script setup>
import { computed, onMounted, ref } from 'vue'
import { usePlayer } from '../composables/usePlayer'

const { state, load, toggle, next, prev, seek, fmt, playAt } = usePlayer()

const track = computed(() => state.tracks[state.index])
const hasSelection = computed(() => state.index >= 0)
const listOpen = ref(false)

function openList() {
  listOpen.value = true
}

function pick(i) {
  playAt(i)
  listOpen.value = false
}

onMounted(() => {
  load()
})
</script>

<template>
  <div class="glass music-mod">
    <div class="disc" :class="{ spin: hasSelection && state.playing }" @click="toggle(state.index)"></div>

    <div class="meta" @click="toggle(state.index)">
      <template v-if="hasSelection">
        <strong>{{ track.title }}</strong>
        <span>{{ track.artist }}</span>
      </template>
      <template v-else>
        <strong>播放一首歌</strong>
        <span>点唱片播放 · 点歌单选歌</span>
      </template>
    </div>

    <div class="ctl">
      <button class="cbtn" title="上一首" @click="prev">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M6 6h2v12H6zm3.5 6l8.5 6V6z"/></svg>
      </button>
      <button class="cbtn big" :title="state.playing ? '暂停' : '播放'" @click="toggle(state.index)">
        <svg v-if="hasSelection && state.playing" viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M6 5h4v14H6zm8 0h4v14h-4z"/></svg>
        <svg v-else viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
      </button>
      <button class="cbtn" title="下一首" @click="next">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M16 6h2v12h-2zM6 18l8.5-6L6 6z"/></svg>
      </button>
    </div>

    <div class="prog" @click="seek">
      <div class="prog-track">
        <div class="bar" :style="{ width: (hasSelection && state.duration ? (state.time / state.duration) * 100 : 0) + '%' }">
          <div class="thumb"></div>
        </div>
      </div>
    </div>
    <div class="times">
      <span>{{ fmt(hasSelection ? state.time : 0) }}</span>
      <span class="toggle" @click="openList">歌单</span>
      <span>{{ fmt(hasSelection ? state.duration : 0) }}</span>
    </div>

    <!-- 歌单弹窗 -->
    <div v-if="listOpen" class="mask" @click.self="listOpen = false">
      <div class="modal glass">
        <div class="mhead">
          <b>选择歌曲</b>
          <button class="mclose" @click="listOpen = false" title="关闭">×</button>
        </div>
        <div class="mlist">
          <div
            v-for="(t, i) in state.tracks"
            :key="t.id"
            class="li"
            :class="{ cur: i === state.index }"
            @click="pick(i)"
          >
            <span class="n">{{ i === state.index ? '♪' : i + 1 }}</span>
            <span class="nm">{{ t.title }}</span>
            <span class="ar">{{ t.artist }}</span>
          </div>
          <div v-if="!state.tracks.length" class="empty">暂无歌曲</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.music-mod {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
}
.disc {
  width: 78px;
  height: 78px;
  border-radius: 50%;
  cursor: pointer;
  display: grid;
  place-items: center;
  background: conic-gradient(from 0deg, var(--accent), var(--accent-2), #ec4899, #f59e0b, var(--accent));
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
  position: relative;
}
.disc::after {
  content: '';
  position: absolute;
  inset: 22%;
  border-radius: 50%;
  background: radial-gradient(circle, #fff 0 24%, #e2e8f0 24.5% 100%);
}
.disc.spin { animation: rot 7s linear infinite; }
@keyframes rot { to { transform: rotate(360deg); } }

.meta { text-align: center; cursor: pointer; min-width: 0; }
.meta strong { color: var(--text-h); font-size: 16px; display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100%; }
.meta span { color: var(--text-muted); font-size: 12px; }

.ctl { display: flex; align-items: center; gap: 10px; }
.cbtn {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  background: var(--accent-bg);
  color: var(--accent);
  display: grid;
  place-items: center;
}
.cbtn.big { width: 46px; height: 46px; background: linear-gradient(135deg, var(--accent), var(--accent-2)); color: #fff; }
.cbtn:hover { transform: scale(1.08); }

.prog { width: 100%; height: 14px; display: flex; align-items: center; cursor: pointer; }
.prog-track { width: 100%; height: 5px; border-radius: 999px; background: rgba(100, 116, 139, 0.3); }
.bar { position: relative; height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent-2), #ec4899, #f59e0b); border-radius: 999px; }
.thumb {
  position: absolute; right: -6px; top: 50%; transform: translateY(-50%);
  width: 11px; height: 11px; border-radius: 50%; background: #fff; border: 2px solid var(--accent);
  opacity: 0; transition: opacity 0.2s;
}
.prog:hover .thumb { opacity: 1; }

.times { width: 100%; display: flex; justify-content: space-between; font-size: 11px; color: var(--text-muted); }
.times .toggle { cursor: pointer; color: var(--accent); }

/* 歌单模态弹窗 */
.mask {
  position: fixed;
  inset: 0;
  z-index: 1500;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
  display: grid;
  place-items: center;
  padding: 16px;
}
.modal {
  width: 100%;
  max-width: 420px;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
  padding: 12px;
  border-radius: 18px;
  box-sizing: border-box;
}
.mhead {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px 10px;
  color: var(--text-h);
}
.mclose {
  border: none;
  background: var(--accent-bg);
  color: var(--accent);
  width: 26px;
  height: 26px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 15px;
  line-height: 1;
}
.mlist { overflow-y: auto; }
.li { display: flex; align-items: center; gap: 8px; padding: 9px 10px; border-radius: 8px; cursor: pointer; font-size: 13px; }
.li:hover { background: var(--accent-bg); }
.li.cur { color: var(--accent); }
.n { width: 20px; text-align: center; color: var(--text-muted); flex-shrink: 0; }
.nm { flex: 1; color: var(--text-h); min-width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.ar { color: var(--text-muted); font-size: 11px; }
.empty { padding: 14px; text-align: center; color: var(--text-muted); font-size: 12px; }
</style>
