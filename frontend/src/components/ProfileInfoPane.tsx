import React, { useEffect, useState } from "react";
import { User } from "../models/UserTypes";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { getKarmaFromApi, getUser, getUserFromApi } from "../services/user";
import Spinner from "./Spinner";

const ProfileInfoPane = (props: {
  user: User | undefined;
  showUpdateButton: boolean;
}) => {
  const { t } = useTranslation();

  const navigate = useNavigate();
  const [user, setUser] = useState<User>(null as unknown as User);

  useEffect(() => {
    async function fetchUser() {
      // If user is passed as prop, use it
      if (props.user) {
        try {
          let karma = await getKarmaFromApi(props.user.id);
          setUser({ ...props.user, karma: karma });
        } catch (error) {
          navigate("/500");
        }
      }
      // If user is not passed as prop, fetch it from API
      else {
        const userId = parseInt(
          window.localStorage.getItem("userId") as string
        );

        try {
          let auxUser = await getUser(userId);
          setUser(auxUser);
        } catch (error) {
          navigate("/500");
        }
      }
    }
    fetchUser();
  }, [navigate]);

  return (
    <div className="white-pill mt-5">
      <div className="card-body overflow-hidden">
        <p className="h3 text-primary text-center">{t("title.profile")}</p>
        <hr className="mb-1" />
        {!user && <Spinner />}
        {user && (
          <div>
            <div className="text-center">
              <img
                className="rounded-circle"
                alt="User profile icon"
                src={
                  "https://avatars.dicebear.com/api/avataaars/" +
                  user.email +
                  ".svg"
                }
                style={{ height: "80px", width: "80px" }}
              />
            </div>
            <p className="h1 text-center text-primary">{user.username}</p>
            <p className="h4 text-center">
              {t("email")}: {user.email}
            </p>
            <div className="d-flex justify-content-center">
              <p className="h4 text-center">
                {t("profile.karma")}
                {user.karma && user.karma.karma}
              </p>
              {user.karma && user.karma.karma > 0 && (
                <div className="h4 mr-2 text-success mb-0 mt-1 ml-3">
                  <i className="fas fa-arrow-alt-circle-up"></i>
                </div>
              )}
              {user.karma && user.karma.karma < 0 && (
                <div className="h4 mr-2 text-warning mb-0 mt-1 ml-3">
                  <i className="fas fa-arrow-alt-circle-down">
                    {user.karma.karma}
                  </i>
                </div>
              )}
            </div>
          </div>
        )}

        {props.showUpdateButton && (
          <div className="text-center mt-3">
            <Link
              to="/dashboard/profile/update"
              className="btn btn-primary text-center"
            >
              {t("profile.update")}
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileInfoPane;
