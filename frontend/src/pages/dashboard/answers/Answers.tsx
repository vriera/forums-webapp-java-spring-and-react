import AskQuestionPane from "../../../components/AskQuestionPane";
import Background from "../../../components/Background";
import DashboardAnswersPane from "../../../components/DashboardAnswersPane";
import DashboardPane from "../../../components/DashboardPane";
import { Answer } from "../../../models/AnswerTypes";
import { Community } from "../../../models/CommunityTypes";
import { Question } from "../../../models/QuestionTypes";
import { Notification } from "../../../models/UserTypes";
import { User } from "../../../models/UserTypes";

const DashboardAnswersPage = (props: {user: User}) => {

function mockAnswerApiCall(){
    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
    }
    let community: Community = {
        id: 1,
        name: "Filosofía",
        description: "Para filosofar",
        moderator: user,
        userCount: 2,
        notifications: {
            requests: 1,
            invites: 2,
            total: 3
        },
    }
    let question: Question = {
        id: 1,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: community,
        votes: 1,
    }
    let answer: Answer = {
        id: 1,
        title: "Title",
        body: "Body",
        owner: user,
        verify:false,
        question:question,
        myVote:true,
        url:"string",
        time:"11pm",
        date: "1/12/2021",
        votes: 1,
    }
    return [answer]
} 
let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }

let answers = mockAnswerApiCall();
return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane user={props.user} notifications={auxNotification} option={"answers"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardAnswersPane answers={answers} user={props.user} page={1} totalPages={5}/>
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