import React from "react";
import { CommunityCard } from "../models/CommunityTypes";
import Pagination from "./Pagination";
import { useTranslation } from "react-i18next";
import { useState, useEffect } from "react";
import CommunityModerationButton from "./CommunityModerationButton";
// Pane to display the list of communities the user is a moderator of and callbacks to select a page and community
const ModeratedCommunitiesPane = (props: {communities: CommunityCard[], selectedCommunity: CommunityCard, setSelectedCommunityCallback: (community: CommunityCard) => void , currentPage: number, totalPages: number, setCurrentPageCallback: (page: number) => void}) => {
   
    const { t } = useTranslation()
    return(
            <div className="white-pill mt-5 mx-3">
                <div className="card-body">
                    <p className="h3 text-primary">{t("dashboard.Modcommunities")}</p>
                    <hr></hr>
                    <div className="container-fluid">
                        {props.communities && props.communities.length > 0 &&
                        props.communities.map( (community) => (
                            <CommunityModerationButton community={community} setSelectedCommunityCallback={props.setSelectedCommunityCallback} selectedCommunity={props.selectedCommunity}/>
                        ))}
                        {
                            props.communities && props.communities.length === 0 &&
                            <div>
                                <p className="row h1 text-gray">{t("dashboard.noBanned")}</p>
                                <div className="d-flex justify-content-center">
                                    <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="Nothing to show"/>
                                </div>
                            </div>                            
                        }
                        {
                            props.communities && props.communities.length === 0 &&
                            <div>
                                <p className="row h1 text-gray">{t("dashboard.noBanned")}</p>
                                <div className="d-flex justify-content-center">
                                    <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="Nothing to show"/>
                                </div>
                            </div>
                        }
                    </div>
                </div>
                <Pagination currentPage={props.currentPage} setCurrentPageCallback={props.setCurrentPageCallback} totalPages={props.totalPages}/>
            </div>
    )
}

export default ModeratedCommunitiesPane;