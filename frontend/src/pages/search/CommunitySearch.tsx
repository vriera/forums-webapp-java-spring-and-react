import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import MainSearchPanel from "../../components/TitleSearchCard";
import { SearchPropieties } from "../../components/TitleSearchCard";
import Tab from "../../components/TabComponent";

import { t } from "i18next";

import { CommunityResponse } from "../../models/CommunityTypes";
import CommunityPreviewCard from "../../components/CommunityPreviewCard";
import { searchCommunity } from "../../services/community";
import Spinner from "../../components/Spinner";
import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
import Pagination from "../../components/Pagination";

// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------

const CenterPanel = (props: {
  activeTab: string;
  updateTab: any;
  setSearch: (f: any) => void;
  currentPageCallback: (page: number) => void;
}) => {
  const { t } = useTranslation();

  const [communitiesArray, setCommunities] = React.useState<CommunityResponse[]>();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  const changePage = (page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  };

  useEffect(() => {
    setCommunities(undefined);
    searchCommunity({ page: currentPage }).then((response) => {
      setCommunities(response.list);
      setTotalPages(response.pagination.total);
    });
  }, [currentPage]);

  function doSearch(q: SearchPropieties) {
    setCommunities(undefined);
    searchCommunity({ query: q.query, page: 1 }).then((response) => {
      setCommunities(response.list);
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
                  isActive={false}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="communities"
                  isActive={true}
                  updateTab={props.updateTab}
                />
                <Tab
                  tabName="users"
                  isActive={false}
                  updateTab={props.updateTab}
                />
              </ul>
            </div>

            {!communitiesArray && <Spinner />}

            {/* Loop through the items in communitiesArray only if its not empty to display a card for each question*/}
            {communitiesArray &&
              communitiesArray.length > 0 &&
              communitiesArray.map((community) => (
                <CommunityPreviewCard community={community} />
              ))}

            {/* no elements to show */}
            {communitiesArray && communitiesArray.length === 0 && (
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

const CommunitySearchPage = () => {
  const [tab, setTab] = React.useState("Communities");

  const navigate = useNavigate();

  const history = createBrowserHistory();
  //query param de page
  let { communityPage, page } = useParams();

  function updateTab(tabName: string) {
    setTab(tabName);
  }

  function setCommunityPage(pageNumber: number) {
    communityPage = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/communities?page=${page}&communityPage=${communityPage}`,
    });
  }

  function setPage(pageNumber: number) {
    const newCommunityPage = communityPage ? communityPage : 1;
    page = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/communities?page=${page}&communityPage=${newCommunityPage}`,
    });
  }

  function selectedCommunityCallback(id: number | string) {
    let url;
    const newCommunityPage = communityPage ? communityPage : 1;
    if (id === "all") {
      url = `/search/communities?page=1&communityPage=${newCommunityPage}`;
    } else {
      url = `/community/${id}?page=1&communityPage=${newCommunityPage}`;
    }
    navigate(url);
  }

  // patron de subscripcion

  let searchFunctions: ((q: SearchPropieties) => void)[] = [
    (q: SearchPropieties) => console.log(q),
  ];

  let doSearch: (q: SearchPropieties) => void = (q: SearchPropieties) => {
    searchFunctions.forEach((x) => x(q));
  };

  function setSearch(f: (q: SearchPropieties) => void) {
    searchFunctions = [];
    searchFunctions.push(f);
  }

  return (
    <>
      <div className="section section-hero section-shaped">
        <Background />
        <MainSearchPanel
          doSearch={doSearch}
          showFilters={false}
          title={t("askAway")}
          subtitle={tab}
        />
        <div className="row">
          <div className="col-3">
            <CommunitiesLeftPane
              selectedCommunity={undefined}
              selectedCommunityCallback={selectedCommunityCallback}
              currentPageCallback={setCommunityPage}
            />
          </div>

          <CenterPanel
            activeTab={tab}
            updateTab={updateTab}
            setSearch={setSearch}
            currentPageCallback={setPage}
          />

          <div className="col-3">
            <AskQuestionPane />
          </div>
        </div>
      </div>
    </>
  );
};

export default CommunitySearchPage;
