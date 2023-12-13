import i18n from "i18next";
import { initReactI18next } from "react-i18next";

import Backend from "i18next-http-backend";
import LanguageDetector from "i18next-browser-languagedetector";
import { login } from "./services/auth";

i18n
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    debug: false,
    interpolation: {
      escapeValue: false,
    },
    fallbackLng: "en", 
    detection: {
      order: ['querystring', 'cookie', 'localStorage', 'navigator', 'htmlTag'], 
    },
    backend: {
      loadPath: (lng) => {
        // Map variations like "en-AU" to "en" and "es-ES" to "es"
        const baseLang = lng[0].split('-')[0];
        if (baseLang === 'es'){
          return `${process.env.PUBLIC_URL}/locales/es/translation.json`;
        }
        return `${process.env.PUBLIC_URL}/locales/en/translation.json`;
     },
    },
  });

  export default i18n;