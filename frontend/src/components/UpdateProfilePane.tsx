import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { User } from "./../models/UserTypes";
import {
  UpdateUserParams,
  getKarma,
  getUserAndKarma,
  updateUser,
} from "../services/user";
import Spinner from "./Spinner";
import ModalPage from "./ModalPage";
import { IncorrectPasswordError, UsernameTakenError } from "../models/HttpTypes";

const UpdateProfilePage = (props: { user: User }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [user, setUser] = useState<User>(null as unknown as User);
  const [newPassword, setNewPassword] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [incorrectCurrentPassword, setIncorrectCurrentPassword] = useState(false);
  const [newUsername, setNewUsername] = useState(""); 
  const [usernameTaken, setUsernameTaken] = useState(false);

  // Get user from API
  useEffect(() => {
    async function fetchUser() {
      try {
        // If user is passed as prop, use it
        if (props.user && !props.user.karma) {
            let karma = await getKarma(props.user.id);
            setUser({ ...props.user, karma: karma });
            setNewUsername(props.user.username);
        }
        // If user is not passed as prop, fetch it from API
        else {
          const userId = parseInt(
            window.localStorage.getItem("userId") as string
          );

            let auxUser = await getUserAndKarma(userId);
            setUser(auxUser);
            setNewUsername(auxUser.username);
          
        }
      } catch (error: any) {
        navigate(`/${error.code}`);
      }
    }

    fetchUser();
  }, [navigate, props.user]);

  async function updateUserData() {
    let params: UpdateUserParams = {
      userId: user.id,
      newUsername: newUsername,
      newPassword: newPassword,
      currentPassword: currentPassword,
    };

    try {
      await updateUser(params);

      setCurrentPassword("");
      setIncorrectCurrentPassword(false);
      setUsernameTaken(false);
      window.localStorage.setItem("username", user.username);
    } catch (error: any) {
      if (error instanceof IncorrectPasswordError)
        setIncorrectCurrentPassword(true);
      else if (error instanceof UsernameTakenError)
        setUsernameTaken(true);
      else navigate(`/${error.code}`);
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
  async function handleUpdate() {
    await updateUserData();
    handleCloseModal();
  }

  return (
    <div className="white-pill mt-5">
      <ModalPage
        buttonName={t("profile.save")}
        show={showModal}
        onClose={handleCloseModal}
        onConfirm={handleUpdate}
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
                  "https://avatars.dicebear.com/api/avataaars/" +
                  user.email +
                  ".svg"
                }
                alt="User profile icon"
                style={{ height: "80px", width: "80px" }}
              />
              <div className="d-flex justify-content-center">
                <p className="h4 text-center">
                  {t("profile.karma")}
                  {user.karma?.karma}
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
                  placeholder={user.username}
                  value={newUsername}
                  onChange={(e) => setNewUsername(e.target.value)}
                />
                {usernameTaken && (
                  <p className="text-warning">
                    {t("error.usernameTaken")}
                  </p>
                )}
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
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                />
                <p className="h6 text-gray">
                  {t("profile.whyCurrentPassword")}
                </p>
                {incorrectCurrentPassword && (
                  <p className="text-warning">
                    {t("error.incorrectCurrentPassword")}
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
