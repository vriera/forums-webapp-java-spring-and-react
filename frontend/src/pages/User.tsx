import React from "react";
import {useState} from "react";
import Background from "./../components/Background";
import DashboardPane from "./../components/DashboardPane"
import AskQuestionPane from "./../components/AskQuestionPane"
import CreateCommunityPane from "./../components/CreateCommunityPane";
import ProfileInfoPane from "../components/ProfileInfoPane";
import UpdateProfilePage from "../components/UpdateProfilePane";
import DashboardQuestionPane from "../components/DashboardQuestionPane";
import DashboardAnswersPane from "../components/DashboardAnswersPane";
import DashboardCommunitiesPane from "../components/DashboardCommunitiesPane";

import {User, Karma} from "./../models/UserTypes"
import {Question} from "./../models/QuestionTypes"
import {Community} from "./../models/CommunityTypes"
import { useTranslation } from "react-i18next";
import { Answer } from "../models/AnswerTypes";
import { Link } from "react-router-dom";
import { NONAME } from "dns";


const community: Community = {
    id: 1,
    name: "Community 1",
    description: "This is the first community",
    moderator: {
        id: 1,
        username: "User 1",
        email: "use1@gmail.com",
    },
    notifications: {
        requests: 1,
        invites: 2,
        total: 3,
    },
    userCount: 5
}


const ReducedDashboardPane = (props: {option: string, user: User, optionCallback: (option: "profile" |  "communities") => void }) => {
    const { t } = useTranslation();
    return(
        <div className="white-pill d-flex flex-column mt-5" >
            {/* INFORMACION DE USUARIO*/}
            <div className="d-flex justify-content-center">
                <p className="h1 text-primary">{props.user.username}</p>
            </div>
            <div className="d-flex justify-content-center">
                <p>{t("emailEquals")}</p>
                <p>{props.user.email}</p>
            </div>
            {/* DASHBOARD - OPCIONES VERTICALES */}
            <ul className="nav nav-pills flex-column mb-auto">

                <li>
                    <button onClick={() => props.optionCallback("profile")} className={"h5 nav-link link-dark w-100 " + (props.option === "profile" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.myProfile")}
                    </button>
                </li>

                <li>
                    <button onClick={() => props.optionCallback("communities")} className={"h5 nav-link link-dark w-100 " + (props.option === "communities" && "active")}>
                        <i className="fas fa-users mr-3"></i>
                        {t("dashboard.communities")}                      
                    </button>
                </li>
            </ul>
        </div>
    )
}



const ModeratedCommunitiesPane = (props: {communities: Community[]}) => {
    const { t } = useTranslation();
    return(
        <>
            <div className="white-pill mt-5">
                <div className="card-body overflow-hidden">
                    <p className="h3 text-primary text-center">{t("community.communities")}</p>
                    <hr className="my-2"/>

                    {props.communities.length == 0 && 
                        <div className="d-flex justify-content-center">
                            <img className="row w-25 h-25" src="/resources/images/empty.png" alt="No hay nada para mostrar"/>
                        </div>
                    }
                        

                    <div className="overflow-auto">
                        {props.communities.map((community: Community) =>
                        
                            <Link className="d-block" to="/"> {/* TODO: cambiar este to cualquiera al path que debe ser */}
                                <div className="card p-3 m-3 shadow-sm--hover ">
                                    <div className="row">
                                        <div className="d-flex flex-column justify-content-start ml-3">

                                            <div className="d-flex h2 text-primary ml-0">
                                                <i className="fas fa-cogs mr-1 "></i>
                                                <p className="h3 text-primary">{community.name}</p>
                                            </div>
                                        </div>
                                        <div className="col-12 text-wrap-ellipsis">
                                            <p className="h5 ml-3">{community.description}</p>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                    
                        )}
                            
                        
                    </div>
                </div>
            </div>
        </>

    )
}




function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
    }
    
    return auxUser
}



async function karmaApiCall(userid: number): Promise<Karma>{
    const response = await fetch(`http://localhost:6900/webapp_war_exploded/karma/${userid}`);
    const karma : Karma = await response.json();
    return Promise.resolve(karma);   
}


//TODO: this page should take the User, Karma and Notification objects for use in the display.
const UserPage = () => {
    const { t } = useTranslation();

    let auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
    }

    let auxKarma : Karma = {
        karma: 420
    }


    karmaApiCall(30).then((karma) =>{
        console.log(karma);
        auxKarma = karma;
    });
    

    // Switches between viewing the profile or updating it in the 'profile' option
    const [updateProfile, setUpdateProfile] = useState(false)

    function updateProfileCallback(){
        setUpdateProfile(!updateProfile)
    }

    // Selects the correct window as specified by the DashboardPane
    const [option, setOption] = useState('profile')   
    
    function optionCallback(option: "profile" | "communities"){
        setOption(option)
    }

    function renderCenterCard(){
        if(option == "profile"){
            if(updateProfile == false){
                return <ProfileInfoPane user={auxUser} karma={auxKarma} updateProfileCallback={updateProfileCallback} showUpdateButton={false}/>
            }
        }

        else if( option == "communities"){
            return <ModeratedCommunitiesPane communities={[community]}/> 
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
                            <ReducedDashboardPane user={auxUser} option={option} optionCallback={optionCallback}/>
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {renderCenterCard()}
                        </div> 

                        {/* ASK QUESTION: No longer here due to previous corrections */}
                        <div className="col-3">
                            
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserPage;