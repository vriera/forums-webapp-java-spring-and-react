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
import { t } from "i18next";
import QuestionPreviewCard from "../../components/QuestionPreviewCard";
import { QuestionResponse } from "../../models/QuestionTypes";
import { searchQuestions } from "../../services/questions";
import Spinner from "../../components/Spinner";
import CommunitiesLeftPane from "../../components/CommunitiesLeftPane";

import { useLocation, useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Pagination from "../../components/Pagination";
import { Community } from "../../models/CommunityTypes";
import {
  getCommunity,
  canAccess,
  setAccessType,
  SetAccessTypeParams,
  hasRequestedAccess,
} from "../../services/community";
import { AccessType } from "../../services/access";
import { set } from "date-fns";
import { access } from "fs/promises";
import { parseQueryParamsForHistory} from "../../services/utils"
const  CenterPanel = (props: {
  currentPageCallback: (page: number) => void;
  setSearch: (f: any) => void;
  searchProperties:SearchProperties;
}) => {
  const { t } = useTranslation();
  const [questionsArray, setQuestionsArray] = useState<QuestionResponse[]>();
  
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);
  const [allowed, setAllowed] = useState(true);
  const [requestingAccess, setRequestingAccess] = useState(false);
  const [accessRequested, setAccessRequested] = useState(false);

  const { communityId } = useParams();
  
  const navigate = useNavigate();

  const userId = window.localStorage.getItem("userId")
    ? parseInt(window.localStorage.getItem("userId") as string)
    : -1;

  const changePage = (page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  };

  useEffect(() => {
    async function fetchQuestions() {
      try {
        let auxAllowed = await canAccess(
          parseInt(communityId as string),
          userId
        );
        setAllowed(auxAllowed);
      } catch (error: any) {
        navigate(`/${error.status}`);
      }

      if (allowed) {
        setQuestionsArray(undefined);
        searchQuestions({
         ...props.searchProperties,
          page: currentPage,
          communityId: parseInt(communityId as string),
          userId: userId,
        }).then((response) => {
          setQuestionsArray(response.list);
          setTotalPages(response.pagination.total);
        });
      } else {
        setQuestionsArray([]);
        try { 
          let auxAccessRequested = await hasRequestedAccess(
            userId,
            parseInt(communityId as string)
          );
          setAccessRequested(auxAccessRequested);
        } catch {
          setAccessRequested(false);
        }
      }
    }
    fetchQuestions();
  }, [currentPage, communityId, allowed, userId, navigate]);

  function doSearch(q: SearchProperties) {
    setQuestionsArray([]);
    searchQuestions({
      query: q.query,
      order: q.order,
      filter: q.filter,
      page: 1,
      communityId: parseInt(communityId as string),
      userId: userId,
    }).then((response) => {
      setQuestionsArray(response.list);
      setTotalPages(response.pagination.total);
      changePage(1);
    });
  }

  async function handleRequestAccess() {
    let params: SetAccessTypeParams = {
      communityId: parseInt(communityId as string),
      targetUserId: userId,
      newAccessType: AccessType.REQUESTED,
    };
    setRequestingAccess(true);
    try { 
      await setAccessType(params);
      setAccessRequested(true);
      setRequestingAccess(false);
    } catch{ 
      setRequestingAccess(false);
    }
    
  }
  props.setSearch(doSearch);
  return (
    <>
      <div className="col-6">
        <div className="white-pill mt-5">
          <div className="card-body">
            {!questionsArray && <Spinner />}

            {!allowed && (
              <div className="card-body">
                <p className="row">
                  {t("community.view.noAccessCallToAction")}
                </p>
                {!accessRequested && (
                  <input
                  onClick={handleRequestAccess}
                  disabled={requestingAccess}
                  className="btn btn-primary"
                  type="submit"
                  value={t("dashboard.RequestAccess")}
                  id="requestBtn"
                />
                )}
                {accessRequested && (
                  <p className="row">
                    {t("dashboard.AccessRequested")}
                  </p>
                )}

                
              </div>
            )}

            {/* Loop through the items in questionsArray only if its not empty to display a card for each question*/}
            {allowed &&
              questionsArray &&
              questionsArray.length > 0 &&
              questionsArray.map((question) => (
                <QuestionPreviewCard question={question} key={question.id} />
              ))}

            {allowed && questionsArray && questionsArray.length === 0 && (
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

const CommunityPage = () => {
  const navigate = useNavigate();

  const history = createBrowserHistory();
  //query param de page
  let {  communityId  } = useParams();
  const location = useLocation();

  // Parse the search query string
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
  //get information about the community using the communityId and getCommunity method
  const [community, setCommunity] = useState<Community>(
    null as any as Community
  );

  useEffect(() => {
    async function updateCommunity() {
      if (communityId) {
        // setCommunity(undefined);
        try {
          const response = await getCommunity(parseInt(communityId));
          setCommunity(response);
        } catch (error: any) {
          navigate("/404");
        }
      }
    }
    updateCommunity();
  }, [communityId]);

  

  function setCommunityPage(pageNumber: number) {
    communityPage = pageNumber.toString();
    history.push({
      pathname: `${process.env.PUBLIC_URL}/community/${communityId}?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function setPage(pageNumber: number) {
    page = pageNumber.toString();
    const newCommunityPage = communityPage ? communityPage : 1;
    history.push({
      pathname: `${process.env.PUBLIC_URL}/community/${communityId}?page=${page}&communityPage=${newCommunityPage}${parseQueryParamsForHistory(queries)}`,
    });
  }

  function selectedCommunityCallback(id: number | string) {
    communityId = id.toString();
    let url;
    const newCommunityPage = communityPage ? communityPage : 1;
    if (id === "all") {
      url = `/search/questions?page=1&communityPage=${newCommunityPage}`;
    } else {
      url =
        "/community/" +
        communityId +
        `?page=1&communityPage=${newCommunityPage}`;
    }
   // url = url + parseQueryParamsForHistory(queries)
    navigate(url);
  }
 
  let searchFunctions: ((q: SearchProperties) => void)[] = [
    (q: SearchProperties) => console.log(q), 
    
  ];

  //PERFM EVENT
  let doSearch: (q: SearchProperties) => void = (q: SearchProperties) => {
    searchFunctions.forEach((x) => x(q));
  };

  //SUSCRIBRE TO SEARCH EVENT
  function setSearch(f: (q: SearchProperties) => void) {
    searchFunctions = [];
    searchFunctions.push(
       (q:SearchProperties) =>  {
        queries = q
        history.push({
        pathname: `${process.env.PUBLIC_URL}/community/${communityId}?page=${page}&communityPage=${communityPage}${parseQueryParamsForHistory(q)}`
      })
    })
    searchFunctions.push(f)
   

  }

  return (
    <>
      <div className="section section-hero section-shaped">
        <Background />
        {community && (
          <MainSearchPanel
            showFilters={true}
            title={community.name}
            subtitle={community.description || t("all")}
            doSearch={doSearch}
            searchProperties={queries}
          />
        )}

        {!community && <Spinner />}
          <>
            <div className="row">
              <div className="col-3">
                <CommunitiesLeftPane
                  selectedCommunity={parseInt(communityId as string)}
                  selectedCommunityCallback={selectedCommunityCallback}
                  currentPageCallback={setCommunityPage}
                  
                />
              </div>

              <CenterPanel
                currentPageCallback={setPage}
                setSearch={setSearch}
                searchProperties={queries}
              />

              <div className="col-3">
                <AskQuestionPane />
              </div>
            </div>
          </>
      </div>
    </>
  );
};

export default CommunityPage;
