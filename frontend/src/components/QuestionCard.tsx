import React, { useEffect, useState } from "react";
import { Question } from "./../models/QuestionTypes";
import { useTranslation } from "react-i18next";
import { User } from "../models/UserTypes";
import { Community } from "../models/CommunityTypes";

import { format } from "date-fns";
import { getCommunityFromUri } from "../services/community";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import VotingOptions from "./VotingOptions";

import { vote, deleteVote } from "../services/questions";


export default function QuestionCard(props: {
  question: Question;
  user: User;
}) {

  const { t } = useTranslation();

  const [image, setImage] = useState<string>();
  
  useEffect(() => {
    const load = async () => {
      setImage(props.question.image);
    };
    load();
  }, [props.question]);

  const [community, setCommunity] = useState<Community>();

  useEffect(() => {
    const load = async () => {
      let _community = await getCommunityFromUri(props.question.community);
      setCommunity(_community);
    };
    load();
  }, [props.question]);


  // TODO: Fix this page, it is not SPA. Changes should reload their respective component, not the whole page.
  // TODO: Add validation, if an operation is not successful, an error alert should be displayed

  return (
    <div className="">
      <div className="d-flex card-body m-0">
        <div className="row">
          <div className="col-2">
          <VotingOptions userVote={props.question.userVote} votes={props.question.votes} userId={props.user.id} id={props.question.id} vote={vote} deleteVote={deleteVote}/>
          </div>


          <div className="col-10 mb-0">
            <p className="h2 text-primary mb-0">{props.question.title}</p>
            <div className="d-flex flex-column justify-content-center">
              <div className="justify-content-center mb-0">
                {community && (
                  <p>
                    <span className="badge badge-primary badge-pill">
                      {community.name}
                    </span>
                  </p>
                )}
                {!community && (
                  <p>
                    <Skeleton />
                  </p>
                )}
              </div>
              <div className="justify-content-center mb-0">
                <p className="h6">
                  {t("question.askedBy")} {props.question.owner.username}
                </p>
              </div>
            </div>
            <div className="text-wrap-ellipsis justify-content-center">
              <p className="h5">{props.question.body}</p>
            </div>
            <div className="d-flex align-items-center ">
              <div className="h4">
                <i className="fas fa-calendar"></i>
              </div>
              <p className="ml-3 h6">
                {format(Date.parse(props.question.time), "dd/MM/yyyy hh:mm:ss")}
              </p>
            </div>
            <div>
              {props.question.image && (
                <img
                  style={{
                    height: "300px",
                    width: "300px",
                    alignSelf: "center",
                  }}
                  src={image}
                  alt={props.question.title}
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
