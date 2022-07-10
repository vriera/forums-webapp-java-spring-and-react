import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

const Navbar = () => {
    const { t } = useTranslation();

    return (
        <div>
            <div className="navbar border-bottom">
                <div className="container-fluid navbar-brand">

                    <div>
                        <div className="d-flex justify-content-end">
                            <a  className="navbar-brand" href="/">
                                <img src={process.env.PUBLIC_URL+'/resources/images/birb.png'} width="30" height="30"/> {/* FIXME: esta imagen no anda pero no estoy segura como embedearla */}
                                {t('askAway')}
                            </a>
                            <div className="nav-item ml-3">
                                <a className="nav-link" aria-current="page" href="/question/ask/community">
                                   {t('title.askQuestion')}
                                </a>
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="/community/create">
                                    {t('title.createCommunity')}
                                </a>
                    
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="/community/view/all">
                                    {t('title.viewAllQuestions')}
                                </a>
                            </div>
                        </div>
                    </div>


                    <div className="d-flex justify-content-start">
                        <div className="nav-item">
                            <a className="nav-link" href="/credentials/register">
                                {t('register.register')}
                            </a>
                        </div>
                        <div className="nav-item">
                            <a className="nav-link" href="/credentials/login">
                                {t('register.login')}
                            </a>
                        </div>
                    </div>


                </div>
            </div>
        </div>
    )
}


export default Navbar;
