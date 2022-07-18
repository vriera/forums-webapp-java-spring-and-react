import React from "react";
import {useState} from "react";
import Background from "./../../components/Background";
import DashboardPane from "./../../components/DashboardPane"
import AskQuestionPane from "./../../components/AskQuestionPane"
import CreateCommunityPane from "./../../components/CreateCommunityPane";
import ProfileInfoPane from "../../components/ProfileInfoPane";
import UpdateProfilePage from "../../components/UpdateProfilePane";
import DashboardQuestionPane from "../../components/DashboardQuestionPane";
import DashboardAnswersPane from "../../components/DashboardAnswersPane";
import DashboardAccessPane from "../../components/DashboardAccessPane";
import DashboardCommunitiesPane from "../../components/DashboardCommunitiesPane";
import CommunitiesCard from "../../components/CommunitiesCard";
import {User, Karma, Notification} from "./../../models/UserTypes"
import {Question} from "./../../models/QuestionTypes"
import {Community} from "./../../models/CommunityTypes"
import { useTranslation } from "react-i18next";
import { Answer } from "../../models/AnswerTypes";



/* function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
    }
    
    return auxUser
} */



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
        voteTotal: 1,
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
        voteTotal: 1,
    }
    return [answer, answer, answer]
}

const fakeCommunities = ["FakeCommunity1", "FakeCommunity2", "FakeCommunity3"]

//TODO: this page should take the User, Karma and Notification objects for use in the display.
const DashboardPage = (props:{user: User}) => {
    const { t } = useTranslation();

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

    /* userApiCall(5).then((user) =>{
        console.log(user);
        auxUser= user;
    }); */




    const questions = mockQuestionApiCall()
    const answers = mockAnswerApiCall()
    

    // Switches between viewing the profile or updating it in the 'profile' option
    const [updateProfile, setUpdateProfile] = useState(false)

    function updateProfileCallback(){
        setUpdateProfile(!updateProfile)
    }

    // Selects the correct window as specified by the DashboardPane
    const [option, setOption] = useState('profile')   
    
    function optionCallback(option: "profile" | "questions" | "answers" | "communities" | "access"){
        setOption(option)
    }

    function renderCenterCard(){
        if(option == "profile"){
            if(updateProfile == false){
                return <ProfileInfoPane user={props.user} karma={auxKarma} updateProfileCallback={updateProfileCallback} showUpdateButton={true}/>
            }
            else{
                return <UpdateProfilePage user={auxUser} updateProfileCallback={updateProfileCallback}/>
            }
        }
        else if(option == "questions"){
            return <DashboardQuestionPane questions={questions} page={1} totalPages={5}/>
        }
        else if (option == "answers"){
            return <DashboardAnswersPane answers={answers} page={1} totalPages={5}/>
        }
        else if( option == "access"){
            return <DashboardAccessPane user={auxUser}/>
        }
        else if (option == "communities"){
            return <DashboardCommunitiesPane communityName="Placeholder Name" communityDescription="Hi! I'm a placeholder community description, this should be changed from dashboard: renderCenterCard() to receive actual information"/>
        }
    }

    function renderRightPane(){
        if(option == "access"){
            return <CreateCommunityPane/>
        }
        else if(option == "questions" || option == "answers"){
            return <AskQuestionPane/>
        }
        else if(option == "communities"){
            return <CommunitiesCard title={t("dashboard.Modcommunities")} communities={fakeCommunities} thisCommunity={"FakeCommunist"}/>
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
                            <DashboardPane user={props.user} notifications={auxNotification} option={option} optionCallback={optionCallback}/>
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {renderCenterCard()}
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