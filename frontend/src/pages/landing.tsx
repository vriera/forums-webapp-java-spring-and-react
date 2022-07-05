import React from "react";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';

import Navbar from "../components/navbar"; //FIXME: Este path siento que debería ser absoluto pero no estoy segura como hacerlo

const Header = () => {
    return (
        <div>
           
           <Navbar/> 

        </div>
    );
}

const LandingPage = () => {
    return (
        <React.Fragment>
            <Header />
        </React.Fragment>

    );
};

export default LandingPage;