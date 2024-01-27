import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import Background from "../../../components/Background";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import DashboardPane from "../../../components/DashboardPane";
import Pagination from "../../../components/Pagination";
import { CommunityResponse } from "../../../models/CommunityTypes";
import { User } from "../../../models/UserTypes";
import {
  UsersByAcessTypeParams,
  getUsersByAccessType,
} from "../../../services/user";
import { AccessType } from "../../../services/Access";
import {
  ModeratedCommunitiesParams,
  SetAccessTypeParams,
  getModeratedCommunities,
  setAccessType,
} from "../../../services/community";
import { useQuery } from "../../../components/UseQuery";
import { createBrowserHistory } from "history";
import ModeratedCommunitiesPane from "../../../components/DashboardModeratedCommunitiesPane";
import Spinner from "../../../components/Spinner";
import ModalPage from "../../../components/ModalPage";
import { Link, useNavigate } from "react-router-dom";

type UserContentType = {
  userList: User[];
  selectedCommunity: CommunityResponse;
  currentPage: number;
  totalPages: number;
  currentCommunityPage: number;
  setCurrentPageCallback: (page: number) => void;
};

const RequestedCard = (props: {
  user: User;
  acceptRequestCallback: () => void;
  rejectRequestCallback: () => void;
  blockCommunityCallback: () => void;
}) => {
  const { t } = useTranslation();
  return (
    <div className="card">
      <div
        className="d-flex flex-row mt-3"
        style={{ justifyContent: "space-between" }}
      >
        <div>
          <p className="h4 card-title ml-2">{props.user.username}</p>
        </div>
        <div className="row">
          <div className="col-auto mx-0 px-0">
            <button
              className="btn mb-0"
              onClick={props.acceptRequestCallback}
              title={t("dashboard.AcceptRequest")}
            >
              <div className="h4 mb-0">
                <i className="fas fa-check-circle"></i>
              </div>
            </button>
          </div>

          <div className="col-auto mx-0 px-0">
            <button
              className="btn mb-0"
              onClick={props.rejectRequestCallback}
              title={t("dashboard.RejectRequest")}
            >
              <div className="h4 mb-0">
                <i className="fas fa-times-circle"></i>
              </div>
            </button>
          </div>

          <div className="col-auto mx-0 px-0">
            <button
              className="btn mb-0"
              onClick={props.blockCommunityCallback}
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
  );
};

const RequestedUsersContent = (props: { params: UserContentType }) => {
  const { t } = useTranslation();

  //create your forceUpdate hook
  const [value, setValue] = useState(0); // integer state

  const [showModalForAccept, setShowModalForAccept] = useState(false);
  const handleCloseModalForAccept = () => {
    setShowModalForAccept(false);
  };
  const handleShowForAccept = () => {
    setShowModalForAccept(true);
  };

  async function handleAccept(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      targetId: userId,
      newAccess: AccessType.ADMITTED,
    };
    await setAccessType(params);
    setValue(value + 1); // update the state to force render
    handleCloseModalForAccept();
  }

  const [showModalForReject, setShowModalForReject] = useState(false);
  const handleCloseModalForReject = () => {
    setShowModalForReject(false);
  };
  const handleShowForReject = () => {
    setShowModalForReject(true);
  };

  async function handleReject(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      targetId: userId,
      newAccess: AccessType.REQUEST_REJECTED,
    };
    await setAccessType(params);
    setValue(value + 1); // update the state to force render
    handleCloseModalForReject();
  }

  const [showModalForBlock, setShowModalForBlock] = useState(false);
  const handleCloseModalForBlock = () => {
    setShowModalForBlock(false);
  };
  const handleShowForBlock = () => {
    setShowModalForBlock(true);
  };

  async function handleBlock(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      targetId: userId,
      newAccess: AccessType.BLOCKED_COMMUNITY,
    };
    await setAccessType(params);
    setValue(value + 1); // update the state to force render
    handleCloseModalForBlock();
  }

  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.requested")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {props.params.userList &&
          props.params.userList.length > 0 &&
          props.params.userList.map((user: User) => (
            <>
              <ModalPage
                buttonName={t("dashboard.AcceptRequest")}
                show={showModalForAccept}
                onClose={handleCloseModalForAccept}
                onConfirm={() => handleAccept(user.id)}
              />
              <ModalPage
                buttonName={t("dashboard.BlockCommunity")}
                show={showModalForBlock}
                onClose={handleCloseModalForBlock}
                onConfirm={() => handleBlock(user.id)}
              />
              <ModalPage
                buttonName={t("dashboard.RejectRequest")}
                show={showModalForReject}
                onClose={handleCloseModalForReject}
                onConfirm={() => handleReject(user.id)}
              />

              <RequestedCard
                user={user}
                key={user.id}
                acceptRequestCallback={handleShowForAccept}
                rejectRequestCallback={handleShowForReject}
                blockCommunityCallback={handleShowForBlock}
              />
            </>
          ))}

        {props.params.userList && props.params.userList.length === 0 && (
          // Show no content image
          <div className="ml-5">
            <p className="row h1 text-gray">
              {t("dashboard.noPendingRequests")}
            </p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../../../images/empty.png")}
                alt="Nothing to show"
              />
            </div>
          </div>
        )}
        <Pagination
          currentPage={props.params.currentPage}
          setCurrentPageCallback={props.params.setCurrentPageCallback}
          totalPages={props.params.totalPages}
        />
      </div>
    </>
  );
};

