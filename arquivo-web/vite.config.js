import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
  },
  build: {
    sourcemap: true, // Enable source maps temporarily for debuggingare disabled for production builds
  },
  server: {
    sourcemap: true, // Enable source maps temporarily for debugging development
  },
});
