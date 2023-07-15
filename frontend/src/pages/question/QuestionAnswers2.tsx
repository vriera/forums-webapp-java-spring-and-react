import React, { useContext, useEffect, useState, createContext } from "react";
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
import QuestionAnswersCenterPanel from "../../components/QuestionAnswersCenterPanel";
import NewAnswerPane from "../../components/NewAnswerPane";
import { AnswerResponse } from "../../models/AnswerTypes";
import { Question } from "../../models/QuestionTypes";
import { Community } from "../../models/CommunityTypes";
import { getQuestion } from "../../services/questions";
import { getAnswers, createAnswer } from "../../services/answers";
import { getCommunityFromUri } from "../../services/community";
import { getUser } from "../../services/user";

import QuestionCard from "../../components/QuestionCard";
import AnswerCard from "../../components/AnswerCard";


import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";
import ProfileInfoPane from "../../components/ProfileInfoPane";



import { QuestionUserContext } from "../../resources/contexts/Contexts";


const AnswerPage2 = (props: { user: User }) => {

    const { questionId } = useParams();
    const [question, setQuestion] = useState<Question>();
    const [community, setCommunity] = useState<Community>();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const history = createBrowserHistory();
    const [questionUser, setQuestionUser] = useState(undefined);


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

    //-----------------------------------------------------------------------
    //Use effect:
    //-----------------------------------------------------------------------
    //get question
    useEffect(() => {
        const load = async () => {
            try {

                if (!questionId) { // Verificar si questionId es undefined
                    navigate("/error"); // Redirigir a la página de error
                    return;
                }

                if (questionId) {
                    let _question = await getQuestion(parseInt(questionId));

                    setQuestion(_question);
                    //TODO: Migrarlo
                    //const responseQuestionUser = await getUser(_question.owner.id);
                    //setQuestionUser(responseQuestionUser);

                }
            } catch (error: any) {
                navigate("/500");
            }
        };
        load();
    }, []);

    //Get community for side pane
    useEffect(() => {
        if (!question) return;
        const load = async () => {
            try {
                let _community = await getCommunityFromUri(question.community);
                setCommunity(_community);
            } catch (error: any) {
                if (error.response.status === 404) navigate("/404");
                else if (error.response.status === 403) navigate("/403");
                else if (error.response.status === 401) navigate("/401");
                else navigate("/500");
            }
        };
        load();
    }, [question, navigate]);


    return (
        <div className="section section-hero section-shaped">
            <Background />
            <div className="row">
                <div className="col-3">
                    <CommunitiesLeftPane
                        selectedCommunity={community?.id}
                        selectedCommunityCallback={selectedCommunityCallback}
                        currentPageCallback={setCommunityPage}
                    />
                </div>
                <div className="col-6">
                    <QuestionUserContext.Provider value={{ questionUser, setQuestionUser }}>
                        <QuestionAnswersCenterPanel user={props.user} currentPageCallback={setPage} question={question} questionId={questionId} />
                    </QuestionUserContext.Provider>
                </div>

                <div className="col-3">
                    <div className="mr-3">
                        <QuestionUserContext.Provider value={{ questionUser, setQuestionUser }}>
                            <ProfileInfoPane user={questionUser} showUpdateButton={false} shouldFetchUser={false} title={"title.ownerProfile"} />
                        </QuestionUserContext.Provider>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AnswerPage2;