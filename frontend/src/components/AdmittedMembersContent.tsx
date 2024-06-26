import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { createBrowserHistory } from "history";
import { useTranslation } from "react-i18next";
import { CommunityResponse } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";
import Pagination from "./Pagination";

import { useQuery } from "./UseQuery";

import {
  SetAccessTypeParams,
  setAccessType,
  inviteUserByEmail,
} from "../services/community";
import {
  GetUsersByAcessTypeParams,
  getUsersByAccessType,
} from "../services/user";
import { AccessType } from "../services/access";
import ModalPage from "./ModalPage";
import Spinner from "./Spinner";

type UserContentType = {
  selectedCommunity: CommunityResponse;
  currentCommunityPage: number;
};

const MemberCard = (props: {
  user: User;
  kickUserCallback: () => void;
  banUserCallback: () => void;
}) => {
  return (
    <div className="card">
      <div className="d-flex flex-row justify-content-end">
        <p className="h4 card-title position-absolute start-0 ml-3 mt-2">
          {props.user.username}
        </p>
        <button className="btn mb-0" onClick={props.kickUserCallback}>
          <div className="h4 mb-0">
            <i className="fas fa-user-minus"></i>
          </div>
        </button>
        <button className="btn mb-0" onClick={props.banUserCallback}>
          <div className="h4 mb-0">
            <i className="fas fa-user-slash"></i>
          </div>
        </button>
      </div>
    </div>
  );
};

const AdmittedMembersContent = (props: { params: UserContentType }) => {
  const navigate = useNavigate();

  const history = createBrowserHistory();

  let { communityId } = useParams();

  const query = useQuery();

  const [userList, setUserList] = useState<User[]>();

  const [userPage, setUserPage] = useState<number>(1);
  const [totalUserPages, setTotalUserPages] = useState<number>(1);

  const currentUserId = parseInt(
    window.localStorage.getItem("userId") as string
  );

  // Set initial pages
  useEffect(() => {
    let communityPageFromQuery = query.get("communityPage")
      ? parseInt(query.get("communityPage") as string)
      : 1;

    let userPageFromQuery = query.get("userPage")
      ? parseInt(query.get("userPage") as string)
      : 1;

    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`,
    });
    setUserPage(userPageFromQuery);
  }, [query, communityId]);

  async function fetchAdmittedUsers() {
    if (!props.params.selectedCommunity) {
      return;
    }

    let params: GetUsersByAcessTypeParams = {
      accessType: AccessType.ADMITTED,
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

  // Get selected community's admitted users from API
  useEffect(() => {
    fetchAdmittedUsers();
  }, [props.params.selectedCommunity, userPage, currentUserId]);

  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${props.params.currentCommunityPage}&userPage=${page}`,
    });
    setUserPage(page);

    setUserList(undefined);
  }

  const { t } = useTranslation();

  const [showModalForKick, setShowModalForKick] = useState(false);
  const handleCloseModalForKick = () => {
    setShowModalForKick(false);
  };
  const handleShowModalForKick = () => {
    setShowModalForKick(true);
  };
  async function handleKick(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      targetUserId: userId,
      newAccessType: AccessType.KICKED,
    };
    await setAccessType(params);
    await fetchAdmittedUsers();
    handleCloseModalForKick();
  }

  const [showModalForBan, setShowModalForBan] = useState(false);
  const handleCloseModalForBan = () => {
    setShowModalForBan(false);
  };
  const handleShowModalForBan = () => {
    setShowModalForBan(true);
  };
  async function handleBan(userId: number) {
    let params: SetAccessTypeParams = {
      communityId: props.params.selectedCommunity.id,
      targetUserId: userId,
      newAccessType: AccessType.BANNED,
    };
    await setAccessType(params);
    await fetchAdmittedUsers();
    handleCloseModalForBan();
  }

  async function handleInvite() {
    let btn = document.getElementById("inviteBtn") as HTMLSelectElement;
    let input = document.getElementById("email") as HTMLSelectElement;
    btn.disabled = true;
    try {
      await inviteUserByEmail({
        email: input.value,
        communityId: props.params.selectedCommunity.id,
      });
    } catch (e) {
      alert("cant send invitation");
    }

    btn.disabled = false;
  }
  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.members")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {userList &&
          userList.length > 0 &&
          userList.map((user: User) => (
            <React.Fragment key={user.id}>
              <ModalPage
                buttonName={t("dashboard.KickUser")}
                show={showModalForKick}
                onClose={handleCloseModalForKick}
                onConfirm={() => handleKick(user.id)}
              />
              <ModalPage
                buttonName={t("dashboard.BanUser")}
                show={showModalForBan}
                onClose={handleCloseModalForBan}
                onConfirm={() => handleBan(user.id)}
              />

              <MemberCard
                user={user}
                key={user.id}
                kickUserCallback={handleShowModalForKick}
                banUserCallback={handleShowModalForBan}
              />
            </React.Fragment>
          ))}
        {userList && userList.length === 0 && (
          <>
            <div className="ml-5">
              <p className="row h1 text-gray">{t("dashboard.noMembers")}</p>
              <div className="d-flex justify-content-center">
                <img
                  className="row w-25 h-25"
                  src={require("../images/empty.png")}
                  alt="Nothing to show"
                />
              </div>
            </div>
          </>
        )}
        {userList && userList.length > 0 && (
          <Pagination
            currentPage={userPage}
            setCurrentPageCallback={setUserPageCallback}
            totalPages={totalUserPages}
          />
        )}

        {!userList && <Spinner />}
        {userList && (
          <div className="form-group mx-5">
            <div className="input-group">
              <input
                className="form-control rounded"
                type="input"
                name="email"
                id="email"
                placeholder={t("email")}
              />
              <input
                onClick={handleInvite}
                className="btn btn-primary"
                type="submit"
                value={t("dashboard.invite")}
                id="inviteBtn"
              />
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default AdmittedMembersContent;

function parseParam(n: string | undefined): number {
  return parseInt(n as string);
}
