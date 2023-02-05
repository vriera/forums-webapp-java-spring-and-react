import React from "react";
import { User } from "../models/UserTypes";

export default function UserPreviewCard(props: { user: User }) {
  const imageString: string =
    "https://avatars.dicebear.com/api/avataaars/" + props.user.email + ".svg";

  return (
    <div className="card p-3 m-3 shadow-sm--hover">
      <div className="d-flex">
        <div className="text-center mr-3">
          <img
            className="rounded-circle"
            src={imageString}
            style={{ height: "80px", width: "80px" }}
            alt="profile"
          ></img>
        </div>
        <p className="h1 text-center text-primary">{props.user.username}</p>
      </div>
    </div>
  );
}
