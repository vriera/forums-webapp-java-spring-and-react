import React from "react";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import UpdateProfilePage from "../../../components/UpdateProfilePane";
import { User } from "../../../models/UserTypes";

const DashboardUpdateProfilePage = (props: { user: User }) => {
  return (
    <div>
      {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
      <div className="wrapper">
        <div className="section section-hero section-shaped pt-3">
          <Background />

          <div className="row">
            {/* COMMUNITIES SIDE PANE*/}
            <div className="col-3">
              <DashboardPane option={"profile"} />
            </div>

            {/* CENTER PANE*/}
            <div className="col-6">
              {<UpdateProfilePage user={props.user} />}
            </div>

            {/* ASK QUESTION */}
            <div className="col-3"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardUpdateProfilePage;
