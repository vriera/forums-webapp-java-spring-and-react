import React from "react";
import { useTranslation } from "react-i18next";
import "../resources/styles/argon-design-system.css";
import "../resources/styles/blk-design-system.css";
import "../resources/styles/general.css";
import "../resources/styles/stepper.css";
import Background from "../components/Background";
import { login } from "../services/auth";
import { Link, useNavigate } from "react-router-dom";
import Spinner from "../components/Spinner";
import { IncorrectPasswordError } from "../models/HttpTypes";
import { createBrowserHistory } from "history";

const LoginPage = (props: { doLogin: any }) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const history = createBrowserHistory();

  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState(false);

  async function loginWithCredentials(email: string, password: string) {
    try {
      setLoading(true);
      setError(false);

      const user = await login(email, password);
      props.doLogin(user); //FIXME: Cargarlo en un context?

      navigate("/");
    } catch (error: any) {
      if (error instanceof IncorrectPasswordError) {
        setError(true);
        setLoading(false);
      }
      else {
        navigate(`/${error.code}`)
      }
    }
  }

  const handleBackButtonClick = () => {
    history.back();
  };

  return (
    <div className="section section-hero section-shaped">
      <Background />

      <div className="container">
        <div className="white-pill">
          <div className="d-flex justify-content-center">
            <div className="h1 text-primary">{t("logIn")}</div>
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

            {/* <%--Dont have an account? Sign up--%> */}
            <div className="form-group mt-3">
              <div>
                <p className="text-black">
                  {t("withoutAccount")}
                  <Link className="text-primary" to="/credentials/signin">
                    {" "}
                    {t("register.register")}
                  </Link>
                </p>
              </div>
            </div>

            {/* <%--Submit--%> */}
            <div className="form-group mt-3 d-flex justify-content-center">
              <button className="btn btn-light" type="submit" onClick={handleBackButtonClick}>
                {t("back")}
              </button>
              <button
                onClick={async () => await loginWithCredentials(email, password)}
                className={"btn btn-primary " + (loading && "disabled")}
                type="submit"
              >
                {t("logIn")}
              </button>
              {loading && <Spinner />}
            </div>

            {error && (
              <div className="d-flex justify-content-center">
                <p className="text-warning">{t("error.invalidLogin")}</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
