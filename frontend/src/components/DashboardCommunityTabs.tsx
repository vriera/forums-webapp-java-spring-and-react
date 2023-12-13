import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { getUser } from "../services/user";
import { User } from "../models/UserTypes";
const DashboardCommunitiesTabs = (props: {
  activeTab: "admitted" | "invited" | "banned" | "requested";
  communityId: number;
  setActiveTab: (tab: "admitted" | "invited" | "banned" | "requested") => void;

}) => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User>(null as unknown as User);

  useEffect(() => {
    async function fetchUser() {
      const userId = parseInt(window.localStorage.getItem("userId") as string);

      try {
        let auxUser = await getUser(userId);
        setUser(auxUser);
      } catch (error) {
        navigate("/500");
      }
    }
    fetchUser();
  }, [navigate]);

  const { t } = useTranslation();
  return (
    <div>
      <ul className="nav nav-tabs">
        <li className="nav-item">
          <a onClick={() => props.setActiveTab("admitted")} className={`nav-link ${props.activeTab === "admitted"? " active" :"" }`}>
            {t("dashboard.members")}
          </a>
        </li>

        <li className="nav-item">
          <a onClick={() => props.setActiveTab("banned")} className={`nav-link ${props.activeTab ==="banned"? "active" :"" }`}>
            {t("dashboard.banned")}
          </a>
          
        </li>

        <li className="nav-item">
          <a onClick={() => props.setActiveTab("invited")} className={`nav-link ${props.activeTab ==="invited"? "active" :"" }`}>
            {t("dashboard.invited")}
          </a>
        </li>

        <li className="nav-item">
          <a onClick={() => props.setActiveTab("requested")} className={`nav-link ${props.activeTab ==="requested"? "active" :"" }`}>
            {t("dashboard.requests")}
          </a>
        </li>
      </ul>
    </div>
  );
};

export default DashboardCommunitiesTabs;
