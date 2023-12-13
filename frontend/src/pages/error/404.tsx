import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import Background from "../../components/Background";
import { createBrowserHistory } from "history";

const Page404 = () => {
  const { t } = useTranslation();

  const history = createBrowserHistory();

  const handleBackButtonClick = () => {
    //go back in history to two pages before
    history.go(-2);
  };

  return (
    <div className="section section-hero section-shaped">
      <Background />
      <div className="container">
        <div className="white-pill">
          <p className="h1 text-primary">
            <strong>AskAway</strong>
          </p>
          <p className="h3 mx-5">{t("error.cantFindPage")}</p>
          <div className="d-flex justify-content-center">
            <img
              src={require("../../images/error404.png")}
              width={500}
              height={500}
              alt="No hay nada para mostrar"
            />
          </div>
          <button className="btn btn-primary" onClick={handleBackButtonClick}>
            {" "}
            {t("volver")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default Page404;
