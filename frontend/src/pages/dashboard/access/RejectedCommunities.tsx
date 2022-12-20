import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardPane from "../../../components/DashboardPane";
import { User, Notification } from "../../../models/UserTypes";
import { Community } from "../../../models/CommunityTypes";
import ModalPage from "../../../components/ModalPage";
import { useState } from "react";
import Pagination from "../../../components/Pagination";


const ManageRequests = (props: {requested: Community[], totalPages: number}) => {
    const {t} = useTranslation();

    const [showModalForRequests, setShowModalForRequests] = useState(false);
  
    const handleCloseModalForRequests = () => {
        setShowModalForRequests(false);
    }

    const handleShowModalForRequests = (event: any) => {
        event.preventDefault();
        setShowModalForRequests(true);

    }

    const [page, setPage] = useState(1);
    return (
        <div className="">
            <ModalPage buttonName="Hola" show={showModalForRequests} onClose={handleCloseModalForRequests} />
            {props.requested.length === 0 &&
                <p className="h3 text-gray">{t("dashboard.noPendingRequests")}</p>
            }

            <div className="overflow-auto">
                {props.requested.map((community: Community) =>
                <div className="card" key={community.id}>
                    <div className="d-flex flex-row mt-3" style={{justifyContent: "space-between"}}>
                        <p className="h4 card-title ml-2">{community.name}</p>
                        {/* TODO: REQUEST ACCESS */}
                        <button className="btn mb-0" onClick={/* () => requestAccess(community) */ handleShowModalForRequests} title={t("dashboard.ResendRequest")}>
                            <div className="h4 mb-0">
                                <i className="fas fa-redo-alt"></i>
                            </div>
                        </button>
                    </div>
                </div>
                )}
            </div>
            <Pagination totalPages={props.totalPages} currentPage={page} setCurrentPageCallback={setPage}/>
        </div>
    )
}

const RejectedCommunitiesPane = (props: {user: User}) => {
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

    const { t } = useTranslation();

    return (
        <div className="white-pill mt-5">
            <div className="card-body">
                <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.rejectedRequests")}</p>
                <DashboardAccessTabs notifications={notifications /* FIXME */ } activeTab="rejected"/>
                <ManageRequests requested={[community]/*TODO: PEDIDO A LA API*/} totalPages={5/* TODO: PEDIDO A LA API */}/>
            </div>
        </div>
        )
}

const RejectedCommunitiesPage = (props: {user: User}) => {

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
                            <RejectedCommunitiesPane user={auxUser}/>

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

export default RejectedCommunitiesPage;