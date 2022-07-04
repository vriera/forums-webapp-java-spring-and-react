import React from 'react';

import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/ask";
import LandingPage from "./pages/landing";



function App(){
    return (
        <div className="content">
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>} />
                    <Route path="/ask" element={<AskPage/>} />
                </Routes>
            </Router>
        </div>

    )
}


export default App;

