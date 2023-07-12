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


    const limit = 5;

    const [loading, setLoading] = useState(true);
    const [question, setQuestion] = useState<Question>();
    const [answers, setAnswers] = useState<AnswerResponse[]>();
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [buttonVerify, setButtonVerify] = useState(false); //TODO: Corroborar que chota esta haciendo con esta variable en el AnswerCard

    const history = createBrowserHistory();
    const navigate = useNavigate();
    const { t } = useTranslation();

    //Esta variable es la nueva answer que estoy escribiendo, a no confundir con la lista de answers
    const [answer, setAnswer] = React.useState("");
    const [blankAnswerError, setBlankAnswerError] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    //TODO: Simplificar el uso de esta variable. No es necesaria
    const [questionIdNumber, setQuestionIdNumber] = useState<number>(0);


    //------------------Functions------------------

    const changePage = (page: number) => {
        setCurrentPage(page);
        props.currentPageCallback(page);
    };

    const refreshAnswers = async () => {
        if (!question) return; //Esto no me encanta, pero está para atajar el caso de question undefined
        try {
            await getAnswers(question, currentPage, limit).then((response) => {
                setAnswers(response.list);
                setTotalPages(Math.ceil(response.pagination.total / limit));
            });
        }
        catch (error: any) {
            navigate(`/${error.code}`);
        }

    }

    function submit(answer: any, idQuestion: number) {
        setIsLoading(true);
        const load = async () => {
            // Check if answer is blank
            if (Object.keys(answer).length === 0) {
                setBlankAnswerError(true);
                return;
            }

            // Create answer
            try {
                await createAnswer(answer, idQuestion);
            } catch (error: any) {
                navigate(`/${error.code}`)
            }
            finally {
                setIsLoading(false);
                setAnswer("");
                refreshAnswers();
            }

        };
        load();
    }

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
                    let _question = await getQuestion(parseInt(props.questionId));
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
            {/* ------------------------------------------------------------------------------------------- */}
            {/* // Sector de pregunta */}
            {/* ------------------------------------------------------------------------------------------- */}
            <div className="col-12 center mt-5">
                <div className="white-pill ">
                    {!question && <Spinner />}
                    {question && (
                        <QuestionCard question={question} user={props.user} />
                    )}
                </div>
            </div>




            <div className="white-pill mt-5">

                {/* ------------------------------------------------------------------------------------------- */}
                {/* Sector de agregar una respuesta nueva */}
                {/* ------------------------------------------------------------------------------------------- */}
                <div className="card-body">
                    <p className="h3 text-primary">{t("answer.answer")}</p>
                    <div className="row">
                        {/* Form de answer */}
                        <div className="col-9 d-flex justify-content-center">
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
                        {/* Boton de submit */}
                        <div className="d-flex justify-content-center col-3">
                            {question && (
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    onClick={() => submit(answer, question.id)}
                                    disabled={isLoading}
                                >
                                    {t("send")}
                                </button>
                            )}
                            {!question && (
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={true}
                                >
                                    {t("send")}
                                </button>
                            )}

                            {isLoading && <Spinner />}
                        </div>
                    </div>

                    {blankAnswerError && (
                        <div className="text-danger">{t("error.emptyAnswer")}</div>
                    )}


                    <hr></hr>
                    {/* ------------------------------------------------------------------------------------------- */}
                    {/* Sector de respuestas historicas */}
                    {/* ------------------------------------------------------------------------------------------- */}
                    {answers === undefined && <Spinner />}

                    <div className="overflow-auto mt-1">
                        {answers && question &&
                            answers.map((answer: AnswerResponse) => (
                                <div className="my-2" key={answer.id}>
                                    <AnswerCard
                                        answer={answer}
                                        verify={buttonVerify}
                                    />
                                </div>
                            ))}
                        {answers !== undefined && answers?.length == 0 && <div className="text-center">{t("answer.noAnswers")}</div>}
                        {answers && answers.length > 0 &&
                            <Pagination
                                currentPage={currentPage}
                                setCurrentPageCallback={changePage}
                                totalPages={totalPages}
                            />
                        }
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

    //-----------------------------------------------------------------------
    //Functions:
    //-----------------------------------------------------------------------

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
                    <NewAnswerPane />
                    {/* TODO: Fix this hardcoded value */}
                </div>
            </div>
        </div>
    );
}

export default AnswerPage2;