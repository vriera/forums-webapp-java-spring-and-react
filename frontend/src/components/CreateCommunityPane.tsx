import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

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
        <Link className="btn btn-primary" to="/community/create">
          {t("button.createCommunity")}
        </Link>
      </div>
    </div>
  );
};

export default CreateCommunityPane;
