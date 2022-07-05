import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/ask";
import LandingPage from "./pages/landing";
import ProfilePage from "./pages/dashboard/user/profile";
import UpdateProfilePage from "./pages/dashboard/user/updateProfile"


function App(){
    return (
        <div className="content">
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>} />
                    <Route path="/ask" element={<AskPage/>} />
                    <Route path="/dashboard/profile" element={<ProfilePage/>} />
                    <Route path="/dashboard/user/updateProfile" element={<UpdateProfilePage/>} />

                </Routes>
            </Router>
        </div>
    )
}


export default App;

