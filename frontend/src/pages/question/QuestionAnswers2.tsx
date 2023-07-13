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
import { getQuestion } from "../../services/questions";
import { getAnswers, createAnswer } from "../../services/answers";

import QuestionCard from "../../components/QuestionCard";
import AnswerCard from "../../components/AnswerCard";


import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";
import ProfileInfoPane from "../../components/ProfileInfoPane";


import { QuestionUserContext } from "../../resources/contexts/Contexts";


const AnswerPage2 = (props: { user: User }) => {

    const { questionId } = useParams();
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
                    <QuestionUserContext.Provider value={{ questionUser, setQuestionUser }}>
                        <QuestionAnswersCenterPanel user={props.user} currentPageCallback={setPage} questionId={questionId} />
                    </QuestionUserContext.Provider>
                </div>

                <div className="col-3">
                    <div className="mr-3">
                        <QuestionUserContext.Provider value={{ questionUser, setQuestionUser }}>
                            <ProfileInfoPane user={questionUser} showUpdateButton={false} />
                        </QuestionUserContext.Provider>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AnswerPage2;