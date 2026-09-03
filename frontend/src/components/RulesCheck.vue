<script setup>
import { ref } from 'vue'

defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])
const open = ref(false)

const rules = [
  '遵守国家法律法规，不发布违法、犯罪类内容。',
  '不传播色情、暴力、血腥及血腥猎奇内容。',
  '不发布涉及国家秘密、危害国家安全、损害国家荣誉和利益的内容。',
  '不散布谣言，不虚构、歪曲事实，不煽动对立。',
  '不使用侮辱、诽谤、人身攻击、地域歧视等不友善言论。',
  '不发送垃圾广告、刷屏、外链引流等无关内容。',
  '不泄露他人隐私（姓名、电话、住址等）。',
  '不冒充他人、不以他人名义发言。',
  '不讨论违法违规的翻墙、黑客入侵、盗版等行为。',
  '尊重版权，不发布侵权内容。',
  '发言内容仅代表个人观点，与本站立场无关。',
]
</script>

<template>
  <div class="rulescheck">
    <label>
      <input
        type="checkbox"
        :checked="modelValue"
        @change="$emit('update:modelValue', $event.target.checked)"
      />
      <span class="txt">我已阅读并同意</span>
      <a class="lnk" @click.prevent="open = true">《留言规范》</a>
    </label>

    <Teleport to="body">
      <div v-if="open" class="mask" @click.self="open = false">
        <div class="box glass">
          <div class="box-head">
            <b>留言规范与免责声明</b>
            <button class="close" @click="open = false">×</button>
          </div>
          <div class="box-body">
            <p class="lead">欢迎来到留言板。为维护良好的交流环境，请仔细阅读并遵守以下条款：</p>
            <ol>
              <li v-for="(r, i) in rules" :key="i">{{ r }}</li>
            </ol>
            <p class="legal">
              1. 本留言板由站长独立管理，站长有权删除任何违反规范的内容，且无需另行通知。<br />
              2. 请文明发言，理性交流；发表留言即视为已阅读并同意本规范。<br />
              3. 如遇违法违规内容，站长将配合相关法律法规进行处理。<br />
              4. 留言所表达观点仅代表留言者本人，与本站无关。
            </p>
          </div>
          <div class="box-foot">
            <button class="agree" @click="emit('update:modelValue', true); open = false">同意并继续</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.rulescheck label {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}
.txt { color: var(--text); }
.lnk { color: var(--accent); cursor: pointer; text-decoration: underline; }

.mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(4px);
  display: grid;
  place-items: center;
  padding: 16px;
}
.box {
  width: 100%;
  max-width: 560px;
  max-height: 60vh;
  height: 54vh;
  display: flex;
  flex-direction: column;
  padding: 16px 20px;
  border-radius: 16px;
  box-sizing: border-box;
  overflow: hidden;
}
.box-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--text-h);
  padding-bottom: 10px;
  border-bottom: 1px solid var(--glass-border);
  flex-shrink: 0;
}
.close {
  border: none;
  background: var(--accent-bg);
  color: var(--accent);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 15px;
  line-height: 1;
}
.box-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
  padding: 12px 6px 4px;
  color: var(--text-h);
  font-size: 14px;
  line-height: 1.9;
}
.box-body::-webkit-scrollbar { width: 6px; }
.box-body::-webkit-scrollbar-thumb { background: var(--accent-bg); border-radius: 999px; }
.box-body .lead { margin: 0 0 8px; color: var(--text-h); font-weight: 700; font-size: 15px; }
.box-body ol { margin: 0; padding-left: 20px; }
.box-body li { margin-bottom: 7px; color: var(--text-h); }
.box-body .legal {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--glass-border);
  color: var(--text);
  font-size: 13px;
}
.box-foot {
  padding-top: 12px;
  flex-shrink: 0;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--glass-border);
  margin-top: 8px;
}
.agree {
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  border: none;
  color: #fff;
  border-radius: 999px;
  padding: 8px 22px;
  font-weight: 600;
  cursor: pointer;
}
</style>
