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
