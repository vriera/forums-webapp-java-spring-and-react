import React from "react";
import { useTranslation } from "react-i18next";
import { createQuestion} from '../../../services/questions';
import {Link, Navigate, useNavigate, useParams} from "react-router-dom";
import Background from "../../../components/Background";




const WriteQuestionPage = () => {

    const { t } = useTranslation();

    
    return(
        <div className="section section-hero section-shaped">
            <Background/>
            <div className="container">
                <div className="white-pill">
                    <div className="d-flex justify-content-center">
                        <p className="h1 text-primary text-center">{t("title.askQuestion")}</p>
                    </div>
                    <hr/>
                        <AskQuestionContent/>

                    <hr/>
                    {/* STEPPER */}
                    <div className="stepper-wrapper">
                        {/* Classname should be active if currentProgress is 1 */}
                        <div className="stepper-item completed" >
                            <div className="step-counter">1</div>
                            <div className="step-name">{t("question.community")}</div>
                        </div>
                        <div className= "stepper-item active" >
                            <div className="step-counter">2</div>
                            <div className="step-name">{t("question.content")}</div>
                        </div>
                        <div className= "stepper-item ">
                            <div className="step-counter">3</div>
                            <div className="step-name">{t("question.wrapup.message")}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}


const AskQuestionContent = () => {

    const {communityId} = useParams();

    const { t } = useTranslation();
    const navigate = useNavigate();

    const submit = async () => {
        console.log("this is the community id as said by use params:" + communityId);
        console.log("this is the community id after parseInt:" + parseInt(communityId as string));
        let files = (document.getElementById('image') as HTMLInputElement).files
        try{
           await createQuestion({
            community: parseInt(communityId as string),
            title: (document.getElementById('title') as HTMLInputElement).value,
            body: (document.getElementById('body') as HTMLInputElement).value,
            } , (files && files.length > 0)? files[0] : null ); 
            navigate("/ask/wrapUp/success");
        }
        catch{
            navigate("/ask/wrapUp/error");
        }
        
        
        console.log("done")
    }
    return (
        <>
            <p className="h5 text-black">{t("question.contentCallToAction")}</p>
            <form>
                <label className="h5 text-black mt-3">{t("title.message")}</label>
                <input className="form-control" placeholder={t("placeholder.questionTitle")} id="title"/>
            </form>

            <form>
                <label className="h5 text-black mt-3">{t("body")}</label>
                <input className="form-control"  placeholder={t("placeholder.questionBody")} id="body"/>
            </form>

            <form className="form-group">
                <label className="h5 text-black mt-3">{t("general.label.image")}</label>
                <input name="image" className="form-control" type="file"  accept="image/png, image/jpeg" id="image"/>
            </form>

            <div className="d-flex justify-content-center">
                <Link to={"/ask/selectCommunity"} className="btn btn-light align-self-start" >{t("profile.back")}</Link>
                <button onClick={() => submit()} className="btn btn-primary mb-3" type="submit">{t("button.continue")}</button>
                {/*
                <Link to={"/ask/wrapUp"} className="btn btn-primary mb-3" type="submit">{t("button.continue")}</Link> */}
            </div>
        </>
    )
}

export default WriteQuestionPage;