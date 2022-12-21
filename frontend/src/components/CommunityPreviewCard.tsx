import React from "react";
import { useTranslation } from "react-i18next"


import { CommunityCard } from "../models/CommunityTypes";
import { Link } from "react-router-dom";

export default function CommunityPreviewCard(props: {community: CommunityCard}) {
    const {t} = useTranslation()


    return (
        <Link to={`/community/${props.community.id}`}>
            <div className="card p-3 m-3 shadow-sm--hover">
                <p className="h1 text-primary">{props.community.name}</p>
                <p className="h4 text-gray text-wrap-ellipsis">{props.community.description}</p>
                {/* If the moderator field is not empty */}
                {props.community.moderator.username !== "AskAway Official" &&
                    <div>
                        <p className="h6 text-gray text-wrap-ellipsis">{t("mod.moderatedBy")} {props.community.moderator.username}</p>
                        <p className="h6 text-gray text-wrap-ellipsis">{t("userCount")}: {props.community.userCount}</p>
                    </div>
                }
                {/* If the moderator field is empty */}
                {props.community.moderator.username === "AskAway Official"  &&
                    <div>
                        <p className="h6 text-gray text-wrap-ellipsis">{t("mod.moderatedBy")} AskAway</p>
                        <p className="h6 text-gray text-wrap-ellipsis">{t("userCount")}: {t("all")}</p>
                    </div>
                }
            </div>
        </Link>
        
    )

}