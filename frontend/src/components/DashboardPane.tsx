import React, {useEffect, useState} from "react";
import {User} from "./../models/UserTypes";
import {useTranslation} from "react-i18next";
import {Link, useNavigate} from "react-router-dom";
import {getNotification, getUserFromApi} from "../services/user";

const DashboardPane = (props: { option: string }) => {
    const navigate = useNavigate();
    const [user, setUser] = useState<User>(null as unknown as User);
    const username = window.localStorage.getItem("username") as string;
    const email = window.localStorage.getItem("email") as string;

    useEffect(() => {
        async function fetchUser() {
            const userId = parseInt(window.localStorage.getItem("userId") as string);

            try {
                let auxUser = await getUserFromApi(userId);
                auxUser = await getNotification(auxUser)
                setUser(auxUser);
            } catch {
            }
        }

        fetchUser();
    }, [navigate]);

    const {t} = useTranslation();
    return (
        <div className="white-pill d-flex flex-column mt-5">
            {/* INFORMACION DE USUARIO*/}
            {username && email && (
                <>
                    <div className="d-flex justify-content-center">
                        <p className="h1 text-primary">{username}</p>
                    </div>
                    <div className="d-flex justify-content-center">
                        <p>{t("emailEquals")}</p>
                        <p>{email}</p>
                    </div>
                </>
            )}
            {(!username || !email) && (
                // Show loading spinner
                <div className="d-flex justify-content-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="sr-only">Loading...</span>
                    </div>
                </div>
            )}
            {/* DASHBOARD - OPCIONES VERTICALES */}
            <ul className="nav nav-pills flex-column mb-auto">
                <li>
                    <Link
                        to="/dashboard/profile/info"
                        className={
                            "h5 nav-link link-dark w-100 " +
                            (props.option === "profile" && "active")
                        }
                    >
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                    </Link>
                </li>

                <li>
                    <Link
                        to="/dashboard/questions"
                        className={
                            "h5 nav-link link-dark w-100 " +
                            (props.option === "questions" && "active")
                        }
                    >
                        <i className="fas fa-question mr-3"></i>
                        {t("dashboard.questions")}
                    </Link>
                </li>
                <li>
                    <Link
                        to="/dashboard/answers"
                        className={
                            "h5 nav-link link-dark w-100 " +
                            (props.option === "answers" && "active")
                        }
                    >
                        <i className="fas fa-reply mr-3"></i>
                        {t("dashboard.answers")}
                    </Link>
                </li>
                <li>
                    <Link
                        to="/dashboard/access/admitted"
                        className={
                            "h5 nav-link link-dark w-100 " +
                            (props.option === "access" && "active")
                        }
                    >
                        <i className="fas fa-envelope mr-3"></i>
                        {t("dashboard.access")}
                        {user && user.notifications && user.notifications.invites > 0 && (
                            <span className="badge badge-secondary bg-warning text-white ml-1">
                {user.notifications.total}
              </span>
                        )}
                    </Link>
                </li>
                <li>
                    <Link
                        to="/dashboard/communities/-1/admitted"
                        className={
                            "h5 nav-link link-dark w-100 " +
                            (props.option === "communities" && "active")
                        }
                    >
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.communities")}
                        {user && user.notifications && user.notifications.requests > 0 && (
                            <span className="badge badge-secondary bg-warning text-white ml-1">
                {user.notifications.total}
              </span>
                        )}
                    </Link>
                </li>
            </ul>
        </div>
    );
};

export default DashboardPane;
