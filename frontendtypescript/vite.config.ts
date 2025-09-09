import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react({
      include: "**/*.tsx"
    })
  ],
  server: {
    open: true,
    watch: {
      usePolling: true
    }
  },
  rollupOptions: {
    external: ["react-i18next", "i18next"]
  }
});
