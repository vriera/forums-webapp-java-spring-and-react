import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import { CommunityResponse } from "../models/CommunityTypes";
import { Link } from "react-router-dom";
import { User } from "../models/UserTypes";
import { getUserFromURI } from "../services/user";
import { Spinner } from "react-bootstrap";

export default function CommunityPreviewCard(props: {
  community: CommunityResponse;
}) {
  const { t } = useTranslation();
  const [moderator, setModerator] = useState<User>();
  useEffect(() => {
    async function fetchUser() {
      const user = await getUserFromURI(props.community.moderator);
      setModerator(user);
    }
    fetchUser()
  } , []);


  return (
    <Link to={`/community/${props.community.id}`}>
      <div className="card p-3 m-3 shadow-sm--hover">
        <p className="h1 text-primary">{props.community.name}</p>
        <p className="h4 text-gray text-wrap-ellipsis">
          {props.community.description}
        </p>
        {/* If the moderator field is not empty */}
        {!moderator  && <Spinner></Spinner>}
        {moderator && moderator.username !== "AskAway Official" && (
          <div>
            <p className="h6 text-gray text-wrap-ellipsis">
              {t("mod.moderatedBy")} {moderator.username}
            </p>
            <p className="h6 text-gray text-wrap-ellipsis">
              {t("userCount")}: {props.community.userCount}
            </p>
          </div>
        )}
        {/* If the moderator field is empty */}
        {moderator && moderator.username === "AskAway Official" && (
          <div>
            <p className="h6 text-gray text-wrap-ellipsis">
              {t("mod.moderatedBy")} AskAway
            </p>
            <p className="h6 text-gray text-wrap-ellipsis">
              {t("userCount")}: {t("all")}
            </p>
          </div>
        )}
      </div>
    </Link>
  );
}
