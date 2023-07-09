import React, { useEffect, useState } from "react";
import {
  QuestionResponse,
} from "../models/QuestionTypes";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { format } from "date-fns";
import { Community } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";
import { getUserFromURI } from "../services/user";
import { getCommunityFromUri } from "../services/community";
import { Spinner } from "react-bootstrap";

export default function QuestionPreviewCard(props: {
  question: QuestionResponse;
}) {
  //despues hay que pasarle todas las comunidades y en cual estoy
  const { t } = useTranslation();
  const [community, setCommunity] = useState<Community>();
  const [owner, setOwner] = useState<User>();

  useEffect(() => {
    async function fetchUser() {
      const user = await getUserFromURI(props.question.owner);
      setOwner(user);
    }
    fetchUser();
  }, [props.question.owner]);

  useEffect(() => {
    async function fetchCommunity() {
      let community = await getCommunityFromUri(props.question.community);
      setCommunity(community);
    }
    fetchCommunity();
  }, [props.question.community]);

  return (
    <Link to={`/questions/${props.question.id}`}>
      <div className="card shadowOnHover">
        <div className="d-flex card-body m-0">
          {/* Arrow plus number of votes total, shouldn't be able to vote from here */}
          <div className="row">
            <div className="col-3">
              <div className="col-auto ml-0 pl-0">
                {/* If voteTotal is greater or equal than 0, then the arrow should be circle up */}
                {props.question.votes >= 0 && (
                  <div className="d-flex align-items-center mt-2">
                    <div className="h4 mr-2 text-success">
                      <i className="fas fa-arrow-alt-circle-up"></i>
                    </div>
                    <p className="h5 text-success">{props.question.votes}</p>
                  </div>
                )}
                {/* If voteTotal is less than 0, then the arrow should be circle down */}
                {props.question.votes < 0 && (
                  <div className="d-flex align-items-center mt-2">
                    <div className="h4 mr-2 text-danger">
                      <i className="fas fa-arrow-alt-circle-down"></i>
                    </div>
                    <p className="h5 text-danger">{props.question.votes}</p>
                  </div>
                )}
              </div>
            </div>
          </div>
          <div className="row">
            <div className="col mb-0">
              <p className="h2 text-primary mb-0">{props.question.title}</p>
              <div className="d-flex flex-column justify-content-center">
                {(!owner || !community) && (
                  // Show spinner
                  <Spinner></Spinner>
                )}
                {props.question && owner && community && (
                  <>
                    <div className="justify-content-center mb-0">
                      <p>
                        <span className="badge badge-primary badge-pill">
                          {community?.name}
                        </span>
                      </p>
                    </div>
                    <div className="justify-content-center mb-0">
                      <p className="h6">
                        {t("question.askedBy")} {owner?.username}
                      </p>
                    </div>
                  </>
                )}
              </div>
              <div className="text-wrap-ellipsis justify-content-center">
                <p className="h5">{props.question.body}</p>
              </div>
              <div className="d-flex align-items-center ">
                <div className="h4">
                  <i className="fas fa-calendar"></i>
                </div>
                <p className="ml-3 h6">
                  {format(
                    Date.parse(props.question.time),
                    "dd/MM/yyyy hh:mm:ss"
                  )}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}
