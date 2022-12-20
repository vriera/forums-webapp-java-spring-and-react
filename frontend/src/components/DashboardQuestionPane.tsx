import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {QuestionCard} from "../models/QuestionTypes"
import Pagination from "./Pagination";
import { getQuestionByUser, QuestionByUserParams } from "../services/questions";
import QuestionPreviewCard from "./QuestionPreviewCard";
import { createBrowserHistory } from "history";
import { useQuery } from "./UseQuery";
import { Navigate, useNavigate } from "react-router-dom";

const DashboardQuestionPane = () => {

    const { t } = useTranslation()
    const userId = parseInt(window.localStorage.getItem("userId") as string);

    const [ totalPages, setTotalPages ] = useState(-1);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ questions, setQuestions ] = useState<QuestionCard[]>();
    
    const navigate = useNavigate();

    const history = createBrowserHistory();
    const query = useQuery();

    // Set initial page
    useEffect(() => {
        let userPageFromQuery = query.get("page")
        setCurrentPage( userPageFromQuery? parseInt(userPageFromQuery) : 1);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`})

    }, [currentPage, history, query])

    // Fetch questions from API
    useEffect(() => {

        async function fetchUserQuestions(){
            let params: QuestionByUserParams = {
            requestorId: userId,
            page: currentPage
            }; 
            try{
                let {list, pagination} = await getQuestionByUser(params);
                setQuestions(list);
                setTotalPages(pagination.total);
            }catch{
                //TODO: Route to error page
                navigate("/error")
            }            
        }
        fetchUserQuestions();
        
    }, [currentPage, navigate, userId])    

    function setPageAndQuery(page: number){
        setCurrentPage(page);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`})
    }
    
    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.questions")}</p>
                <hr/>
                {questions && questions.length === 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="Nothing to show"/>
                    </div>
                </div>
                }
                {!questions && 
                    // Show loading spinner
                    <div className="d-flex justify-content-center">
                        <div className="spinner-border text-primary" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                }
                <div className="overflow-auto">
                    {questions && 
                    questions.map((question: QuestionCard) =>
                    <div key={question.id}>
                      <QuestionPreviewCard question={question} />
                    </div>
                    )                    
                    }                   
                </div>
                <Pagination currentPage={currentPage} totalPages={totalPages} setCurrentPageCallback={setPageAndQuery}/>
            </div>
        </div>
    );
}

export default DashboardQuestionPane