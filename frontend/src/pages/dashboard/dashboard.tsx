import {useState} from "react";
import Background from "./../../components/Background";
import DashboardPane from "./../../components/DashboardPane"
import AskQuestionPane from "./../../components/AskQuestionPane"
import CreateCommunityPane from "./../../components/CreateCommunityPane";
import ProfileInfoPane from "../../components/ProfileInfoPane";
import UpdateProfilePage from "../../components/UpdateProfilePane";
import DashboardQuestionPane from "../../components/DashboardQuestionPane";
import {User, Karma, Notification} from "./../../models/UserTypes"
import {Question} from "./../../models/QuestionTypes"
import {Community} from "./../../models/CommunityTypes"
import { useTranslation } from "react-i18next";



function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
        password: "asereje",
    }
    
    return auxUser
}

function mockKarmaApiCall(user: User): Karma{
    var auxKarma : Karma = {
        user: user,
        karma: 420
    }
    return auxKarma
}

function mockNotificationApiCall(user: User): Notification{
    var auxNotification: Notification = {
        user: user,
        requests: 1,
        invites: 2,
        total: 3
    }
    return auxNotification
}

function mockQuestionApiCall(){
    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
        password: "tu vieja"
    }
    let community: Community = {
        id: 1,
        name: "Filosofía",
        description: "Para filosofar",
        moderator: user,
        userCount: 2,
        notificationTotal: 0
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

//TODO: this page should take the User, Karma and Notification objects for use in the display.
const DashboardPage = () => {
    const { t } = useTranslation();
    const user: User = mockUserApiCall()
    const karma: Karma = mockKarmaApiCall(user)
    const notifications: Notification = mockNotificationApiCall(user)
    const questions = mockQuestionApiCall()
    

    // Switches between viewing the profile or updating it in the 'profile' option
    const [updateProfile, setUpdateProfile] = useState(false)

    function updateProfileCallback(){
        setUpdateProfile(!updateProfile)
    }

    // Selects the correct window as specified by the DashboardPane
    const [option, setOption] = useState('profile')    
    
    function profileCallback(){
        setOption("profile")
    }

    function questionsCallback(){
        setOption("questions")
    }

    function answersCallback(){
        setOption("answers")
    }

    function communitiesCallback(){
        setOption("communities")
    }

    let optionCallbacks = {
        profileCallback,
        questionsCallback,
        answersCallback,
        communitiesCallback
    }

    function renderCenterCard(updateProfile: boolean){
        if(option == "profile"){
            if(updateProfile == false){
                return <ProfileInfoPane user={user} karma={karma} updateProfileCallback={updateProfileCallback}/>
            }
            else{
                return <UpdateProfilePage user={user} updateProfileCallback={updateProfileCallback}/>
            }
        }
        else if(option == "questions"){
            return <DashboardQuestionPane questions={questions}/>
        }
    }

    function renderRightPane(){
        if(option == "communities"){
            return <CreateCommunityPane/>
        }
        else{
            return <AskQuestionPane/>
        }
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
                            <DashboardPane user={user} notifications={notifications} option={option} optionCallbacks={optionCallbacks}/>
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {renderCenterCard(updateProfile)}
                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            {renderRightPane()}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardPage;