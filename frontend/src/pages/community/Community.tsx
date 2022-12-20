import React from "react";
import { useState, useEffect } from "react";
import { useParams } from 'react-router-dom';
import { useTranslation } from "react-i18next";
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import CommunitiesCard from "../../components/CommunitiesCard";
import MainSearchPanel from "../../components/TitleSearchCard";
import Tab from "../../components/TabComponent";
import DashboardQuestionPane from "../../components/DashboardQuestionPane";

import {User, Karma, Notification} from "../../models/UserTypes"
import {Question} from "../../models/QuestionTypes"
import {Community} from "../../models/CommunityTypes"

import {CommunitySearchParams, getCommunity, searchCommunity} from "../../services/community"

import { t } from "i18next";
import {SmartDate} from "../../models/SmartDateTypes";

const communities = [
    "Historia","matematica","logica"
]

function mockQuestionApiCall(){

    let smartDate1: SmartDate = {
        date: "2021-10-18",
        time: "2021-10-18T19:09:45.463689-03:00"
    }
    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
    }
    let community: Community = {
        id: 1,
        name: "Filosofía",
        description: "Para filosofar",
        moderator: user,
        userCount: 2,
        notifications: {
            requests: 1,
            invites: 2,
            total: 3
        }
    }
    let question: Question = {
        id: 1,
        title: "Como pintar con acuarelas?",
        body: "no se como pintar y que quede lindo",
        owner: user,
        smartDate: smartDate1,
        community: community,
        votes: 10,
    }
    let question2: Question = {
        id: 2,
        title: "Puedo hacer cuadros con tiza?",
        body: "alguien me dijo que no puedo hacer cuadros con tiza",
        owner: user,
        smartDate: smartDate1,
        community: community,
        votes: 0,
        myVote: true,
    }
    let question3: Question = {
        id: 3,
        title: "guernica",
        body: "Hm",
        owner: user,
        smartDate: smartDate1,
        community: community,
        votes: -1,
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


const CommunityPage = () => {
    const questions = mockQuestionApiCall()
    let { id } = useParams();
    let communityId = parseInt(new String(id).toString())

    const [community, setCommunity] = useState<Community>(null as unknown as Community);
    const [questionsList, setQuestionsList] = React.useState<Question[]>(questions);
    const [page, setPage] = React.useState(1);
    const [totalPages, setTotalPages] = React.useState();

    useEffect(() => {
        getCommunity(communityId).then(community => {
            setCommunity(community)
        })

    }, [communityId])



    return(
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                {community &&
                    <MainSearchPanel showFilters={true} communityId={community.id} title={community.name} subtitle={community.description}/>
                }
                <div className="row">
                    <div className="col-3">
                        {/* < CommunitiesCard title={t("landing.communities.message")} communities={communities} thisCommunity={"matematica"}/> */}
                    </div>  

                    <div className="col-6">
                        {/* FIXME: This should be a custom list, dashboard has additional security parameters! */}
                        <DashboardQuestionPane />
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