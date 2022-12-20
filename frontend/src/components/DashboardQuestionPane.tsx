import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {QuestionCard} from "../models/QuestionTypes"
import Pagination from "./Pagination";
import { getQuestionByUser, QuestionByUserParams } from "../services/questions";
import QuestionPreviewCard from "./QuestionPreviewCard";
import { createBrowserHistory } from "history";
import { useQuery } from "./UseQuery";

const DashboardQuestionPane = () => {

    const { t } = useTranslation()
    const userId = parseInt(window.localStorage.getItem("userId") as string);
    const [ totalPages, setTotalPages ] = useState(-1);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ questions, setQuestions ] = useState<QuestionCard[]>([]);

    const history = createBrowserHistory();
    const query = useQuery();

    // Set initial page
    useEffect(() => {
        let userPageFromQuery = query.get("page")
        setCurrentPage( userPageFromQuery? parseInt(userPageFromQuery) : 1);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`})

    }, [])

    // Fetch questions from API
    useEffect(() => {
       

        async function fetchUserQuestions(){
            let params: QuestionByUserParams = {
            requestorId: userId,
            page: currentPage
            }; 

            let {list, pagination} = await getQuestionByUser(params);
            setQuestions(list);
            setTotalPages(pagination.total);
        }
        fetchUserQuestions();
        
    }, [currentPage])    

    function setPageAndQuery(page: number){
        setCurrentPage(page);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`})
    }
    
    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.questions")}</p>
                <hr/>
                {questions.length === 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                    </div>
                </div>
                }
                <div className="overflow-auto">
                    {
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