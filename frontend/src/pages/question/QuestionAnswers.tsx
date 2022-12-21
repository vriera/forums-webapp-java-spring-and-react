import React, {useEffect, useState} from 'react';
import {useTranslation} from "react-i18next";
import {Question} from "../../models/QuestionTypes"
import {User} from "../../models/UserTypes"
import {Community} from "../../models/CommunityTypes"
import {getQuestion} from "../../services/questions";
//import { Pagination, Skeleton } from "@material-ui/lab";

import './ask.css'
import '../../components/CommunitiesCard'
import CommunitiesCard from "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";


import {AnswerResponse} from "../../models/AnswerTypes";
import AnswerCard from "../../components/AnswerCard";
import {useNavigate, useParams} from "react-router-dom";
import {getAnswers, createAnswer} from "../../services/answers";
import {getCommunityFromUrl} from "../../services/community";
import Spinner from "../../components/Spinner";
import Pagination from "../../components/Pagination";
import {PaginationInfo} from "../../services/api";
import {createBrowserHistory} from "history";



const QuestionAnswers = (props: any) => {
    const {t} = useTranslation();
    const [question, setQuestion] = useState<Question>();
    const [community , setCommunity ] = useState<Community>();
    const [ totalPages, setTotalPages ] = useState(1);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [blankAnswerError, setBlankAnswerError] = useState(false);
    const history = createBrowserHistory();

    function submit(answer:any, idQuestion:number){
        const load = async () => {
            if(Object.keys(answer).length === 0){
               setBlankAnswerError(true)
                return;

            }else{
                await createAnswer(answer,idQuestion);
                window.location.reload()
            }


        };
        load();

    }


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
            const params = new URLSearchParams(history.location.search);
            const page = params.get("page");
            page && setCurrentPage(Number(page))
        };
        load();
    }, []);

    const [answers, setAnswers] = useState<AnswerResponse[]>();
    useEffect(() => {
        let limit = 5;
        if(!question) return
        const load = async () => {
            await getAnswers(question, currentPage,limit).then((response) => {
                    setAnswers(response.list)
                    setTotalPages(Math.ceil(response.pagination.total/limit))

            }
            )

        };
        load();
    }, [question, currentPage]);


    const [currentModeratedCommunityPage, setCurrentModeratedCommunityPage] = useState(1)
    const [moderatedCommunityPages, setModeratedCommunityPages] = useState(null as unknown as number)
    const [answer, setAnswer] = React.useState("");

    const changePage = (page:number) => {
        if(!totalPages) return
        setCurrentPage(page);
        setPage(page);
        setAnswers(undefined)
    }

    function setPage(pageNumber: number){
        if(!question) return
        const page = pageNumber.toString();
        history.push({pathname: `${process.env.PUBLIC_URL}/questions/${question.id}?page=${page}`})

    }

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
                            {
                                !community && <Spinner/>
                            }
                        </div>
                        <div className="col">
                            {question &&
                            <QuestionCard question={question} user={props.user}/>
                            }
                            {
                                !question && <Spinner/>
                            }
                            <div>&emsp;</div>
                            <div className="overflow-auto"  >
                                { question && answers &&
                                    answers.map((answer: AnswerResponse) =>
                                        <div className="my-2" key={answer.id}>
                                            <AnswerCard answer={answer} question={question}/>
                                        </div>
                                    )
                                    //TODO: ACA NO PONGO SPINNER PORQUE SE TRABA CAMBIAR ALGO??
                                }
                                <Pagination currentPage={currentPage} setCurrentPageCallback={changePage} totalPages={totalPages}/>
                            </div>
                        </div>
                        <div className="col">
                            <div className="white-pill mt-5 mx-3">
                                <div className="card-body">
                                    <p className="h3 text-primary">{t("answer.answer")}</p>
                                    <hr></hr>
                                    <div className="form-group mt-3">
                                        <input required={true} className="form-control" type="answer" id="answer" value={answer}
                                               placeholder={t("placeholder.email")}
                                               onChange={(e) => setAnswer(e.target.value)}/>
                                    </div>
                                    <div className="d-flex justify-content-center mb-3 mt-3">
                                        {question &&
                                        <button type="submit" className="btn btn-primary" onClick={() => submit(answer,question.id)}>{t("send")}</button>
                                        }

                                    </div>
                                    {blankAnswerError && <div className="text-danger" >{t("error.emptyAnswer")}</div>}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    )
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
