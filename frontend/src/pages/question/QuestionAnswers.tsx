import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {Question} from "../../models/QuestionTypes";
import {User} from "../../models/UserTypes";
import {Community} from "../../models/CommunityTypes";
import {getQuestion} from "../../services/questions";
//import { Pagination, Skeleton } from "@material-ui/lab";
import "react-loading-skeleton/dist/skeleton.css";

import "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";

import {AnswerResponse} from "../../models/AnswerTypes";
import AnswerCard from "../../components/AnswerCard";
import {useNavigate, useParams} from "react-router-dom";
import {createAnswer, getAnswers} from "../../services/answers";
import {getCommunityFromUrl} from "../../services/community";
import Pagination from "../../components/Pagination";
import {createBrowserHistory} from "history";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
import Spinner from "../../components/Spinner";
import EmptyQuestionCard from "../../components/EmptyQuestionCard";

const QuestionAnswers = (props: any) => {
    const {t} = useTranslation();
    const [question, setQuestion] = useState<Question>();
    const [community, setCommunity] = useState<Community>();
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [blankAnswerError, setBlankAnswerError] = useState(false);
    const [butonVerify, setButonVerify] = useState(false);
    const history = createBrowserHistory();
    let {communityPage} = useParams();
    const navigate = useNavigate();
    const [loadingAnswers, setLoadingAnswers] = useState(true);

    function setCommunityPage(pageNumber: number) {
        if (!question) return;
        communityPage = pageNumber.toString();
        history.push({
            pathname: `${process.env.PUBLIC_URL}/questions/${question.id}?page=${currentPage}&communityPage=${communityPage}`,
        });
    }

    function selectedCommunityCallback(id: number | string) {
        let url;
        const newCommunityPage = communityPage ? communityPage : 1;

        if (id === "all") {
            url = `/search/questions?page=1&communityPage=${newCommunityPage}`;
        } else {
            url = `/community/${id}?page=1&communityPage=${newCommunityPage}`;
        }
        navigate(url);
    }

    function submit(answer: any, idQuestion: number) {
        const load = async () => {
            if (Object.keys(answer).length === 0) {
                setBlankAnswerError(true);
                return;
            } else {
                try {
                    await createAnswer(answer, idQuestion);
                    setAnswer("");
                    const question = await getQuestion(idQuestion);
                    const updatedAnswers = await getAnswers(question, currentPage, 5);
                    setAnswers(updatedAnswers.list);

                } catch (error: any) {
                    //navigate(`/${error.code}`)
                }

            }
        };
        load();
        /*  let btn = (document.getElementById("answerButton") as HTMLInputElement);
            try{

                btn.disabled = true;
                load();
            }catch(error:any){

            }
            btn.disabled = false;*/
    }

    useEffect(() => {
        if (!question) return;
        const load = async () => {
            try {
                let _community = await getCommunityFromUrl(question.community);
                setCommunity(_community);
            } catch (error: any) {
                //navigate(`/${error.code}`);
            }
        };
        load();
    }, [question]);

    useEffect(() => {
        const load = async () => {
            try {
                let _question = await getQuestion(props.id);
                setQuestion(_question);
                const params = new URLSearchParams(history.location.search);
                const page = params.get("page");
                page && setCurrentPage(Number(page));
            } catch (error: any) {
                navigate(`/${error.code}`);
            }
        };
        load();
    }, []);

    const [answers, setAnswers] = useState<AnswerResponse[]>();
    useEffect(() => {
        let limit = 5;
        if (!question) return;
        const load = async () => {
            try {
                await getAnswers(question, currentPage, limit).then((response) => {
                    setAnswers(response.list);
                    setTotalPages(Math.ceil(response.pagination.total / limit));
                });
            } catch (error: any) {
                //navigate(`/${error.code}`);
            } finally {
                setLoadingAnswers(false); // Indica que las respuestas han terminado de cargar
            }
        };
        load();
    }, [question, currentPage]);

    useEffect(() => {
        const load = async () => {
            if (!question) return;
            if (question.owner.id === props.user.id) {
                setButonVerify(true);
            }
        };
        load();
    }, [question]);

    const [answer, setAnswer] = React.useState("");

    const changePage = (page: number) => {
        if (!totalPages) return;
        setCurrentPage(page);
        setPage(page);
        setAnswers(undefined);
    };

    function setPage(pageNumber: number) {
        if (!question) return;
        const page = pageNumber.toString();
        history.push({
            pathname: `${process.env.PUBLIC_URL}/questions/${question.id}?page=${page}`,
        });
    }

    return (
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <Background/>
                <div className="float-parent-element">
                    <div className="row">
                        <div className="col-3">
                            {community && (
                                <CommunitiesLeftPane
                                    selectedCommunity={community.id}
                                    selectedCommunityCallback={selectedCommunityCallback}
                                    currentPageCallback={setCommunityPage}
                                />
                            )}
                            {!community && <Spinner/>}
                        </div>
                        <div className="col">
                            {question && (
                                <QuestionCard question={question} user={props.user}/>
                            )}
                            {!question && <EmptyQuestionCard/>}
                            <div>&emsp;</div>
                            <div className="overflow-auto">
                                {question && answers && community &&
                                    answers.map((answer: AnswerResponse) => (
                                        <div className="my-2" key={answer.id}>
                                            <AnswerCard
                                                answer={answer}
                                                question={question}
                                                verify={butonVerify}
                                                community={community}
                                            />
                                        </div>
                                    ))}
                                {question && answers && community && !loadingAnswers && <Pagination
                                    currentPage={currentPage}
                                    setCurrentPageCallback={changePage}
                                    totalPages={totalPages}
                                />}
                            </div>
                        </div>
                        <div className="col">
                            <div className="white-pill mt-5 mx-3">
                                <div className="card-body">
                                    <p className="h3 text-primary">{t("answer.answer")}</p>
                                    <hr></hr>
                                    <div className="form-group mt-3">
                                        <input
                                            required={true}
                                            className="form-control"
                                            type="answer"
                                            id="answer"
                                            value={answer}
                                            placeholder={t("placeholder.answer")}
                                            onChange={(e) => setAnswer(e.target.value)}
                                        />
                                    </div>
                                    <div className="d-flex justify-content-center mb-3 mt-3">
                                        {question && (
                                            <button
                                                type="submit"
                                                className="btn btn-primary"
                                                onClick={() => submit(answer, question.id)}
                                            >
                                                {t("send")}
                                            </button>
                                        )}
                                    </div>
                                    {blankAnswerError && (
                                        <div className="text-danger">{t("error.emptyAnswer")}</div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const AnswerPage = (props: { user: User }) => {
    const {questionId} = useParams();
    return (
        <React.Fragment>
            {
                <QuestionAnswers id={questionId} user={props.user}/> //
            }
        </React.Fragment>
    );
};

export default AnswerPage;
