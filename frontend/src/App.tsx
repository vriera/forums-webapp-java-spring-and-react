import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/ask";
import LandingPage from "./pages/landing";
import ProfilePage from "./pages/dashboard/dashboard";
import Navbar from './components/Navbar';
import LoginPage from './pages/Login';
import SigninPage from './pages/Signup';
import SearchPage from './pages/Search';

function App(){
    //axios.defaults.baseURL = `${process.env.PUBLIC_URL}/api`

    return (
        <div>
            <Navbar/>
        
            <div className="content">
                <Router>
                    <Routes>
                        <Route path="/" element={<LandingPage/>} />
                        <Route path="/ask" element={<AskPage/>} />
                        <Route path="/dashboard" element={<ProfilePage/>} />
                        <Route path="/credentials/login" element={<LoginPage/>} />
                        <Route path="/credentials/signin" element={<SigninPage/>} />
                        <Route path='/search' element={<SearchPage/>} />
                    </Routes>
                </Router>
            </div>
        </div>
    )
}


export default App;

