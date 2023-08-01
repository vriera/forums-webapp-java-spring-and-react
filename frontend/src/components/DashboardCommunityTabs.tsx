import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { getUser } from "../services/user";
import { User } from "../models/UserTypes";
import { Button } from "react-bootstrap";

const DashboardCommunitiesTabs = (props: {
  activeTab: "admitted" | "invited" | "banned" | "requested";
  communityId: number;
  setActiveTab: (tab: "admitted" | "invited" | "banned" | "requested") => void;
  communityPage: number;
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
          <Button onClick={() => props.setActiveTab("admitted")} className="nav-link">
            {t("dashboard.members")}
          </Button>
        </li>

        <li className="nav-item">
          <Button onClick={() => props.setActiveTab("banned")} className="nav-link">
            {t("dashboard.banned")}
          </Button>
          
        </li>

        <li className="nav-item">
          <Button onClick={() => props.setActiveTab("invited")} className="nav-link">
            {t("dashboard.invited")}
          </Button>
        </li>

        <li className="nav-item">
          <Button onClick={() => props.setActiveTab("requested")} className="nav-link">
            {t("dashboard.requests")}
            {user?.notifications && user.notifications.requests > 0 && (
              <span className="badge badge-secondary bg-warning text-white ml-1">
                {user.notifications.requests}
              </span>
            )}
          </Button>
        </li>
      </ul>
    </div>
  );
};

export default DashboardCommunitiesTabs;
