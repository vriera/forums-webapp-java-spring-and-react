import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/ask";
import LandingPage from "./pages/landing";
import Profile from "./pages/dashboard/profile";


function App(){
    return (
        <div className="content">
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>} />
                    <Route path="/ask" element={<AskPage/>} />
                    <Route path="/dashboard/profile" element={<Profile/>} />
                </Routes>
            </Router>
        </div>
    )
}


export default App;

