import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

import Background from "../components/Background";
import AskQuestionPane from "../components/AskQuestionPane";
import CommunitiesCard from "../components/CommunitiesCard";
import MainSearchPanel from "../components/TitleSearchCard";
import Tab from "../components/TabComponent";

import { t } from "i18next";

const communities = [
    "Historia","matematica","logica"
]


// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------



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

    function shouldFiltersShow() {
        if (tab === "questions") {
            return true
        }
        else {
            return false
        }
    }



    return (
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel showFilters={shouldFiltersShow()} title={tab}/>
                <div className="row">
                    <div className="col-3">
                        < CommunitiesCard title={t("landing.communities.message")} communities={communities} thisCommunity={"matematica"}/>
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
