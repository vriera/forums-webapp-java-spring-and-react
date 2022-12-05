import React from "react";
import {User, Notification} from "./../models/UserTypes"
import { useTranslation } from "react-i18next";
import { Link } from 'react-router-dom';

const DashboardPane = (props: {option: string, user: User, notifications: Notification }) => {
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
                <Link to="/dashboard/profile/info" className={"h5 nav-link link-dark w-100 " + (props.option === "profile" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                </Link>
                </li>

                <li>
                    <Link to="/dashboard/questions" className={"h5 nav-link link-dark w-100 " + (props.option === "questions" && "active")}>
                        <i className="fas fa-question mr-3"></i>
                        {t("dashboard.questions")}
                    </Link>
                    
                </li>
                <li>
                    <Link to="/dashboard/answers" className={"h5 nav-link link-dark w-100 " + (props.option === "answers" && "active")}>
                        <i className="fas fa-reply mr-3"></i>
                        {t("dashboard.answers")}
                    </Link>
                </li>
                <li>
                    <Link to="/dashboard/access" className={"h5 nav-link link-dark w-100 " + (props.option === "access" && "active")}>
                        <i className="fas fa-envelope mr-3"></i>
                        {t("dashboard.access")}
                        {props.notifications.total > 0 &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.total}</span>
                        }    
                    </Link>
                </li>
                <li>
                    <Link to="/dashboard/communities/10/admitted" className={"h5 nav-link link-dark w-100 " + (props.option === "communities" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.communities")}
                    </Link>
                </li>
            </ul>
        </div>
    )
}

export default DashboardPane

