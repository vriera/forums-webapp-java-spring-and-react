import React from "react";
import { useTranslation } from "react-i18next";
import { User } from "./../models/UserTypes"


const UpdateProfilePage = (props: {user: User, updateProfileCallback: any}) => {
    const { t } = useTranslation();

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.profile")}</p>
                <hr className="mb-1"/>
                <div className="text-center">
                    <img className="rounded-circle" src={"https://avatars.dicebear.com/api/avataaars/"+props.user.email+".svg"} style={{height: "80px", width: "80px"}}/>
                </div>

                {/* <c:url value="/dashboard/user/updateProfile" var="postPath"/>
                <form:form modelAttribute="updateUserForm" action="${postPath}" method="post">
                    <p className="h5">{t("profile.updateUsername")}</p>
                    <div className="mb-3 text-center">
                        <form:input path="newUsername" type="text" className="form-control" value={user.username}/>
                        <form:errors path="newUsername" cssclassName="error text-warning" element="p"/>
                    </div>

                    <p className="h5">Email</p>
                    <div className="mb-3 text-center">
                        <input type="email" className="form-control" placeholder="${user.email}" readonly/>
                    </div>

                    <p className="h5">{t("profile.changePassword")}</p>
                    {t("profile.optional")} var="optional 
                    <div className="mb-3 text-center">
                        <form:input path="newPassword" type="password" className="form-control" id="password" placeholder="${optional}
                        <form:errors path="newPassword" cssclassName="error text-warning" element="p"/>
                    </div>

                    <div className="d-flex">
                        <p className="h5">{t("profile.currentPassword")}</p>
                        <p className="h5 text-warning bold">*</p>
                    </div>
                    <div className="mb-3 text-center">
                        <form:input path="currentPassword" type="password" className="form-control" id="password
                        <form:errors path="currentPassword" cssclassName="error text-warning" element="p"/>
                        <p className="h6 text-gray">{t("profile.whyCurrentPassword")}</p>
                        <c:if test="${isOldPasswordCorrect == true}">
                            <p className="text-warning">{t("profile.incorrectCurrentPassword")}</p>
                        </c:if>

                    </div>

                    <div className="text-center">
                        <a href="/dashboard/user/myProfile" className="btn btn-secondary text-center">{t("profile.back")}</a>
                        <button type="submit" className="btn btn-primary text-center">{t("profile.save")}</button>
                    </div>

                </form:form> */}
                    <div className="text-center">
                        <button onClick={props.updateProfileCallback} className="btn btn-secondary text-center">{t("profile.back")}</button>
                        <button type="submit" className="btn btn-primary text-center">{t("profile.save")}</button>
                    </div>
            </div>
        </div>
    )
}

export default UpdateProfilePage