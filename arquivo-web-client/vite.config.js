import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
//import dns from 'dns';

// localhost part
//dns.setDefaultResultOrder('verbatim');

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
  //  cors: false,
    host: true,
    port: 80,
    origin: 'http://13.50.15.148'
  },
})

