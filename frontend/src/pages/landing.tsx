import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

import Navbar from "../components/Navbar";

import { t } from "i18next";

const Header = () => {
    return (
        <div>
           <Navbar/> 
        </div>
    );
}

const InformationPane = (props: { title: String, bodyText: String, buttonText: String}) => {
    const { t } = useTranslation();
    return (
        <div>
            <div className="card card-lift--hover shadow border-0">
                        <div className="card-body">
                            <div className="icon icon-shape icon-shape-primary rounded-circle mb-4">
                                <i className="ni ni-check-bold"></i>
                            </div>
                            <p className="h3 text-primary">{props.title}</p>
                            <p className="fs-5 description my-3">{props.bodyText}</p>
                            <a className="btn btn-primary" href="question/ask/community">{props.buttonText}</a>
                        </div>
                    </div>
        </div>
    )
}

const BaddassBackdrop = () => {
    return(
        <div className="shape shape-style-1 shape-default shape-skew viewheight-90">
                        <span className="span-150 square1"></span>
                        <span className="span-50 square2"></span>
                        <span className="span-50 square3"></span>
                        <span className="span-75 square4"></span>
                        <span className="span-100 square5"></span>
                        <span className="span-75 square6"></span>
                        <span className="span-50 square7"></span>
                        <span className="span-100 square3"></span>
                        <span className="span-50 square2"></span>
                        <span className="span-100 square4"></span>
                    </div>
    )
}

const LandingPage = () => {
    const { t } = useTranslation();
    return (
        <React.Fragment>
            <Header />

            <div>
                <div className="section section-hero section-shaped">
                    <BaddassBackdrop/>
                    <div className="container">
                        <div className="white-pill">
                            <p className="h1 text-primary"><strong>{t('askAway')}</strong></p>
                            <p className="h3 mx-5">{t('landing.slogan')}</p>
                            {/* Barra de busquedas */}
                            <div className="form-group mx-5">
                                {/* <spring:message code="search" var="search"/>
                                <spring:message code="landing.searchCallToAction" var="searchPlaceholder"/> */}
                                <form action="/community/view/all" method="get">
                                    <div className="input-group">
                                        <input className="form-control rounded" type="search" name="query" id="query" placeholder={t('placeholder.searchQuestion')}/>
                                        <input className="btn btn-primary" type="submit" value={t('search')}/>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    
                    <div className="container-xl mt-5">
                        <div className="row">
                            <div className="col">
                            <InformationPane title={t('landing.question.trigger')} bodyText={t('landing.question.callToAction')} buttonText={t('ask')} />
                            </div>
                            <div className="col">
                            <InformationPane title={t('landing.communities.message')} bodyText={t('landing.communities.callToAction')} buttonText={t('create')} />
                            </div>
                            <div className="col">
                            <InformationPane title={t('landing.discover')} bodyText={t('landing.discoverCallToAction')} buttonText={t('discover')} />
                            </div>
                            

                        </div>
                    </div>
                </div>
            </div>

            

        </React.Fragment>

    );
};

export default LandingPage;