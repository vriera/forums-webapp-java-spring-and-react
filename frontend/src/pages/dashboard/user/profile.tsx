import {useState} from "react";
import Background from "./../../../components/Background";
import Navbar from "./../../../components/navbar"
import DashboardPane from "./../../../components/DashboardPane"
import AskQuestionPane from "./../../../components/AskQuestionPane"
import ProfileInfoPane from "../../../components/ProfileInfoPane";
import UpdateProfilePage from "../../../components/UpdateProfilePane";
import {User, Karma, Notification} from "./../../../models/UserTypes"
import { useTranslation } from "react-i18next";

function mockUserApiCall(): User{
    var auxUser: User = {
        id: 69,
        username: "Salungo",
        email: "s@lung.o",
        password: "asereje",
    }
    
    return auxUser
}

function mockKarmaApiCall(user: User): Karma{
    var auxKarma : Karma = {
        user: user,
        karma: 420
    }
    return auxKarma
}

function mockNotificationApiCall(user: User): Notification{
    var auxNotification: Notification = {
        user: user,
        requests: 1,
        invites: 2,
        total: 3
    }
    return auxNotification
}

//TODO: this page should take the User, Karma and Notification objects for use in the display.
const ProfilePage = () => {
    const { t } = useTranslation();
    const user: User = mockUserApiCall()
    const karma: Karma = mockKarmaApiCall(user)
    const notifications: Notification = mockNotificationApiCall(user)
    const [updateProfile, setUpdateProfile] = useState(false)

    function updateProfileCallback(){
        setUpdateProfile(!updateProfile)
    }

    function renderCenterCard(updateProfile: boolean){
        if(updateProfile == false){
            return <ProfileInfoPane user={user} karma={karma} updateProfileCallback={updateProfileCallback}/>
        }
        else{
            return <UpdateProfilePage user={user} updateProfileCallback={updateProfileCallback}/>
        }
    }

    return (
        <div>
            <Navbar/>
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        <div className="col-3">
                            <DashboardPane user={user} notifications={notifications} option={"profile"}/>
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {renderCenterCard(updateProfile)}
                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <AskQuestionPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;