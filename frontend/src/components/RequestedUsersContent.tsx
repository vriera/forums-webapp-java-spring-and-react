import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Pagination from "./Pagination";
import { CommunityResponse } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";
import { AccessType } from "../services/access";
import { SetAccessTypeParams, setAccessType } from "../services/community";
import ModalPage from "./ModalPage";
import {
  GetUsersByAcessTypeParams,
  getUsersByAccessType,
} from "../services/user";
import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import Spinner from "./Spinner";

type UserContentType = {
  selectedCommunity: CommunityResponse;
  currentCommunityPage: number;
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
  const navigate = useNavigate();
  const { t } = useTranslation();
  const history = createBrowserHistory();

  let { communityId } = useParams();
  const currentUserId = parseInt(
    window.localStorage.getItem("userId") as string
  );

  const [userList, setUserList] = useState<User[]>();
  const [userPage, setUserPage] = useState<number>(1);
  const [totalUserPages, setTotalUserPages] = useState<number>(1);

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
      targetUserId: userId,
      newAccessType: AccessType.ADMITTED,
    };

    try {
      await setAccessType(params);
      setUserList(userList?.filter((user) => user.id !== userId));
    } catch {
      fetchRequestedUsers();
    }
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
      targetUserId: userId,
      newAccessType: AccessType.REQUEST_REJECTED,
    };
    try {
      await setAccessType(params);
      setUserList(userList?.filter((user) => user.id !== userId));
    } catch {
      fetchRequestedUsers();
    }
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
      targetUserId: userId,
      newAccessType: AccessType.BLOCKED,
    };
    try {
      await setAccessType(params);
      setUserList(userList?.filter((user) => user.id !== userId));
    } catch {
      fetchRequestedUsers();
    }
    handleCloseModalForBlock();
  }

  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${props.params.currentCommunityPage}&userPage=${page}`,
    });
    setUserPage(page);
    setUserList(undefined);
  }

  async function fetchRequestedUsers() {
    if (!props.params.selectedCommunity) {
      return;
    }

    let params: GetUsersByAcessTypeParams = {
      accessType: AccessType.REQUESTED,
      communityId: props.params.selectedCommunity.id,
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

  // Get selected community's banned users from API
  useEffect(() => {
    fetchRequestedUsers();
  }, [props.params.selectedCommunity, userPage, currentUserId]);

  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.requested")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {userList &&
          userList.length > 0 &&
          userList.map((user: User) => (
            <Fragment key={user.id}>
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
            </Fragment>
          ))}

        {userList && userList.length > 0 && (
          <Pagination
            currentPage={userPage}
            setCurrentPageCallback={setUserPageCallback}
            totalPages={totalUserPages}
          />
        )}

        {userList && userList.length === 0 && (
          // Show no content image
          <div className="ml-5">
            <p className="row h1 text-gray">
              {t("dashboard.noPendingRequests")}
            </p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../images/empty.png")}
                alt="Nothing to show"
              />
            </div>
          </div>
        )}

        {!userList && <Spinner />}
      </div>
    </>
  );
};

export default RequestedUsersContent;
