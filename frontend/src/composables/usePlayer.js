import { reactive } from 'vue'
import { api } from '../api'

let audio = null
let listenersBound = false

const state = reactive({
  tracks: [],
  loaded: false,
  index: -1,
  playing: false,
  time: 0,
  duration: 0,
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
  audio.addEventListener('timeupdate', syncTime)
  audio.addEventListener('loadedmetadata', syncTime)
  audio.addEventListener('durationchange', syncTime)
  audio.addEventListener('ended', onEnded)
  return audio
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

export function usePlayer() {
  return { state, load, toggle, next, prev, seek, playAt, fmt }
}
