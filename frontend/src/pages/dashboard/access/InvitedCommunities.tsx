import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardPane from "../../../components/DashboardPane";
import ModalPage from "../../../components/ModalPage";
import Pagination from "../../../components/Pagination";
import { CommunityResponse } from "../../../models/CommunityTypes";
import { useEffect, useState } from "react";
import {
  CommunitiesByAcessTypeParams,
  SetAccessTypeParams,
  getCommunitiesByAccessType,
  setAccessType,
} from "../../../services/community";
import { createBrowserHistory } from "history";
import { useNavigate } from "react-router-dom";
import { useQuery } from "../../../components/UseQuery";
import { AccessType } from "../../../services/Access";
import Spinner from "../../../components/Spinner";

const ManageInvites = () => {
  const { t } = useTranslation();

  const userId = parseInt(window.localStorage.getItem("userId") as string);
  const history = createBrowserHistory();
  const navigate = useNavigate();
  const query = useQuery();

  const [showModalForAccept, setShowModalForAccept] = useState(false);
  const handleCloseModalForAccept = () => {
    setShowModalForAccept(false);
  };
  const handleShowModalForAccept = (event: any) => {
    event.preventDefault();
    setShowModalForAccept(true);
  };
  async function handleAccept(communityId: number) {
    let params: SetAccessTypeParams = {
      communityId: communityId,
      userId: userId,
      accessType: AccessType.ADMITTED,
    };
    await setAccessType(params);
    let listWithoutCommunity = communities?.filter(
      (community: CommunityResponse) => community.id !== communityId
    );
    setCommunities(listWithoutCommunity);
    handleCloseModalForAccept();
  }

  const [showModalForReject, setShowModalForReject] = useState(false);
  const handleCloseModalForReject = () => {
    setShowModalForReject(false);
  };
  const handleShowModalForReject = (event: any) => {
    event.preventDefault();
    setShowModalForReject(true);
  };
  async function handleReject(communityId: number) {
    let params: SetAccessTypeParams = {
      communityId: communityId,
      userId: userId,
      accessType: AccessType.INVITE_REJECTED,
    };
    await setAccessType(params);
    let listWithoutCommunity = communities?.filter(
      (community: CommunityResponse) => community.id !== communityId
    );
    setCommunities(listWithoutCommunity);
    handleCloseModalForReject();
  }

  const [showModalForBlock, setShowModalForBlock] = useState(false);
  const handleCloseModalForBlock = () => {
    setShowModalForBlock(false);
  };
  const handleShowModalForBlock = (event: any) => {
    event.preventDefault();
    setShowModalForBlock(true);
  };
  async function handleBlock(communityId: number) {
    let params: SetAccessTypeParams = {
      communityId: communityId,
      userId: userId,
      accessType: AccessType.BLOCKED_COMMUNITY,
    };
    await setAccessType(params);
    let listWithoutCommunity = communities?.filter(
      (community: CommunityResponse) => community.id !== communityId
    );
    setCommunities(listWithoutCommunity);
    handleCloseModalForBlock();
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
        accessType: AccessType.INVITED,
        page: currentPage,
      };
      try {
        let { list, pagination } = await getCommunitiesByAccessType(params);
        setCommunities(list);
        setTotalPages(pagination.total);
      } catch {
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
        <p className="h3 text-gray mt-2">{t("dashboard.noPendingInvites")}</p>
      )}
      {communities && communities.length > 0 && (
        <div className="my-3">
          {communities &&
            communities.map((community: CommunityResponse) => (
              <div className="card" key={community.id}>
                <ModalPage
                  buttonName={t("dashboard.AcceptInvite")}
                  show={showModalForAccept}
                  onClose={handleCloseModalForAccept}
                  onConfirm={() => handleAccept(community.id)}
                />
                <ModalPage
                  buttonName={t("dashboard.BlockCommunity")}
                  show={showModalForBlock}
                  onClose={handleCloseModalForBlock}
                  onConfirm={() => handleBlock(community.id)}
                />
                <ModalPage
                  buttonName={t("dashboard.RejectInvite")}
                  show={showModalForReject}
                  onClose={handleCloseModalForReject}
                  onConfirm={() => handleReject(community.id)}
                />
                <div
                  className="d-flex flex-row mt-3"
                  style={{ justifyContent: "space-between" }}
                >
                  <div>
                    <p className="h4 card-title ml-2">{community.name}</p>
                  </div>
                  <div className="row">
                    <div className="col-auto mx-0 px-0">
                      <button
                        className="btn mb-0"
                        onClick={
                          handleShowModalForAccept
                        }
                        title={t("dashboard.AcceptInvite")}
                      >
                        <div className="h4 mb-0">
                          <i className="fas fa-check-circle"></i>
                        </div>
                      </button>
                    </div>

                    <div className="col-auto mx-0 px-0">
                      <button
                        className="btn mb-0"
                        onClick={
                         handleShowModalForReject
                        }
                        title={t("dashboard.RejectInvite")}
                      >
                        <div className="h4 mb-0">
                          <i className="fas fa-times-circle"></i>
                        </div>
                      </button>
                    </div>

                    <div className="col-auto mx-0 px-0">
                      <button
                        className="btn mb-0"
                        onClick={
                          handleShowModalForBlock
                        }
                        title={t("dashboard.BlockCommunity")}
                      >
                        <div className="h4 mb-0">
                          <i className="fas fa-ban"></i>
                        </div>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
        </div>
      )}
      <Pagination
        totalPages={totalPages}
        currentPage={currentPage}
        setCurrentPageCallback={setCurrentPageCallback}
      />
    </div>
  );
};

const InvitedCommunitiesPane = () => {
  const { t } = useTranslation();

  return (
    <div className="white-pill mt-5">
      <div className="card-body">
        <p className="h2 text-primary text-center mt-3 text-uppercase">
          {t("dashboard.pendingInvites")}
        </p>
        <DashboardAccessTabs activeTab={"invited"} />
        <ManageInvites />
      </div>
    </div>
  );
};

const InvitedCommunitiesPage = () => {
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
              <InvitedCommunitiesPane />
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

export default InvitedCommunitiesPage;
