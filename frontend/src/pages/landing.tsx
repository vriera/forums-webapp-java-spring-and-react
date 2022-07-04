import React from "react";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';
const Header = () => {
    return (
        <div className="card-header">
            <span>AskAway</span>

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