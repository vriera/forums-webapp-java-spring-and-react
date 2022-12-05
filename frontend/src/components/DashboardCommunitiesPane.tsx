import React from "react";
import { useTranslation } from "react-i18next";
import { t } from "i18next";
import { useEffect, useState } from "react";
import { getCommunityModerationList, getModerationListType, CommunityModerationSearchParams} from "../services/community";
import Pagination from "./Pagination";
import { Community } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";
import { getUserFromURI, updateUserInfo } from "../services/user";
import { Link } from "react-router-dom";



const communities = [
    "Historia","matematica","logica"
]


const CommunityInformationPane = (props: {communityName: string, communityDescription: string}) => {
    const { t } = useTranslation();

    return (
        <div className="col-6 center">
                <div className="white-pill h-75 ">
                    <div className="align-items-start d-flex justify-content-center my-3">
                        <p className="h1 text-primary bold"><strong>{t(props.communityName)}</strong></p>
                    </div>
                    <div className="text-gray text-center mt--4 mb-2">{props.communityDescription}</div>

                </div>
        </div>
    )
}


const Tabs = (props: { option: string, setOptionCallback: (option: "members" | "access" | "banned") => void}) => {
    const {t} = useTranslation();

    return(
        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "members" && "active")} onClick={() => props.setOptionCallback("members")}>
                        {t("dashboard.members")}
                    </button>
                </li>

                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "access" && "active")} onClick={() => props.setOptionCallback("access")}>
                        {t("dashboard.invites")}
                    </button>
                </li>

                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "banned" && "active")} onClick={() => props.setOptionCallback("banned")}>
                        {t("dashboard.banned")}
                    </button>
                </li>
                              
                
            </ul>
        </div>
    )
}

const MemberCard = (props: {user: User}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.user.username}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-user-minus"></i>
                    </div>
                </button>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-user-slash"></i>
                    </div>
                </button>

            </div>
        </div>
    )
}

const BlockedMemberCard = (props: {user: User}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.user.username}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-unlock"></i>
                    </div>
                </button>
            </div>
        </div>
    )
}

const AccessCard = (props: {user: User}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.user.username}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-times-circle"></i>
                    </div>
                </button>
            </div>
        </div>
    )
}


const MembersContent = (props: {community: Community}) => {
    const {t} = useTranslation();
    const [category, setCategory] = useState("admitted");
    const [membersList, setMembersList] = useState(null as unknown as User[])
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState();
    
    console.log("Community from pane: " + props.community.name)

    // Update lists every time the page changes  
    useEffect( () => {
        let params: CommunityModerationSearchParams = {
            type: getModerationListType(category),
            communityId: props.community.id,
            page: currentPage
        }
        // Returns a list of user URLs 
        getCommunityModerationList(params).then((res) => {
            setTotalPages(res.totalPages);
            let promises: Promise<any>[] = []
            let members: User[] = []

            let usersList = res.usersList;
            usersList.forEach((userURI: string) => {
                promises.push(getUserFromURI(userURI))
            })

            Promise.all(promises).then(
                (usersList) => 
                    (usersList).forEach(
                        (resolvedUser: User) => {
                            members.push(resolvedUser)
                            setMembersList(members)
                        }
                    )
            )
        })
                
    }, [currentPage, category]);
    return (
        <>
            {/* Different titles according to the corresponding tab */}
            {category === "members" &&
                <p className="h3 text-primary">{t("dashboard.members")}</p>
            }
            {category === "access" &&
                <p className="h3 text-primary">{t("dashboard.invites")}</p>
            }
            {category === "banned" &&
                <p className="h3 text-primary">{t("dashboard.banned")}</p>
            }
            
            {/* If members length is greater than 0  */}
            
            <div className="overflow-auto">
                {category === "members" && membersList && 
                membersList.map((user: User) => 
                    <MemberCard user={user} key={user.id}/>
                )}
                {category === "banned" && membersList &&
                membersList.map((user: User) =>
                    <BlockedMemberCard user={user} key={user.id}/>
                )}
                {category === "access" && membersList &&
                membersList.map((user: User) =>
                    <AccessCard user={user} key={user.id}/>
                )}
                {category === "members" &&
                    <div className="d-flex justify-content-center mt-3">
                        <input className="btn btn-primary" type="submit" value={t("dashboard.invite")}/> {/* TODO: cablear botón! */}
                    </div>
                }
            </div>          

            {totalPages && <Pagination currentPage={currentPage} setCurrentPageCallback={setCurrentPage} totalPages={totalPages}/>}

            {/* If category is members and members length is 0 */}
            {category === "members" && membersList.length === 0 &&
                <div className="d-flex justify-content-center mt-3">
                    <input className="btn btn-primary" type="submit" value={t("dashboard.invite")}/>
                </div>
            }
            {/* If category is banned and banned length is 0 */}
            {category === "banned" && membersList.length === 0 &&
                <p className="h3 text-gray">{t("dashboard.nobanned")}</p>
            }
            {/* If category is access and access length is 0 */}
            {category === "access" && membersList.length === 0 &&
                <p className="h3 text-gray">{t("dashboard.noaccess")}</p>
            }
        </>
    )
}



const DashboardCommunitiesPane = (props: {selectedCommunity: Community}) => {
    const {t} = useTranslation();

    const [option, setOption] = React.useState("members");
    const [currentPage, setCurrentPage] = React.useState(1);

    function setOptionCallback(option: "members" | "access" | "banned") {
        setCurrentPage(1);
        setOption(option);
    }    


    return(
        <div className="white-pill mt-5">

            <div className="align-items-start d-flex justify-content-center my-3">
                        <p className="h1 text-primary bold"><strong>{t(props.selectedCommunity.name)}</strong></p>
            </div>
            <div className="text-gray text-center mt--4 mb-2">{props.selectedCommunity.description}</div>

            <hr/>

            <Tabs option={option} setOptionCallback={setOptionCallback}/>
            <div className="card-body">
                <MembersContent community={props.selectedCommunity} />
            </div>  
        </div>
        )
}

export default DashboardCommunitiesPane;