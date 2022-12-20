import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import '../../resources/styles/argon-design-system.css';
import '../../resources/styles/blk-design-system.css';
import '../../resources/styles/general.css';
import '../../resources/styles/stepper.css';

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import CommunitiesCard from "../../components/CommunitiesCard";
import MainSearchPanel from "../../components/TitleSearchCard";
import Tab from "../../components/TabComponent";

import { t } from "i18next";
import QuestionPreview from "../../components/QuestionCard";
import QuestionPreviewCard from "../../components/QuestionPreviewCard";
import { QuestionCard } from "../../models/QuestionTypes";
import { searchQuestions } from "../../services/questions";
import Spinner from "../../components/Spinner";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";

import { useNavigate, useParams } from 'react-router-dom';
import { createBrowserHistory } from "history";
const communities = [
    "Historia","matematica","logica"
]


// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------




const CenterPanel = (props: {activeTab: string, updateTab: any}) => { 
    const { t } = useTranslation();
    const [questionsArray, setQuestions] = React.useState<QuestionCard[]>();


    useEffect( () => {
        searchQuestions({}).then(
            (response) => {
                    setQuestions(response.list);
                    console.log(questionsArray);
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
                                <Tab tabName="questions" isActive={true} updateTab={props.updateTab}/>
                                <Tab tabName="communities" isActive={false} updateTab={props.updateTab}/>
                                <Tab tabName="users" isActive={false} updateTab={props.updateTab}/>
                            </ul>
                        </div>

                       {!questionsArray &&
                        <Spinner/>

                       }
                       
                        {/* Loop through the items in questionsArray only if its not empty to display a card for each question*/}
                        {questionsArray && questionsArray.length > 0 && questionsArray.map((question) => (
                            <QuestionPreviewCard question={question}/>
                        ))}

                        {questionsArray && questionsArray.length==0 && (
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



const QuestionSearchPage = () => {
    
    const navigate = useNavigate();
    const [tab, setTab] = React.useState("questions");
    const history = createBrowserHistory();
    //query param de page 
    let { communityPage , page } = useParams();

    //query param de community page

    function updateTab(tabName: string) {
        setTab(tabName)
    }

    function setCommunityPage(pageNumber: number){
        communityPage = pageNumber.toString();
        history.push({pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${communityPage}`})
    }

    function selectedCommunityCallback( id : number | string){
        let url
        const newCommunityPage = communityPage? communityPage : 1;
        if(id == "all"){
            url = "/search/questions"+ `?page=1&communityPage=${newCommunityPage}`;
        }
        else{
            url = "/community/view/" + id + `?page=1&communityPage=${newCommunityPage}`;
        }
        navigate(url);
    }

    return (
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel showFilters={true} title={t("askAway")} subtitle={tab}/>
                <div className="row">
                    <div className="col-3">
                     < CommunitiesLeftPane selectedCommunity={undefined} selectedCommunityCallback={selectedCommunityCallback} currentPageCallback={setCommunityPage}/>
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

export default QuestionSearchPage;