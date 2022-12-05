import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessPane from "../../../components/DashboardAccessPane";
import DashboardPane from "../../../components/DashboardPane";
import { User, Notification } from "../../../models/UserTypes";

const DashboardAccessPage = (props: {user: User}) => {

    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }
        let auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
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
                            <DashboardPane user={props.user} notifications={auxNotification} option={"access"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardAccessPane user={auxUser}/>

                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <CreateCommunityPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}