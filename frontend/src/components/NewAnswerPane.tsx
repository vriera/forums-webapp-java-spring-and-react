import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import Spinner from "./Spinner";
import { Question } from "../models/QuestionTypes";
import { createAnswer } from "../services/answers";

import { useNavigate } from "react-router-dom";



const NewAnswerPane = (props: { questionId: number }) => {
    const { t } = useTranslation();

    const [answer, setAnswer] = React.useState("");
    const [blankAnswerError, setBlankAnswerError] = useState(false);
    const navigate = useNavigate();

    // ---------------------------------------------
    // FUNCTIONS
    // ---------------------------------------------
    function submit(answer: any, idQuestion: number) {
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

        };
        load();
    }
    // ---------------------------------------------

    return (
        <div className="white-pill mt-5 mx-3">
            <div className="card-body">
                <p className="h3 text-primary">{t("answer.answer")}</p>
                <hr></hr>
                <div className="form-group mt-3">
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
                <div className="d-flex justify-content-center mb-3 mt-3">
                    <button
                        type="submit"
                        className="btn btn-primary"
                        onClick={() => submit(answer, props.questionId)}
                    >
                        {t("send")}
                    </button>
                </div>
                {blankAnswerError && (
                    <div className="text-danger">{t("error.emptyAnswer")}</div>
                )}
            </div>
        </div>
    );
}

export default NewAnswerPane;