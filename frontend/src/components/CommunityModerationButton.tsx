import React from "react";
import { CommunityCard } from "../models/CommunityTypes";
import Pagination from "./Pagination";
import { useTranslation } from "react-i18next";
import { useState, useEffect } from "react";
import { getCommunityNotifications } from "../services/community";



const CommunityModerationButton = (props: {community: CommunityCard, selectedCommunity: CommunityCard, setSelectedCommunityCallback: (community: CommunityCard) => void }) => {
 
        const community = props.community;
        const [notifications , setNotifications ] = useState(0);
       // const [selectedCommunity , setSelected ] = useState<CommunityCard>(props.selectedCommunity);

        useEffect(() => {
            async function getNotifications(){
                const n = await getCommunityNotifications(props.community.id);
                console.log("notifications:" + n);
                setNotifications(n);
            }
            getNotifications()
        }, []);
      //  useEffect( () => {} , [selectedCommunity])
        function setSelectedCommunity(community: CommunityCard){
            props.setSelectedCommunityCallback(community)
            //setSelected(community);
            
        }
        return (
            <div className="row">
                <button onClick={() => setSelectedCommunity(community)} className={"btn  badge-pill badge-lg my-3 " + (community.id !== props.selectedCommunity.id?  "btn-outline-primary":"") + (community.id === props.selectedCommunity.id? "btn-light":"")}>{community.name}   
                {notifications > 0 && community.id !== props.selectedCommunity.id && <>
                    <span className="ml-3 btn text-red  badge-pill ">{notifications}</span>
        </>} </button> 
                         
            </div>
        )

}

export default CommunityModerationButton;