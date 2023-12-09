import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import Pagination from "./Pagination";
import { CommunityResponse } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";

import { AccessType } from "../services/access";
import {
  SetAccessTypeParams,
  setAccessType,
} from "../services/community";

import ModalPage from "./ModalPage";
import { useNavigate, useParams } from "react-router-dom";

import {
  GetUsersByAcessTypeParams,
  getUsersByAccessType,
} from "../services/user";
import { createBrowserHistory } from "history";
import React from "react";
import Spinner from "./Spinner";

type UserContentType = {
  selectedCommunity: CommunityResponse;
  currentCommunityPage: number;
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

//TODO: Chequear necesidad del UserContentType, siento que a esta altura es al pedo en todos los ...Content
const BannedUsersContent = (props: { params: UserContentType }) => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const history = createBrowserHistory();

  let { communityId } = useParams();
  let pagesParam = parseParam(useParams().userPage);
  const currentUserId = parseInt(window.localStorage.getItem("userId") as string);

  const [userList, setUserList] = useState<User[]>();
  const [userPage, setUserPage] = useState(pagesParam);
  const [totalUserPages, setTotalUserPages] = useState(-1);


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
      targetUserId: userId,
      newAccessType: AccessType.NONE,
    };

    try {
      await setAccessType(params);
      setUserList(userList?.filter((user) => user.id !== userId));
    }
    catch {
      //TODO: Add alert in this state
      fetchBannedUsers();
    }
    handleCloseModalForUnban();
  }

  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${props.params.currentCommunityPage}&userPage=${page}`,
    });
    setUserPage(page);
    setUserList(undefined);
  }

  async function fetchBannedUsers() {
    if (!props.params.selectedCommunity) {
      return;
    }

    let params: GetUsersByAcessTypeParams = {
      accessType: AccessType.BANNED,
      moderatorId: currentUserId,
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
    fetchBannedUsers();
  }, [props.params.selectedCommunity, userPage, currentUserId]);


  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.banned")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {userList &&
          userList.length > 0 &&
          userList.map((user: User) => (
            <React.Fragment key={user.id}>
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
            </React.Fragment>
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
            <p className="row h1 text-gray">{t("dashboard.noBanned")}</p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../images/empty.png")}
                alt="Nothing to show"
              />
            </div>
          </div>
        )}
        {!userList && (
          <Spinner />
        )}

      </div>
    </>
  );
};

export default BannedUsersContent;

function parseParam(n: string | undefined): number {
  return parseInt(n as string);
}