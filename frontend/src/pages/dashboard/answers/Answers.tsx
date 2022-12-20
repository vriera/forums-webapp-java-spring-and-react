import AskQuestionPane from "../../../components/AskQuestionPane";
import Background from "../../../components/Background";
import DashboardAnswersPane from "../../../components/DashboardAnswersPane";
import DashboardPane from "../../../components/DashboardPane";
import { Notification } from "../../../models/UserTypes";
import { User } from "../../../models/UserTypes";

const DashboardAnswersPage = (props: {user: User}) => {

return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane option={"answers"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardAnswersPane />
                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <AskQuestionPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default DashboardAnswersPage;