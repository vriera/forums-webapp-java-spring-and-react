import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { AskableCommunitySearchParams, getAskableCommunities } from "../services/community";
import { CommunityResponse } from "../models/CommunityTypes";
import Pagination from "./Pagination";
import Spinner from "./Spinner";
import { useParams } from "react-router-dom";

const CommunitiesLeftPane = (props: {
  selectedCommunity?: number;
  selectedCommunityCallback: (communityId: number | string) => void;
  currentPageCallback: (page: number) => void;
}) => {
  const { t } = useTranslation();

  const userId = window.localStorage.getItem("userId")
    ? parseInt(window.localStorage.getItem("userId") as string)
    : null;

  const [totalPages, setTotalPages] = useState(-1);
  const [currentPage, setCurrentPage] = useState(1);
  const [communities, setCommunities] = useState<CommunityResponse[]>();



  const changePage = (page: number) => {
    setCurrentPage(page);
    props.currentPageCallback(page);
  };

  useEffect(() => {
    async function getCommunities() {
      setCommunities(undefined);

      try {

        const params: AskableCommunitySearchParams = {
          userId: userId ?? -1,
          page: currentPage,
        };

        const res = await getAskableCommunities(params);
        setCommunities(res.list);
        setTotalPages(res.pagination.total);
      } catch (e) {
        setCommunities([]);
      }
    }
    getCommunities();
  }, [currentPage, userId]);

  return (
    <div className="white-pill mt-5 mx-3">
      <div className="card-body">
        <p className="h3 text-primary">{t("communities")}</p>
        <hr></hr>
        <div className="container-fluid">
          {(communities === undefined || props.selectedCommunity === undefined) && <Spinner />}
          {/* Selected community is 0 means the button of all is active */}
          {(communities) && (
            <button
              onClick={() => props.selectedCommunityCallback("all")}
              className={"btn  badge-pill badge-lg my-3" +
                (props.selectedCommunity === 0 ? " btn-primary" : "") +
                (props.selectedCommunity !== 0 ? " btn-outline-primary" : "")

              }
            >
              {t("community.all")}
            </button>
          )}

          {communities?.map((c: CommunityResponse) => (
            <button
              onClick={() => props.selectedCommunityCallback(c.id)}
              className={`btn badge-pill badge-lg my-3 ${c.id !== props.selectedCommunity ? "btn-outline-primary" : ""
                } ${c.id === props.selectedCommunity ? "btn-primary" : ""}`}
              key={c.id}
            >
              {c.name}
            </button>
          ))}
        </div>
      </div>
      <Pagination
        currentPage={currentPage}
        setCurrentPageCallback={changePage}
        totalPages={totalPages}
      />
    </div>
  );
};

export default CommunitiesLeftPane;
