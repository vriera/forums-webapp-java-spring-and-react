import React from 'react';
import Background from '../../../components/Background';
import DashboardPane from '../../../components/DashboardPane';
import UpdateProfilePage from '../../../components/UpdateProfilePane';
import { Karma, User, Notification } from '../../../models/UserTypes';

const DashboardUpdateProfilePage = (props : {user: User}) => {

    let auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
    }

    let auxKarma : Karma = {
        karma: 420
    }

    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }

    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane user={props.user} option={"profile"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {<UpdateProfilePage user={auxUser}/>}
                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardUpdateProfilePage;