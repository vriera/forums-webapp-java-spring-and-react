import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";
import AskQuestionPane from "../../components/AskQuestionPane";
import MainSearchPanel from "../../components/TitleSearchCard";
import Tab from "../../components/TabComponent";

import { t } from "i18next";

import { User } from "../../models/UserTypes";
import UserPreviewCard from "../../components/UserPreviewCard";
import { searchUser } from "../../services/user";
import Spinner from "../../components/Spinner";
import { Link, useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";
import Pagination from "../../components/Pagination";
import { SearchProperties } from "../../components/TitleSearchCard";

// --------------------------------------------------------------------------------------------------------------------
//COMPONENTS FOR BOTTOM PART, THREE PANES
// --------------------------------------------------------------------------------------------------------------------

const CenterPanel = (props: {
  activeTab: string;
  updateTab: any;
  currentPageCallback: (page: number) => void;
  setSearch: (f: any) => void;
}) => {
  const { t } = useTranslation();

  const [usersArray, setUsers] = React.useState<User[]>();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  const changePage = (page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  };

  useEffect(() => {
    setUsers(undefined);
    searchUser({ page: currentPage }).then((response) => {
      setUsers(response.list);
      setTotalPages(response.pagination.total);
    });
  }, [currentPage]);

  function doSearch(q: SearchProperties) {
    setUsers(undefined);
    searchUser({ query: q.query, page: 1 }).then((response) => {
      setUsers(response.list);
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
                <Link to={`/user/${user.id}/profile`}>
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
  //query param de page
  let { communityPage, page } = useParams();

  function updateTab(tabName: string) {
    setTab(tabName);
  }

  function setCommunityPage(pageNumber: number) {
    communityPage = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/users?page=${page}&communityPage=${communityPage}`,
    });
  }

  function setPage(pageNumber: number) {
    page = pageNumber.toString();
    const newCommunityPage = communityPage ? communityPage : 1;
    history.push({
      pathname: `${process.env.PUBLIC_URL}/search/users?page=${page}&communityPage=${newCommunityPage}`,
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
            currentPageCallback={setPage}
            setSearch={setSearch}
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
