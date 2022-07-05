import React from "react";
import { useTranslation } from "react-i18next";
import './../resources/styles/argon-design-system.css';
import './../resources/styles/blk-design-system.css';
import './../resources/styles/general.css';
import './../resources/styles/stepper.css';

const Navbar = () => {
    const { t } = useTranslation();
    const is_user_present = false;
    
    return (
        <div className="Navbar">
            <nav className="navbar border-bottom">
                <div className="container-fluid navbar-brand">

                    {/* <div>
                        <div className="d-flex justify-content-end">
                            <a  className="navbar-brand" href="<c:url value="/"/>">
                                <img src="<c:url value="/resources/images/birb.png"/>" width="30" height="30"/>
                                AskAway
                            </a>
                            <div className="nav-item ml-3">
                                <a className="nav-link" aria-current="page">{t("title.askQuestion")}</a>
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="<c:url value="/community/create"/> "><spring:message key="title.createCommunity"/></a>
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="<c:url value="/community/view/all"/> "><spring:message key="title.viewAllQuestions"/></a>
                            </div>
                        </div>
                    </div> */}


                    <div className="d-flex justify-content-start">
                        <div className="nav-item">
                            <a className="nav-link" aria-current="page">{t('register.register')}</a>
                        </div>
                        <div className="nav-item">
                            <a className="nav-link" >{t('register.login')}</a>
                        </div>
                    </div>
                </div>
            </nav>
        </div>
    );
}



export default Navbar;