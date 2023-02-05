import React from "react";
import { useTranslation } from "react-i18next";
import "../resources/styles/argon-design-system.css";
import "../resources/styles/blk-design-system.css";
import "../resources/styles/general.css";
import "../resources/styles/stepper.css";

import { Link } from "react-router-dom";

const Tab = (props: { tabName: string; isActive: boolean; updateTab: any }) => {
  const { t } = useTranslation();
  if (props.isActive) {
    return (
      <li className="nav-item">
        {/* should redirect to /search/tabName */}
        <Link
          className="nav-link active"
          to={"/search/" + props.tabName}
          onClick={() => props.updateTab(props.tabName)}
        >
          {t(props.tabName)}
        </Link>
      </li>
    );
  } else {
    return (
      <li className="nav-item">
        <Link
          className="nav-link"
          to={"/search/" + props.tabName}
          onClick={() => props.updateTab(props.tabName)}
        >
          {t(props.tabName)}
        </Link>
      </li>
    );
  }
};

export default Tab;
