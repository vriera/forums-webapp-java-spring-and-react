import React from "react";
import { Community } from "../models/CommunityTypes";
import Pagination from "./Pagination";

const CommunitiesCard = (props: {communities: Community[], selectedCommunity: Community, selectedCommunityCallback: (community: Community) => void , currentPage: number, totalPages: number, currentPageCallback: (page: number) => void, title: string}) => {
    console.log("selected:"+props.selectedCommunity.name)
    return(
            <div className="white-pill mt-5 mx-3">
                <div className="card-body">
                    <p className="h3 text-primary">{props.title}</p>
                    <hr></hr>
                    <div className="container-fluid">
                        {props.communities.map( community => 
                        <button onClick={() => props.selectedCommunityCallback(community)} className={"btn  badge-pill badge-lg my-3 " + (community.id !== props.selectedCommunity.id?  "btn-outline-primary":"") + (community.id === props.selectedCommunity.id? "btn-light":"")}>{community.name}</button> )}
                    </div>
                </div>
                <Pagination currentPage={props.currentPage} setCurrentPageCallback={props.currentPageCallback} totalPages={props.totalPages}/>
            </div>
    )
}

export default CommunitiesCard;