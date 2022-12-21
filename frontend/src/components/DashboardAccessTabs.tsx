import { useTranslation } from "react-i18next";
import { User } from "../models/UserTypes";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getUserFromApi } from "../services/user";

const DashboardAccessTabs = (props: { activeTab: "admitted" | "invited" | "requested" | "rejected"}) => {
    const {t} = useTranslation();
    
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

    return(
        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <Link to= "/dashboard/access/admitted" className={"nav-link " + (props.activeTab === "admitted" && "active")}>
                        {t("dashboard.admitted")}
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to="/dashboard/access/invited" className={"nav-link " + (props.activeTab === "invited" && "active")} >
                        {t("dashboard.invites")}
                        {user && user.notifications && user.notifications.invites > 0  &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{user.notifications.invites}</span>
                        }
                    </Link>
                </li>
                
                <li className="nav-item">
                    <Link to="/dashboard/access/requested" className={"nav-link " + (props.activeTab === "requested" && "active")}>
                        {t("dashboard.requests")}
                        {user && user.notifications && user.notifications.requests > 0  &&
                        <span className="badge badge-secondary bg-warning text-white ml-1">{user.notifications.requests}</span>
                        }
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to="/dashboard/access/rejected" className={"nav-link " + (props.activeTab === "rejected" && "active")} >
                        {t("dashboard.rejections")}
                    </Link>
                </li>               
                
            </ul>
        </div>
    )
}

export default DashboardAccessTabs;