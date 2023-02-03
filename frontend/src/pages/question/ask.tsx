import React from "react";
import { useTranslation } from "react-i18next";
import "../../resources/styles/argon-design-system.css";
import "../../resources/styles/blk-design-system.css";
import "../../resources/styles/general.css";
import "../../resources/styles/stepper.css";

import Background from "../../components/Background";

import { t } from "i18next";
import { Link } from "react-router-dom";
import internal from "stream";
import { Community } from "../../models/CommunityTypes";
import { Button } from "react-bootstrap";

const community1: Community = {
  id: 1,
  name: "Community 1",
  description: "This is the first community",
  moderator: {
    id: 1,
    username: "User 1",
    email: "use1@gmail.com",
  },
  notifications: {
    requests: 1,
    invites: 2,
    total: 3,
  },
  userCount: 5,
};
const community2: Community = {
  id: 1,
  name: "Community 2",
  description: "This is the first community",
  moderator: {
    id: 1,
    username: "User 1",
    email: "use1@gmail.com",
  },
  notifications: {
    requests: 1,
    invites: 2,
    total: 3,
  },
  userCount: 5,
};

const AskQuestionMainCard = (props: {
  title: string;
  currentProgress: number;
  incrementProgress: any;
  decrementProgress: any;
}) => {
  const { t } = useTranslation();
  return (
    <div className="container">
      <div className="white-pill">
        <div className="d-flex justify-content-center">
          <p className="h1 text-primary text-center">
            {t("title.askQuestion")}
          </p>
        </div>
        <hr />
        {/* CONTENT DEPENDING ON PROGRESS */}
        {/* If currentProgress is 1 show selectCommunity */}
        {props.currentProgress === 1 && (
          <SelectCommunity
            communityList={[community1, community2]}
            incrementProgress={props.incrementProgress}
          />
        )}
        {/* If currentProgress is 2 show askQuestionContent */}
        {props.currentProgress === 2 && (
          <AskQuestionContent
            incrementProgress={props.incrementProgress}
            decrementProgress={props.decrementProgress}
          />
        )}
        {/* If currentProgress is 3 show wrapup */}
        {props.currentProgress === 3 && (
          <WrapUp wasOperationSuccessful={false} />
        )}

        <hr />
        {/* STEPPER */}
        <div className="stepper-wrapper">
          {/* Classname should be active if currentProgress is 1 */}
          <div
            className={
              "stepper-item " +
              (props.currentProgress == 1 && " active ") +
              (props.currentProgress > 1 && " completed")
            }
          >
            <div className="step-counter">1</div>
            <div className="step-name">{t("question.community")}</div>
          </div>
          <div
            className={
              "stepper-item " +
              (props.currentProgress === 2 && " active ") +
              (props.currentProgress > 2 && " completed")
            }
          >
            <div className="step-counter">2</div>
            <div className="step-name">{t("question.content")}</div>
          </div>
          <div
            className={
              "stepper-item " + (props.currentProgress === 3 && " active")
            }
          >
            <div className="step-counter">3</div>
            <div className="step-name">{t("question.wrapup.message")}</div>
          </div>
        </div>
      </div>
    </div>
  );
};

const SelectCommunity = (props: {
  communityList: Community[];
  incrementProgress: any;
}) => {
  const { t } = useTranslation();
  return (
    <>
      <p className="h5 text-black">
        {t("question.chooseCommunityCallToAction")}
      </p>
      <div className="container">
        {props.communityList.map((community) => (
          <button
            onClick={() => props.incrementProgress()}
            className="btn btn-outline-primary badge-pill badge-lg my-3"
          >
            {community.name}
          </button>
        ))}
      </div>
    </>
  );
};

const AskQuestionContent = (props: {
  incrementProgress: any;
  decrementProgress: any;
}) => {
  const { t } = useTranslation();
  return (
    <>
      <p className="h5 text-black">{t("question.contentCallToAction")}</p>
      <form>
        <label className="h5 text-black mt-3">{t("title.message")}</label>
        <input
          className="form-control"
          placeholder={t("placeholder.questionTitle")}
          id="title"
        />
      </form>

      <form>
        <label className="h5 text-black mt-3">{t("body")}</label>
        <input
          className="form-control"
          placeholder={t("placeholder.questionBody")}
          id="title"
        />
      </form>

      <form className="form-group">
        <label className="h5 text-black mt-3">{t("general.label.image")}</label>
        <input
          name="image"
          className="form-control"
          type="file"
          accept="image/png, image/jpeg"
        />
      </form>

      <div className="d-flex justify-content-center">
        <button
          onClick={() => props.decrementProgress()}
          className="btn btn-light align-self-start"
        >
          {t("profile.back")}
        </button>
        <button
          onClick={() => props.incrementProgress()}
          className="btn btn-primary mb-3"
          type="submit"
        >
          {t("button.continue")}
        </button>
      </div>
    </>
  );
};

const WrapUp = (props: { wasOperationSuccessful: boolean }) => {
  const { t } = useTranslation();
  return (
    <>
      {props.wasOperationSuccessful === true && (
        <div>
          <div className="row">
            <div className="d-flex justify-content-center">
              <div className="h1 text-success">
                {t("question.wrapUpSuccess")}
              </div>
            </div>
          </div>
          <hr />
          <div className="row d-flex justify-content-center mb-5">
            <img
              className="w-25 h-25"
              src={require("../../images/success.png")}
              alt="ÉXITO"
            />
          </div>

          {/* Buttons */}
          <div className="d-flex justify-content-center">
            <Link to="/" className="btn btn-light">
              {t("question.wrapup.return")}
            </Link>
            <Link
              to="/question/view/${question.id}"
              className="btn btn-primary"
            >
              {t("question.wrapup.seeQuestion")}
            </Link>
          </div>
        </div>
      )}

      {props.wasOperationSuccessful === false && (
        <>
          <div className="d-flex justify-content-center">
            <p className="h1 text-danger">{t("question.wrapup.error")}</p>
          </div>
          <hr />
          <div className="row d-flex justify-content-center mb-5">
            <img
              className="w-25 h-25"
              src={require("../../images/error.png")}
              alt="ERROR"
            />
          </div>

          {/* Buttons */}
          <div className="d-flex justify-content-center">
            <Link to="/" className="btn btn-light">
              {t("question.wrapup.return")}
            </Link>
            <Link to="/search" className="btn btn-primary">
              {t("question.wrapup.seeAllQuestions")}
            </Link>
          </div>
        </>
      )}
    </>
  );
};

const AskQuestionPage = () => {
  const { t } = useTranslation();
  const [progress, setProgress] = React.useState(1);

  function incrementProgress() {
    setProgress((previousProgress) => previousProgress + 1);
  }

  function decrementProgress() {
    setProgress((previousProgress) => previousProgress - 1);
  }

  return (
    <div className="section section-hero section-shaped">
      <Background />
      <AskQuestionMainCard
        title={t("community.select")}
        currentProgress={progress}
        incrementProgress={incrementProgress}
        decrementProgress={decrementProgress}
      />
    </div>
  );
};

export default AskQuestionPage;
