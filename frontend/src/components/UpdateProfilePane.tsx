import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { User } from "./../models/UserTypes"


const UpdateProfilePage = (props: {user: User}) => {
    const { t } = useTranslation();
    const [username, setUsername] = useState(props.user.username)
    const [password, setPassword] = useState('')
    const [currentPassword, setCurrentPassword] = useState('')

    function updateUser(newUsername: string, newPassword: string, insertedCurrentPassword: string, user: User){
       //TODO: SHOULD BE PASSED TO THE API
    }
    

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.profile")}</p>
                <hr className="mb-1"/>
                <div className="text-center">
                    <img className="rounded-circle" src={"https://avatars.dicebear.com/api/avataaars/"+props.user.email+".svg"} alt="User profile icon" style={{height: "80px", width: "80px"}}/>
                </div>

                <div>
                    <p className="h5">{t("profile.updateUsername")}</p>
                    <div className="mb-3 text-center">
                        <input type="text" className="form-control" value={username} onChange={(e) => setUsername(e.target.value)}/>
                    </div>

                    <p className="h5">{t("email")}</p>
                    <div className="mb-3 text-center">
                        <input type="email" className="form-control" placeholder={props.user.email} readOnly/>
                    </div>

                    <p className="h5">{t("profile.changePassword")}</p>
                    <div className="mb-3 text-center">
                        <input type="password" className="form-control" placeholder={t("profile.optional")}onChange={(e) => setPassword(e.target.value)}/>
                    </div>

                    <div className="d-flex">
                        <p className="h5">{t("profile.currentPassword")}</p>
                        <p className="h5 text-warning bold">*</p>
                    </div>
                    <div className="mb-3 text-center">
                        <input type="password" className="form-control" onChange={(e) => setCurrentPassword(e.target.value)}/>
                        <p className="h6 text-gray">{t("profile.whyCurrentPassword")}</p>
                        {currentPassword && //TODO: ESTO DEBERÍA SER UN API CALL!
                            <p className="text-warning">{t("profile.incorrectCurrentPassword")}</p>
                        }

                    </div>

                    <div className="text-center">
                        <Link to="/dashboard/profile/info" className="btn btn-secondary text-center">{t("profile.back")}</Link>
                        <button onClick={() => updateUser(username, password, currentPassword, props.user)} className="btn btn-primary text-center">{t("profile.save")}</button>
                    </div>

                </div>
                    
            </div>
        </div>
    )
}

export default UpdateProfilePage