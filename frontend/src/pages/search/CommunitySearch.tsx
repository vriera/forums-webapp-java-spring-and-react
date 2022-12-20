import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import MainSearchPanel from "../../components/TitleSearchCard";
import Tab from "../../components/TabComponent";

import { t } from "i18next";

import { CommunityCard } from "../../models/CommunityTypes";
import CommunityPreviewCard from "../../components/CommunityPreviewCard";
import { searchCommunity } from "../../services/community";
import Spinner from "../../components/Spinner";

const communities = [
    "Historia","matematica","logica"
]


// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------






const CenterPanel = (props: {activeTab: string, updateTab: any}) => { 
    const { t } = useTranslation();

    const [communitiesArray, setCommunities] = React.useState<CommunityCard[]>();
    console.log("Estamos en communitySearch");

    useEffect( () => {
        searchCommunity({}).then(
            (response) => {
                    setCommunities(response.list);
            }
        )
    }, [])

    return (
        <>
            <div className="col-6">
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <div className="h2 text-primary">
                            <ul className="nav nav-tabs">
                                <Tab tabName="questions" isActive={false} updateTab={props.updateTab}/>
                                <Tab tabName="communities" isActive={true} updateTab={props.updateTab}/>
                                <Tab tabName="users" isActive={false} updateTab={props.updateTab}/>
                            </ul>
                        </div>

                       {!communitiesArray &&
                            <Spinner/>
                       }

                        {/* Loop through the items in communitiesArray only if its not empty to display a card for each question*/}
                        {communitiesArray && communitiesArray.length > 0 && communitiesArray.map((community) => (
                            <CommunityPreviewCard community={community}/>
                        ))}

                        {/* no elements to show */}
                        {communitiesArray && communitiesArray.length==0 && (
                            <div>
                                <p className="row h1 text-gray">{t("community.noResults")}</p>
                                <div className="d-flex justify-content-center">
                                    <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                                </div>
                            </div>
                        )}
                
                    </div>
                
                </div>
            </div>
            
        </>

    )
}



const CommunitySearchPage = () => {
    const [tab, setTab] = React.useState("Communities");

    function updateTab(tabName: string) {
        setTab(tabName)
    }




    return (
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel showFilters={false} title={t("askAway")} subtitle={tab}/>
                <div className="row">
                    <div className="col-3">
                        {/* < CommunitiesCard communities={[]} selectedCommunity={null}/> */}
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

export default CommunitySearchPage;
