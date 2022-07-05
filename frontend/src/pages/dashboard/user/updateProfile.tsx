import React from "react";
import Background from "./../../../components/Background";
import Navbar from "./../../../components/navbar"
import {User, Karma, Notification} from "./../../../models/UserTypes"
import { useTranslation } from "react-i18next";

function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69,
        username: "Salungo",
        email: "s@lung.o",
        password: "asereje",
    }
    
    return auxUser
}

function mockKarmaApiCall(user: User): Karma{
    var auxKarma : Karma = {
        user: user,
        karma: 420
    }
    return auxKarma
}

function mockNotificationApiCall(user: User): Notification{
    var auxNotification: Notification = {
        user: user,
        requests: 1,
        invites: 2,
        total: 3
    }
    return auxNotification
}

const UpdateProfilePage = () => {
    const { t } = useTranslation();
    const user: User = mockUserApiCall()
    const karma: Karma = mockKarmaApiCall(user)
    const notifications: Notification = mockNotificationApiCall(user)

    return (
        <div>
            <Navbar/>
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        <div className="col-3">
                            <div className="white-pill d-flex flex-column mt-5" >
                                {/* INFORMACION DE USUARIO */}
                                <div className="d-flex justify-content-center">
                                    <p className="h1 text-primary">{user.username}</p>
                                </div>
                                <div className="d-flex justify-content-center">
                                    <p>{t("emailEquals")}</p>
                                    <p>{user.email}</p>
                                </div>
                                {/* <!-- DASHBOARD - OPCIONES VERTICALES --> */}
                                <ul className="nav nav-pills flex-column mb-auto">

                                    <li>
                                        <a href="/dashboard/user/myProfile" className="h5 nav-link link-dark active">
                                            <i className="fas fa-users mr-3"></i>
                                            {t("dashboard.myProfile")}
                                        </a>
                                    </li>

                                    <li>
                                        <a href="/dashboard/question/view?page=0" className="h5 nav-link" aria-current="page">
                                            <i className="fas fa-question mr-3"></i>
                                            {t("dashboard.questions")}
                                        </a>
                                    </li>
                                    <li>
                                        <a href="/dashboard/answer/view?page=0" className="h5 nav-link link-dark">
                                            <i className="fas fa-reply mr-3"></i>
                                            {t("dashboard.answers")}
                                        </a>
                                    </li>
                                    <li>
                                        <a href="/dashboard/community/admitted?page=0" className="h5 nav-link link-dark">
                                            <i className="fas fa-users mr-3"></i>
                                            {t("dashboard.communities")}
                                            <span className="badge badge-secondary bg-warning text-white ml-1" > {notifications.total}</span>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>


                        {/* <%--Update profile--%> */}
                        <div className="col-6">
                            <div className="white-pill mt-5">
                                <div className="card-body overflow-hidden">
                                    <p className="h3 text-primary text-center">{t("title.profile")}</p>
                                    <hr className="mb-1"/>
                                    <div className="text-center">
                                        <img className="rounded-circle" src="https://avatars.dicebear.com/api/avataaars/${user.email}.svg" style={{height: "80px", width: "80px"}}/>
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

                                </div>
                            </div>
                        </div>

                        {/* <%--HACER PREGUNTA--%> */}
                        <div className="col-3">
                            <div className="white-pill mt-5 mr-3">
                                <div className="card-body">
                                    <p className="h3 text-primary text-center">{t("title.askQuestion")}</p>
                                    <hr/>
                                    <p className="h5 my-3">{t("subtitle.askQuestion")}</p>
                                    <a className="btn btn-primary" href="/question/ask/community">{t("button.askQuestion")}</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default UpdateProfilePage