import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";

const UserTabs = (props: { activeTab: "profile" | "communities" }) => {
  const { t } = useTranslation();
  let { userId } = useParams();

  return (
    <div>
      <ul className="nav nav-tabs">
        <li className="nav-item">
          <Link
            to={"/user/" + userId + "/profile"}
            className={
              "nav-link " + (props.activeTab === "profile" && "active")
            }
          >
            {t("user.profile")}
          </Link>
        </li>

        <li className="nav-item">
          <Link
            to={"/user/" + userId + "/communities"}
            className={
              "nav-link " + (props.activeTab === "communities" && "active")
            }
          >
            {t("user.communities")}
          </Link>
        </li>
      </ul>
    </div>
  );
};

export default UserTabs;
