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
import {getAnswers} from "../../services/answers";


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

let auxNotification: Notification = {
    requests: 1,
    invites: 2,
    total: 3
}


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

    const [answers, setAnswers] = useState<Answer[]>();
    useEffect(() => {
        if(!question) return
        const load = async () => {
            let _answers = await getAnswers(question)
            setAnswers(_answers)
        };
        load();
    }, [question]);

    console.log(answers)

    useEffect(()=>{
        if(!question) return
        setSelectedCommunity(question.community)
    },[question])

    const [selectedCommunity, setSelectedCommunity] = useState(null as unknown as Community)
    const [currentModeratedCommunityPage, setCurrentModeratedCommunityPage] = useState(1)
    const [moderatedCommunityPages, setModeratedCommunityPages] = useState(null as unknown as number)
    const [answer, setAnswer] = React.useState("");


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
                                { answers &&
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
