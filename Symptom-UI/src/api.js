import axios from 'axios'

const api = axios.create({
  baseURL: '', // '' means same origin → Vite proxy will forward /api
  // baseURL: import.meta.env.VITE_API_BASE || '', // uncomment if not using proxy
})

// Attach JWT from localStorage before each request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token') // you said you already store it
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
