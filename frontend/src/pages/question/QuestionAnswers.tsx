import React, {useEffect, useState} from 'react';
import { useTranslation } from "react-i18next";
import {Question} from "../../models/QuestionTypes"
import {User} from "../../models/UserTypes"
import {Community} from "../../models/CommunityTypes"
import { getQuestion } from "../../services/questions";
//import { Pagination, Skeleton } from "@material-ui/lab";

import './ask.css'
import '../../components/CommunitiesCard'
import CommunitiesCard from "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";


import { t } from "i18next";
import {getCommunityFromUrl, getModeratedCommunities} from "../../services/community";


const communities = [
    "Historia","matematica","logica"
]

let user: User = {
    id: 1,
    username: "HOLA",
    email: "hoLA",
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


const Questions = () => {
    const { t } = useTranslation();
    const [question,setQuestion] = useState<Question>();
    useEffect(() => {
        const load = async () => {
            let  _question = await getQuestion(1);
            setQuestion(_question);
        };
        load();
    }, []);

    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
    }
    let community: Community = {
        id: 1,
        name: "Matematica",
        description: "Para primer grado",
        moderator: user,
        userCount: 2,
        notifications: {
            requests: 1,
            invites: 2,
            total: 3
        }
    }

    const [selectedCommunity, setSelectedCommunity] = useState(null as unknown as Community)
    const [moderatedCommunities, setModeratedCommunities] = useState(null as unknown as Community[])
    const [currentModeratedCommunityPage, setCurrentModeratedCommunityPage] = useState(1)
    const [moderatedCommunityPages, setModeratedCommunityPages] = useState(null as unknown as number)
    const [answer, setAnswer] = React.useState("");

   /* useEffect(
        () => {
                //Fetch moderated communities from API
                getModeratedCommunities(parseInt(new String(window.localStorage.getItem("userId")).toString()), currentModeratedCommunityPage)
                    .then((res) => {
                        setModeratedCommunityPages(res.totalPages)

                        let communities = res.communities;
                        //Fetch all the communities in the list and load them into the moderatedCommunities
                        let communityList: Community[] = []
                        let promises : Promise<any>[] = [];


                        communities.forEach((community: string) => {
                            promises.push( getCommunityFromUrl(community))
                        })

                        Promise.all(promises).then(
                            (communities) =>
                                (communities).forEach(
                                    (resolvedCommunity: Community) => {

                                        if(moderatedCommunities === null && communityList.length === 0){
                                            console.log("Inserting first community" + resolvedCommunity.name)
                                            setSelectedCommunity(resolvedCommunity)
                                        }
                                        // If it's the first time the user is loading the page, set the moderated communities and select the first one to moderate
                                        communityList.push(resolvedCommunity)

                                        // If the user is already on the page, just update the moderated communities
                                        setModeratedCommunities(communityList)
                                    })
                        )
                    })
        },[]);*/

    return(
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <Background />
                <div className="float-parent-element">
                    <div className="float-child-element">
                        <CommunitiesCard
                            communities={[community]} selectedCommunity={community} selectedCommunityCallback={setSelectedCommunity}
                            currentPage={currentModeratedCommunityPage} totalPages={moderatedCommunityPages/* FIXME: levantar de la API */} currentPageCallback={setCurrentModeratedCommunityPage} title={t("comunities")}/>
                    </div>
                    <div className="float-child-element2">
                        {question &&
                        <QuestionCard question={question}/>
                        }
                        {/*{!question &&  <Skeleton width="80vw" height="50vh" animation="wave" />}*/}
                    </div>
                    <div className="float-child-element3">
                        <div className="white-pill mt-5">
                            <div className="card-body">
                                <p className="h3 text-primary">{t("answer.answer")}</p>
                                <hr></hr>
                            <div className="form-group mt-3">
                                <input className="form-control" type="email" id="email" value={answer} placeholder={t("placeholder.email")} onChange={(e) => setAnswer(e.target.value)} />
                            </div>
                                <div className="d-flex justify-content-center mb-3 mt-3">
                                    <button type="submit" className="btn btn-primary">
                                        {t("send")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}


const AnswerPage = () => {
    return (
        <React.Fragment>
            <Questions />
        </React.Fragment>

    );
};


export default AnswerPage;
