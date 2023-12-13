import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import MainSearchPanel, { SearchProperties } from "../../components/MainSearchPanel";
import Tab from "../../components/TabComponent";

import { t } from "i18next";

import { User } from "../../models/UserTypes";
import UserPreviewCard from "../../components/UserPreviewCard";
import { searchUser } from "../../services/user";
import Spinner from "../../components/Spinner";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
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

  const [usersArray, setUsers] = React.useState<User[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  // Wrap changePage in useCallback to avoid infinite loop
  const changePage = useCallback((page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  }, [props]);


  useEffect(() => {
    setUsers(undefined);
    let params : any = { page: currentPage }
     if(props.searchProperties.query)
        params.query = props.searchProperties.query;
    searchUser(params).then((response) => {
      setUsers(response.list);
      setTotalPages(response.pagination.total);
    });
  }, [currentPage]);

  function doSearch(q: SearchProperties) {
    setUsers(undefined);
    searchUser({ query: q.query, page: 1 }).then((response) => {
      setUsers(response.list);
      setTotalPages(response.pagination.total);
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
                  isActive={false}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="communities"
                  isActive={false}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="users"
                  isActive={true}
                  updateTab={props.updateTab}
                />
              </ul>
            </div>

            {!usersArray && (
              // Show loading spinner
              <Spinner />
            )}

            {/* Loop through the items in questionsArray only if its not empty to display a card for each question*/}
            {usersArray &&
              usersArray.length > 0 &&
              usersArray.map((user) => (
                <Link to={`/user/${user.id}/profile`} key={user.id}>
                  <UserPreviewCard user={user} />
                </Link>
              ))}

            {usersArray && usersArray.length === 0 && (
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

const UserSearchPage = () => {
  const [tab, setTab] = React.useState("users");

  const navigate = useNavigate();
  const history = createBrowserHistory();

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  
  // Get specific query parameters
  let query = searchParams.get("query"); 
  let filter = undefined
  let order = undefined; 
  let communityPage = searchParams.get("communityPage")? searchParams.get("communityPage") : undefined;
  let page = searchParams.get("page")? searchParams.get("page") : undefined;
  
  let queries : SearchProperties = {
    query: query? query : undefined,
    filter: filter ? (isNaN(parseInt(filter)) ? undefined : parseInt(filter)) : undefined,
    order: order ? (isNaN(parseInt(order)) ? undefined : parseInt(order)) : undefined
  };
  //query param de page

  function updateTab(tabName: string) {
    setTab(tabName);
  }

  function setCommunityPage(pageNumber: number) {
    communityPage = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/users?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function setPage(pageNumber: number) {
    page = pageNumber.toString();
    const newCommunityPage = communityPage ? communityPage : 1;
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/users?page=${page}&communityPage=${newCommunityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function selectedCommunityCallback(id: number | string) {
    let url;
    const newCommunityPage = communityPage ? communityPage : 1;
    if (id === "all") {
      url = `/search/users?page=1&communityPage=${newCommunityPage}`;
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
       pathname: `${process.env.PUBLIC_URL}/search/users?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(q)}`
     })
   })
    searchFunctions.push(f);
  }

  return (
    <>
      <div className="section section-hero section-shaped">
        <Background />
        <MainSearchPanel
          showFilters={false}
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

export default UserSearchPage;
