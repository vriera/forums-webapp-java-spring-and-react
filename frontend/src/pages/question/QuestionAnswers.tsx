import React, { useEffect, useState } from "react";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";

import { User } from "../../models/UserTypes";

import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
import QuestionAnswersCenterPanel from "../../components/QuestionAnswersCenterPanel";
import { Question } from "../../models/QuestionTypes";
import { Community } from "../../models/CommunityTypes";
import { getQuestion } from "../../services/questions";
import { getCommunityFromUri } from "../../services/community";
import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import ProfileInfoPane from "../../components/ProfileInfoPane";
import { QuestionUserContext } from "../../resources/contexts/Contexts";
import { InternalServerError, NotFoundError } from "../../models/HttpTypes";


const AnswerPage = (props: { user: User }) => {

    const { questionId } = useParams();
    const [question, setQuestion] = useState<Question>();
    const [community, setCommunity] = useState<Community>();
    const navigate = useNavigate();
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
        debugger;
        async function loadQuestion() {
            try {

                if (!questionId) { // Verificar si questionId es undefined
                    throw new InternalServerError("Tried to fetch question with undefined id");
                }

                if (questionId) {
                    let _question = await getQuestion(parseInt(questionId));
                    setQuestion(_question);
                }
            } catch (error: any) {
                navigate(`/${error.code}`);
            }
        };
        loadQuestion();
    }, [navigate, questionId]);

    //Get community for side pane
    useEffect(() => {
        async function loadCommunity() {
            if (!question) return;

            try {
                let _community = await getCommunityFromUri(question.community);
                setCommunity(_community);
            } catch (error: any) {
                navigate(`/${error.code}`);
            }
        };

        loadCommunity();
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

export default AnswerPage;