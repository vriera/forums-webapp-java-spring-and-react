import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import { Community, CommunityCard } from "../../../models/CommunityTypes";
import Background from "../../../components/Background";
import { getAllowedCommunity } from "../../../services/community";





const community1: Community = {
    id: 1,
    name: "Community 1",
    description: "This is the first community",
    moderator: {
        id: 1,
        username: "User 1",
        email: "use1@gmail.com",
    },
    notifications: {
        requests: 1,
        invites: 2,
        total: 3,
    },
    userCount: 5
}
const community2: Community = {
    id: 1,
    name: "Community 2",
    description: "This is the first community",
    moderator: {
        id: 1,
        username: "User 1",
        email: "use1@gmail.com",
    },
    notifications: {
        requests: 1,
        invites: 2,
        total: 3,
    },
    userCount: 5
}

const SelectCommunityPage = (props: {}) => {
    
    const { t } = useTranslation();

    const [communitiesArray, setCommunities] = React.useState<CommunityCard[]>([]);
    const requestorId = parseInt(window.localStorage.getItem("userId") as string)

    useEffect(() => {
        getAllowedCommunity({requestorId: requestorId, page: 1}).then(
            (response) => {
                setCommunities(response);
            }
        )
    }, [])
    return (
        <div className="section section-hero section-shaped">
                <Background/>
            <div className="container">
                <div className="white-pill">
                    <div className="d-flex justify-content-center">
                        <p className="h1 text-primary text-center">{t("title.askQuestion")}</p>
                    </div>
                    <hr/>
                        <SelectCommunity communityList={communitiesArray}/>

                    <hr/>
                    {/* STEPPER */}
                    <div className="stepper-wrapper">
                        {/* Classname should be active if currentProgress is 1 */}
                        <div className="stepper-item active">
                            <div className="step-counter">1</div>
                            <div className="step-name">{t("question.community")}</div>
                        </div>
                        <div className= "stepper-item ">
                            <div className="step-counter">2</div>
                            <div className="step-name">{t("question.content")}</div>
                        </div>
                        <div className= "stepper-item " >
                            <div className="step-counter">3</div>
                            <div className="step-name">{t("question.wrapup.message")}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}


const SelectCommunity = (props:{communityList: CommunityCard[]} ) => {

    const { t } = useTranslation();
    
    return (
        <>
            <p className="h5 text-black">{t("question.chooseCommunityCallToAction")}</p>
            <div className="container">
                {props.communityList.map(community => 
                    <Link to={`/ask/writeQuestion/${community.id}`} className="btn btn-outline-primary badge-pill badge-lg my-3">{community.name}</Link>
                )}
            </div>
        </>
    )
}

export default SelectCommunityPage