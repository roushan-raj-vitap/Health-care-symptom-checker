import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // your Spring Boot backend
        changeOrigin: true,
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
