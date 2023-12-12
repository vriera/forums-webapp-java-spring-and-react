import React, { useEffect, useState } from "react";
import { AnswerResponse } from "./../models/AnswerTypes";
import { useTranslation } from "react-i18next";
import { User } from "../models/UserTypes";
import { Community } from "../models/CommunityTypes";
import Spinner from "./Spinner";
import {
  deleteVote,
  unVerifyAnswer,
  verifyAnswer,
  vote,
} from "../services/answers";
import { Question } from "../models/QuestionTypes";
import { format } from "date-fns";
import { getUserFromUri } from "../services/user";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import VotingOptions from "./VotingOptions";

export default function AnswerCard(props: {
  answer: AnswerResponse;
  verify?: Boolean;
}) {
  const { t } = useTranslation();
  const [user, setUser] = useState<User>();
  const userId = parseInt(window.localStorage.getItem("userId") as string);

  // Add loading state
  const [loading, setLoading] = useState<boolean>(false);
  const [verified, setVerified] = useState<boolean>(props.answer.verify);

  useEffect(() => {
    async function ownerLoad() {
      const _user = await getUserFromUri(props.answer.owner);
      setUser(_user);
    }
    ownerLoad();
  }, []);


  function verify() {
    const verify = async () => {
      await verifyAnswer(props.answer.id);
      setVerified(true);
      setLoading(false);
    };
    verify();
  }

  function unVerify() {
    const unverify = async () => {
      await unVerifyAnswer(props.answer.id);
      setVerified(false);
      setLoading(false);
    };
    unverify();
  }

  return (
    <div className="white-pill-no-shadow card">
      <div className="">
        <div className="card-body mx-0">
          <div className="row">
            {/* Parte de flechitas */}
            <div className="col-2">
              <VotingOptions userVote={props.answer.userVote} votes={props.answer.voteCount} userId={isNaN(userId)?null : userId } id={props.answer.id} vote={vote} deleteVote={deleteVote} />
            </div>

            {/* Contenido de la respuesta */}
            <div className="col-10 mb-0 mx-0">
              <div className="mx-0">
                {/* <p className="h2 text-primary mb-0">{props.answer.title}</p> */}
                <div className="d-flex justify-content-left">
                  <div className="justify-content-left mb-0">
                    {verified && (
                      <span className="badge badge-pill badge-success mb-2">{t("answer.verified")}</span>
                    )}
                    {/* Cargo contenido del usuario */}
                    {user && (
                      <p className="h6">
                        {t("question.answeredBy")} {user.username}
                      </p>
                    )}{" "}
                    {!user && (
                      <p className="h6">
                        {" "}
                        <Skeleton />{" "}
                      </p>
                    )}
                  </div>
                </div>
                <div className="justify-content-left">
                  <p className="h5">{props.answer.body}</p>
                </div>
                <div className="d-flex align-items-left ">
                  <div className="h4">
                    <i className="fas fa-calendar"></i>
                  </div>
                  <p className="ml-3 h6">
                    {format(Date.parse(props.answer.time), "dd/MM/yyyy hh:mm:ss")}
                  </p>
                </div>
                <div className="">
                  {props.verify && !verified && (
                    <button
                      type="submit"
                      className="btn btn-primary"
                      onClick={() => verify()}
                      disabled={loading}
                    >
                      {loading ? <Spinner /> : t("verify.message")}
                    </button>
                  )}

                  {props.verify && verified && (
                    <button
                      type="submit"
                      className="btn btn-primary"
                      onClick={() => unVerify()}
                      disabled={loading}
                    >
                      {loading ? <Spinner /> : t("unverify")}
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>


        </div>
      </div>
    </div>
  );
}
