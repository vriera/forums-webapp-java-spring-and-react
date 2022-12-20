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
import {Answer, AnswerResponse} from "../../models/AnswerTypes";
import AnswerCard from "../../components/AnswerCard";
import Pagination from "../../components/Pagination";
import {useParams} from "react-router-dom";
import {getAnswers, setAnswer} from "../../services/answers";
import Popup from 'reactjs-popup';
import {getCommunityFromUrl} from "../../services/community";



const QuestionAnswers = (props: any) => {
    const {t} = useTranslation();
    const [question, setQuestion] = useState<Question>();
    const [community , setCommunity ] = useState<Community>();

    useEffect(() => {
        if(!question) return
        const load = async () => {
            let _community = await getCommunityFromUrl(question.community);
            setCommunity(_community);
        };
        load();
    }, [question]);

    useEffect(() => {
        const load = async () => {
            let _question = await getQuestion(props.id);
            setQuestion(_question);
        };
        load();
    }, []);

    const [answers, setAnswers] = useState<AnswerResponse[]>();
    useEffect(() => {
        if(!question) return
        const load = async () => {
            let _answers = await getAnswers(question)
            setAnswers(_answers)
        };
        load();
    }, [question]);


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
                            {community &&
                            <CommunitiesCard
                                communities={[community]} selectedCommunity={community}
                                selectedCommunityCallback={setCommunity}
                                currentPage={currentModeratedCommunityPage}
                                totalPages={0}
                                currentPageCallback={setCurrentModeratedCommunityPage} title={t("comunities")}/>
                            }
                        </div>
                        <div className="col">
                            {question &&
                            <QuestionCard question={question} user={props.user}/>
                            }
                            {/*{!question &&  <Skeleton width="80vw" height="50vh" animation="wave" />}*/}
                            <div className="overflow-auto">
                                { question && answers &&
                                    answers.map((answer: AnswerResponse) =>
                                        <div key={answer.id}>
                                            <AnswerCard answer={answer} question={question}/>
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
                                        {question &&
                                        <button type="submit" className="btn btn-primary" onClick={() => submit(answer,question.id)}>{t("send")}</button>
                                        }
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
function submit(answer:any, idQuestion:number){
    const load = async () => {
        if(Object.keys(answer).length === 0){
            <Popup position="right center">
                <div>Popup content here !!</div>
            </Popup>
            return
        }
        await setAnswer(answer,idQuestion);
        window.location.reload()
    };
    load();

}


const AnswerPage = (props: {user: User}) => {
    const {questionId} = useParams();
    return (
        <React.Fragment>{
           <QuestionAnswers id={questionId} user={props.user}/> //
        }
        </React.Fragment>

    );
};


export default AnswerPage;
