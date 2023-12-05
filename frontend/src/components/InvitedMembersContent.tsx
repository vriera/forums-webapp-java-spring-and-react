import { useState } from "react";
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

type UserContentType = {
  userList: User[];
  selectedCommunity: CommunityResponse;
  currentPage: number;
  totalPages: number;
  currentCommunityPage: number;
  setCurrentPageCallback: (page: number) => void;
};

const AccessCard = (props: {
  user: User;
  resendInviteCallback: () => void;
}) => {
  return (
    <div className="card">
      <div className="d-flex flex-row justify-content-end">
        <p className="h4 card-title position-absolute start-0 ml-3 mt-2">
          {props.user.username}
        </p>
        <button className="btn mb-0">
          <div className="h4 mb-0">
            <i className="fas fa-times-circle"></i>
          </div>
        </button>
      </div>
    </div>
  );
};

const InvitedMembersContent = (props: { params: UserContentType }) => {

    //ARRANCA PARTE AFANADA


    //TERMINA PARTE AFANADA




    const { t } = useTranslation();
  
    //create your forceUpdate hook
    const [value, setValue] = useState(0); // integer state
  
    const [showModalForResend, setShowModalForResend] = useState(false);
    const handleCloseModalForResend = () => {
      setShowModalForResend(false);
    };
    const handleShowModalForResend = () => {
      setShowModalForResend(true);
    };
    async function handleResend(userId: number) {
      let params: SetAccessTypeParams = {
        communityId: props.params.selectedCommunity.id,
        targetUserId: userId,
        newAccessType: AccessType.NONE,
      };
      await setAccessType(params);
      setValue(value + 1); //To force update
      handleCloseModalForResend();
    }
    return (
      <>
        {/* Different titles according to the corresponding tab */}
        <p className="h3 text-primary">{t("dashboard.invites")}</p>
  
        {/* If members length is greater than 0  */}
        <div className="overflow-auto">
          {props.params.userList &&
            props.params.userList.length > 0 &&
            props.params.userList.map((user: User) => (
              <>
                <ModalPage
                  buttonName={t("dashboard.ResendInvite")}
                  show={showModalForResend}
                  onClose={handleCloseModalForResend}
                  onConfirm={() => handleResend(user.id)}
                />
  
                <AccessCard
                  user={user}
                  key={user.id}
                  resendInviteCallback={handleShowModalForResend}
                />
              </>
            ))}
          {props.params.userList && props.params.userList.length === 0 && (
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
          <Pagination
            currentPage={props.params.currentPage}
            setCurrentPageCallback={props.params.setCurrentPageCallback}
            totalPages={props.params.totalPages}
          />
        </div>
      </>
    );
  };

export default InvitedMembersContent;