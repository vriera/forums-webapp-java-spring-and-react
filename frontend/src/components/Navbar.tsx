import React from "react";
import Dropdown from "react-bootstrap/Dropdown";
import {useState , useEffect, useMemo} from "react";
import {User} from "../models/UserTypes"
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { getUserFromApi } from "../services/user"
import { logout } from "../services/auth"
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';


const Navbar = (props: {user: User, logoutFunction: any}) => {
 
    const { t } = useTranslation();
    const isLoggedIn = useMemo(() => props.user !== null, [props.user]);
    return (
        <div>
            <div className="navbar border-bottom">
                <div className="container-fluid navbar-brand">

                    <div>
                        <div className="d-flex justify-content-end">
                            <Link  className="navbar-brand" to="/">
                                <img src={process.env.PUBLIC_URL+'/resources/images/birb.png'} width="30" height="30"/> {/* FIXME: esta imagen no anda pero no estoy segura como embedearla */}
                                {t('askAway')}
                            </Link>
                            
                            <div className="nav-item">
                                <Link className="nav-link" to="/search/questions">
                                    {t('title.viewAllQuestions')}
                                </Link>
                            </div>
                        </div>
                    </div>

                    {!isLoggedIn &&
                        <div className="d-flex justify-content-start">
                            <div className="nav-item">
                                <Link className="nav-link" to="/credentials/signin">
                                    {t('register.register')}
                                </Link>
                            </div>
                            <div className="nav-item">
                                <Link className="nav-link" to="/credentials/login">
                                    {t('register.login')}
                                </Link>
                            </div>
                        </div>
                    }
                    {isLoggedIn && props.user && 
                        <DropdownButton user={props.user} logoutFunction={props.logoutFunction}/>
                    }
                </div>
            </div>
        </div>
    )
}


const DropdownButton = (props: {user: User, logoutFunction: any}) => {
    return(
        <Dropdown className="dropdown " >
            <Dropdown.Toggle className="btn btn-primary pb-0">
                <div className="d-flex">
                    {/* <c:if test="${param.user_notifications > 0 }"> */}
                        {/* <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning py-0 ">
                            <div className="text-white h6 mx-1 my-0"> ${param.user_notifications} </div>
                        </span> */}
                    {/* </c:if> */}
                    <div className="dropdown_title row">
                        <div className="col-auto">
                            <img src={"https://avatars.dicebear.com/api/avataaars/"+ props.user.email + ".svg"} className="img"  alt="profile"/>
                        </div>
                        <div className="col-auto">
                            <p className="margin-sides-3">{props.user.username}</p>
                        </div>
                    </div>
                </div>
            </Dropdown.Toggle>
        

            <Dropdown.Menu className="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <Link className="dropdown-item" to="/dashboard/profile/info">Dashboard</Link>
                <button className="dropdown-item" onClick={props.logoutFunction}>Logout</button>
            </Dropdown.Menu>
        </Dropdown>
    )
}


export default Navbar;
