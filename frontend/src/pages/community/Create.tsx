import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Background from "../../components/Background";

const CreateCommunityPage = () => {
    const { t } = useTranslation();
    const [communityName, setCommunityName] = useState("");
    const [communityDescription, setCommunityDescription] = useState("");
    const [nameErrors, setNameErrors] = useState(false);
    const [nameTaken, setNameTaken] = useState(false);

    let history = useNavigate();

    function getNameErrorMessage(){
        if(communityName.length < 3){
            return "ERROR EN EL NOMBRE - ESTO ES DUMMY";
        }
        else if(nameTaken == true){
            return t("community.nameTaken");
        }
        
    }
    function createCommunity(){
        //TODO: create commnuity on behalf of user
        setNameTaken(!nameTaken); //Remember to validate if name is taken
    }
    return (
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <Background/>
                {/* CARD */}
                <div className="container">
                    <div className="white-pill">
                        <div className="d-flex justify-content-center">
                            <div className="h1 text-primary">{t("community.create.message")}</div>
                        </div>
                        <div className="p">{t("community.create.callToAction")}</div>
                        <hr/>
                        <div>
                            { /* COMMUNITY NAME */ }
                            <div className="form-group mt-3">
                                {/* NAME */}
                                <label className="text-black">{t("community.name")}</label>
                                <input className="form-control" id="name" placeholder={t("placeholder.community.name")} onChange={(e) => setCommunityName(e.target.value)}/>
                                <p className="p text-warning">{getNameErrorMessage()}</p>
                                
                            </div>
                            {/* DESCRIPTION */}
                            <div className="form-group">
                                <label>{t("community.description")}</label>
                                <textarea className="form-control" id="description" rows={3} placeholder={t("placeholder.community.description")} onChange={(e) => setCommunityDescription(e.target.value)}/>
                            </div>
                            {/* BUTTONS */}
                            <div className="d-flex justify-content-center">
                                <button className="btn btn-light align-self-start" onClick={() => history(-1)}>{t("back")}</button>
                                <button className="btn btn-primary mb-3" onClick={createCommunity}>{t("button.continue")}</button>
                            </div>
                            <hr/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default CreateCommunityPage;