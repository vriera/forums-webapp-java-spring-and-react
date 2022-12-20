import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import DashboardPane from "../../../components/DashboardPane";
import ModalPage from "../../../components/ModalPage";
import Pagination from "../../../components/Pagination";
import { Community } from "../../../models/CommunityTypes";
import { User, Notification } from "../../../models/UserTypes";
import { useState } from "react";

const ManageInvites = (props: {invited: Community[], totalPages: number, user: User}) => {
    const {t} = useTranslation();
    const [page, setPage] = useState(1);

    function acceptInvite(community: Community) {
        //Accept invite on behalf of the user
    }

    function rejectInvite(community: Community) {
        //Reject invite on behalf of the user
    }

    function blockCommunity(community: Community) {
        //Block community on behalf of the user

    }

    const [showModalForInvites, setShowModalForInvites] = useState(false);
  
    const handleCloseModalForInvites = () => {
        setShowModalForInvites(false);
    }

    const handleShowModalForInvites = (event: any) => {
        event.preventDefault();
        setShowModalForInvites(true);

    } 

    return (
    <div>
        <ModalPage buttonName="Hola" show={showModalForInvites} onClose={handleCloseModalForInvites} />
        {props.invited.length !== 0 &&
        <div>
            {props.invited.map((community: Community) => 
            <div className="card" key={community.id}>
                <div className="d-flex flex-row mt-3" style={{justifyContent: "space-between"}}>
                    <div>
                        <p className="h4 card-title ml-2">{community.name}</p>
                    </div>
                    <div className="row">
                        <div className="col-auto mx-0 px-0">
                            {/* TODO: ACCEPT INVITE */}
                            <button className="btn mb-0" onClick={/* () => acceptInvite(community); */ handleShowModalForInvites} title={t("dashboard.AcceptInvite")} >
                                <div className="h4 mb-0">
                                    <i className="fas fa-check-circle"></i>
                                </div>
                            </button>
                        </div>

                        <div className="col-auto mx-0 px-0">
                            {/* TODO: REJECT INVITE */}
                            <button className="btn mb-0" onClick={/* () => rejectInvite(community) */ handleShowModalForInvites} title={t("dashboard.RejectInvite")}>
                                <div className="h4 mb-0">
                                    <i className="fas fa-times-circle"></i>
                                </div>
                            </button>
                        </div>

                        <div className="col-auto mx-0 px-0">
                            {/* TODO: BLOCK COMMUNITY */}
                            <button className="btn mb-0" onClick={/* () => blockCommunity(community) */ handleShowModalForInvites} title={t("dashboard.BlockCommunity")}>
                                <div className="h4 mb-0">
                                    <i className="fas fa-ban"></i>
                                </div>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            )}
        </div>
        }
        <Pagination totalPages={props.totalPages} currentPage={page} setCurrentPageCallback={setPage}/>
    </div>
    )
}

const InvitedCommunitiesPane = (props: {user: User}) => {
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
   
    const {t} = useTranslation();

    return (
    <div className="white-pill mt-5">
        <div className="card-body">
            <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.pendingInvites")}</p>
            <DashboardAccessTabs notifications={notifications /*FIXME */} activeTab={"invited"}/>
            <ManageInvites invited={[community]/*TODO: PEDIDO A LA API*/} totalPages={5/* TODO: PEDIDO A LA API */} user={props.user}/>
        </div>
    </div>
    )
}

const InvitedCommunitiesPage = (props: {user: User}) => {

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
                            <DashboardPane option={"access"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            <InvitedCommunitiesPane user={auxUser}/>

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

export default InvitedCommunitiesPage;