import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
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



type UserContentType = {
  userList: User[];
  selectedCommunity: CommunityResponse;
  currentPage: number;
  totalPages: number;
  currentCommunityPage: number;
  setCurrentPageCallback: (page: number) => void;
};

const BannedCard = (props: { user: User; unbanUserCallback: () => void }) => {
  return (
    <div className="card">
      <div className="d-flex flex-row justify-content-end">
        <p className="h4 card-title position-absolute start-0 ml-3 mt-2">
          {props.user.username}
        </p>
        <button className="btn mb-0" onClick={props.unbanUserCallback}>
          <div className="h4 mb-0">
            <i className="fas fa-unlock"></i>
          </div>
        </button>
      </div>
    </div>
  );
};

const BannedUsersContent = (props: { params: UserContentType }) => {

  const { t } = useTranslation();

  //create your forceUpdate hook
  const [value, setValue] = useState(0); // integer state

  const [showModalForUnban, setShowModalForUnban] = useState(false);
  const handleCloseModalForUnban = () => {
    setShowModalForUnban(false);
  };
  const handleShowModalForUnban = () => {
    setShowModalForUnban(true);
  };
  async function handleUnban(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      userId: userId,
      accessType: AccessType.NONE,
    };
    await setAccessType(params);
    setValue(value + 1); //To force update
    handleCloseModalForUnban();
  }

  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.banned")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {props.params.userList &&
          props.params.userList.length > 0 &&
          props.params.userList.map((user: User) => (
            <>
              <ModalPage
                buttonName={t("dashboard.UnbanUser")}
                show={showModalForUnban}
                onClose={handleCloseModalForUnban}
                onConfirm={() => handleUnban(user.id)}
              />

              <BannedCard
                user={user}
                key={user.id}
                unbanUserCallback={handleShowModalForUnban}
              />
            </>
          ))}

        {props.params.userList && props.params.userList.length === 0 && (
          // Show no content image
          <div className="ml-5">
            <p className="row h1 text-gray">{t("dashboard.noBanned")}</p>
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

const BannedUsersPane = (props: { params: UserContentType }) => {
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
        activeTab="banned"
        communityId={props.params.selectedCommunity.id}
        communityPage={props.params.currentCommunityPage}
      />
      <div className="card-body">
        <BannedUsersContent params={props.params} />
      </div>
    </div>
  );
};

const BannedUsersPage = () => {
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
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/banned?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`,
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
      } catch (error:any) {
        //navigate(`/${error.code}`);
      }
    }
    fetchModeratedCommunities();
  }, [communityPage, userId]);

  // Get selected community's admitted users from API
  useEffect(() => {
    async function fetchAdmittedUsers() {
      if (selectedCommunity !== undefined) {
        let params: UsersByAcessTypeParams = {
          accessType: AccessType.BANNED,
          communityId: selectedCommunity?.id as number,
          page: userPage,
        };
        try {
          let { list, pagination } = await getUsersByAccessType(params);
          setUserList(list);
          setTotalUserPages(pagination.total);
        } catch (error:any) {
          //navigate(`/${error.code}`);
        }
      }
    }
    fetchAdmittedUsers();
  }, [selectedCommunity, userPage, userId]);

  function setCommunityPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/banned?communityPage=${page}&userPage=${userPage}`,
    });
    setCommunityPage(page);
    setModeratedCommunities(undefined);
  }

  function setSelectedCommunityCallback(community: CommunityResponse): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}/banned?communityPage=${communityPage}&userPage=${userPage}`,
    });
    setSelectedCommunity(community);
  }

  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/banned?communityPage=${communityPage}&userPage=${page}`,
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
                <BannedUsersPane
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

export default BannedUsersPage;

function parseParam(n: string | undefined): number {
  return parseInt(n as string);
}