const RequestedUsersPane = (props: { params: UserContentType }) => {
  const { t } = useTranslation();

  return (
    <div className="white-pill mt-5">
      <div className="align-items-start d-flex justify-content-center my-3">
        <p className="h1 text-primary bold">
          <strong>{t(props.params.selectedCommunity.name)}</strong>
        </p>
      </div>
      <div className="text-gray text-center mt--4 mb-2">
        {props.params.selectedCommunity.description}
      </div>

      <hr />

      <DashboardCommunitiesTabs
        activeTab="requested"
        communityId={props.params.selectedCommunity.id}
        communityPage={props.params.currentCommunityPage}
      />
      <div className="card-body">
        <RequestedUsersContent params={props.params} />
      </div>
    </div>
  );
};

const RequestedUsersPage = () => {
  const history = createBrowserHistory();
  const navigate = useNavigate();
  let pagesParam = parseParam(useParams().userPage);
  let communityPageParam = parseParam(useParams().communityPage);
  let { communityId } = useParams();
  const query = useQuery();

  const [moderatedCommunities, setModeratedCommunities] =
    useState<CommunityResponse[]>();
  const [selectedCommunity, setSelectedCommunity] = useState<CommunityResponse>();

  const [communityPage, setCommunityPage] = useState(communityPageParam);
  const [totalCommunityPages, setTotalCommunityPages] = useState(-1);

  const [userList, setUserList] = useState<User[]>();

  const [userPage, setUserPage] = useState(pagesParam);
  const [totalUserPages, setTotalUserPages] = useState(-1);

  const userId = parseInt(window.localStorage.getItem("userId") as string);

  // Set initial pages
  useEffect(() => {
    let communityPageFromQuery = query.get("communityPage")
      ? parseInt(query.get("communityPage") as string)
      : 1;

    let userPageFromQuery = query.get("userPage")
      ? parseInt(query.get("userPage") as string)
      : 1;

    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/requested?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`,
    });
    setUserPage(userPageFromQuery);
    setCommunityPage(communityPageFromQuery);
  }, [query, communityId]);

  // Get user's moderated communities from API
  useEffect(() => {
    async function fetchModeratedCommunities() {
      let params: ModeratedCommunitiesParams = {
        moderatorId: userId,
        page: communityPage,
      };
      try {
        let { list, pagination } = await getModeratedCommunities(params);
        setModeratedCommunities(list);
        let index = list.findIndex((x) => x.id === parseParam(communityId));
        if (index === -1) index = 0;
        setSelectedCommunity(list[index]);
        setUserPage(1);
        setTotalCommunityPages(pagination.total);
      } catch (error: any) {
          navigate(`/${error.code}`);
      }
    }
    fetchModeratedCommunities();
  }, [communityPage, userId]);

  // Get selected community's admitted users from API
  useEffect(() => {
    async function fetchAdmittedUsers() {
      if (selectedCommunity !== undefined) {
        let params: UsersByAcessTypeParams = {
          accessType: AccessType.REQUESTED,
          communityId: selectedCommunity?.id as number,
          page: userPage,
        };
        try {
          let { list, pagination } = await getUsersByAccessType(params);
          setUserList(list);
          setTotalUserPages(pagination.total);
        } catch (error: any) {
          navigate(`/${error.code}`);
        }
      }
    }
    fetchAdmittedUsers();
  }, [selectedCommunity, userPage, userId]);

  function setCommunityPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/requested?communityPage=${page}&userPage=${userPage}`,
    });
    setCommunityPage(page);
    setModeratedCommunities(undefined);
  }

  function setSelectedCommunityCallback(community: CommunityResponse): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}/requested?communityPage=${communityPage}&userPage=${userPage}`,
    });
    setSelectedCommunity(community);
  }

  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/requested?communityPage=${communityPage}&userPage=${page}`,
    });
    setUserPage(page);

    setUserList(undefined);
  }

  return (
    <div>
      {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
      <div className="wrapper">
        <div className="section section-hero section-shaped pt-3">
          <Background />

          <div className="row">
            {/* COMMUNITIES SIDE PANE*/}
            <div className="col-3">
              <DashboardPane option={"communities"} />
            </div>

            {/* CENTER PANE*/}
            <div className="col-6">
              {(!selectedCommunity || !userList) && (
                <>
                  <div className="white-pill mt-5">
                    <div className="align-items-start d-flex justify-content-center my-3">
                      <p className="h1 text-primary bold">
                        <Spinner />
                      </p>
                    </div>
                    <hr />
                    <div className="card-body">
                      <Spinner />
                    </div>
                  </div>
                </>
              )}
              {selectedCommunity && userList && (
                <RequestedUsersPane
                  params={{
                    selectedCommunity: selectedCommunity,
                    userList: userList,
                    currentPage: userPage,
                    totalPages: totalUserPages,
                    currentCommunityPage: communityPage,
                    setCurrentPageCallback: setUserPageCallback,
                  }}
                />
              )}
            </div>

            {/* MODERATED COMMUNITIES SIDE PANE */}
            <div className="col-3">
              {(!moderatedCommunities || !selectedCommunity) && (
                <>
                  <div className="white-pill mt-5 mx-3">
                    <div className="card-body">
                      <Spinner />
                    </div>
                  </div>
                </>
              )}
              {moderatedCommunities && selectedCommunity && (
                <ModeratedCommunitiesPane
                  communities={moderatedCommunities}
                  selectedCommunity={selectedCommunity}
                  setSelectedCommunityCallback={setSelectedCommunityCallback}
                  currentPage={communityPage}
                  totalPages={totalCommunityPages}
                  setCurrentPageCallback={setCommunityPageCallback}
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RequestedUsersPage;

function parseParam(n: string | undefined): number {
  return parseInt(n as string);
}
