import React, { useEffect, useState } from "react";
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
import { SearchPropieties } from "../../components/TitleSearchCard";
import { t } from "i18next";
import QuestionPreview from "../../components/QuestionCard";
import QuestionPreviewCard from "../../components/QuestionPreviewCard";
import { QuestionCard } from "../../models/QuestionTypes";
import { searchQuestions } from "../../services/questions";
import Spinner from "../../components/Spinner";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";

import { useNavigate, useParams } from 'react-router-dom';
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";
const communities = [
    "Historia","matematica","logica"
]


// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------




const CenterPanel = (props: {activeTab: string, updateTab: any, currentPageCallback: (page: number) => void , setSearch : ( f : any) => void}) => { 
    const { t } = useTranslation();
    const [questionsArray, setQuestions] = React.useState<QuestionCard[]>();

    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(-1);

    const changePage =(page:number) => {
        setCurrentPage(page);
        props.currentPageCallback(page);
    }

    useEffect( () => {
        setQuestions(undefined);
        searchQuestions({page: currentPage}).then(
            (response) => {
                    setQuestions(response.list);
                    setTotalPages(response.pagination.total)
            }
        )
    }, [currentPage])

    
    function doSearch( q : SearchPropieties ){
        setQuestions(undefined);
        searchQuestions({query: q.query , order: q.order, filter: q.filter, page :1}).then(
             (response) => {
                setQuestions(response.list)
                setTotalPages(response.pagination.total);
                changePage(1);
             }
        )
    }
    props.setSearch(doSearch);
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
                    <Pagination currentPage={currentPage} setCurrentPageCallback={changePage} totalPages={totalPages}/>
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

    function setPage(pageNumber: number){
        page = pageNumber.toString();
        const newCommunityPage = communityPage? communityPage : 1;
        history.push({pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${newCommunityPage}`})

    }

    function selectedCommunityCallback( id : number | string){
        let url
        const newCommunityPage = communityPage? communityPage : 1;
        if(id == "all"){
            url = "/search/questions"+ `?page=1&communityPage=${newCommunityPage}`;
        }
        else{
            url = "/community/" + id + `?page=1&communityPage=${newCommunityPage}`;
        }
        navigate(url);
    }

    

    let searchFunctions : ((q : SearchPropieties) => void)[] = [ (q : SearchPropieties) => console.log(q) ];

    let doSearch : (q : SearchPropieties) => void = ( q : SearchPropieties) => {
        searchFunctions.forEach(x => x(q));
    };
    
    function setSearch( f : (q : SearchPropieties) => void){
        searchFunctions = [];
        searchFunctions.push(f);
    }


    return (
        <>
            <div className="section section-hero section-shaped">
                <Background/>
                <MainSearchPanel showFilters={true} title={t("askAway")} subtitle={tab} doSearch={doSearch}/>
                <div className="row">
                    <div className="col-3">
                     < CommunitiesLeftPane selectedCommunity={undefined} selectedCommunityCallback={selectedCommunityCallback} currentPageCallback={setCommunityPage}/>
                    </div>  

                   <CenterPanel activeTab={tab} updateTab={updateTab} currentPageCallback={setPage} setSearch={setSearch}/>

                    <div className="col-3">
                        <AskQuestionPane/>
                    </div>
                </div>
            </div>
        </>
    );


}

export default QuestionSearchPage;