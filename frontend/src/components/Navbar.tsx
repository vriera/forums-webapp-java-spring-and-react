import React from "react";
import Dropdown from "react-bootstrap/Dropdown";
import { User } from "../models/UserTypes";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { validateLogin } from "../services/auth";

import "../resources/styles/argon-design-system.css";
import "../resources/styles/blk-design-system.css";
import "../resources/styles/general.css";
import "../resources/styles/stepper.css";
import Spinner from "./Spinner";

const Navbar = (props: { user: User; logoutFunction: any }) => {
  const { t } = useTranslation();
  const isLoggedIn = validateLogin();
  return (
    <div>
      <div className="navbar border-bottom">
        <div className="container-fluid navbar-brand">
          <div>
            <div className="d-flex justify-content-end">
              <Link className="navbar-brand" to="/">
                <img
                  src={require("../images/birb.png")}
                  width="30"
                  height="30"
                  alt="AskAway logo"
                />{" "}
                {t("askAway")}
              </Link>

              <div className="nav-item">
                <Link className="nav-link" to="/search/questions">
                  {t("title.viewAllQuestions")}
                </Link>
              </div>
            </div>
          </div>

          {!isLoggedIn && (
            <div className="d-flex justify-content-start">
              <div className="nav-item">
                <Link className="nav-link" to="/credentials/signin">
                  {t("register.register")}
                </Link>
              </div>
              <div className="nav-item">
                <Link className="nav-link" to="/credentials/login">
                  {t("register.login")}
                </Link>
              </div>
            </div>
          )}
          {isLoggedIn && !props.user && <Spinner />}
          {isLoggedIn && props.user && (
            <DropdownButton
              user={props.user}
              logoutFunction={props.logoutFunction}
            />
          )}
        </div>
      </div>
    </div>
  );
};

const DropdownButton = (props: { user: User; logoutFunction: any }) => {
  return (
    <Dropdown className="dropdown ">
      <Dropdown.Toggle className="btn btn-primary pb-0">
        <div className="d-flex">
          {props.user.notifications && props.user.notifications.total > 0 && (
            <>
              <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning py-0 ">
                <div className="text-white h6 mx-1 my-0">
                  {props.user.notifications.total}{" "}
                </div>
              </span>
            </>
          )}
          <div className="dropdown_title row">
            <div className="col-auto">
              <img
                src={
                 "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=" +
                  props.user.email
                }
                className="img"
                alt="profile"
              />
            </div>
            <div className="col-auto">
              <p className="margin-sides-3">
                {props.user.username}{" "}
                {props.user.notifications &&
                  props.user.notifications.total > 0 && (
                    <>
                      <span className="red-pill"> </span>
                    </>
                  )}
              </p>
            </div>
          </div>
        </div>
      </Dropdown.Toggle>

      <Dropdown.Menu
        className="dropdown-menu"
        aria-labelledby="dropdownMenuButton"
      >
        <Link className="dropdown-item" to="/dashboard/profile/info">
          Dashboard
        </Link>
        <button className="dropdown-item" onClick={props.logoutFunction}>
          Logout
        </button>
      </Dropdown.Menu>
    </Dropdown>
  );
};

export default Navbar;
