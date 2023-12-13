import React, { useEffect, useState } from "react";
import { AnswerResponse } from "./../models/AnswerTypes";
import { useTranslation } from "react-i18next";
import { Community } from "../models/CommunityTypes";
import { deleteVote, vote } from "../services/answers";
import { Question } from "../models/QuestionTypes";
import { getQuestionFromUri } from "../services/questions";
import { format } from "date-fns";
import { getCommunityFromUri } from "../services/community";
import Spinner from "./Spinner";
import { useNavigate } from "react-router-dom";

export default function AnswerCardURI(props: { answer: AnswerResponse }) {
  //despues hay que pasarle todas las comunidades y en cual estoy
  const { t } = useTranslation();
  const [community, setCommunity] = useState<Community>();
  const [question, setQuestion] = useState<Question>();
  const [, setError] = useState<boolean>(false);

  const userId = parseInt(window.localStorage.getItem("userId") as string);
  const username = window.localStorage.getItem("username") as string;

  const navigate = useNavigate();

  useEffect(() => {
    async function fetchQuestion() {
      const question = await getQuestionFromUri(props.answer.question);
      setQuestion(question);
    }
    fetchQuestion();
  }, [props.answer.question]);

  useEffect(() => {
    if (!question) return;
    const load = async () => {
      let _community = await getCommunityFromUri(question.community);
      setCommunity(_community);
    };
    load();
  }, [question]);

  const handleClick = () => {
    if (question) {
      navigate(`/questions/${question.id}`);
    }
  };

  return (
    <div
      className="card shadowOnHover"
      onClick={handleClick}
      style={{ cursor: "pointer" }}
    >
      <div className="d-flex card-body m-0">
        <div className="row">
          <div className="col mb-0">
            <p className="h2 text-primary mb-0">{props.answer.body}</p>
            {(!question || !community) && (
              // Show spinner
              <Spinner />
            )}
            <p className="h4 text-secondary-d mb-0">
              {question && t("question.title") + ":" + question.title}
            </p>
            <div className="d-flex flex-column justify-content-center">
              {community && (
                <div className="justify-content-center mb-0">
                  <p>
                    <span className="badge badge-primary badge-pill">
                      {community.name}
                    </span>
                  </p>
                </div>
              )}
              {username && (
                <div className="justify-content-center mb-0">
                  <p className="h6">
                    {t("question.askedBy")} {username}
                  </p>
                </div>
              )}
            </div>
            <div className="text-wrap-ellipsis justify-content-center">
              <p className="h5">{props.answer.body}</p>
            </div>
            <div className="d-flex align-items-center ">
              <div className="h4">
                <i className="fas fa-calendar"></i>
              </div>
              <p className="ml-3 h6">
                {format(Date.parse(props.answer.time), "dd/MM/yyyy hh:mm:ss")}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
