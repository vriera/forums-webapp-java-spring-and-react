import React from 'react';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AskPage from "./pages/question/askNatu-NoFunciona";
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
import SearchPage from './pages/Search';
import CommunityPage from './pages/community/Community';
import CreateCommunityPage from './pages/community/Create';
import UserPage from './pages/User';
import axios from 'axios';


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
                        <Route path="/dashboard" element={<ProfilePage/>} />
                        <Route path="/credentials/login"  element={<LoginPage doLogin={doLogin} />} />
                        <Route path="/credentials/signin" element={<SigninPage/>} />
                        <Route path='/search' element={<SearchPage/>} />
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

