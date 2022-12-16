import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardPane from "../../../components/DashboardPane";
import { User, Notification } from "../../../models/UserTypes";
import { Community } from "../../../models/CommunityTypes";
import { Link } from "react-router-dom";
import ModalPage from "../../../components/ModalPage";
import { useState } from "react";

const notifications = 
 {
        requests: 1,
        invites: 2,
        total: 3,
    }

const community: Community = {
    id: 1,
    name: "Community 1",
    description: "This is the first community",
    moderator: {
        id: 1,
        username: "User 1",
        email: "use1@gmail.com",
    },
    notifications: {
        requests: 1,
        invites: 2,
        total: 3,
    },
    userCount: 5
}

const AdmittedCommunities = (props: {communities: Community[]}) => {
    const {t} = useTranslation();

    const [showModalForAdmitted, setShowModalForAdmitted] = useState(false);
  
    const handleCloseModalForAdmitted = () => {
        setShowModalForAdmitted(false);
    }

    const handleShowModalForAdmitted = (event: any) => {
        event.preventDefault();
        setShowModalForAdmitted(true);

    } 

    return (
        <div>
            {/* DON'T MOVE MODAL*/}
            <ModalPage buttonName="Hola" show={showModalForAdmitted} onClose={handleCloseModalForAdmitted} />
            {props.communities.length === 0 &&
            <div>
                <p className="row h1 text-gray">{t("dashboard.noCommunities")}</p>
                <div className="d-flex justify-content-center">
                    <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                </div>
            </div>
            }
            <div className="overflow-auto">
            {props.communities.map((community: Community) =>
                <div key={community.id}>
                    <Link className="d-block" to={`${process.env.PUBLIC_URL}/community/view/${community.id}`}>
                        <div className="card p-3 m-3 shadow-sm--hover ">
                            <div className="d-flex" style={{justifyContent: "space-between"}}>
                                <div>
                                    <p className="h2 text-primary">{community.name}</p>
                                </div>
                                <div className="row">
                                    {/* TODO: LEAVE COMMUNITY */}
                                    <div className="col-auto px-0">
                                        <button className="btn mb-0" title={t("dashboard.LeaveCommunity")} onClick={handleShowModalForAdmitted} >
                                            <div className="h4 mb-0">
                                                <i className="fas fa-sign-out-alt"></i> 
                                            </div>
                                        </button>
                                        
                                    </div>

                                    <div className="col-auto px-0">
                                        {/* TODO: BLOCK COMMUNITY */}
                                        <>
                                        <button className="btn mb-0" title={t("dashboard.BlockCommunity")} onClick={handleShowModalForAdmitted}>
                                            <div className="h4 mb-0">
                                                <i className="fas fa-ban"></i>
                                            </div>
                                        </button>
                                        </>
                                    </div>

                                </div>
                            </div>

                            <div className="row">
                                <div className="col-12 text-wrap-ellipsis">
                                    <p className="h5">{community.description}</p>
                                </div>
                            </div>
                        </div>
                    </Link>
                </div>
            )}
            </div>
        </div>
    )
}

const AdmittedCommunitiesPane = (props: {user: User}) => {
    const { t } = useTranslation();
    return (
        <div className="white-pill mt-5">
            <div className="card-body">
            <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.admittedCommunities")}</p>
                <DashboardAccessTabs notifications={ notifications/*FIXME */} activeTab={"admitted"}/>
                <AdmittedCommunities communities={[community]}/*FIXME*//>
            </div>
        </div>
    )
}

const AdmittedCommunitiesPage = (props: {user: User}) => {

    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }
        let auxUser: User = {
        id: 69, //Nice
        username: "Salungo",
        email: "s@lung.o",
        notifications: auxNotification
    }
    
    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background/>

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane user={props.user} notifications={auxNotification} option={"access"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <AdmittedCommunitiesPane user={auxUser}/>

                        </div> 

                        {/* ASK QUESTION */}
                        <div className="col-3">
                            <CreateCommunityPane/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AdmittedCommunitiesPage;