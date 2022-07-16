import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

import Background from "../components/Background";
import AskQuestionPane from "../components/AskQuestionPane";
import CommunitiesCard from "../components/CommunitiesCard";
import MainSearchPanel from "../components/TitleSearchCard";
import Tab from "../components/TabComponent";
import DashboardQuestionPane from "../components/DashboardQuestionPane";

import {User, Karma, Notification} from "./../models/UserTypes"
import {Question} from "./../models/QuestionTypes"
import {Community} from "./../models/CommunityTypes"

import { t } from "i18next";

const communities = [
    "Historia","matematica","logica"
]

function mockQuestionApiCall(){
    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
        password: "tu vieja"
    }
    let community: Community = {
        id: 1,
        name: "Filosofía",
        description: "Para filosofar",
        moderator: user,
        userCount: 2,
        notificationTotal: 0
    }
    let question: Question = {
        id: 1,
        title: "Hm?",
        body: "Hm",
        owner: "http://localhost:8080/api/users/1",
        date: "1/12/2021",
        community: "http://localhost:8080/api/communities/1",
        voteTotal: 1,
    }
    let question2: Question = {
        id: 2,
        title: "Hm?",
        body: "Hm",
        owner: "http://localhost:8080/api/users/1",
        date: "1/12/2021",
        community: "http://localhost:8080/api/communities/1",
        voteTotal: 0,
        myVote: true,
    }
    let question3: Question = {
        id: 3,
        title: "Hm?",
        body: "Hm",
        owner: "http://localhost:8080/api/users/1",
        date: "1/12/2021",
        community: "http://localhost:8080/api/communities/1",
        voteTotal: -1,
        myVote: false
    }
    return [question, question2, question3]
}


const CenterPanel = (props: {activeTab: string, updateTab: any}) => { 
    const { t } = useTranslation();
    return (
        <>
            <div className="col-6">
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <p className="h3 text-primary text-center">{t("title.questions")}</p>
                        <hr/>

                        <p className="row h1 text-gray">{t("community.noResults")}</p>
                        <div className="d-flex justify-content-center">
                            <img className="row w-25 h-25" src="/resources/images/empty.png" alt="No hay nada para mostrar"/>
                        </div>
                
                    </div>
                
                </div>
            </div>
            
        </>

    )
}


const CommunityPage = (props: {communityName: string}) => {
    const questions = mockQuestionApiCall()

    return(
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel showFilters={true} title={props.communityName}/>
                <div className="row">
                    <div className="col-3">
                        < CommunitiesCard communities={communities} thisCommunity={"matematica"}/>
                    </div>  

                    <div className="col-6">
                    <DashboardQuestionPane questions={questions} page={1} totalPages={5}/>
                    </div>
                    
                    <div className="col-3">
                        <AskQuestionPane/>
                    </div>
                </div>
            </div>
        </>
    )
}

export default CommunityPage;