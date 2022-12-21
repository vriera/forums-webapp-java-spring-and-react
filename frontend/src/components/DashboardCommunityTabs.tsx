import { cp } from "fs";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import { useQuery } from "./UseQuery";


const DashboardCommunitiesTabs = (props: { activeTab: "admitted" | "invited" | "banned", communityId: number , communityPage : number }) => {
    
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
                    <Link to={`/dashboard/communities/`+props.communityId+`/invited?communityPage=${props.communityPage}}`} className={"nav-link " + (props.activeTab === "invited" && "active")} >
                        {t("dashboard.invited")}
                    </Link>
                </li>              
            </ul>
        </div>
    )
}

export default DashboardCommunitiesTabs;