const BASE = import.meta.env.VITE_API_BASE || '/api'

let adminToken = ''

export function setAdminToken(t) {
  adminToken = t
}
export function clearAdminToken() {
  adminToken = ''
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' }
  if (adminToken) headers['X-Admin-Token'] = adminToken
  const res = await fetch(`${BASE}${path}`, { headers, ...options })
  if (res.status === 401) {
    clearAdminToken()
    throw new Error('UNAUTHORIZED')
  }
  if (!res.ok) {
    throw new Error(`请求失败: ${res.status}`)
  }
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  get: (path) => request(path),
  post: (path, body) =>
    request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) =>
    request(path, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (path) => request(path, { method: 'DELETE' }),
}

export async function upload(path, file, field = 'file', extra = {}) {
  const fd = new FormData()
  fd.append(field, file)
  for (const [k, v] of Object.entries(extra)) fd.append(k, v)
  const headers = {}
  if (adminToken) headers['X-Admin-Token'] = adminToken
  const ctrl = new AbortController()
  const timer = setTimeout(() => ctrl.abort(), 120000)
  try {
    const res = await fetch(`${BASE}${path}`, { method: 'POST', headers, body: fd, signal: ctrl.signal })
    if (res.status === 401) {
      clearAdminToken()
      throw new Error('UNAUTHORIZED')
    }
    if (!res.ok) throw new Error(`上传失败: ${res.status}`)
    return res.json()
  } finally {
    clearTimeout(timer)
  }
}

// ---------- 留言用户系统（独立于 admin） ----------
const USER_TOKEN_KEY = 'user_token'
const USER_NAME_KEY = 'user_name'
const USER_EXPIRE_KEY = 'user_expire'
const USER_TTL = 5 * 24 * 3600 * 1000 // 5 天

export function setUserSession(token, name) {
  localStorage.setItem(USER_TOKEN_KEY, token)
  if (name) localStorage.setItem(USER_NAME_KEY, name)
  localStorage.setItem(USER_EXPIRE_KEY, String(Date.now() + USER_TTL))
}

export function clearUserSession() {
  localStorage.removeItem(USER_TOKEN_KEY)
  localStorage.removeItem(USER_NAME_KEY)
  localStorage.removeItem(USER_EXPIRE_KEY)
}

export function isUserLoggedIn() {
  const t = localStorage.getItem(USER_TOKEN_KEY)
  if (!t) return false
  const exp = Number(localStorage.getItem(USER_EXPIRE_KEY) || 0)
  if (!exp || Date.now() > exp) {
    clearUserSession()
    return false
  }
  return true
}

function userToken() {
  if (!isUserLoggedIn()) return ''
  return localStorage.getItem(USER_TOKEN_KEY) || ''
}

async function uReq(path, options = {}, needAuth = false) {
  const headers = { 'Content-Type': 'application/json' }
  const t = userToken()
  if (t) headers['X-User-Token'] = t
  const res = await fetch(`${BASE}${path}`, { headers, ...options })
  if (res.status === 401) {
    if (needAuth) {
      clearUserSession()
    }
    const err = new Error('UNAUTHORIZED')
    err.status = 401
    throw err
  }
  if (!res.ok) {
    let msg = `请求失败: ${res.status}`
    try {
      const j = await res.json()
      if (j && j.error) msg = j.error
    } catch {}
    const err = new Error(msg)
    err.status = res.status
    throw err
  }
  if (res.status === 204) return null
  return res.json()
}

export const userApi = {
  register: (username, password, inviteCode) =>
    uReq('/user/register', { method: 'POST', body: JSON.stringify({ username, password, inviteCode: inviteCode || '' }) }),
  login: (username, password) => uReq('/user/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
  logout: () => uReq('/user/logout', { method: 'POST' }),
  me: () => uReq('/user/me'),
  messages: () => uReq('/messages'),
  postMessage: (content, username) =>
    uReq('/messages', { method: 'POST', body: JSON.stringify({ content, username }) }, true),
  deleteMessage: (id) =>
    uReq(`/messages/${id}`, { method: 'DELETE' }, true),
}
