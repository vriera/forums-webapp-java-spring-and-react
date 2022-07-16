import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import {Question} from "../models/QuestionTypes"
import QuestionCard from "./QuestionCard";
import Pagination from "./Pagination";

const DashboardQuestionPane = (props: {questions: Question[], page: number, totalPages: number}) => {
    const { t } = useTranslation()
    const [currentPage, setCurrentPage] = useState(props.page)
    
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
                    <div className="m-3" key={question.id}>
                      <QuestionCard question={question} />
                    </div>
                    )                    
                    }                   
                </div>
                <Pagination currentPage={currentPage} totalPages={props.totalPages} setCurrentPageCallback={setCurrentPage}/>
            </div>
        </div>
    );
}

export default DashboardQuestionPane