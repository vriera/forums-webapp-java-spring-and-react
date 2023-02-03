import React from "react";
import { useTranslation } from "react-i18next";

const CreateCommunityPane = () => {
  const { t } = useTranslation();

  return (
    // <%--CREATE COMMUNITY--%>
    <div className="white-pill mt-5 mr-3">
      <div className="card-body">
        <p className="h3 text-primary text-center">
          {t("title.createCommunity")}
        </p>
        <hr />
        <p className="h5 my-3">{t("subtitle.createCommunity")}</p>
        <a className="btn btn-primary" href="/community/create">
          {t("button.createCommunity")}
        </a>
      </div>
    </div>
  );
};

export default CreateCommunityPane;
