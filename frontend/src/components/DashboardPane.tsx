import React from "react";
import {User, Notification} from "./../models/UserTypes"
import { useTranslation } from "react-i18next";

const DashboardPane = (props: {option: string /*"profile" | "questions" | "answers" | "communities"*/, user: User, notifications: Notification, optionCallbacks: any}) => {
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
                    <button onClick={props.optionCallbacks.profileCallback} className={"h5 nav-link link-dark w-100 " + (props.option === "profile" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                    </button>
                </li>

                <li>
                    <button onClick={props.optionCallbacks.questionsCallback} className={"h5 nav-link link-dark w-100 " + (props.option === "questions" && "active")}>
                        <i className="fas fa-question mr-3"></i>
                        {t("dashboard.questions")}
                    </button>
                </li>
                <li>
                    <button onClick={props.optionCallbacks.answersCallback} className={"h5 nav-link link-dark w-100 " + (props.option === "answers" && "active")}>
                        <i className="fas fa-reply mr-3"></i>
                        {t("dashboard.answers")}
                    </button>
                </li>
                <li>
                    <button onClick={props.optionCallbacks.communitiesCallback} className={"h5 nav-link link-dark w-100 " + (props.option === "communities" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.communities")}
                        
                        {props.notifications.total > 0 &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.total}</span>
                        }                        
                    </button>
                </li>
            </ul>
        </div>
    )
}

export default DashboardPane

