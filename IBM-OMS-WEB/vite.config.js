import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server runs on 5173. API base is configurable via VITE_API_BASE_URL and
// defaults to the OMS backend on localhost:8081.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
});
