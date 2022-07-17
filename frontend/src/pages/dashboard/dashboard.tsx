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
import DashboardCommunitiesPane from "../../components/DashboardCommunitiesPane";

import {User, Karma, Notification} from "./../../models/UserTypes"
import {Question} from "./../../models/QuestionTypes"
import {Community} from "./../../models/CommunityTypes"
import { useTranslation } from "react-i18next";
import { Answer } from "../../models/AnswerTypes";



function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
        password: "asereje",
    }
    
    return auxUser
}

async function karmaApiCall(userid: number): Promise<Karma>{
    const response = await fetch(`http://localhost:6900/webapp_war_exploded/karma/${userid}`);
    const karma : Karma = await response.json();
    return Promise.resolve(karma);   
}

async function mockNotificationApiCall(userid: number): Promise<Notification>{
    const response = await fetch(`http://localhost:6900/webapp_war_exploded/notifications/${userid}`);
    const notifications : Notification = await response.json();
    return Promise.resolve(notifications);
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
        password: "tu vieja"
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

//TODO: this page should take the User, Karma and Notification objects for use in the display.
const DashboardPage = () => {
    const { t } = useTranslation();

    let auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
        password: "asereje",
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

    karmaApiCall(30).then((karma) =>{
        console.log(karma);
        auxKarma = karma;
    });
        mockNotificationApiCall(11).then( (notifications) =>{
        console.log(notifications);
        auxNotification = notifications;
    });



    const questions = mockQuestionApiCall()
    const answers = mockAnswerApiCall()
    

    // Switches between viewing the profile or updating it in the 'profile' option
    const [updateProfile, setUpdateProfile] = useState(false)

    function updateProfileCallback(){
        setUpdateProfile(!updateProfile)
    }

    // Selects the correct window as specified by the DashboardPane
    const [option, setOption] = useState('profile')   
    
    function optionCallback(option: "profile" | "questions" | "answers" | "communities"){
        setOption(option)
    }

    function renderCenterCard(){
        if(option == "profile"){
            if(updateProfile == false){
                return <ProfileInfoPane user={auxUser} karma={auxKarma} updateProfileCallback={updateProfileCallback}/>
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
        else if( option == "communities"){
            return <DashboardCommunitiesPane user={auxUser}/>
        }
    }

    function renderRightPane(){
        if(option == "communities"){
            return <CreateCommunityPane/>
        }
        else if(option == "questions" || option == "answers"){
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
                            <DashboardPane user={auxUser} notifications={auxNotification} option={option} optionCallback={optionCallback}/>
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