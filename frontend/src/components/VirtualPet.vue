<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { BLOG } from '../config'

const POOL = [
  'public class Main {',
  '  public static void main() {',
  '    int bug = 999;',
  '    while (bug > 0) {',
  '      pet.catchBug(); // 我来!',
  '      snack = fish() + coffee();',
  '    }',
  '    System.out.println("喵, 通过!");',
  '  }',
  '}',
]

const MOOD_MSG = {
  normal: '正努力写 bug 中...',
  happy: '喵呜! 收到投喂~',
  sleepy: 'zzZ 好困, 眼睛快闭上了',
  busy: '别闹, 我正调 bug 呢!',
}
const EYES = { normal: 'o.o', happy: '^_^', sleepy: '-.-', busy: '>.<' }
const CAT_ART = ['    /\\_/\\', '   ( EE )', '   (---)']

const mood = ref('normal')
const moodText = ref(MOOD_MSG.normal)
const eyes = computed(() => EYES[mood.value])
const catArt = computed(() => CAT_ART.map((l) => l.replace('EE', eyes.value)))

// ---------- 终端：逐字符敲代码 ----------
const buffer = ref('')
const typing = ref(true)
const cursor = ref(true)
let typeTimer = null
let cursorTimer = null
let pos = 0

const fullCode = computed(() => POOL.map((l, i) => (i === POOL.length - 1 ? l : l + '\n')).join(''))
const shown = computed(() => buffer.value.split('\n').slice(-4).join('\n'))

function tick() {
  if (!typing.value) return
  if (pos >= fullCode.value.length) {
    buffer.value = ''
    pos = 0
    return
  }
  buffer.value = fullCode.value.slice(0, pos + 1)
  pos++
  lightKey() // 打一个字，敲一下键
}

function startTyping() {
  clearInterval(typeTimer)
  typeTimer = setInterval(tick, 150)
}

function toggleTyping() {
  typing.value = !typing.value
  if (typing.value) startTyping()
  else clearInterval(typeTimer)
}

// ---------- 键盘：多排键帽 + 两只随机按下的爪 ----------
const kbRows = [
  { off: 0, n: 13, type: 'top' },
  { off: 13, n: 12, type: 'mid' },
  { off: 25, n: 11, type: 'bot' },
]

const litSet = ref([])          // 本次被按下的两个键位
const pawL = ref({ x: 0, y: 0 }) // 左爪落点（相对键盘 px）
const pawR = ref({ x: 0, y: 0 }) // 右爪落点

// 接近真实键盘的左右手键位（data-kb 序号）
const LEFT_IDX = [0, 1, 2, 3, 4, 5, 13, 14, 15, 16, 17, 25, 26, 27, 28, 29]
const RIGHT_IDX = [7, 8, 9, 10, 11, 12, 19, 20, 21, 22, 23, 24, 31, 32, 33, 34, 35]

function lightKey() {
  const kb = document.querySelector('.kb')
  const kbRect = kb?.getBoundingClientRect()
  if (!kbRect) return

  const locate = (idx) => {
    const el = document.querySelector(`[data-kb="${idx}"]`)
    if (!el) return null
    const r = el.getBoundingClientRect()
    return { idx, x: r.left - kbRect.left + r.width / 2, y: r.top - kbRect.top }
  }

  const leftCands = LEFT_IDX.map(locate).filter(Boolean)
  const rightCands = RIGHT_IDX.map(locate).filter(Boolean)
  if (!leftCands.length || !rightCands.length) return

  const pick = (arr) => arr[Math.floor(Math.random() * arr.length)]
  const lc = pick(leftCands)
  const rc = pick(rightCands)
  litSet.value = [lc.idx, rc.idx]
  pawL.value = { x: lc.x, y: lc.y }
  pawR.value = { x: rc.x, y: rc.y }

  // 每敲一次刷新熄灭计时：连续打字灯常亮于新键，停顿后自然熄灭
  clearTimeout(lightKey.t)
  lightKey.t = setTimeout(() => (litSet.value = []), 200)
}
function isLit(off, c) {
  return litSet.value.includes(off + c)
}

// ---------- 互动 ----------
const moodOrder = ['normal', 'happy', 'sleepy', 'busy']
function poke() {
  const i = moodOrder.indexOf(mood.value)
  mood.value = moodOrder[(i + 1) % moodOrder.length]
  moodText.value = MOOD_MSG[mood.value]
  setTimeout(() => {
    mood.value = 'normal'
    moodText.value = MOOD_MSG.normal
  }, 3600)
}

onMounted(() => {
  startTyping()
  setTimeout(lightKey, 50) // 初始把爪子放到正确键位上
  cursorTimer = setInterval(() => (cursor.value = !cursor.value), 480)
})
onBeforeUnmount(() => {
  clearInterval(typeTimer)
  clearInterval(cursorTimer)
})
</script>

