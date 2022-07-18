import React from "react";
import { useTranslation } from "react-i18next";
import { t } from "i18next";
import Pagination from "./Pagination";



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

const MemberCard = (props: {member: string}) => {
    
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.member}</p>
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

const BlockedMemberCard = (props: {member: string}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.member}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-unlock"></i>
                    </div>
                </button>
            </div>
        </div>
    )
}

const AccessCard = (props: {member: string}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.member}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-times-circle"></i>
                    </div>
                </button>
            </div>
        </div>
    )
}


const MembersContent = (props: {members: string[], banned: string[], access: string[], category: string, currentPage: number, totalPages:number, setPage: any}) => {
    const {t} = useTranslation();
    return (
        <>
            {/* Different titles according to the corresponding tab */}
            {props.category == "members" &&
                <p className="h3 text-primary">{t("dashboard.members")}</p>
            }
            {props.category == "access" &&
                <p className="h3 text-primary">{t("dashboard.invites")}</p>
            }
            {props.category == "banned" &&
                <p className="h3 text-primary">{t("dashboard.banned")}</p>
            }
            
            {/* If members length is greater than 0  */}
            {props.members.length > 0 && (
                <div className="overflow-auto">
                    {props.category =="members" && props.members.map((member: string) => 
                        <MemberCard member={member} key={member}/>
                    )}
                    {props.category =="banned"&& props.banned.map((member: string) =>
                        <BlockedMemberCard member={member} key={member}/>
                    )}
                    {props.category =="access"&& props.access.map((member: string) =>
                        <AccessCard member={member} key={member}/>
                    )}


                    <div className="mt-3">
                        <Pagination totalPages={props.totalPages} currentPage={props.currentPage} setCurrentPageCallback={props.setPage}/>
                    </div>

                    {props.category== "members" &&
                        <div className="d-flex justify-content-center mt-3">
                            <input className="btn btn-primary" type="submit" value={t("dashboard.invite")}/>
                        </div>
                    }
                    
                </div>     
            )}


            {/* If category is members and members length is 0 */}
            {props.category == "members" && props.members.length == 0 &&
                <div className="d-flex justify-content-center mt-3">
                    <input className="btn btn-primary" type="submit" value={t("dashboard.invite")}/>
                </div>
            }
            {/* If category is banned and banned length is 0 */}
            {props.category == "banned" && props.banned.length == 0 &&
                <p className="h3 text-gray">{t("dashboard.nobanned")}</p>
            }
            {/* If category is access and access length is 0 */}
            {props.category == "access" && props.access.length == 0 &&
                <p className="h3 text-gray">{t("dashboard.noaccess")}</p>
            }
        </>
    )

}



const DashboardCommunitiesPane = (props: {communityName: string, communityDescription: string}) => {
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
                        <p className="h1 text-primary bold"><strong>{t(props.communityName)}</strong></p>
            </div>
            <div className="text-gray text-center mt--4 mb-2">{props.communityDescription}</div>

            <hr/>

            <Tabs option={option} setOptionCallback={setOptionCallback}/>
            <div className="card-body">
                <MembersContent members={["Juan", "Pablo", "María"]} banned={["José"]} access={["Anita"]} category={option} currentPage={currentPage} totalPages={5} setPage={setCurrentPage}/>
            </div>  
        </div>
        )
}

export default DashboardCommunitiesPane;