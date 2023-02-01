import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import Background from "../../components/Background";

const Page401 = () => {

    const { t } = useTranslation();
    return (
        <div className="section section-hero section-shaped">
            <Background/>
            <div className="container">
            <div className="white-pill">
                <p className="h1 text-primary"><strong>AskAway</strong></p>
                <p className="h3 mx-5">{t("error.unexpectedError")}</p>
                <div className="d-flex justify-content-center" >
                    <img  src={require('../../images/error500.png')} width={500} height={500} alt="No hay nada para mostrar"/>
                </div>
                <Link className="btn btn-primary" to={"/"}> {t("volver")}</Link>
            </div>
        </div>
        </div>
    )
}


export default Page401;