import React from "react";
import {User, Notification} from "./../models/UserTypes"
import { useTranslation } from "react-i18next";

const DashboardPane = (props: {option: string, user: User, notifications: Notification, optionCallback: (option: "profile" | "questions" | "answers" | "communities" | "access") => void }) => {
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
                    <button onClick={() => props.optionCallback("profile")} className={"h5 nav-link link-dark w-100 " + (props.option === "profile" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                    </button>
                </li>

                <li>
                    <button onClick={() => props.optionCallback("questions")} className={"h5 nav-link link-dark w-100 " + (props.option === "questions" && "active")}>
                        <i className="fas fa-question mr-3"></i>
                        {t("dashboard.questions")}
                    </button>
                </li>
                <li>
                    <button onClick={() => props.optionCallback("answers")} className={"h5 nav-link link-dark w-100 " + (props.option === "answers" && "active")}>
                        <i className="fas fa-reply mr-3"></i>
                        {t("dashboard.answers")}
                    </button>
                </li>
                <li>
                    <button onClick={() => props.optionCallback("access")} className={"h5 nav-link link-dark w-100 " + (props.option === "access" && "active")}>
                        <i className="fas fa-envelope mr-3"></i>
                        {t("dashboard.access")}
                    </button>
                </li>
                <li>
                    <button onClick={() => props.optionCallback("communities")} className={"h5 nav-link link-dark w-100 " + (props.option === "communities" && "active")}>
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

