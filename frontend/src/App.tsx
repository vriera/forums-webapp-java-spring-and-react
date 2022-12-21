import React from 'react';
import { BrowserRouter as Router, Route, Routes ,  Navigate  } from "react-router-dom";
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
import UserProfilePage from './pages/user/Profile';
import UserCommunitiesPage from './pages/user/Communities';
import RequestedUsersPage from './pages/dashboard/communities/RequestedUsers';


 const ProtectedRoute= (props: { user : any , children : any }) =>{
    if(!props.user)
        return <Navigate to="/credentials/login" replace/>;
    
    return props.children;
 };
 const NotIfLogged= (props: { user : any , children : any }) =>{
    if(props.user)
        return <Navigate to="/" replace/>;
    
    return props.children;
 };
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
                        <Route path="/ask" element={
                        <ProtectedRoute user={user}>
                            <AskQuestionPage/>
                        </ProtectedRoute>} />
                        <Route path="/ask/selectCommunity" element={
                            <ProtectedRoute user={user}>
                                <SelectCommunityPage/>
                            </ProtectedRoute>}/>
                        <Route path="/ask/writeQuestion/:communityId" element={
                         <ProtectedRoute user={user}>
                            <WriteQuestionPage/>
                        </ProtectedRoute>}/>
                        <Route path="ask/wrapUp/:questionId" element={
                        <ProtectedRoute user={user}>
                            <WrapUpPage/>
                        </ProtectedRoute>}/>
                        <Route path="/questions/:questionId" element={
                        <ProtectedRoute user={user}>
                            <AnswerPage user={user}/>
                        </ProtectedRoute>}/>
                        
                        {/* Dashboard communities */}
                        <Route path="/dashboard/communities/:communityId/admitted" element={
                        <ProtectedRoute user={user}>
                            <AdmittedUsersPage/>
                        </ProtectedRoute>}/>
                        <Route path="/dashboard/communities/:communityId/banned" element={
                            <ProtectedRoute user={user}>
                                <BannedUsersPage/>
                            </ProtectedRoute>}/>
                        
                        <Route path="/dashboard/communities/:communityId/invited" element={
                        <ProtectedRoute user={user}>
                           <InvitedUsersPage/>
                       </ProtectedRoute>}/>
                
                        <Route path="/dashboard/communities/:communityId/requested" element={ 
                        <ProtectedRoute user={user}>
                            <RequestedUsersPage/>
                       </ProtectedRoute>}/>


                        {/* Dashboard access */}
                        <Route path="/dashboard/access/admitted" element={
                         <ProtectedRoute user={user}>
                            <AdmittedCommunitiesPage />
                            </ProtectedRoute>}/>
                        <Route path="/dashboard/access/invited" element={ 
                            <ProtectedRoute user={user}>
                                    <InvitedCommunitiesPage />
                            </ProtectedRoute>}/>

                        <Route path="/dashboard/access/rejected" element={
                        <ProtectedRoute user={user}>
                            <RejectedCommunitiesPage />
                        </ProtectedRoute>}/>
                        
                        <Route path="/dashboard/access/requested" element={ 
                        <ProtectedRoute user={user}>
                            <RequestedCommunitiesPage/>
                        </ProtectedRoute>}/>

                        {/* Dashboard questions */}
                        { <Route path="/dashboard/questions" element={
                            <ProtectedRoute user={user}>
                                <DashboardQuestionsPage/>
                            </ProtectedRoute>}/>
                        } 
                        
                        {/* Dashboard answers */}
                        { <Route path="/dashboard/answers" element={
                        <ProtectedRoute user={user}>
                            <DashboardAnswersPage/>
                        </ProtectedRoute>}/>}
                                               
                        {/* Dashboard profile */}
                        { <Route path="/dashboard/profile/update" element={
                        <ProtectedRoute user={user}>
                            <DashboardUpdateProfilePage user={user}/>
                        </ProtectedRoute>}/>}
                        { <Route path="/dashboard/profile/info" element={
                          <ProtectedRoute user={user}>
                            <DashboardProfilePage user={user}/>
                            </ProtectedRoute>}/>}
                        
                       
                        
                        {/* Error pages */}
                        <Route path="*" element={<Page404/>}/>
                        <Route path="/401" element={<Page401/>}/>
                        <Route path="/403" element={<Page403/>}/>
                        <Route path="/500" element={<Page500/>}/>

                        <Route path="/credentials/login"  element={
                        <NotIfLogged user={user}>
                            <LoginPage doLogin={doLogin} />
                        </NotIfLogged> } />
                        <Route path="/credentials/signin" element={
                        <NotIfLogged user={user}>
                            <SigninPage doLogin={doLogin}/>
                        </NotIfLogged>} />
                        <Route path='/search/questions' element={<QuestionSearchPage/>} />
                        <Route path='/search/communities' element={<CommunitySearchPage/>} />
                        <Route path='/search/users' element={<UserSearchPage/>} />

                        <Route path='/community/:communityId' element={<CommunityPage/>} />
                        <Route path='/community/create' element={
                         <ProtectedRoute user={user}>
                            <CreateCommunityPage/>
                         </ProtectedRoute >
                        }/>
                        

                        <Route path='/user/:userId/profile' element={
                          <UserProfilePage/>
                       }/>
                            
                        <Route path='/user/:userId/communities' element={
                              <UserCommunitiesPage/>
                        }/>
                    </Routes>
                </Router>
            </div>
        </div>
    )
}


export default App;

