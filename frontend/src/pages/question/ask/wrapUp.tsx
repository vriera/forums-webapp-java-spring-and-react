import React from "react";
import { useTranslation } from "react-i18next";

import { Link } from "react-router-dom";
import Background from "../../../components/Background";



const WrapUpPage = (props: {}) => {

    const { t } = useTranslation();

    return(
        <div className="section section-hero section-shaped">
        <Background/>
            <div className="container">
                <div className="white-pill">
                    <div className="d-flex justify-content-center">
                        <p className="h1 text-primary text-center">{t("title.askQuestion")}</p>
                    </div>
                        <WrapUp wasOperationSuccessful={false}/>

                    <hr/>
                    {/* STEPPER */}
                    <div className="stepper-wrapper">
                        {/* Classname should be active if currentProgress is 1 */}
                        <div className="stepper-item completed">
                            <div className="step-counter">1</div>
                            <div className="step-name">{t("question.community")}</div>
                        </div>
                        <div className= "stepper-item completed">
                            <div className="step-counter">2</div>
                            <div className="step-name">{t("question.content")}</div>
                        </div>
                        <div className= "stepper-item active">
                            <div className="step-counter">3</div>
                            <div className="step-name">{t("question.wrapup.message")}</div>
                        </div>
                    </div>
                </div>

            </div>
            </div>
    )
}

const WrapUp = (props: {wasOperationSuccessful: boolean}) => {


    const { t } = useTranslation();

   
    return (
        <>
           {props.wasOperationSuccessful===true &&
           <div>
                <div className="row">
                    <div className="d-flex justify-content-center">
                    <div className="h1 text-success">{t("question.wrapUpSuccess")}</div>
                    </div>
                </div>
                <hr/>
                <div className="row d-flex justify-content-center mb-5">
                    <img className="w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/success.png`} alt="ÉXITO"/>
                </div>

                {/* Buttons */}
                <div className="d-flex justify-content-center">
                    <Link to="/" className="btn btn-light">{t("question.wrapup.return")}</Link>
                    <Link to="/question/view/${question.id}" className="btn btn-primary">{t("question.wrapup.seeQuestion")}</Link>
                </div>
            </div>
           }

            {props.wasOperationSuccessful===false &&
                <>
                    <div className="d-flex justify-content-center">
                        <p className="h1 text-danger">{t("question.wrapup.error")}</p>
                    </div>
                    <hr/>
                    <div className="row d-flex justify-content-center mb-5">
                        <img className="w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/error.png`} alt="ERROR"/>
                    </div>
        
                    {/* Buttons */}
                    <div className="d-flex justify-content-center">
                        <Link to="/" className="btn btn-light">{t("question.wrapup.return")}</Link>
                        <Link to="/search" className="btn btn-primary">{t("question.wrapup.seeAllQuestions")}</Link>
                    </div>
                </>
            }
        </>
    )
}

export default WrapUpPage;