import { useTranslation } from "react-i18next";
import { Notification } from "../models/UserTypes";
import { Link } from "react-router-dom";

const DashboardAccessTabs = (props: {notifications: Notification, activeTab: "admitted" | "invited" | "requested" | "rejected"}) => {
    const {t} = useTranslation();

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
                        {props.notifications.invites > 0  &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.invites}</span>
                        }
                    </Link>
                </li>
                
                <li className="nav-item">
                    <Link to="/dashboard/access/requested" className={"nav-link " + (props.activeTab === "requested" && "active")}>
                        {t("dashboard.requests")}
                        {props.notifications.requests > 0  &&
                        <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.requests}</span>
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