import React from "react";
import { useTranslation } from "react-i18next";
import '../resources/styles/argon-design-system.css';
import '../resources/styles/blk-design-system.css';
import '../resources/styles/general.css';
import '../resources/styles/stepper.css';


import Navbar from "../components/Navbar";
import BaddassBackdrop from "../components/BadassBackdrop";
import LoginPane from "../components/LoginPane";
import SigninPane from "../components/SigninPane";




const CredentialsPage = () => {
    const { t } = useTranslation();
    return (
        <div>
            <Navbar />
            <div className="section section-hero section-shaped">   
                <BaddassBackdrop />
                <SigninPane />
            </div>
        </div>
    )

}

export default CredentialsPage;