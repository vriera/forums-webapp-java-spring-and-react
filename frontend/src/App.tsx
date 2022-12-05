import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import {User} from "./models/UserTypes"
import {logout} from "./services/auth"
import {getUserFromApi} from "./services/user"
import {useState , useEffect, useMemo} from "react";
import AskQuestionPage from './pages/question/ask';
import LandingPage from "./pages/landing";
import ProfilePage from "./pages/dashboard/dashboard";
import Navbar from './components/Navbar';
import LoginPage from './pages/Login';
import SigninPage from './pages/Signup';

import QuestionSearchPage from './pages/search/QuestionsSearch';
import CommunitySearchPage from './pages/search/CommunitySearch';
import UserSearchPage from './pages/search/UserSearch';

import CommunityPage from './pages/community/Community';
import CreateCommunityPage from './pages/community/Create';
import UserPage from './pages/User';
import axios from 'axios';
import DashboardCommunitiesPage from './pages/dashboard/communities/Communities';
import DashboardQuestionsPage from './pages/dashboard/questions/Questions';
import DashboardAnswersPage from './pages/dashboard/answers/Answers';
import DashboardUpdateProfilePage from './pages/dashboard/profile/Update';
import DashboardProfilePage from './pages/dashboard/profile/Profile';
import AdmittedUsersPage from './pages/dashboard/communities/AdmittedCommunities';
import Answers from "./pages/dashboard/answers/Answers";
import AnswerPage from "./pages/question/answer";


function App(){
    axios.defaults.baseURL = `${process.env.PUBLIC_URL}/api`
    const [isLoggedIn , setLoggedIn] = useState(  window.localStorage.getItem("token"));
    const [user, setUser] = useState(  null as unknown as User);

    useEffect( () =>{
        if(isLoggedIn){
            getUserFromApi(
                parseInt(new String(window.localStorage.getItem("userId")).toString())
            ).then(
                (user ) => 
                    {   if(user)
                            setUser(user);
                        console.log(user);
                        return;
                    }
            )
        }
    }, [isLoggedIn] );



    function doLogout(){
        logout();
        setUser(null as unknown as User);
        setLoggedIn(null);
    }

    async function doLogin(){
       // await new Promise(r => setTimeout(r, 2000));
        setLoggedIn(window.localStorage.getItem("token"));

    }

    return (
        <div>
            <div className="content">
                <Router basename={`${process.env.PUBLIC_URL}`}>
                    <Navbar user={user} logoutFunction={doLogout}/>
                    <Routes>
                        <Route path="/" element={<LandingPage/>} />
                        <Route path="/ask" element={<AskQuestionPage/>} />
                        <Route path="/answers" element={<AnswerPage/>}/>
                        {/* TODO: Remove */}
                        <Route path="/dashboard" element={<ProfilePage user={user}/>} /> 
                        
                        {/* Dashboard communities */}
                        <Route path="/dashboard/communities/:communityId/admitted" element={<AdmittedUsersPage user={user}/>}/>
                        {/* <Route path="/dashboard/communities/:communityId/invited" element={<DashboardCommunitiesPage user={user}/>}/>
                        <Route path="/dashboard/communities/:communityId/banned" element={<DashboardCommunitiesPage user={user}/>}/> */}
                        
                        {/* Dashboard questions */}
                        { <Route path="/dashboard/questions" element={<DashboardQuestionsPage user={user}/>}/> }
                        
                        {/* Dashboard answers */}
                        { <Route path="/dashboard/answers" element={<DashboardAnswersPage user={user}/>}/>}
                        
                        {/* Dashboard moderated communities */}
                        {/* <Route path="/dashboard/access" element={<DashboardAccessPage user={user}/>}/> */}
                        
                        {/* Dashboard profile */}
                        { <Route path="/dashboard/profile/update" element={<DashboardUpdateProfilePage user={user}/>}/> }
                        { <Route path="/dashboard/profile/info" element={<DashboardProfilePage user={user}/>}/> }
                        
                        <Route path="/credentials/login"  element={<LoginPage doLogin={doLogin} />} />
                        <Route path="/credentials/signin" element={<SigninPage/>} />
                        <Route path='/search/questions' element={<QuestionSearchPage/>} />
                        <Route path='/search/communities' element={<CommunitySearchPage/>} />
                        <Route path='/search/users' element={<UserSearchPage/>} />

                        <Route path='/community/:id' element={<CommunityPage/>} />
                        <Route path='/community/create' element={<CreateCommunityPage/>}/>
                        <Route path='/user' element={<UserPage/>}/>
                    </Routes>
                </Router>
            </div>
        </div>
    )
}


export default App;

