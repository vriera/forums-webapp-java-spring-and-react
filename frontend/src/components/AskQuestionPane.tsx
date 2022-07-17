import React from "react";
import { Link } from 'react-router-dom';
import { useTranslation } from "react-i18next";

const AskQuestionPane = () => {
    const { t } = useTranslation();

    return (
        <div className="white-pill mt-5 mr-3">
            <div className="card-body">
                <p className="h3 text-primary text-center">{t("title.askQuestion")}</p>
                <hr/>
                <p className="h5 my-3">{t("subtitle.askQuestion")}</p>
                <Link className="btn btn-primary" to="/ask">{t("button.askQuestion")}</Link>
            </div>
        </div>
    )
}

export default AskQuestionPane