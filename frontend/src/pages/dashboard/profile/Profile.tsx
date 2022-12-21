import React from "react";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import ProfileInfoPane from "../../../components/ProfileInfoPane";
import { User } from "../../../models/UserTypes";

const DashboardProfilePage = (props: {user: User} )=> {
    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane option={"profile"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {<ProfileInfoPane user={props.user} showUpdateButton={true}/>}
                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default DashboardProfilePage;