import { useTranslation } from "react-i18next";
import "../resources/styles/argon-design-system.css";
import "../resources/styles/blk-design-system.css";
import "../resources/styles/general.css";
import "../resources/styles/stepper.css";
import { login } from "../services/auth";
import { CreateUserParams, createUser } from "../services/user";
import { Link, useNavigate } from "react-router-dom";
import { Spinner } from "react-bootstrap";
import Background from "../components/Background";
import { useState } from "react";
import {
  BadRequestError,
  EmailTakenError,
  InvalidEmailError,
  UsernameTakenError,
} from "../models/HttpTypes";

const SignupPage = (props: { doLogin: any }) => {
  const { t } = useTranslation();

  const [email, setEmail] = useState("");

  const [name, setName] = useState("");

  const [password, setPassword] = useState("");

  const [repeatPassword, setRepeatPassword] = useState("");

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const navigate = useNavigate();

  async function doSignupAndLogin(
    email: string,
    username: string,
    password: string,
    repeatPassword: string
  ) {
    // Show spinner and reset errors
    setLoading(true);
    setError(false);

    if (password !== repeatPassword) {
      setError(true);
      setErrorMessage(t("error.passwordsDoNotMatch"))
      setLoading(false);
      return;
    }

    if(!email || !username || !password || !repeatPassword) {
      setError(true);
      setErrorMessage(t("error.emptyFields"))
      setLoading(false);
      return;
    }
      

    try {
      const createUserParams: CreateUserParams = {
        email: email,
        username: username,
        password: password,
      };
      await createUser(createUserParams);

      // doLogin is used to load the user data in the navbar
      await login(email, password).then(() => props.doLogin());

      navigate("/");
    } catch (error: any) {
      setError(true);
      if (error instanceof EmailTakenError) setErrorMessage(t("error.emailTaken"))
      else if (error instanceof UsernameTakenError) setErrorMessage(t("error.usernameTaken"))
      else if (error instanceof BadRequestError) setErrorMessage(t("error.genericSignupError"))
      else if (error instanceof InvalidEmailError) setErrorMessage(t("error.invalidEmail"))
      else navigate(`/${error.code}`);

      // Hide spinner
      setLoading(false);
    }
  }

  return (
    <div className="section section-hero section-shaped">
      <Background />
      <div className="container">
        <div className="white-pill">
          <div className="d-flex justify-content-center">
            <div className="h1 text-primary">{t("register.register")}</div>
          </div>
          <hr />

          <div>
            {/* <%--Email--%> */}
            <div className="form-group mt-3">
              <p className="text-black">{t("email")}</p>
              <input
                className="form-control"
                type="email"
                id="email"
                value={email}
                placeholder={t("placeholder.email")}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          {/* <%--Username--%> */}
          <div className="form-group mt-3">
            <p className="text-black">{t("username")}</p>
            <input
              className="form-control"
              type="text"
              id="username"
              value={name}
              placeholder={t("placeholder.username")}
              onChange={(e) => setName(e.target.value)}
            />
            {/* Make it show an error if it is empty */}
          </div>

          {/* <%--Password--%> */}
          <div className="form-group mt-3">
            <p className="text-black">{t("password.message")}</p>
            <input
              className="form-control"
              type="password"
              id="password"
              value={password}
              placeholder={t("placeholder.password")}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {/* <%--Repeat password--%> */}
          <div className="form-group mt-3">
            <p className="text-black">{t("password.repeat")}</p>
            <input
              className="form-control"
              type="password"
              id="repeatPassword"
              value={repeatPassword}
              placeholder={t("password.repeat")}
              onChange={(e) => setRepeatPassword(e.target.value)}
            />
          </div>

          {/* <%--Already have an account? Sign in--%> */}
          <div className="form-group mt-3">
            <p className="text-black">
              {t("register.question")}
              <Link to="/credentials/login" className="text-primary">
                {" "}
                {t("register.login")}
              </Link>
            </p>
          </div>

          {/* <%--Sign in button--%> */}
          <div className="form-group mt-3 d-flex justify-content-center">
            <button className="btn btn-light" type="submit">
              {t("back")}
            </button>
            <button
              onClick={() =>
                doSignupAndLogin(email, name, password, repeatPassword)
              }
              className="btn btn-primary"
              type="submit"
            >
              {t("register.register")}
            </button>
            {loading && <Spinner />}
          </div>

          {/* ERRORS */}
          {error && (
            <div className="d-flex justify-content-center">
              <p className="text-warning">{errorMessage}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SignupPage;
