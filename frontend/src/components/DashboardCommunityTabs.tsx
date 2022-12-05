import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const DashboardCommunitiesTabs = (props: { activeTab: string, communityId: number }) => {
    const {t} = useTranslation();

    return(
        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <Link to={"/dashboard/communities/" + props.communityId + "admitted"} className={"nav-link " + (props.activeTab === "admitted" && "active")}>
                        {t("dashboard.members")}
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to={"/dashboard/communities/"+props.communityId+"/invited"} className={"nav-link " + (props.activeTab === "invited" && "active")} >
                        {t("dashboard.invites")}
                    </Link>
                </li>

                <li className="nav-item">
                    <Link to={"/dashboard/communities/"+props.communityId+"/banned"} className={"nav-link " + (props.activeTab === "banned" && "active")} >
                        {t("dashboard.banned")}
                    </Link>
                </li>
                              
                
            </ul>
        </div>
    )
}

export default DashboardCommunitiesTabs;