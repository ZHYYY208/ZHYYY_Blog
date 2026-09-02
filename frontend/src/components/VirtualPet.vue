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
}

function startTyping() {
  clearInterval(typeTimer)
  typeTimer = setInterval(tick, 90)
}

function toggleTyping() {
  typing.value = !typing.value
  if (typing.value) startTyping()
  else clearInterval(typeTimer)
}

// ---------- 键盘高亮 ----------
const keys = ref(Array.from({ length: 9 }, (_, i) => i))
const litKey = ref(-1)
let keyTimer = null
function lightKey() {
  litKey.value = Math.floor(Math.random() * keys.value.length)
  setTimeout(() => (litKey.value = -1), 140)
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
  keyTimer = setInterval(lightKey, 240)
  cursorTimer = setInterval(() => (cursor.value = !cursor.value), 480)
})
onBeforeUnmount(() => {
  clearInterval(typeTimer)
  clearInterval(keyTimer)
  clearInterval(cursorTimer)
})
</script>

<template>
  <div class="devcat">
    <div class="bar">
      <span class="led" :class="{ off: !typing }"></span>
      <span class="title">{{ BLOG.author }}</span>
      <span class="status">{{ moodText }}</span>
    </div>

    <div class="term" title="点击暂停 / 继续" @click="toggleTyping">
      <pre>{{ shown }}<span class="cur" :class="{ off: !cursor || !typing }"></span></pre>
    </div>

    <div class="stage" title="戳一戳小猫" @click="poke">
      <pre class="cat" :class="mood">{{ catArt.join('\n') }}</pre>
      <div class="paws">
        <div class="paw p-l"></div>
        <div class="paw p-r"></div>
      </div>
      <div class="keys">
        <span v-for="k in keys" :key="k" class="key" :class="{ lit: k === litKey }"></span>
      </div>
    </div>

    <div class="hint">点代码暂停 · 戳猫互动</div>
  </div>
</template>

<style scoped>
.devcat {
  position: fixed;
  right: 16px;
  bottom: 16px;
  width: 300px;
  max-width: calc(100vw - 32px);
  z-index: 900;
  background: rgba(255, 255, 255, 0.55);
  -webkit-backdrop-filter: blur(20px);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  border: 1px solid var(--glass-border);
  box-shadow: var(--glass-shadow);
  overflow: hidden;
  font-family: ui-monospace, 'Cascadia Mono', Consolas, 'Microsoft YaHei', monospace;
  color: #334155;
  user-select: none;
}

.bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.35);
  border-bottom: 1px solid rgba(148, 163, 184, 0.25);
  color: #64748b;
  font-size: 11px;
}
.led { width: 8px; height: 8px; border-radius: 50%; background: #22c55e; box-shadow: 0 0 8px #22c55e; }
.led.off { background: #f59e0b; box-shadow: 0 0 8px #f59e0b; }
.title { font-weight: 800; color: var(--accent); letter-spacing: 1px; }
.status { margin-left: auto; color: #94a3b8; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.term {
  padding: 8px 12px;
  height: 82px;
  overflow: hidden;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.35);
  margin: 0 8px;
  border-radius: 10px;
}
.term pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  color: #475569;
  font-size: 12px;
  line-height: 1.5;
}
.cur {
  display: inline-block;
  width: 6px;
  height: 12px;
  margin-left: 1px;
  vertical-align: text-bottom;
  background: var(--accent);
  animation: curBlink 1s step-end infinite;
}
.cur.off { animation: none; opacity: 0; }
@keyframes curBlink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

.stage {
  position: relative;
  padding: 4px 12px 8px;
  cursor: pointer;
  background: radial-gradient(ellipse at 50% 130%, var(--accent-bg), transparent 60%);
}
.cat {
  margin: 0;
  text-align: center;
  color: #f59a45;
  font-size: 13px;
  line-height: 1.35;
  transform-origin: bottom center;
  animation: catBreathe 3s ease-in-out infinite;
}
.cat.happy { color: #eab308; animation: catJump 0.4s ease; }
.cat.sleepy { color: #7c9cf7; }
.cat.busy { color: #f472b6; }

.paws { position: relative; height: 14px; margin-top: -2px; }
.paw {
  position: absolute;
  top: 0;
  width: 18px;
  height: 12px;
  background: #f59a45;
  border-radius: 45% 45% 55% 55% / 70% 70% 30% 30%;
  box-shadow: 0 2px 3px rgba(0, 0, 0, 0.2);
  animation: tap 0.24s ease-in-out infinite alternate;
}
.p-l { left: 34%; }
.p-r { right: 34%; animation-delay: 0.12s; }

.keys {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-top: 2px;
  padding: 0 6px 2px;
}
.key {
  width: 18px;
  height: 8px;
  border-radius: 3px;
  background: rgba(100, 116, 139, 0.18);
  transition: background 0.05s;
}
.key.lit { background: var(--accent); box-shadow: 0 0 8px var(--accent); }

.hint {
  padding: 3px 0 6px;
  text-align: center;
  font-size: 10px;
  color: #94a3b8;
}

@keyframes tap { from { transform: translateY(2px); } to { transform: translateY(6px); } }
@keyframes catBreathe { 0%, 100% { transform: scaleY(1); } 50% { transform: scaleY(0.96); } }
@keyframes catJump { 0% { transform: translateY(0); } 40% { transform: translateY(-4px); } 100% { transform: translateY(0); } }

/* 手机端隐藏小猫 */
@media (max-width: 760px) {
  .devcat { display: none; }
}
</style>
