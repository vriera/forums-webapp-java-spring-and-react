import React from "react";
import {Answer, AnswerResponse} from "../models/AnswerTypes";

import { useTranslation } from "react-i18next";
import Pagination from "./Pagination";
import AnswerCardURI from "./AnswerCardURI";
import {User} from "../models/UserTypes";
import { createBrowserHistory } from "history";
import { useQuery } from "./UseQuery";
import { AnswersByOwnerParams , getByOwner } from "../services/answers"
import { useState , useEffect } from "react";

const DashboardAnswersPane = () => {
    const { t } = useTranslation()


    const [ totalPages, setTotalPages ] = useState(-1);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ answers, setAnswers ] = useState<AnswerResponse[]>([]);

    const history = createBrowserHistory();
    const query = useQuery();

    // Set initial page
    useEffect(() => {
        let userPageFromQuery = query.get("page")
        setCurrentPage( userPageFromQuery? parseInt(userPageFromQuery) : 1);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/answers?page=${currentPage}`})

    }, [])

    // Fetch questions from API
    useEffect(() => {
        const userId = parseInt(window.localStorage.getItem("userId") as string);

        async function fetchUserAnswer(){
            let params: AnswersByOwnerParams = {
            requestorId: userId,
            page: currentPage
            }; 

            let {list, pagination} = await getByOwner(params);
            setAnswers(list);
            setTotalPages(pagination.total);
        }
        fetchUserAnswer();
        
    }, [currentPage])    

    function setPageAndQuery(page: number){
        setCurrentPage(page);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`})
    }
    
    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.answers")}</p>
                <hr/>
                {answers.length === 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                    </div>
                </div>
                }
                <div className="overflow-auto">
                    {
                    answers.map((answer: AnswerResponse) =>
                    <div key={answer.id}>
                        <AnswerCardURI answer={answer}/>
                    </div>
                    )                    
                    }                   
                </div>
                <Pagination currentPage={currentPage} totalPages={totalPages} setCurrentPageCallback={setCurrentPage}/>
            </div>
        </div>
    )
}

export default DashboardAnswersPane