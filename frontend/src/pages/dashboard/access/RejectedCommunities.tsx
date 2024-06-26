import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardPane from "../../../components/DashboardPane";
import { CommunityResponse } from "../../../models/CommunityTypes";
import ModalPage from "../../../components/ModalPage";
import { useEffect, useState } from "react";
import Pagination from "../../../components/Pagination";
import { createBrowserHistory } from "history";
import { useNavigate } from "react-router-dom";
import { useQuery } from "../../../components/UseQuery";
import {
  CommunitiesByAcessTypeParams,
  SetAccessTypeParams,
  getCommunitiesByAccessType,
  setAccessType,
} from "../../../services/community";
import { AccessType } from "../../../services/access";
import Spinner from "../../../components/Spinner";

const ManageRequests = () => {
  const { t } = useTranslation();

  const userId = parseInt(window.localStorage.getItem("userId") as string);
  const history = createBrowserHistory();
  const navigate = useNavigate();
  const query = useQuery();

  const [showModalForRequest, setShowModalForRequest] = useState(false);
  const handleCloseModalForRequest = () => {
    setShowModalForRequest(false);
  };
  const handleShowModalForRequest = (event: any) => {
    event.preventDefault();
    setShowModalForRequest(true);
  };
  async function handleRequest(communityId: number) {
    let params: SetAccessTypeParams = {
      communityId: communityId,
      targetUserId: userId,
      newAccessType: AccessType.REQUESTED,
    };
    await setAccessType(params);
    let listWithoutCommunity = communities?.filter(
      (community: CommunityResponse) => community.id !== communityId
    );
    setCommunities(listWithoutCommunity);
    handleCloseModalForRequest();
  }

  const [communities, setCommunities] = useState<CommunityResponse[]>();

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  // Set initial page
  useEffect(() => {
    let pageFromQuery = query.get("page")
      ? parseInt(query.get("page") as string)
      : 1;
    setCurrentPage(pageFromQuery);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/access/invited?page=${pageFromQuery}`,
    });
  }, [query]);

  // Fetch communities from API
  useEffect(() => {
    async function fetchUserQuestions() {
      let params: CommunitiesByAcessTypeParams = {
        userId: userId,
        accessType: AccessType.REQUEST_REJECTED,
        page: currentPage,
      };
      try {
        let { list, pagination } = await getCommunitiesByAccessType(params);
        setCommunities(list);
        setTotalPages(pagination.total);
      } catch (e: any) {
        navigate(`/${e.code}`);
      }
    }
    fetchUserQuestions();
  }, [currentPage, navigate, userId]);

  function setCurrentPageCallback(page: number) {
    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/access/invited?page=${page}`,
    });
    setCommunities(undefined);
  }

  return (
    <div>
      {!communities && (
        <div className="my-5">
          <Spinner />
        </div>
      )}
      {communities && communities.length === 0 && (
        <p className="h3 text-gray mt-2">{t("dashboard.noRejectedRequests")}</p>
      )}
      <div className="overflow-auto">
        {communities &&
          communities.length > 0 &&
          communities.map((community: CommunityResponse) => (
            <div className="card" key={community.id}>
              <ModalPage
                buttonName={t("dashboard.RequestAccess")}
                show={showModalForRequest}
                onClose={handleCloseModalForRequest}
                onConfirm={() => handleRequest(community.id)}
              />

              <div
                className="d-flex flex-row mt-3"
                style={{ justifyContent: "space-between" }}
              >
                <p className="h4 card-title ml-2">{community.name}</p>
                <button
                  className="btn mb-0"
                  onClick={handleShowModalForRequest}
                  title={t("dashboard.ResendRequest")}
                >
                  <div className="h4 mb-0">
                    <i className="fas fa-redo-alt"></i>
                  </div>
                </button>
              </div>
            </div>
          ))}
      </div>
      <Pagination
        totalPages={totalPages}
        currentPage={currentPage}
        setCurrentPageCallback={setCurrentPageCallback}
      />
    </div>
  );
};

const RejectedCommunitiesPane = () => {
  const { t } = useTranslation();

  return (
    <div className="white-pill mt-5">
      <div className="card-body">
        <p className="h2 text-primary text-center mt-3 text-uppercase">
          {t("dashboard.rejectedRequests")}
        </p>
        <DashboardAccessTabs activeTab="rejected" />
        <ManageRequests />
      </div>
    </div>
  );
};

const RejectedCommunitiesPage = () => {
  return (
    <div>
      {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
      <div className="wrapper">
        <div className="section section-hero section-shaped pt-3">
          <Background />

          <div className="row">
            {/* COMMUNITIES SIDE PANE*/}
            <div className="col-3">
              <DashboardPane option={"access"} />
            </div>

            {/* CENTER PANE*/}
            <div className="col-6">
              <RejectedCommunitiesPane />
            </div>

            {/* ASK QUESTION */}
            <div className="col-3">
              <CreateCommunityPane />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RejectedCommunitiesPage;