<template>
  <div class="devcat">
    <!-- 状态条 -->
    <div class="bar">
      <span class="led" :class="{ off: !typing }"></span>
      <span class="title">{{ BLOG.author }}</span>
      <span class="status">{{ moodText }}</span>
    </div>

    <!-- 代码板（深色背景） -->
    <div class="term" title="点击暂停 / 继续" @click="toggleTyping">
      <pre>{{ shown }}<span class="cur" :class="{ off: !cursor || !typing }"></span></pre>
    </div>

    <!-- 小猫 + 键盘 -->
    <div class="stage">
      <pre class="cat" :class="mood" title="戳一戳小猫" @click="poke">{{ catArt.join('\n') }}</pre>
      <div class="kb">
        <div class="paws">
          <span class="paw p-l" :style="{ left: pawL.x + 'px', top: pawL.y + 'px' }"></span>
          <span class="paw p-r" :style="{ left: pawR.x + 'px', top: pawR.y + 'px' }"></span>
        </div>
        <div v-for="(row, ri) in kbRows" :key="ri" class="kb-row" :class="row.type">
          <span
            v-for="c in row.n"
            :key="c"
            class="keycap"
            :data-kb="row.off + c - 1"
            :class="{ lit: isLit(row.off, c - 1) }"
          ></span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.devcat {
  position: fixed;
  right: 14px;
  bottom: 14px;
  width: 320px;
  max-width: calc(100vw - 24px);
  z-index: 900;
  font-family: ui-monospace, 'Cascadia Mono', Consolas, 'Microsoft YaHei', monospace;
  color: #334155;
  user-select: none;
}

/* 状态条 */
.bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px 6px 4px;
  color: #64748b;
  font-size: 11px;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}
.led { width: 8px; height: 8px; border-radius: 50%; background: #22c55e; box-shadow: 0 0 6px #22c55e; }
.led.off { background: #f59e0b; box-shadow: 0 0 6px #f59e0b; }
.title { font-weight: 800; color: var(--accent); letter-spacing: 1px; }
.status { margin-left: auto; color: #94a3b8; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* 代码板（深色） */
.term {
  padding: 8px 12px 10px;
  min-height: 84px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.92);
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.28);
}
.term pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  color: #9fe8c4;
  font-size: 12px;
  line-height: 1.5;
}
.cur {
  display: inline-block;
  width: 6px;
  height: 12px;
  margin-left: 1px;
  vertical-align: text-bottom;
  background: #4ade80;
  animation: curBlink 1s step-end infinite;
}
.cur.off { animation: none; opacity: 0; }
@keyframes curBlink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

/* 小猫 */
.stage {
  position: relative;
  margin-top: 2px;
  padding: 6px 8px 8px;
}
.cat {
  position: relative;
  z-index: 2;
  margin: 0;
  text-align: center;
  color: #c2410c;
  font-size: 13px;
  line-height: 1.35;
  font-weight: 700;
  cursor: pointer;
  transform-origin: bottom center;
  animation: catBreathe 3s ease-in-out infinite;
}
.cat.happy { color: #a16207; animation: catJump 0.4s ease; }
.cat.sleepy { color: #4338ca; }
.cat.busy { color: #be185d; }

/* 键盘 */
.kb {
  position: relative;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.kb-row {
  display: flex;
  gap: 4px;
  justify-content: center;
}
.kb-row.top { padding: 0 6px; }
.kb-row.mid { padding: 0 14px; }
.kb-row.bot { padding: 0 22px; }

/* 猫爪：随机落在键盘任一键上按下 */
.paws {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 4;
  pointer-events: none;
}
.paw {
  position: absolute;
  width: 16px;
  height: 13px;
  margin-left: -8px; /* 相对 left 居中 */
  background: #d97706;
  border-radius: 50% 50% 55% 55% / 60% 60% 30% 30%;
  box-shadow: 0 2px 3px rgba(0, 0, 0, 0.25);
  transition: left 0.14s ease, top 0.14s ease;
  animation: tapMove 0.2s ease-in-out infinite alternate;
}
.paw::before,
.paw::after {
  content: '';
  position: absolute;
  top: 0;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #b45309;
}
.paw::before { left: 2px; }
.paw::after { right: 2px; }
.p-l { animation-delay: 0s; }
.p-r { animation-delay: 0.1s; }
@keyframes tapMove { from { transform: translateY(0); } to { transform: translateY(5px); } }

.keycap {
  flex: 1;
  height: 14px;
  border-radius: 4px;
  background: #fff;
  border: 1px solid rgba(100, 116, 139, 0.25);
  box-shadow:
    0 2px 0 rgba(100, 116, 139, 0.28),
    inset 0 -3px 0 rgba(148, 163, 184, 0.28);
  transition: transform 0.04s, background 0.04s;
}
.keycap.lit {
  background: linear-gradient(180deg, var(--accent), var(--accent-2));
  border-color: transparent;
  box-shadow: 0 0 10px var(--accent);
  transform: translateY(2px);
}

@keyframes catBreathe { 0%, 100% { transform: scaleY(1); } 50% { transform: scaleY(0.96); } }
@keyframes catJump { 0% { transform: translateY(0); } 40% { transform: translateY(-4px); } 100% { transform: translateY(0); } }

/* 手机端隐藏 */
@media (max-width: 760px) {
  .devcat { display: none; }
}
</style>
