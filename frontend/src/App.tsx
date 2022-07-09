import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/ask";
import LandingPage from "./pages/landing";
import ProfilePage from "./pages/dashboard/dashboard";
import LoginPage from "./pages/login";

function App(){
    //axios.defaults.baseURL = `${process.env.PUBLIC_URL}/api`

    return (
        <div className="content">
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>} />
                    <Route path="/ask" element={<AskPage/>} />
                    <Route path="/dashboard" element={<ProfilePage/>} />
                    <Route path="/credentials" element={<LoginPage/>} />
                </Routes>
            </Router>
        </div>
    )
}


export default App;

