import React, { ChangeEvent } from "react";
import {Link} from "react-router-dom";
import {User, Notification} from "../models/UserTypes";
import {Community} from "../models/CommunityTypes";
import { useTranslation } from "react-i18next";
import { useState } from "react";
import Pagination from "./Pagination";
import Modal from "react-bootstrap/Modal";
import Button from 'react-bootstrap/Button';


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

const communities = [community, community, community]


const ModalPage = (props: {buttonName: string, show:boolean, onClose: any}) => {
    const {t} = useTranslation();

    return (
      <>
  
        <Modal show={props.show} onHide={props.onClose}>
          <Modal.Header closeButton>
            <Modal.Title>{t("Warning")}</Modal.Title>
          </Modal.Header>

          <Modal.Body>{t("dashboard.ConfirmationGeneric")}</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={props.onClose}>
                {t("cancel")}
            </Button>
            <Button variant="primary" onClick={props.onClose}>
                {t("profile.save")}
            </Button>
          </Modal.Footer>
        </Modal>
      </>
    );
  }




const Tabs = (props: {notifications: Notification, option: string, setOptionCallback: (option: "admitted" | "invites" | "requests" | "rejections") => void}) => {
    const {t} = useTranslation();

    return(
        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "admitted" && "active")} onClick={() => props.setOptionCallback("admitted")}>
                        {t("dashboard.admitted")}
                    </button>
                </li>

                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "invites" && "active")} onClick={() => props.setOptionCallback("invites")}>
                        {t("dashboard.invites")}
                        {props.notifications.invites > 0  &&
                            <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.invites}</span>
                        }
                    </button>
                </li>
                
                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "requests" && "active")} onClick={() => props.setOptionCallback("requests")}>
                        {t("dashboard.requests")}
                        {props.notifications.requests > 0  &&
                        <span className="badge badge-secondary bg-warning text-white ml-1">{props.notifications.requests}</span>
                        }
                    </button>
                </li>

                <li className="nav-item">
                    <button className={"nav-link " + (props.option == "rejected" && "active")} onClick={() => props.setOptionCallback("rejections")}>
                        {t("dashboard.rejections")}
                    </button>
                </li>               
                
            </ul>
        </div>
    )
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
            {/* Nadie mueva esto de aca carajo mierda no lo muevan dejenme al modal */}
            <ModalPage buttonName="Hola" show={showModalForAdmitted} onClose={handleCloseModalForAdmitted} />
            {props.communities.length == 0 &&
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

// const ModeratedCommunities = (props: {communities: Community[]}) => {
//     const {t} = useTranslation();

//     return (
//         <div>
//             {props.communities.length == 0 && 
//             <div>
//                 <p className="row h1 text-gray">{t("dashboard.noCommunities")}</p>
//                 <div className="d-flex justify-content-center">
//                     <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
//                 </div>
//             </div>
//             }
//             <div className="overflow-auto">
//             {props.communities.map((community: Community) =>
//             <a className="d-block" href={`${process.env.PUBLIC_URL}/community/${community.id}/view/members`} key={community.id}>
//                 <div className="card p-3 m-3 shadow-sm--hover ">
//                     <div className="row">
//                         <div className="d-flex flex-column justify-content-start ml-3">

//                             <div className="h2 text-primary">
//                                 <i className="fas fa-cogs"></i>
//                                 {community.name}
//                                 {community.notifications? community.notifications.total > 0 &&
//                                 <span className="badge badge-secondary bg-warning text-white ml-1">{community.notifications.total}</span>
//                                 : null
//                                 }
                                
//                             </div>
//                         </div>
//                         <div className="col-12 text-wrap-ellipsis">
//                             <p className="h5">{community.description}</p>
//                         </div>

//                     </div>
//                 </div>
//             </a>
//             )}
//             </div>
//         </div>
//     )
// }

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
        {props.invited.length != 0 &&
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


    function requestAccess(community: Community) {
        //Request access on behalf of the user
    }

    const [page, setPage] = useState(1);
    return (
        <div className="">
            <ModalPage buttonName="Hola" show={showModalForRequests} onClose={handleCloseModalForRequests} />
            {props.requested.length == 0 &&
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
const ManageRejections = (props: {rejected: Community[], totalPages: number, user: User}) => {
    const {t} = useTranslation();
    const [page, setPage] = useState(1);


    const [showModalForRejections, setShowModalForRejections] = useState(false);
  
    const handleCloseModalForRejections = () => {
        setShowModalForRejections(false);
    }

    const handleShowModalForRejections = (event: any) => {
        event.preventDefault();
        setShowModalForRejections(true);

    } 

    function requestAccess(community: Community) {
        //Request access on behalf of the user
    }

    return (
        <div>
            <ModalPage buttonName="Hola" show={showModalForRejections} onClose={handleCloseModalForRejections} />
            <div className="">
                <div className="overflow-auto">
                    {props.rejected.length == 0 &&
                    <p className="h3 text-gray">{t("dashboard.noRejections")}YEET</p>
                    }
                    {props.rejected.map((community: Community) =>
                    <div className="card" key={community.id}>
                        <div className="d-flex flex-row mt-3" style={{justifyContent: "space-between"}}>
                            <p className="h4 card-title ml-2">{community.name}</p>
                            {/* TODO: REQUEST ACCESS */}
                            <button className="btn mb-0" onClick={/* () => requestAccess(community) */ handleShowModalForRejections} title={t("dashboard.ResendRequest")}>
                                <div className="h4 mb-0">
                                    <i className="fas fa-redo-alt"></i>
                                </div>
                            </button>
                        </div>
                    </div>
                    )}
                    <Pagination totalPages={props.totalPages} currentPage={page} setCurrentPageCallback={setPage}/>
                </div>
            </div>
        </div>
    )
}

const DashboardAccessPane = (props: {user: User}) => {
    const {t} = useTranslation();

    const [option, setOption] = useState("admitted");
    function setOptionCallback(option: "admitted" | "invites" | "requests" | "rejections") {
        setOption(option);
    }
    function renderPageContent() {
        switch(option) {
        
            case "admitted":
                return (
                    <div className="white-pill mt-5">
					    <div className="card-body">
                        <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.admittedCommunities")}</p>
                            <Tabs notifications={ notifications/*FIXME */} option={option} setOptionCallback={setOptionCallback}/>
                            <AdmittedCommunities communities={communities/*FIXME*/}/>
                        </div>
                    </div>
                )
            case "invites":
                return (
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.pendingInvites")}</p>
                        <Tabs notifications={notifications /*FIXME */} option={option} setOptionCallback={setOptionCallback}/>
                        <ManageInvites invited={communities/*TODO: PEDIDO A LA API*/} totalPages={5/* TODO: PEDIDO A LA API */} user={props.user}/>
                    </div>
                </div>
                )
            case "requests":
                return (
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.pendingRequests")}</p>
                        <Tabs notifications={notifications /* FIXME */ } option={option} setOptionCallback={setOptionCallback}/>
                        <ManageRequests requested={communities/*TODO: PEDIDO A LA API*/} totalPages={5/* TODO: PEDIDO A LA API */}/>
                    </div>
                </div>
                )
            case "rejections":
                return (
                <div className="white-pill mt-5">
                    <div className="card-body">
                        <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.rejectedRequests")}</p>
                        <Tabs notifications={notifications /*FIXME */} option={option} setOptionCallback={setOptionCallback}/>
                        <ManageRejections rejected={communities/*TODO: PEDIDO A LA API*/} totalPages={5/* TODO: PEDIDO A LA API */} user={props.user}/>
                    </div>
                </div>
                )
            
        }
    }
    return (
        <div>
            {renderPageContent()}
        </div>
    );
}

export default DashboardAccessPane