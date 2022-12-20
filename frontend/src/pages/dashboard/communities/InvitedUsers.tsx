import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import Background from "../../../components/Background";
import CommunitiesCard from "../../../components/CommunitiesCard";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import DashboardPane from "../../../components/DashboardPane";
import Pagination from "../../../components/Pagination";
import { Community } from "../../../models/CommunityTypes";
import { User, Notification } from "../../../models/UserTypes";

const AccessCard = (props: {user: User}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.user.username}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-times-circle"></i>
                    </div>
                </button>
            </div>
        </div>
    )
}

const InvitedMembersContent = (props: {selectedCommunity: Community}) => {

    // TODO: Fetch admitted users from API
    const [invitedUsers, setInvitedUsers] = useState(null as unknown as User[]);
    useEffect( () => setInvitedUsers([{
        id: 1,
        username:"aneta",
        email:"boneta"
    }]), []);
    
    const [totalPages, setTotalPages] = useState(10);
    const [currentPage, setCurrentPage] = useState(1)
    const {t} = useTranslation();

    return (
      <>
        {/* Different titles according to the corresponding tab */}
        <p className="h3 text-primary">{t("dashboard.invites")}</p>

        {/* If members length is greater than 0  */}
        <div className="overflow-auto">
            {invitedUsers &&
            invitedUsers.map((user: User) =>
                <AccessCard user={user} key={user.id}/>
            )}
        </div>

        {totalPages && 
          <Pagination currentPage={currentPage} setCurrentPageCallback={setCurrentPage} totalPages={totalPages}/>
        }

            {/* If category is invited and list length is 0 */}
            {invitedUsers.length === 0 &&
                <p className="h3 text-gray">{t("dashboard.noaccess")}</p>
            }
      </>
    );
}

const InvitedUsersPane = (props: {selectedCommunity: Community}) => {

    const {t} = useTranslation();

    return (
      <div className="white-pill mt-5">
        <div className="align-items-start d-flex justify-content-center my-3">
          <p className="h1 text-primary bold">
            <strong>{t(props.selectedCommunity.name)}</strong>
          </p>
        </div>
        <div className="text-gray text-center mt--4 mb-2">
          {props.selectedCommunity.description}
        </div>

        <hr />

        <DashboardCommunitiesTabs activeTab="invited" communityId={props.selectedCommunity.id}/>
        <div className="card-body">
          <InvitedMembersContent selectedCommunity={props.selectedCommunity} />
        </div>
      </div>
    );
}

const InvitedUsersPage = (props: {user: User}) => {
    const {communityId} = useParams();
    let [selectedCommunity, setSelectedCommunity] = useState(null as unknown as Community);
    const [moderatedCommunities, setModeratedCommunities] = useState(null as unknown as Community[]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages] = useState(null as unknown as number);    
    const {t} = useTranslation();

    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
        total: 3
    }
    let auxCommunity : Community = {
        id: 1,
        name: "Neener",
        description: "Nanner"
    };
    console.log("Selected community: ", selectedCommunity)

    //TODO: fetch from API
    useEffect( () => {setModeratedCommunities([auxCommunity])}, [currentPage])
    useEffect( () => setSelectedCommunity(auxCommunity), []); 

    // TODO: Fetch communities from API
    // console.log("Selected community:" + auxCommunities[0].name);
    return (
        <div>
        {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
        <div className="wrapper">
            <div className="section section-hero section-shaped pt-3">
                <Background/>

                <div className="row">
                    {/* COMMUNITIES SIDE PANE*/}
                    <div className="col-3">
                        <DashboardPane option={"communities"} />
                    </div>

                    {/* CENTER PANE*/}
                    <div className="col-6">
                        {selectedCommunity &&
                            <InvitedUsersPane selectedCommunity={selectedCommunity}/>
                        }
                    </div> 

                    {/* MODERATED COMMUNITIES SIDE PANE */}
                    <div className="col-3">
                        {/* TODO: Page for when there are no moderated communities*/}
                        {   moderatedCommunities && 
                            <CommunitiesCard title={ t("dashboard.Modcommunities")}
                            communities={moderatedCommunities} selectedCommunity={selectedCommunity} selectedCommunityCallback={setSelectedCommunity} 
                            currentPage={currentPage} totalPages={totalPages} currentPageCallback={setCurrentPage}/> 
                        }
                    </div>
                </div>
            </div>
        </div>
    </div>
    )
}

export default InvitedUsersPage;