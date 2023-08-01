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


export default RequestedUsersContent;