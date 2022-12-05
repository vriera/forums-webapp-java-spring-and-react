import AskQuestionPane from "../../../components/AskQuestionPane";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import DashboardQuestionPane from "../../../components/DashboardQuestionPane";
import { Community } from "../../../models/CommunityTypes";
import { Question } from "../../../models/QuestionTypes";
import { User } from "../../../models/UserTypes";
import { Notification } from "../../../models/UserTypes";

function mockQuestionApiCall(){
    
    //Levanto mi user => tengo el id y el uri del karma
    //Levanto el karma
    //Levanto las notificaciones
    
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
        }
    }

    let question: Question = {
        id: 1,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: community,
        voteTotal: 1,
    }
    let question2: Question = {
        id: 2,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: community,
        voteTotal: 0,
        myVote: true,
    }
    let question3: Question = {
        id: 3,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: community,
        voteTotal: -1,
        myVote: false
    }
    return [question, question2, question3]
}


    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }
const DashboardQuestionsPage = (props: {user: User}) => {
    let questions = mockQuestionApiCall();
    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane user={props.user} notifications={auxNotification} option={"questions"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <DashboardQuestionPane questions={questions} page={1} totalPages={5}/>

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

export default DashboardQuestionsPage;
