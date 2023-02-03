import React from "react";
import { useTranslation } from "react-i18next";
import "../resources/styles/argon-design-system.css";
import "../resources/styles/blk-design-system.css";
import "../resources/styles/general.css";
import "../resources/styles/stepper.css";

import Background from "../components/Background";

import { t } from "i18next";
import { Link, useNavigate } from "react-router-dom";
import { env } from "process";

const InformationPane = (props: {
  title: string;
  bodyText: string;
  buttonText: string;
  linkReference: string;
}) => {
  const { t } = useTranslation();
  return (
    <div>
      <div className="card card-lift--hover shadow border-0">
        <div className="card-body">
          <div className="icon icon-shape icon-shape-primary rounded-circle mb-4">
            <i className="ni ni-check-bold"></i>
          </div>
          <p className="h3 text-primary">{props.title}</p>
          <p className="fs-5 description my-3">{props.bodyText}</p>
          <Link className="btn btn-primary" to={props.linkReference}>
            {props.buttonText}
          </Link>
        </div>
      </div>
    </div>
  );
};

const LandingPane = () => {
  const navigate = useNavigate();
  function search() {
    let query = (document.getElementById("query") as HTMLInputElement).value;
    navigate("/search/questions?query=" + query);
  }
  const { t } = useTranslation();
  return (
    <>
      <div className="container">
        <div className="white-pill">
          <p className="h1 text-primary">
            <strong>{t("askAway")}</strong>
          </p>
          <p className="h3 mx-5">{t("landing.slogan")}</p>
          {/* Barra de busquedas */}
          <div className="form-group mx-5">
            {/* <spring:message code="search" var="search"/>
                                <spring:message code="landing.searchCallToAction" var="searchPlaceholder"/> */}

            <div className="input-group">
              <input
                className="form-control rounded"
                type="search"
                name="query"
                id="query"
                placeholder={t("placeholder.searchQuestion")}
              />
              <input
                className="btn btn-primary"
                type="submit"
                value={t("search")}
                onClick={search}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="container-xl mt-5">
        <div className="row">
          <div className="col">
            <InformationPane
              title={t("landing.question.trigger")}
              bodyText={t("landing.question.callToAction")}
              buttonText={t("ask")}
              linkReference="/ask/selectCommunity"
            />
          </div>
          <div className="col">
            <InformationPane
              title={t("landing.communities.message")}
              bodyText={t("landing.communities.callToAction")}
              buttonText={t("create")}
              linkReference="/community/create"
            />
          </div>
          <div className="col">
            <InformationPane
              title={t("landing.discover")}
              bodyText={t("landing.discoverCallToAction")}
              buttonText={t("discover")}
              linkReference="/search/questions"
            />
          </div>
        </div>
      </div>
    </>
  );
};

const LandingPage = () => {
  return (
    <>
      <div>
        <div className="section section-hero section-shaped">
          <Background />
          <LandingPane />
        </div>
      </div>
    </>
  );
};

export default LandingPage;
