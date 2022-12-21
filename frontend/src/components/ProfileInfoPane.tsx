import React from "react";
import { User, Karma } from "../models/UserTypes";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";


const ProfileInfoPane = (props: {user: User, showUpdateButton: boolean}) => {
    const { t } = useTranslation();

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.profile")}</p>
                <hr className="mb-1"/>
                <div className="text-center">
                    <img className="rounded-circle" alt="User profile icon" src={"https://avatars.dicebear.com/api/avataaars/"+props.user.email+".svg"} style={{height: "80px", width: "80px"}}/> 
                </div>
                <p className="h1 text-center text-primary">{props.user.username}</p>
                <p className="h4 text-center">{t('email')}: {props.user.email}</p>
                <div className="d-flex justify-content-center">
                    <p className="h4 text-center">{t("profile.karma")}{props.user.karma && props.user.karma.karma}</p>
                    {props.user.karma && props.user.karma.karma > 0 &&
                        <div className="h4 mr-2 text-success mb-0 mt-1 ml-3">
                            <i className="fas fa-arrow-alt-circle-up"></i>
                        </div>
                    }
                    {props.user.karma && props.user.karma.karma < 0 &&
                        <div className="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                            <i className="fas fa-arrow-alt-circle-down"></i>
                        </div>
                    }
                </div>

                {props.showUpdateButton &&
                    <div className="text-center mt-3">
                        <Link to="/dashboard/profile/update" className="btn btn-primary text-center">{t("profile.update")}</Link>
                    </div>
                }
            </div>
        </div>
    )
}

export default ProfileInfoPane