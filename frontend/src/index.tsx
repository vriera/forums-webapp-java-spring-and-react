import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { I18nextProvider } from "react-i18next";
import i18n from "./i18n";
import "./resources/styles/argon-design-system.css";
import "./resources/styles/blk-design-system.css";
import "./resources/styles/general.css";
import "./resources/styles/stepper.css";
import { BrowserRouter as Router} from "react-router-dom";
const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <I18nextProvider i18n={i18n}>
      <Router basename={`${process.env.PUBLIC_URL}`}>
        <App />
      </Router>
    </I18nextProvider>
  </React.StrictMode>
);
