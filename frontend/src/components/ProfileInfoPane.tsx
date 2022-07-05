import React from "react";
import { User, Karma } from "../models/UserTypes";
import { useTranslation } from "react-i18next";


const ProfileInfoPane = (props: {user: User, karma: Karma, updateProfileCallback: any}) => {
    const { t } = useTranslation();

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.profile")}</p>
                <hr className="mb-1"/>
                <div className="text-center">
                    <img className="rounded-circle" src={"https://avatars.dicebear.com/api/avataaars/"+props.user.email+".svg"} style={{height: "80px", width: "80px"}}/> 
                </div>
                <p className="h1 text-center text-primary">{props.user.username}</p>
                <p className="h4 text-center">{t('email')}: {props.user.email}</p>
                <div className="d-flex justify-content-center">
                    <p className="h4 text-center">{t("profile.karma")}{props.karma.karma}</p>
                    {props.karma.karma > 0 &&
                        <div className="h4 mr-2 text-success mb-0 mt-1 ml-3">
                            <i className="fas fa-arrow-alt-circle-up"></i>
                        </div>
                    }
                    {props.karma.karma < 0 &&
                        <div className="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                            <i className="fas fa-arrow-alt-circle-down"></i>
                        </div>
                    }
                </div>

                <div className="text-center mt-3">
                    <button onClick={props.updateProfileCallback} className="btn btn-primary text-center">{t("profile.update")}</button>
                </div>

            </div>
        </div>
    )
}

export default ProfileInfoPane