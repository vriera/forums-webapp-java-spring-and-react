import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import { Community } from "../../../models/CommunityTypes";
import { User, Notification } from "../../../models/UserTypes";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import Pagination from "../../../components/Pagination";
import CommunitiesCard from "../../../components/CommunitiesCard";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";

const MemberCard = (props: {user: User}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">{props.user.username}</p>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-user-minus"></i>
                    </div>
                </button>
                <button className="btn mb-0" >
                    <div className="h4 mb-0">
                        <i className="fas fa-user-slash"></i>
                    </div>
                </button>

            </div>
        </div>
    )
}

const AdmittedMembersContent = (props: {selectedCommunity: Community}) => {

    // TODO: Fetch admitted users from API
    const [admittedUsers, setAdmittedUsers] = useState(null as unknown as User[]);
    useEffect( () => setAdmittedUsers([{
        id: 1,
        username:"aneta",
        email:"boneta"
    }]), []);
    
    const [totalPages] = useState(10);
    const [currentPage, setCurrentPage] = useState(1)
    const {t} = useTranslation();

    return (
      <>
        {/* Different titles according to the corresponding tab */}
        <p className="h3 text-primary">{t("dashboard.members")}</p>        

        {/* If members length is greater than 0  */}
        <div className="overflow-auto">
            {
            admittedUsers &&
            admittedUsers.map((user: User) => (
              <MemberCard user={user} key={user.id} />
            ))
            }
           
            <div className="d-flex justify-content-center mt-3">
              <input
                className="btn btn-primary"
                type="submit"
                value={t("dashboard.invite")}
              />{" "}
              {/* TODO: cablear botón! */}
            </div>
        </div>

        {totalPages && 
          <Pagination currentPage={currentPage} setCurrentPageCallback={setCurrentPage} totalPages={totalPages}/>
        }

        {/* If category is members and members length is 0 */}
        {admittedUsers && admittedUsers.length === 0 && (
          <div className="d-flex justify-content-center mt-3">
            <input
              className="btn btn-primary"
              type="submit"
              value={t("dashboard.invite")}
            />
          </div>
        )}
      </>
    );
}

const AdmittedUsersPane = (props: {selectedCommunity: Community}) => {
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

        <DashboardCommunitiesTabs activeTab="admitted" communityId={props.selectedCommunity.id}/>
        <div className="card-body">
          <AdmittedMembersContent selectedCommunity={props.selectedCommunity} />
        </div>
      </div>
    );

}

function useQuery() {
  const { search } = useLocation();

  return React.useMemo(() => new URLSearchParams(search), [search]);
}

// Follows endpoint /dashboard/communities/:communityId/admitted?communityPage={number}&userPage={number}
const AdmittedUsersPage = (props: {user: User}) => {
    const navigate = useNavigate();
    const history = createBrowserHistory();

    let { communityId } = useParams();
    const query = useQuery()

    const [moderatedCommunities, setModeratedCommunities] = useState(null as unknown as Community[]);
    const [selectedCommunity, setSelectedCommunity] = useState(null as unknown as Community);

    let communityPageFromQuery = query.get("communityPage");
    const [communityPage, setCommunityPage] = useState(communityPageFromQuery? parseInt(communityPageFromQuery) : 0);
    const [totalCommunityPages] = useState(null as unknown as number);    

    let userPageFromQuery = query.get("userPage")
    const [userPage, setUserPage] = useState(userPageFromQuery? parseInt(userPageFromQuery) : 0);
    const [totalUserPages] = useState(null as unknown as number);

    // Update URL in case of property changes
    useEffect( () => {history.push({ pathname: `/dashboard/communities/${selectedCommunity.id}/admitted?communityPage=${communityPage}&userPage=${userPage}`})  },[communityPage, userPage, selectedCommunity])

    let auxNotification: Notification = {
        requests: 1,
        invites: 2,
      }
        total: 3
    let auxCommunity : Community = {
        id: 1,
        name: "Neener",
        description: "Nanner"
    };

    useEffect( () => setModeratedCommunities([auxCommunity]), [])

    // TODO: Fetch communities from API
    return (
        <div>
        {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
        <div className="wrapper">
            <div className="section section-hero section-shaped pt-3">
                <Background/>

                <div className="row">
                    {/* COMMUNITIES SIDE PANE*/}
                    <div className="col-3">
                        <DashboardPane user={props.user} notifications={auxNotification} option={"communities"} />
                    </div>

                    {/* CENTER PANE*/}
                    <div className="col-6">
                        {selectedCommunity &&
                            <AdmittedUsersPane selectedCommunity={selectedCommunity}/>
                        }
                    </div> 

                    {/* MODERATED COMMUNITIES SIDE PANE */}
                    <div className="col-3">
                        {/* TODO: Page for when there are no moderated communities*/}
                        {   moderatedCommunities && 
                            <CommunitiesCard 
                            communities={moderatedCommunities} selectedCommunity={selectedCommunity} selectedCommunityCallback={setSelectedCommunity} 
                            currentPage={communityPage} totalPages={totalCommunityPages} currentPageCallback={setCommunityPage}/> 
                        }
                    </div>
                </div>
            </div>
        </div>
    </div>
    )
}

export default AdmittedUsersPage;