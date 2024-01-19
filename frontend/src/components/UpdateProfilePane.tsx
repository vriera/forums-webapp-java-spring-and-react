import React, { useEffect } from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { User } from "./../models/UserTypes";
import {
  UserUpdateParams,
  getKarmaFromApi,
  getUser,
  updateUser,
} from "../services/user";
import Spinner from "./Spinner";
import ModalPage from "./ModalPage";
import { IncorrectPasswordError } from "../models/HttpTypes";

const UpdateProfilePage = (props: { user: User }) => {
  const { t } = useTranslation();
  const [user, setUser] = useState<User>(null as unknown as User);
  const [newPassword, setNewPassword] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [incorrectCurrentPassword, setIncorrectCurrentPassword] =
    useState(false);
  const navigate = useNavigate();

  // Get user from API
  useEffect(() => {
    async function fetchUser() {
      // If user is passed as prop, use it
      if (props.user) {
        try {
          let karma = await getKarmaFromApi(props.user.id);
          setUser({ ...props.user, karma: karma });
        } catch (error: any) {
          navigate(`/${error.code}`);
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
        } catch (error: any) {
          navigate(`/${error.code}`);
        }
      }
    }
    fetchUser();
  }, [navigate]);

  async function updateUserData() {
    let params: UserUpdateParams = {
      userId: user.id,
      newUsername: user.username,
      newPassword: newPassword,
      currentPassword: currentPassword,
    };

    try {
      await updateUser(params);

      setIncorrectCurrentPassword(false);
      window.localStorage.setItem("username", user.username);
    } catch (error: any) {
      if (error instanceof IncorrectPasswordError)
        setIncorrectCurrentPassword(true);
      else navigate(`/${error.status}}`);
    }
  }

  const [showModal, setShowModal] = useState(false);
  const handleCloseModal = () => {
    setShowModal(false);
  };
  const handleShowModal = (event: any) => {
    event.preventDefault();
    setShowModal(true);
  };
  async function handleBlock(communityId: number) {
    await updateUserData();
    handleCloseModal();
  }

  return (
    <div className="white-pill mt-5">
      <ModalPage
        buttonName={t("profile.save")}
        show={showModal}
        onClose={handleCloseModal}
        onConfirm={handleBlock}
      />

      <div className="card-body overflow-hidden">
        <p className="h3 text-primary text-center">{t("title.profile")}</p>
        <hr className="mb-1" />
        {!user && <Spinner />}
        {user && (
          <div>
            <div className="text-center">
              <img
                className="rounded-circle"
                src={
                    "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=" +
                    props.user.email
                }
                alt="User profile icon"
                style={{ height: "80px", width: "80px" }}
              />
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
            <div>
              <p className="h5">{t("profile.updateUsername")}</p>
              <div className="mb-3 text-center">
                <input
                  type="text"
                  className="form-control"
                  value={user.username}
                  onChange={(e) =>
                    setUser({ ...user, username: e.target.value })
                  }
                />
              </div>

              <p className="h5">{t("email")}</p>
              <div className="mb-3 text-center">
                <input
                  type="email"
                  className="form-control"
                  placeholder={user.email}
                  readOnly
                />
              </div>

              <p className="h5">{t("profile.changePassword")}</p>
              <div className="mb-3 text-center">
                <input
                  type="password"
                  className="form-control"
                  placeholder={t("profile.optional")}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
              </div>

              <div className="d-flex">
                <p className="h5">{t("profile.currentPassword")}</p>
                <p className="h5 text-warning bold">*</p>
              </div>
              <div className="mb-3 text-center">
                <input
                  type="password"
                  className="form-control"
                  onChange={(e) => setCurrentPassword(e.target.value)}
                />
                <p className="h6 text-gray">
                  {t("profile.whyCurrentPassword")}
                </p>
                {incorrectCurrentPassword && (
                  <p className="text-warning">
                    {t("profile.incorrectCurrentPassword")}
                  </p>
                )}
              </div>

              <div className="text-center">
                <Link
                  to="/dashboard/profile/info"
                  className="btn btn-secondary text-center"
                >
                  {t("cancel")}
                </Link>
                <button
                  onClick={handleShowModal}
                  className="btn btn-primary text-center"
                >
                  {t("profile.update")}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UpdateProfilePage;
