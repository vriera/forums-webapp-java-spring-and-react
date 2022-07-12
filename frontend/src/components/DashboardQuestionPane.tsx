import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import {Question} from "../models/QuestionTypes"
import QuestionCard from "./QuestionCard";

const DashboardQuestionPane = (props: {questions: Question[], page: number, totalPages: number}) => {
    const { t } = useTranslation()
    const [currentPage, setCurrentPage] = useState(props.page)
    const pages = Array.from({length: props.totalPages}, (_, index) => index + 1);
    console.log(currentPage)



    function prevPage(){
        if(!(currentPage == 1)) 
            setCurrentPage(currentPage-1)
    }

    function nextPage(){
        if(!(currentPage == props.totalPages || props.totalPages == 1)) 
            setCurrentPage(currentPage+1)
    }
    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.questions")}</p>
                <hr/>
                {props.questions.length == 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src="/resources/images/empty.png" alt="No hay nada para mostrar"/>
                    </div>
                </div>
                }
                <div className="overflow-auto">
                    {
                    props.questions.map((question: Question) =>
                    <div key={question.id}>
                        <QuestionCard question={question}/>
                    </div>
                    )                    
                    }                   
                
                    {/* <!-- PAGINACION --> */}
                    <nav aria-label="Page navigation example" className="d-flex justify-content-center">
                        <ul className="pagination">

                            {/* <!-- FLECHITA DE PREVIOUS; QUEDA DISABLED SI ESTOY EN = --> */}
                            <li className="page-item">
                                <button className={"page-link " + ((currentPage == 1)? "disabled" : "")} onClick={prevPage}>
                                    <i className="fa fa-angle-left"></i>
                                </button>
                            </li>

                            {/* <!-- NUMERICOS --> */}
                            {
                            pages.map((page: number) =>
                            <li key={page} className={"page-item " + ((page == currentPage)? "active" : "")} >
                                <button className="page-link" onClick={() => setCurrentPage(page)}>
                                    {page}
                                </button>
                            </li>
                            )
                            }

                            { /*<!-- FLECHITA DE NEXT --> */}                            
                            <li className="page-item">
                                <button className={"page-link " + ((currentPage == props.totalPages || props.totalPages == 1)? "disabled" : "")} onClick={nextPage} aria-label="Next">
                                    <i className="fa fa-angle-right"></i>
                                </button>
                            </li>
                        
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    );
}

export default DashboardQuestionPane