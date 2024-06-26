import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import MainSearchPanel, {
  SearchProperties,
} from "../../components/MainSearchPanel";
import Tab from "../../components/TabComponent";
import { t } from "i18next";
import QuestionPreviewCard from "../../components/QuestionPreviewCard";
import { QuestionResponse } from "../../models/QuestionTypes";
import { searchQuestions } from "../../services/questions";
import Spinner from "../../components/Spinner";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";

import { useLocation, useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";
import { parseQueryParamsForHistory } from "../../services/utils";
// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------

const CenterPanel = (props: {
  activeTab: string;
  updateTab: any;
  currentPageCallback: (page: number) => void;
  setSearch: (f: any) => void;
  searchProperties: SearchProperties;
}) => {
  const { t } = useTranslation();
  const [questionsArray, setQuestions] = React.useState<QuestionResponse[]>();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  const changePage = (page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  };

  useEffect(() => {
    setQuestions(undefined);
    searchQuestions({ ...props.searchProperties ,page: currentPage }).then((response) => {
      setQuestions(response.list);
      setTotalPages(response.pagination.total);
    });
  }, [currentPage]);

  function doSearch(q: SearchProperties) {
    setQuestions(undefined);
    searchQuestions({
      query: q.query,
      order: q.order,
      filter: q.filter,
      page: 1,
    }).then((response) => {
      setQuestions(response.list);
      setTotalPages(response.pagination.total);
      changePage(1);
    });
  }
  props.setSearch(doSearch);
  return (
    <>
      <div className="col-6">
        <div className="white-pill mt-5">
          <div className="card-body">
            <div className="h2 text-primary">
              <ul className="nav nav-tabs">
                <Tab
                  tabName="questions"
                  isActive={true}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="communities"
                  isActive={false}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="users"
                  isActive={false}
                  updateTab={props.updateTab}
                />
              </ul>
            </div>

            {!questionsArray && <Spinner />}

            {/* Loop through the items in questionsArray only if its not empty to display a card for each question*/}
            {questionsArray &&
              questionsArray.length > 0 &&
              questionsArray.map((question) => (
                <QuestionPreviewCard question={question} key={question.id} />
              ))}

            {questionsArray && questionsArray.length === 0 && (
              <div>
                <p className="row h1 text-gray">{t("community.noResults")}</p>
                <div className="d-flex justify-content-center">
                  <img
                    className="row w-25 h-25"
                    src={require("../../images/empty.png")}
                    alt="No hay nada para mostrar"
                  />
                </div>
              </div>
            )}
          </div>
          <Pagination
            currentPage={currentPage}
            setCurrentPageCallback={changePage}
            totalPages={totalPages}
          />
        </div>
      </div>
    </>
  );
};

const QuestionSearchPage = () => {
  const navigate = useNavigate();
  const [tab, setTab] = React.useState("questions");
  const history = createBrowserHistory();
  //query param de page
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  // Get specific query parameters
  let query = searchParams.get("query"); 
  let filter = searchParams.get("filter"); 
  let order = searchParams.get("order"); 
  let communityPage = searchParams.get("communityPage")? searchParams.get("communityPage") : undefined;
  let page = searchParams.get("page")? searchParams.get("page") : undefined;
  
  let queries : SearchProperties = {
    query: query? query : undefined,
    filter: filter ? (isNaN(parseInt(filter)) ? undefined : parseInt(filter)) : undefined,
    order: order ? (isNaN(parseInt(order)) ? undefined : parseInt(order)) : undefined
  };
  //get inform
  //query param de community page

  function updateTab(tabName: string) {
    setTab(tabName);
  }

  function setCommunityPage(pageNumber: number) {
    communityPage = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function setPage(pageNumber: number) {
    page = pageNumber.toString();
    const newCommunityPage = communityPage ? communityPage : 1;
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${newCommunityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function selectedCommunityCallback(id: number | string) {
    let url;
    const newCommunityPage = communityPage ? communityPage : 1;
    if (id === "all") {
      url = `/search/questions?page=1&communityPage=${newCommunityPage}`;
    } else {
      url = `/community/${id}?page=1&communityPage=${newCommunityPage}`;
    }
    navigate(url);
  }

  let searchFunctions: ((q: SearchProperties) => void)[] = [
    (q: SearchProperties) => console.log(q),
  ];

  let doSearch: (q: SearchProperties) => void = (q: SearchProperties) => {
    searchFunctions.forEach((x) => x(q));
  };

  function setSearch(f: (q: SearchProperties) => void) {
    searchFunctions = [];
    searchFunctions.push(
      (q:SearchProperties) =>  {
       queries = q
       history.push({
       pathname: `${process.env.PUBLIC_URL}/search/questions?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(q)}`
     })
   })
    searchFunctions.push(f);
  }


   

  return (
    <>
      <div className="section section-hero section-shaped">
        <Background />
        <MainSearchPanel
          showFilters={true}
          title={t("askAway")}
          subtitle={tab}
          doSearch={doSearch}
          searchProperties={queries}
    
        />
        <div className="row">
          <div className="col-3">
            <CommunitiesLeftPane
              selectedCommunity={0}
              selectedCommunityCallback={selectedCommunityCallback}
              currentPageCallback={setCommunityPage}
              
            />
          </div>

          <CenterPanel
            activeTab={tab}
            updateTab={updateTab}
            currentPageCallback={setPage}
            setSearch={setSearch}
            searchProperties={queries}
          />

          <div className="col-3">
            <AskQuestionPane />
          </div>
        </div>
      </div>
    </>
  );
};

export default QuestionSearchPage;
