import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";

import { User } from "../../models/UserTypes";

import Spinner from "../../components/Spinner";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
import NewAnswerPane from "../../components/NewAnswerPane";
import { AnswerResponse } from "../../models/AnswerTypes";
import { Question } from "../../models/QuestionTypes";
import { getQuestion } from "../../services/questions";
import { getAnswers, createAnswer } from "../../services/answers";

import QuestionCard from "../../components/QuestionCard";
import AnswerCard from "../../components/AnswerCard";

import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";


const CenterPanel = (props: {
    user: User;
    currentPageCallback: (page: number) => void;
    questionId: string | undefined;
}) => {


    const [loading, setLoading] = useState(true);
    const [question, setQuestion] = useState<Question>();
    const [answers, setAnswers] = useState<AnswerResponse[]>();
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [buttonVerify, setButtonVerify] = useState(false); //TODO: Corroborar que chota esta haciendo con esta variable en el AnswerCard

    const history = createBrowserHistory();
    const navigate = useNavigate();
    const { t } = useTranslation();

    //------------------Functions------------------

    const changePage = (page: number) => {
        setCurrentPage(page);
        props.currentPageCallback(page);
    };

    //---------------------------------------------


    // ------------------UseEffect------------------
    //get question
    useEffect(() => {
        const load = async () => {
            try {

                if (!props.questionId) { // Verificar si questionId es undefined
                    navigate("/error"); // Redirigir a la página de error
                    return;
                }

                if (props.questionId) {
                    let _question = await getQuestion(parseInt(props.questionId));//TODO: ver que hago con esto, que pasa si es undefined?
                    setQuestion(_question);
                }
                const params = new URLSearchParams(history.location.search);
                const page = params.get("page");
                page && setCurrentPage(Number(page));
            } catch (error: any) {
                navigate("/500");
            }
        };
        load();
    }, []);



    //get answers
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
                navigate(`/${error.code}`);
            }
        };
        load();
    }, [question, currentPage]);
    //---------------------------------------------

    return (

        <div>
            <div className="col-12 center mt-5">
                <div className="white-pill ">
                {!question && <Spinner />}
                    {question && (
                        <QuestionCard question={question} user={props.user} />
                    )}
                </div>
            </div>


            <div className="white-pill mt-5">
                <div className="card-body">
                    <div className="overflow-auto">
                        {answers && question &&
                            answers.map((answer: AnswerResponse) => (
                                <div className="my-2" key={answer.id}>
                                    <AnswerCard
                                        answer={answer}
                                        question={question} //TODO: Para que necesito este question en el answer card??? 
                                        verify={buttonVerify}
                                    />
                                </div>
                            ))}
                        <Pagination
                            currentPage={currentPage}
                            setCurrentPageCallback={changePage}
                            totalPages={totalPages}
                        />
                    </div>
                </div>
            </div>
        </div>

    );
};


const AnswerPage2 = (props: { user: User }) => {

    const { questionId } = useParams();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const history = createBrowserHistory();

    let { communityPage, page } = useParams();

    //Functions:

    function setPage(pageNumber: number) {
        page = pageNumber.toString();
        const newCommunityPage = communityPage ? communityPage : 1;
        history.push({
            pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${newCommunityPage}`,
        });
    }

    //Community panel functions (left panel)
    function setCommunityPage(pageNumber: number) {
        communityPage = pageNumber.toString();
        history.push({
            pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${communityPage}`,
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


    return (
        <div className="section section-hero section-shaped">
            <Background />

            <div className="row">
                <div className="col-3">
                    <CommunitiesLeftPane
                        selectedCommunity={undefined}
                        selectedCommunityCallback={selectedCommunityCallback}
                        currentPageCallback={setCommunityPage}
                    />
                </div>
                <div className="col-6">
                    <CenterPanel user={props.user} currentPageCallback={setPage} questionId={questionId} />
                </div>

                <div className="col-3">
                    <NewAnswerPane questionId={39} />
                    {/* TODO: Fix this hardcoded value */}
                </div>
            </div>
        </div>
    );
}

export default AnswerPage2;