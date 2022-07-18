import React from "react";
import { Community } from "../models/CommunityTypes";
import Pagination from "./Pagination";
import { useTranslation } from "react-i18next";

interface arg{
    communities : Array<String>
    thisCommunity: String
}
export default function CommunitiesCard(props: {communities: Community[], selectedCommunity: Community, selectedCommunityCallback: (community: Community) => void , currentPage: number, totalPages: number, currentPageCallback: (page: number) => void}){ 
    const {t} = useTranslation();
    console.log("selected:"+props.selectedCommunity)
    return(
            <div className="white-pill mt-5 mx-3">
                <div className="card-body">
                    <p className="h3 text-primary">{t("dashboard.Modcommunities")}</p>
                    <hr></hr>
                    <div className="container-fluid">
                        {props.communities.map( community => 
                        <button onClick={() => props.selectedCommunityCallback(community)} className={"btn  badge-pill badge-lg my-3 " + (community.id != props.selectedCommunity.id?  "btn-outline-primary":"") + (community.id == props.selectedCommunity.id? "btn-light":"")}>{community.name}</button> )}
                    </div>
                </div>
                <Pagination currentPage={props.currentPage} setCurrentPageCallback={props.currentPageCallback} totalPages={props.totalPages}/>
            </div>
    )
}