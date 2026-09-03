import { reactive } from 'vue'
import { api } from '../api'

let audio = null
let preloadAudio = null
let listenersBound = false

const state = reactive({
  tracks: [],
  loaded: false,
  index: -1,
  playing: false,
  time: 0,
  duration: 0,
  buffering: false,
})

function syncTime() {
  if (!audio) return
  state.time = audio.currentTime || 0
  state.duration = audio.duration || 0
}

function onEnded() {
  next()
}

function ensureAudio() {
  if (audio) return audio
  audio = new Audio()
  audio.preload = 'auto'
  audio.addEventListener('timeupdate', syncTime)
  audio.addEventListener('loadedmetadata', syncTime)
  audio.addEventListener('durationchange', syncTime)
  audio.addEventListener('ended', onEnded)
  return audio
}

// 预热某首歌，让鼠标悬停后就缓冲，点击播放时几乎立即出声
function preload(url) {
  try {
    if (!url) return
    if (preloadAudio && preloadAudio.dataset.url === url) return
    if (!preloadAudio) {
      preloadAudio = new Audio()
      preloadAudio.preload = 'auto'
    }
    if (preloadAudio.dataset.url !== url) {
      preloadAudio.dataset.url = url
      preloadAudio.src = url
      preloadAudio.load()
    }
  } catch {}
}

export function fmt(s) {
  if (!isFinite(s)) return '0:00'
  const m = Math.floor(s / 60)
  const sec = Math.floor(s % 60)
  return `${m}:${sec.toString().padStart(2, '0')}`
}

async function load() {
  if (state.loaded) return
  try {
    state.tracks = await api.get('/music')
    state.loaded = true
    // 无播放时预载第一首，让点击首曲几乎立即出声
    if (state.tracks.length) {
      const first = state.tracks[0]
      if (first && first.url) preload(first.url)
    }
  } catch {
    state.tracks = []
    state.loaded = true
  }
}

function playAt(i) {
  if (!state.tracks.length) return
  const el = ensureAudio()
  const idx = ((i % state.tracks.length) + state.tracks.length) % state.tracks.length
  state.index = idx
  state.playing = true
  el.src = state.tracks[idx].url
  el.play().catch(() => (state.playing = false))
}

function toggle(i) {
  if (!state.tracks.length) return
  if (state.index === i) {
    const el = ensureAudio()
    if (el.paused) {
      el.play()
      state.playing = true
    } else {
      el.pause()
      state.playing = false
    }
  } else {
    playAt(i)
  }
}

function next() {
  playAt(state.index + 1)
}

function prev() {
  playAt(state.index - 1)
}

function seek(e) {
  if (!audio || !state.duration) return
  const r = e.currentTarget.getBoundingClientRect()
  const ratio = (e.clientX - r.left) / r.width
  audio.currentTime = ratio * state.duration
}

async function playById(id) {
  await load()
  const idx = state.tracks.findIndex((t) => String(t.id) === String(id))
  if (idx >= 0) playAt(idx)
}

export function usePlayer() {
  return { state, load, toggle, next, prev, seek, playAt, playById, preload, fmt }
}
