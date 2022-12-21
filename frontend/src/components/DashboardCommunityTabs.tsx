import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { getUserFromApi } from "../services/user";
import { User } from "../models/UserTypes";


const DashboardCommunitiesTabs = (props: { activeTab: "admitted" | "invited" | "banned" | "requested", communityId: number , communityPage : number }) => {

    const navigate = useNavigate();
    const [user, setUser] = useState<User>(null as unknown as User);

    useEffect(() => {
        async function fetchUser() {
            const userId = parseInt(window.localStorage.getItem("userId") as string);
            
            try{
                let auxUser = await getUserFromApi(userId)
                setUser(auxUser)
            }catch(error){
                navigate("/500");
            }
        }
        fetchUser();
    }, [navigate])
    
    const {t} = useTranslation();
    return(
        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <Link to={`/dashboard/communities/` + props.communityId + `/admitted?communityPage=${props.communityPage}`} className={"nav-link " + (props.activeTab === "admitted" && "active")}>
                        {t("dashboard.members")}
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to={`/dashboard/communities/`+props.communityId+`/banned?communityPage=${props.communityPage}`} className={"nav-link " + (props.activeTab === "banned" && "active")} >
                        {t("dashboard.banned")}
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to={`/dashboard/communities/`+props.communityId+`/invited?communityPage=${props.communityPage}`} className={"nav-link " + (props.activeTab === "invited" && "active")} >
                        {t("dashboard.invited")}
                    </Link>
                </li> 

                <li className="nav-item">
                    <Link to={`/dashboard/communities/`+props.communityId+`/requested?communityPage=${props.communityPage}`} className={"nav-link " + (props.activeTab === "requested" && "active")} >
                        {t("dashboard.requests")}
                        {user && user.notifications && user.notifications.requests > 0  &&
                        <span className="badge badge-secondary bg-warning text-white ml-1">{user.notifications.requests}</span>
                        }
                    </Link>
                </li>                
            </ul>
        </div>
    )
}

export default DashboardCommunitiesTabs;