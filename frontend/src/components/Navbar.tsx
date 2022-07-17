import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
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
                            <Link  className="navbar-brand" to="/">
                                <img src={process.env.PUBLIC_URL+'/resources/images/birb.png'} width="30" height="30"/> {/* FIXME: esta imagen no anda pero no estoy segura como embedearla */}
                                {t('askAway')}
                            </Link>
                            
                            <div className="nav-item">
                                <Link className="nav-link" to="/search">
                                    {t('title.viewAllQuestions')}
                                </Link>
                            </div>
                        </div>
                    </div>


                    <div className="d-flex justify-content-start">
                        <div className="nav-item">
                            <Link className="nav-link" to="/credentials/signin">
                                {t('register.register')}
                            </Link>
                        </div>
                        <div className="nav-item">
                            <Link className="nav-link" to="/credentials/login">
                                {t('register.login')}
                            </Link>
                        </div>
                    </div>


                </div>
            </div>
        </div>
    )
}


export default Navbar;
