import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

import { CommunityResponse } from "../../../models/CommunityTypes";
import Background from "../../../components/Background";
import { getAllowedCommunities } from "../../../services/community";
import Spinner from "../../../components/Spinner";
import { createBrowserHistory } from "history";
import { useQuery } from "../../../components/UseQuery";
import Pagination from "../../../components/Pagination";

const SelectCommunityPage = (props: {}) => {
  const { t } = useTranslation();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  const history = createBrowserHistory();
  const query = useQuery();

  const [communitiesArray, setCommunities] = React.useState<CommunityResponse[]>();
  const requestorId = parseInt(window.localStorage.getItem("userId") as string);

  // Set initial page
  useEffect(() => {
    let pageFromQuery = query.get("page")
      ? parseInt(query.get("page") as string)
      : 1;
    setCurrentPage(pageFromQuery);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/ask/selectCommunity?page=${pageFromQuery}`,
    });
  }, [query]);

  function setPageAndQuery(page: number) {
    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/ask/selectCommunity?page=${page}`,
    });
    setCommunities(undefined);
  }

  useEffect(() => {
    getAllowedCommunities({ requestorId: requestorId, page: currentPage }).then(
      (response) => {
        setCommunities(response.list);
        setTotalPages(response.pagination.total);
      }
    );
  }, [currentPage, requestorId]);

  return (
    <div className="section section-hero section-shaped">
      <Background />
      <div className="container">
        <div className="white-pill">
          <div className="d-flex justify-content-center">
            <p className="h1 text-primary text-center">
              {t("title.askQuestion")}
            </p>
          </div>
          <hr />
          {!communitiesArray && <Spinner />}

          {communitiesArray && (
            <>
              <p className="h5 text-black">
                {t("question.chooseCommunityCallToAction")}
              </p>
              <div className="container">
                {communitiesArray.map((community) => (
                  <Link
                    to={`/ask/writeQuestion/${community.id}`}
                    className="btn btn-outline-primary badge-pill badge-lg my-3"
                  >
                    {community.name}
                  </Link>
                ))}
              </div>
              <Pagination
                totalPages={totalPages}
                currentPage={currentPage}
                setCurrentPageCallback={setPageAndQuery}
              />
            </>
          )}

          <hr />
          {/* STEPPER */}
          <div className="stepper-wrapper">
            {/* Classname should be active if currentProgress is 1 */}
            <div className="stepper-item active">
              <div className="step-counter">1</div>
              <div className="step-name">{t("question.community")}</div>
            </div>
            <div className="stepper-item ">
              <div className="step-counter">2</div>
              <div className="step-name">{t("question.content")}</div>
            </div>
            <div className="stepper-item ">
              <div className="step-counter">3</div>
              <div className="step-name">{t("question.wrapup.message")}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectCommunityPage;
