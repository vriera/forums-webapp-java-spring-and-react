import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Pagination from "./Pagination";
import { CommunityResponse } from "../models/CommunityTypes";
import { User } from "../models/UserTypes";

import { AccessType } from "../services/access";
import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import {
  GetUsersByAcessTypeParams,
  getUsersByAccessType,
} from "../services/user";
import React from "react";
import Spinner from "./Spinner";

type UserContentType = {
  selectedCommunity: CommunityResponse;
  currentCommunityPage: number;
};

const AccessCard = (props: {
  user: User;
}) => {
  return (
    <div className="card mb-2">
      <p className="h4 card-title start-0 ml-3 mt-2 mb-2">
        {props.user.username}
      </p>
    </div >
  );
};

const InvitedMembersContent = (props: { params: UserContentType }) => {

  const navigate = useNavigate();
  const { t } = useTranslation();
  const history = createBrowserHistory();

  let { communityId } = useParams();
  let pagesParam = parseParam(useParams().userPage);
  const currentUserId = parseInt(window.localStorage.getItem("userId") as string);

  const [userList, setUserList] = useState<User[]>();
  const [userPage, setUserPage] = useState(pagesParam);
  const [totalUserPages, setTotalUserPages] = useState(-1);


  function setUserPageCallback(page: number): void {
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${props.params.currentCommunityPage}&userPage=${page}`,
    });
    setUserPage(page);
    setUserList(undefined);
  }

  async function fetchInvitedMembers() {
    if (!props.params.selectedCommunity) {
      return;
    }

    let params: GetUsersByAcessTypeParams = {
      accessType: AccessType.INVITED,
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
    fetchInvitedMembers();
  }, [props.params.selectedCommunity, userPage, currentUserId]);


  return (
    <>
      {/* Different titles according to the corresponding tab */}
      <p className="h3 text-primary">{t("dashboard.invites")}</p>

      {/* If members length is greater than 0  */}
      <div className="overflow-auto">
        {userList &&
          userList.length > 0 &&
          userList.map((user: User) => (
            <React.Fragment key={user.id}>

              <AccessCard
                user={user}
                key={user.id}
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
            <p className="row h1 text-gray">
              {t("dashboard.noPendingInvites")}
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

        {!userList && (
          <Spinner />
          )}

      </div>
    </>
  );
};

export default InvitedMembersContent;

function parseParam(n: string | undefined): number {
  return parseInt(n as string);
}