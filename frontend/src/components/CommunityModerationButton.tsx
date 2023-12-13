import React from "react";
import { CommunityResponse } from "../models/CommunityTypes";
import { useState, useEffect } from "react";
import { getCommunityNotifications } from "../services/community";

const CommunityModerationButton = (props: {
  community: CommunityResponse;
  selectedCommunity: CommunityResponse;
  setSelectedCommunityCallback: (community: CommunityResponse) => void;
}) => {
  const community = props.community;
  const [notifications, setNotifications] = useState(0);

  useEffect(() => {
    async function getNotifications() {
      const n = await getCommunityNotifications(props.community.id);

      setNotifications(n);
    }
    getNotifications();
  }, [props.community.id]);
  function setSelectedCommunity(community: CommunityResponse) {
    props.setSelectedCommunityCallback(community);
  }
  return (
    <div className="row">
      <button
        onClick={() => setSelectedCommunity(community)}
        className={
          "btn  badge-pill badge-lg my-3 " +
          (community.id !== props.selectedCommunity.id
            ? "btn-outline-primary"
            : "") +
          (community.id === props.selectedCommunity.id ? "btn-primary " : "")
        }
      >
        {community.name}
        {notifications > 0 && (
          <>
            <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning py-0 ">
              <div className="text-white h6 mx-1 my-0">{notifications} </div>
            </span>
          </>
        )}{" "}
      </button>
    </div>
  );
};

export default CommunityModerationButton;
