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

const communities = [
    "Historia","matematica","logica"
]



// --------------------------------------------------------------------------------------------------------------------
// COMPONENTS FOR TOP CARD REGARDING SEARCH
// --------------------------------------------------------------------------------------------------------------------
const QuestionSearchConditionals = (props: {isQuestionSearch: boolean}) => {
    const { t } = useTranslation();
    if (props.isQuestionSearch) {
        return (
            <div className="container mt-3">
                        <div className="row">
                            <div className="col">
                                <select className="form-control" name="filter" aria-label={t("filter.name")} id="filterSelect">
                                    <option selected value="0">{t("filter.noFilter")}</option>
                                    <option value="1">{t("filter.hasAnswers")}</option>
                                    <option value="2">{t("filter.noAnswers")}</option>
                                    <option value="3">{t("filter.verifiedAnswers")}</option>
                                </select>
                            </div>
                            <div className="col">
                                <select className="form-control" name="order" aria-label={t("order")} id="orderSelect">
                                    <option selected value="0">{t("order.mostRecent")}</option>
                                    <option value="1">{t("order.leastRecent")}</option>
                                    <option value="2">{t("order.closestMatch")}</option>
                                    <option value="3">{t("order.positiveQuestionVotes")}</option>
                                    <option value="4">{t("order.positiveAnswerVotes")}</option>
                                </select>
                            </div>
                        </div>
                    </div>
        )
    }
    else {
        return (
            <>
            </>
        )
    }
}

const MainSearchPanel = (props: {isQuestionSearch: boolean, searchType: string}) => {
    const { t } = useTranslation();


    return (
        <>
            <div className="col-6 center">
                <div className="white-pill h-75 ">
                    <div className="align-items-start d-flex justify-content-center my-3">
                        <p className="h1 text-primary bold"><strong>{t("askAway")}</strong></p>
                    </div>
                    <div className="text-gray text-center mt--4 mb-2">{t(props.searchType)}</div>
                
                    <div className="form-group mx-5">
                        <div className="input-group">
                                <input className="form-control rounded" type="search" name="query" id="query" placeholder={t("placeholder.searchQuestion")}/>
                                <input className="btn btn-primary" type="submit" value={t("search")}/>
                        </div>
                        <QuestionSearchConditionals isQuestionSearch={props.isQuestionSearch}/>
                    </div>

                </div>
            </div>
        </>
    )

}



// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------

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

const CenterPanel = (props: {activeTab: string, updateTab: any}) => { 
    const { t } = useTranslation();
    return (
        <>
            <div className="col-6">
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <div className="h2 text-primary">
                            <ul className="nav nav-tabs">
                                <Tab tabName="questions" activeTab={props.activeTab} updateTab={props.updateTab}/>
                                <Tab tabName="communities" activeTab={props.activeTab} updateTab={props.updateTab}/>
                                <Tab tabName="users" activeTab={props.activeTab} updateTab={props.updateTab}/>
                            </ul>
                        </div>

                        <p className="row h1 text-gray">{t("community.noResults")}</p>
                        <div className="d-flex justify-content-center">
                            <img className="row w-25 h-25" src="/resources/images/empty.png" alt="No hay nada para mostrar"/>
                        </div>
                
                    </div>
                
                </div>
            </div>
            
        </>

    )
}



const SearchPage = () => {
    const [tab, setTab] = React.useState("questions")

    function updateTab(tabName: string) {
        setTab(tabName)
    }



    return (
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel isQuestionSearch={true} searchType="communities"/>
                <div className="row">
                    <div className="col-3">
                        < CommunitiesCard communities={communities} thisCommunity={"matematica"}/>
                    </div>  

                    <CenterPanel activeTab={tab} updateTab={updateTab}/>

                    <div className="col-3">
                        <AskQuestionPane/>
                    </div>
                </div>
            </div>
        </>
    );


}

export default SearchPage;
