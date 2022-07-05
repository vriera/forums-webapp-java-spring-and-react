import React from "react";
import {User, Notification} from "./../models/UserTypes"
import { useTranslation } from "react-i18next";

const DashboardPane = (props: {option: "profile" | "questions" | "answers" | "communities", user: User, notifications: Notification}) => {
    const { t } = useTranslation();
    return(
        <div className="white-pill d-flex flex-column mt-5" >
            {/* INFORMACION DE USUARIO*/}
            <div className="d-flex justify-content-center">
                <p className="h1 text-primary">{props.user.username}</p>
            </div>
            <div className="d-flex justify-content-center">
                <p>{t("emailEquals")}</p>
                <p>{props.user.email}</p>
            </div>
            {/* DASHBOARD - OPCIONES VERTICALES */}
            <ul className="nav nav-pills flex-column mb-auto">

                <li>
                    <a href="/dashboard/user/myProfile" className={"h5 nav-link link-dark " + (props.option == "profile" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                    </a>
                </li>

                <li>
                    <a href="/dashboard/question/view?page=0" className={"h5 nav-link link-dark " + (props.option == "questions" && "active")}>
                        <i className="fas fa-question mr-3"></i>
                        {t("dashboard.questions")}
                    </a>
                </li>
                <li>
                    <a href="/dashboard/answer/view?page=0" className={"h5 nav-link link-dark " + (props.option == "answers" && "active")}>
                        <i className="fas fa-reply mr-3"></i>
                        {t("dashboard.answers")}
                    </a>
                </li>
                <li>
                    <a href="/dashboard/community/admitted?page=0" className={"h5 nav-link link-dark " + (props.option == "communities" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.communities")}
                        
                        {props.notifications.total > 0 &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.total}</span>
                        }
                        
                    </a>
                </li>
            </ul>
        </div>
    )
}

export default DashboardPane

