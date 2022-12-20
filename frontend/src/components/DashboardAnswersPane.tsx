import React from "react";
import {Answer} from "../models/AnswerTypes";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import Pagination from "./Pagination";
import AnswerCard from "./AnswerCard";
import {User} from "../models/UserTypes";

const DashboardAnswersPane = (props: {answers: Answer[], page: number, totalPages: number, user: User}) => {
    const { t } = useTranslation()
    const [currentPage, setCurrentPage] = useState(props.page)

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.answers")}</p>
                <hr/>
                {props.answers.length === 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                    </div>
                </div>
                }
                <div className="overflow-auto">
                    {
                    props.answers.map((answer: Answer) =>
                    <div key={answer.id}>
                    <AnswerCard answer={answer} user={props.user}/>
                    </div>
                    )                    
                    }                   
                </div>
                <Pagination currentPage={currentPage} totalPages={props.totalPages} setCurrentPageCallback={setCurrentPage}/>
            </div>
        </div>
    )
}

export default DashboardAnswersPane