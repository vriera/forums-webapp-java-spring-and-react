import React from "react";
import Background from "./../../components/Background";
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';
import { useTranslation } from "react-i18next";

const Profile = () => {
    const { t } = useTranslation();

    return (
        <div className="wrapper">
            <div className="section section-hero section-shaped pt-3">
                <Background/>

                <div className="row">
                    <div className="col-3">
                        <div className="white-pill d-flex flex-column mt-5" >
                            {/* INFORMACION DE USUARIO*/}
                            <div className="d-flex justify-content-center">
                                <p className="h1 text-primary">{/*user.username*/}</p>
                            </div>
                            <div className="d-flex justify-content-center">
                                <p>{t("emailEquals")}</p>
                                <p>{/*user.email*/}</p>
                            </div>
                            {/* DASHBOARD - OPCIONES VERTICALES */}
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
                                        
                                        {/*notifications.total > 0 &&*/
                                            <span className="badge badge-secondary bg-warning text-white ml-1">{/*notifications.getTotal()*/}</span>
                                        }
                                        
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>


                    {/* PREGUNTAS */}
                    <div className="col-6">
                        <div className="white-pill mt-5">
                            <div className="card-body overflow-hidden">
                                <p className="h3 text-primary text-center">{t("title.profile")}</p>
                                <hr className="mb-1"/>
                                <div className="text-center">
                                    <img className="rounded-circle" src="https://avatars.dicebear.com/api/avataaars/{user.email}.svg" style={{height: "80px", width: "80px"}}/> {/*TODO: Arreglar el href*/}
                                </div>
                                <p className="h1 text-center text-primary">{/*user.username*/}</p>
                                <p className="h4 text-center">{t('email')}: {/*FIXME:user.email*/}</p>
                                <div className="d-flex justify-content-center">
                                    <p className="h4 text-center">{t("profile.karma")}{/*karma*/}</p>
                                    {/*karma > 0 &&*/
                                        <div className="h4 mr-2 text-success mb-0 mt-1 ml-3">
                                            <i className="fas fa-arrow-alt-circle-up"></i>
                                        </div>
                                    }
                                    {/*karma < 0 &&*/
                                        <div className="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                                            <i className="fas fa-arrow-alt-circle-down"></i>
                                        </div>
                                    }
                                </div>

                                <div className="text-center mt-3">
                                    <a href="/dashboard/user/updateProfile" className="btn btn-primary text-center">{t("profile.update")}</a>
                                </div>

                            </div>
                        </div>
                    </div> 

                    {/* HACER PREGUNTA */}
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
    );
};

export default Profile;