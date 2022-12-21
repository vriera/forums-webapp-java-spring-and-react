import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import {User} from "./models/UserTypes"
import {logout} from "./services/auth"
import {getUserFromApi} from "./services/user"
import {useState , useEffect} from "react";
import AskQuestionPage from './pages/question/ask';
import SelectCommunityPage from './pages/question/ask/selectCommunity';
import WriteQuestionPage from './pages/question/ask/writeQuestion';
import WrapUpPage from './pages/question/ask/wrapUp';

import LandingPage from "./pages/landing";
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
import DashboardQuestionsPage from './pages/dashboard/questions/Questions';
import DashboardAnswersPage from './pages/dashboard/answers/Answers';
import DashboardUpdateProfilePage from './pages/dashboard/profile/Update';
import DashboardProfilePage from './pages/dashboard/profile/Profile';
import AdmittedUsersPage from './pages/dashboard/communities/AdmittedUsers';
import BannedUsersPage from './pages/dashboard/communities/BannedUsers';
import InvitedUsersPage from './pages/dashboard/communities/InvitedUsers';
import InvitedCommunitiesPage from './pages/dashboard/access/InvitedCommunities';
import AdmittedCommunitiesPage from './pages/dashboard/access/AdmittedCommunities';
import RejectedCommunitiesPage from './pages/dashboard/access/RejectedCommunities';
import RequestedCommunitiesPage from './pages/dashboard/access/RequestedCommunities';
import AnswerPage from './pages/question/QuestionAnswers';
import Page404 from './pages/error/404';
import Page401 from './pages/error/401';
import Page403 from './pages/error/403';
import Page500 from './pages/error/500';



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
                        <Route path="/ask/selectCommunity" element={<SelectCommunityPage/>}/>
                        <Route path="/ask/writeQuestion/:communityId" element={<WriteQuestionPage/>}/>
                        <Route path="ask/wrapUp/:questionId" element={<WrapUpPage/>}/>
                        <Route path="/question/:questionId" element={<AnswerPage user={user}/>}/>
                        
                        {/* Dashboard communities */}
                        <Route path="/dashboard/communities/:communityId/admitted" element={<AdmittedUsersPage/>}/>
                        <Route path="/dashboard/communities/:communityId/banned" element={<BannedUsersPage/>}/>
                        <Route path="/dashboard/communities/:communityId/invited" element={<InvitedUsersPage/>}/>

                        {/* Dashboard access */}
                        <Route path="/dashboard/access/admitted" element={<AdmittedCommunitiesPage />}/>
                        <Route path="/dashboard/access/invited" element={<InvitedCommunitiesPage />}/>
                        <Route path="/dashboard/access/rejected" element={<RejectedCommunitiesPage />}/>
                        <Route path="/dashboard/access/requested" element={<RequestedCommunitiesPage/>}/>

                        {/* Dashboard questions */}
                        { <Route path="/dashboard/questions" element={<DashboardQuestionsPage/>}/> }
                        
                        {/* Dashboard answers */}
                        { <Route path="/dashboard/answers" element={<DashboardAnswersPage/>}/>}
                                               
                        {/* Dashboard profile */}
                        { <Route path="/dashboard/profile/update" element={<DashboardUpdateProfilePage user={user}/>}/> }
                        { <Route path="/dashboard/profile/info" element={<DashboardProfilePage user={user}/>}/> }
                        
                        {/* Error pages */}
                        <Route path="*" element={<Page404/>}/>
                        <Route path="/401" element={<Page401/>}/>
                        <Route path="/403" element={<Page403/>}/>
                        <Route path="/500" element={<Page500/>}/>

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

