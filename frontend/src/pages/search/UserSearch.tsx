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

import { User } from "../../models/UserTypes";
import UserPreviewCard from "../../components/UserPreviewCard";
import { searchUser } from "../../services/user";


const communities = [
    "Historia","matematica","logica"
]


// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------




const CenterPanel = (props: {activeTab: string, updateTab: any}) => { 
    const { t } = useTranslation();

    const [usersArray, setUsers] = React.useState<User[]>([]);

    useEffect( () => {
        searchUser({}).then(
            (response) => {
                    setUsers(response);
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
                                <Tab tabName="communities" isActive={false} updateTab={props.updateTab}/>
                                <Tab tabName="users" isActive={true} updateTab={props.updateTab}/>
                            </ul>
                        </div>

                       
                        {/* TODO: this  statements of array length should be adapted to use whatever the service brings back*/}
                        {/* Loop through the items in questionsArray only if its not empty to display a card for each question*/}
                        {usersArray.length > 0 && usersArray.map((user) => (
                            <UserPreviewCard user={user}/>
                        ))}

                        {usersArray.length==0 && (
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



const UserSearchPage = () => {
    const [tab, setTab] = React.useState("users")

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

export default UserSearchPage;
