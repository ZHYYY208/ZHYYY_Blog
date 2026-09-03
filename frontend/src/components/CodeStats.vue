<script setup>
import { onMounted, ref, reactive } from 'vue'
import { api } from '../api'

const cfg = ref({})
const cf = ref(null)
const cfErr = ref(false)
const loaded = ref(false)

const imgErr = reactive({ cf: false, lg: false, nc: false })

const LOGOS = {
  cf: 'https://codeforces.com/favicon.ico',
  lg: 'https://www.luogu.com.cn/favicon.ico',
  nc: 'https://ac.nowcoder.com/favicon.ico',
}

const COLORS = {
  newbie: '#808080',
  pupil: '#008000',
  specialist: '#03a89e',
  expert: '#0000ff',
  candidate: '#aa00aa',
  master: '#ff8c00',
  grandmaster: '#ff0000',
  legendary: '#ff0000',
}

async function loadSite() {
  try {
    cfg.value = await api.get('/site')
  } catch {}
}

async function loadCF() {
  if (!cfg.value.cfHandle) return
  try {
    const res = await fetch(
      `/api/cf/user?handle=${encodeURIComponent(cfg.value.cfHandle)}`,
      { signal: AbortSignal.timeout(10000) }
    )
    if (!res.ok) throw new Error()
    const j = await res.json()
    if (j.status === 'OK') {
      const u = j.result[0]
      const has = typeof u.rating === 'number'
      if (has) {
        const rank = (u.rank || 'unrated').toLowerCase().replace(/\s/g, '')
        u.rankColor = COLORS[rank] || '#808080'
      } else {
        u.rankColor = '#808080'
      }
      cf.value = u
    } else {
      throw new Error()
    }
  } catch {
    cfErr.value = true
  }
}

onMounted(async () => {
  await loadSite()
  loaded.value = true // 卡片立即显示
  loadCF()            // CF 数据异步填充，不阻塞首屏
})

function luoguUser() {
  const m = String(cfg.value.luogu || '').match(/(?:user\/)?(\d+)/)
  return m ? m[1] : ''
}
function nowcoderId() {
  const m = String(cfg.value.nowcoder || '').match(/(?:id=)?(\d+)/)
  return m ? m[1] : ''
}
</script>

<template>
  <div class="glass stats-card">
    <div class="head">
      <h2>算法战绩</h2>
      <span class="sub">一路刷题记录</span>
    </div>

    <div class="grid" v-if="loaded">
      <!-- Codeforces -->
      <div class="cell" v-if="cfg.cfHandle">
        <span class="logo cf">
          <img v-if="!imgErr.cf" :src="LOGOS.cf" alt="" @error="imgErr.cf = true" />
          <template v-else>CF</template>
        </span>
        <div class="info">
          <b>{{ cfg.cfHandle }}</b>
          <template v-if="cf">
            <span v-if="typeof cf.rating === 'number'" class="rating" :style="{ color: cf.rankColor }">{{ cf.rating }} · {{ cf.rank }}</span>
            <span v-else class="rating" :style="{ color: cf.rankColor }">Unrated · 尚未定级</span>
            <span v-if="typeof cf.maxRating === 'number'" class="detail">最高 {{ cf.maxRating }}</span>
          </template>
          <span v-else-if="cfErr" class="detail err">拉取失败（网络或被限流）</span>
          <span v-else class="detail">加载中…</span>
        </div>
        <a :href="`https://codeforces.com/profile/${encodeURIComponent(cfg.cfHandle)}`" target="_blank" rel="noopener" class="go">去主页 →</a>
      </div>

      <!-- 洛谷 -->
      <div class="cell" v-if="cfg.luogu">
        <span class="logo lg">
          <img v-if="!imgErr.lg" :src="LOGOS.lg" alt="" @error="imgErr.lg = true" />
          <template v-else>洛谷</template>
        </span>
        <div class="info">
          <b>洛谷主页</b>
          <span class="detail">个人主页</span>
        </div>
        <a :href="`https://www.luogu.com.cn/user/${luoguUser()}`" target="_blank" rel="noopener" class="go">去看看 →</a>
      </div>

      <!-- 牛客 -->
      <div class="cell" v-if="cfg.nowcoder">
        <span class="logo nc">
          <img v-if="!imgErr.nc" :src="LOGOS.nc" alt="" @error="imgErr.nc = true" />
          <template v-else>牛客</template>
        </span>
        <div class="info">
          <b>牛客主页</b>
          <span class="detail">个人主页</span>
        </div>
        <a :href="nowcoderId() ? `https://ac.nowcoder.com/acm/contest/profile/${nowcoderId()}` : '#'" target="_blank" rel="noopener" class="go">去看看 →</a>
      </div>

      <div class="cell empty" v-if="!cfg.cfHandle && !cfg.luogu && !cfg.nowcoder">
        <span>后台「网站设置」填好平台账号后自动显示</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-card {
  flex: 1;
  padding: 12px 14px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}
.head { display: flex; align-items: baseline; gap: 8px; margin-bottom: 8px; }
.head h2 { margin: 0; font-size: 14px; color: var(--text-h); }
.sub { color: var(--text-muted); font-size: 11px; }
.grid { display: flex; flex-direction: column; gap: 8px; flex: 1; }

.cell {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 6px 12px;
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.35);
}
.logo {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 800;
  font-size: 13px;
  flex-shrink: 0;
  overflow: hidden;
}
.logo img { width: 100%; height: 100%; object-fit: cover; display: block; }
.cf { background: #1f8acb; }
.lg { background: #e91e63; }
.nc { background: #2db55d; }
.info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.info b { color: var(--text-h); font-size: 15px; }
.rating { font-weight: 800; font-size: 15px; }
.detail { color: var(--text-muted); font-size: 12px; }
.err { color: #ef4444; }
.go { font-size: 13px; color: var(--accent); flex-shrink: 0; }
.empty { justify-content: center; color: var(--text-muted); font-size: 12px; }
</style>
