import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { persistor, store } from "./store.ts";
import { PersistGate } from "redux-persist/integration/react";
import { Provider } from "react-redux";
import { Spin } from "antd";
import axios from "axios";
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import translation from "./locale/en.json";
import { Language } from "./enums/generics.ts";

axios.defaults.baseURL = import.meta.env.VITE_BASE_API_URL;

i18n.use(initReactI18next).init({
  resources: {
    en: { translation }
  },
  lng: Language.EN
});

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Provider store={store}>
      <PersistGate loading={<Spin />} persistor={persistor}>
        <App />
      </PersistGate>
    </Provider>
  </StrictMode>
);
