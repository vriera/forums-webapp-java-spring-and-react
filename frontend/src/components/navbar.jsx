import React from "react";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

const Navbar = () => {
    return (
        <div>
            <div className="navbar border-bottom">
                <div className="container-fluid navbar-brand">

                    <div>
                        <div className="d-flex justify-content-end">
                            <a  className="navbar-brand" href="/">
                                <img src="/resources/images/birb.png" width="30" height="30"/>
                                AskAway
                            </a>
                            <div className="nav-item ml-3">
                                <a className="nav-link" aria-current="page" href="/question/ask/community">
                                    Ask question
                                </a>
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="/community/create">
                                    Create community
                                </a>
                    
                            </div>
                            <div className="nav-item">
                                <a className="nav-link" href="/community/view/all">
                                    View all questions
                                </a>
                            </div>
                        </div>
                    </div>


                    <div className="d-flex justify-content-start">
                        <div className="nav-item">
                            <a className="nav-link" href="/credentials/register">
                                Register
                            </a>
                        </div>
                        <div className="nav-item">
                            <a className="nav-link" href="/credentials/login">
                                Login
                            </a>
                        </div>
                    </div>


                </div>
            </div>
        </div>
    )
}


export default Navbar;
