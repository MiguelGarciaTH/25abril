import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import dns from 'dns';

// localhost part
dns.setDefaultResultOrder('verbatim');

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    cors: false,
    host: "13.50.15.148",
    port: 80,
  },
})

