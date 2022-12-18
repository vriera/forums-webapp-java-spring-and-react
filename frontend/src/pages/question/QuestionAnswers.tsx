import React, {useEffect, useState} from 'react';
import {useTranslation} from "react-i18next";
import {Question} from "../../models/QuestionTypes"
import {Notification, User} from "../../models/UserTypes"
import {Community} from "../../models/CommunityTypes"
import {getQuestion} from "../../services/questions";
//import { Pagination, Skeleton } from "@material-ui/lab";

import './ask.css'
import '../../components/CommunitiesCard'
import CommunitiesCard from "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";


import {t} from "i18next";
import {getCommunityFromUrl, getModeratedCommunities} from "../../services/community";
import {Answer} from "../../models/AnswerTypes";
import AnswerCard from "../../components/AnswerCard";
import Pagination from "../../components/Pagination";
import {useParams} from "react-router-dom";


const communities = [
    "Historia", "matematica", "logica"
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

function mockAnswerApiCall() {
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
        },
    }
    let question: Question = {
        id: 1,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: community,
        voteTotal: 1,
    }
    let answer: Answer = {
        id: 1,
        title: "Title",
        body: "Body",
        owner: user,
        verify: false,
        question: question,
        myVote: true,
        url: "string",
        time: "11pm",
        date: "1/12/2021",
        voteTotal: 1,
    }
    return [answer, answer, answer]
}

let auxNotification: Notification = {
    requests: 1,
    invites: 2,
    total: 3
}

let answers = mockAnswerApiCall();


const QuestionAnswers = (props: any) => {
    const {t} = useTranslation();
    const [question, setQuestion] = useState<Question>();
    useEffect(() => {
        const load = async () => {
            let _question = await getQuestion(props.id);
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

   /*  useEffect(
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

    return (
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <Background/>
                <div className="float-parent-element">
                    <div className="row">
                        <div className="col">
                            <CommunitiesCard
                                communities={[community]} selectedCommunity={community}
                                selectedCommunityCallback={setSelectedCommunity}
                                currentPage={currentModeratedCommunityPage}
                                totalPages={moderatedCommunityPages/* FIXME: levantar de la API */}
                                currentPageCallback={setCurrentModeratedCommunityPage} title={t("comunities")}/>
                        </div>
                        <div className="col">
                            {question &&
                            <QuestionCard question={question}/>
                            }
                            {/*{!question &&  <Skeleton width="80vw" height="50vh" animation="wave" />}*/}
                            <div className="overflow-auto">
                                {
                                    answers.map((answer: Answer) =>
                                        <div key={answer.id}>
                                            <AnswerCard answer={answer}/>
                                        </div>
                                    )
                                }
                            </div>
                        </div>
                        <div className="col">
                            <div className="white-pill mt-5 mx-3">
                                <div className="card-body">
                                    <p className="h3 text-primary">{t("answer.answer")}</p>
                                    <hr></hr>
                                    <div className="form-group mt-3">
                                        <input className="form-control" type="answer" id="answer" value={answer}
                                               placeholder={t("placeholder.email")}
                                               onChange={(e) => setAnswer(e.target.value)}/>
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
        </div>

    )
}


const AnswerPage = (props: any) => {
    const {questionId} = useParams();
    return (
        <React.Fragment>{
           <QuestionAnswers id={questionId}/> //
        }

        </React.Fragment>

    );
};


export default AnswerPage;
