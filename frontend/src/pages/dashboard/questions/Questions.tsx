import { useEffect, useState } from "react";
import AskQuestionPane from "../../../components/AskQuestionPane";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import DashboardQuestionPane from "../../../components/DashboardQuestionPane";
import { User } from "../../../models/UserTypes";
import { getUserFromApi } from "../../../services/user";
import { useNavigate } from "react-router-dom";


const DashboardQuestionsPage = () => {
        
    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane option={"questions"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardQuestionPane/>

                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <AskQuestionPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default DashboardQuestionsPage;
