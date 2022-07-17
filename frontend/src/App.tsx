import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/askNatu-NoFunciona";
import AskQuestionPage from './pages/question/ask';
import LandingPage from "./pages/landing";
import ProfilePage from "./pages/dashboard/dashboard";
import Navbar from './components/Navbar';
import LoginPage from './pages/Login';
import SigninPage from './pages/Signup';
import SearchPage from './pages/Search';
import CommunityPage from './pages/community/Community';
import CreateCommunityPage from './pages/community/Create';
import UserPage from './pages/User';
import axios from 'axios';


function App(){
    axios.defaults.baseURL = `${process.env.PUBLIC_URL}/api`

    return (
        <div>
            <div className="content">
                <Router basename={`${process.env.PUBLIC_URL}`}>
                    <Navbar/>
                    <Routes>
                        <Route path="/" element={<LandingPage/>} />
                        <Route path="/ask" element={<AskQuestionPage/>} />
                        <Route path="/dashboard" element={<ProfilePage/>} />
                        <Route path="/credentials/login" element={<LoginPage/>} />
                        <Route path="/credentials/signin" element={<SigninPage/>} />
                        <Route path='/search' element={<SearchPage/>} />
                        <Route path='/community' element={<CommunityPage communityName="Sotuyo aprobame"/>} />
                        <Route path='/community/create' element={<CreateCommunityPage/>}/>
                        <Route path='/user' element={<UserPage/>}/>
                    </Routes>
                </Router>
            </div>
        </div>
    )
}


export default App;

