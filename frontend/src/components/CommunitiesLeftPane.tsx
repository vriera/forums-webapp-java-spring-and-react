import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { getAskableCommunities } from "../services/community";
import { CommunityResponse } from "../models/CommunityTypes";
import Pagination from "./Pagination";
import Spinner from "./Spinner";

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
        const res = await getAskableCommunities({
          requestorId: userId || -1,
          page: currentPage,
        });
        setCommunities(res.list);
        setTotalPages(res.pagination.total);
      } catch (e) {
        setCommunities([]);
      }
    }
    getCommunities();
  }, [currentPage]);

  return (
    <div className="white-pill mt-5 mx-3">
      <div className="card-body">
        <p className="h3 text-primary">{t("communities")}</p>
        <hr></hr>
        <div className="container-fluid">
          {communities === undefined && <Spinner />}
          {communities && (
            <button
              onClick={() => props.selectedCommunityCallback("all")}
              className={
                "btn  badge-pill badge-lg my-3 " +
                (props.selectedCommunity ? "btn-outline-primary" : "") +
                (!props.selectedCommunity ? "btn-light" : "")
              }
            >
              {t("community.all")}
            </button>
          )}
          {communities &&
            communities.map((c: CommunityResponse) => (
              // TODO: Check this statement, there must be a better way of doing the same thing
              <button
                onClick={() => props.selectedCommunityCallback(c.id)}
                className={
                  "btn  badge-pill badge-lg my-3 " +
                  (c.id !== props.selectedCommunity
                    ? "btn-outline-primary"
                    : "") +
                  (c.id === props.selectedCommunity ? "btn-primary" : "")
                }
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
