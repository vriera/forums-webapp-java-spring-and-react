import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

import Background from "../components/Background";
import AskQuestionPane from "../components/AskQuestionPane";
import CommunitiesCard from "../components/CommunitiesCard";

import { t } from "i18next";

const Tab = (props: {tabName: string, activeTab: string, updateTab: any}) => {
    const { t } = useTranslation();
    if (props.tabName === props.activeTab) {
        return (
            <li className="nav-item">
                <a className="nav-link active" onClick={() => props.updateTab(props.tabName)}>{t(props.tabName)}</a>
            </li>
        )
    }
    else {
        return (
            <li className="nav-item">
                <a className="nav-link" onClick={() => props.updateTab(props.tabName)}>{t(props.tabName)}</a>
            </li>
        )
    }
}

export default Tab