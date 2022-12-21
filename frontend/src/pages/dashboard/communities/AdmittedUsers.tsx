import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import { CommunityCard } from "../../../models/CommunityTypes";
import { User} from "../../../models/UserTypes";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import Pagination from "../../../components/Pagination";
import { useLocation, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import { ModeratedCommunitiesParams, getModeratedCommunities } from "../../../services/community";
import ModeratedCommunitiesPane from "../../../components/DashboardModeratedCommunitiesPane";
import { UsersByAcessTypeParams, getUsersByAccessType } from "../../../services/user";
import { AccessType } from "../../../services/Access";
import { useQuery } from "../../../components/UseQuery";
import Spinner from "../../../components/Spinner";

type UserContentType =  {
  userList: User[],
  selectedCommunity: CommunityCard,
  currentPage: number,
  totalPages: number,
  setCurrentPageCallback: (page: number) => void
}

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

const AdmittedMembersContent = (props: {params: UserContentType }) => { 
  
    const {t} = useTranslation();

    const [showModalForLeave, setShowModalForLeave] = useState(false);  
    const handleCloseModalForLeave = () => {
        setShowModalForLeave(false);
    }
    const handleShowModalForLeave = (event: any) => {
        event.preventDefault();
        setShowModalForLeave(true);
    }
    async function handleLeave(userId: number){
        let params: SetAccessTypeParams = {
            communityId: props.params.selectedCommunity.id,
            targetId: userId,
            newAccess : AccessType.LEFT
        }
        await setAccessType(params);
        let listWithoutUser = props.params.userList?.filter((user: User) => user.id !== userId);
        setCommunities(listWithoutUser);
        handleCloseModalForLeave();
    }

    const [showModalForBlock, setShowModalForBlock] = useState(false);  
    const handleCloseModalForBlock = () => {
        setShowModalForBlock(false);
    }
    const handleShowModalForBlock = (event: any) => {
        event.preventDefault();
        setShowModalForBlock(true);
    }
    async function handleBlock(communityId: number){
        let params: SetAccessTypeParams = {
            communityId: communityId,
            targetId: userId,
            newAccess : AccessType.BLOCKED_COMMUNITY
        }
        await setAccessType(params);
        let listWithoutCommunity = communities?.filter((community: CommunityCard) => community.id !== communityId);
        setCommunities(listWithoutCommunity);
        handleCloseModalForBlock();
    }

    return (
      <>
        {/* Different titles according to the corresponding tab */}
        <p className="h3 text-primary">{t("dashboard.members")}</p>        

        {/* If members length is greater than 0  */}
        <div className="overflow-auto">
            {
            props.params.userList && props.params.userList.length > 0 &&
            props.params.userList.map((user: User) => (
              <MemberCard user={user} key={user.id} />
            ))
            }
            {props.params.userList && props.params.userList.length === 0 && 
              // Show no content image
              <div className="ml-5">
                  <p className="row h1 text-gray">{t("dashboard.noMembers")}</p>
                  <div className="d-flex justify-content-center">
                      <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="Nothing to show"/>
                  </div>
              </div>
            }
            <Pagination currentPage={props.params.currentPage} setCurrentPageCallback={props.params.setCurrentPageCallback} totalPages={props.params.totalPages}/>

            
            <div className="d-flex justify-content-center mt-3">
              <input
                className="btn btn-primary"
                type="submit"
                value={t("dashboard.invite")}
              />{" "}
              {/* TODO: cablear botón! */}
            </div>
        </div>

        

        
      </>
    );
}

const AdmittedUsersPane = (props: {params: UserContentType}) => {
    const {t} = useTranslation();

    return (
      <div className="white-pill mt-5">
        <div className="align-items-start d-flex justify-content-center my-3">
          <p className="h1 text-primary bold">
            <strong>{t(props.params.selectedCommunity.name)}</strong>
          </p>
        </div>
        <div className="text-gray text-center mt--4 mb-2">
          {props.params.selectedCommunity.description}
        </div>

        <hr />

        <DashboardCommunitiesTabs activeTab="admitted" communityId={props.params.selectedCommunity.id}/>
        <div className="card-body">
          <AdmittedMembersContent params={props.params} />
        </div>
      </div>
    );

}


// Follows endpoint /dashboard/communities/:communityId/admitted?communityPage={number}&userPage={number}
const AdmittedUsersPage = () => {
  const history = createBrowserHistory();

  let { communityId } = useParams();
  let pagesParam = parseParam(useParams().userPage);
  let communityPageParam = parseParam(useParams().communityPage);
  const query = useQuery()

  const [moderatedCommunities, setModeratedCommunities] = useState<CommunityCard[]>();
  const [selectedCommunity, setSelectedCommunity] = useState<CommunityCard>();

  const [communityPage, setCommunityPage] = useState(communityPageParam);
  const [totalCommunityPages, setTotalCommunityPages] = useState(-1);    

  const [userList, setUserList] = useState<User[]>();

  const [userPage, setUserPage] = useState(pagesParam);
  const [totalUserPages, setTotalUserPages] = useState(-1);

  const userId = parseInt(window.localStorage.getItem("userId") as string);

  // Set initial pages
  useEffect(() => {
    let communityPageFromQuery = query.get("communityPage")? parseInt(query.get("communityPage") as string) : 1;
    setCommunityPage( communityPageFromQuery );

    let userPageFromQuery = query.get("userPage")? parseInt(query.get("userPage") as string) : 1;
    setUserPage( userPageFromQuery );
    
    history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`})

}, [query, communityId])

  // Get user's moderated communities from API
  useEffect( () => 
  {
    async function fetchModeratedCommunities() {
      let params: ModeratedCommunitiesParams = {
        userId: userId,
        page: communityPage
      }
      try{
        let {list, pagination} = await getModeratedCommunities(params);
        setModeratedCommunities(list);
        setSelectedCommunity(list[0]);
        setUserPage(1);
        setTotalCommunityPages(pagination.total);
      } 
      catch (error) {
        // Show error page

      }

    }
    fetchModeratedCommunities();
  }
  , [communityPage, userId]);

  // Get selected community's admitted users from API
  useEffect( () =>
  {
    async function fetchAdmittedUsers() {
      if(selectedCommunity !== undefined){
        let params: UsersByAcessTypeParams = {
          accessType: AccessType.ADMITTED,
          moderatorId: userId,
          communityId: selectedCommunity?.id as number,
          page: userPage
        }
        try{
          let {list, pagination} = await getUsersByAccessType(params);
          setUserList(list);
          setTotalUserPages(pagination.total);
        }
        catch (error) {
        }
      }
    }
    fetchAdmittedUsers()
  }
  , [selectedCommunity, userPage, userId]);

  function setCommunityPageCallback(page: number): void{
    setCommunityPage(page);
    history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${page}&userPage=${userPage}`})
    setModeratedCommunities(undefined);
  }

  function setSelectedCommunityCallback(community: CommunityCard): void{
    setSelectedCommunity(community);
    history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}/admitted?communityPage=${communityPage}&userPage=${userPage}`})
  }


  function setUserPageCallback(page: number): void{
    setUserPage(page);
    history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${communityPage}&userPage=${page}`})
    setUserList(undefined);
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
                        <DashboardPane option={"communities"} />
                    </div>

                    {/* CENTER PANE*/}
                    <div className="col-6">
                        {(!selectedCommunity || !userList) &&
                          <>
                            <div className="white-pill mt-5">
                                <div className="align-items-start d-flex justify-content-center my-3">
                                  <p className="h1 text-primary bold">
                                  <Spinner/>
                                  </p>
                                </div>
                                <hr />
                                <div className="card-body">
                                <Spinner/>
                                </div>
                              </div>
                            </>
                        }
                        {selectedCommunity && userList &&
                            <AdmittedUsersPane params={ 
                              {                             
                                selectedCommunity: selectedCommunity,
                                userList: userList,
                                currentPage: userPage,
                                totalPages: totalUserPages,
                                setCurrentPageCallback: setUserPageCallback
                              }
                            }                           
                            />
                        }
                    </div> 

                    {/* MODERATED COMMUNITIES SIDE PANE */}
                    <div className="col-3">
                        {
                            (!moderatedCommunities || !selectedCommunity) &&
                            <>
                            
                            <div className="white-pill mt-5 mx-3">
                                <div className="card-body">
                                <Spinner/>
                                </div>
                            </div>
                            
                            </>
                        }                        
                        {   moderatedCommunities && selectedCommunity && 
                            <ModeratedCommunitiesPane 
                            communities={moderatedCommunities} selectedCommunity={selectedCommunity} setSelectedCommunityCallback={setSelectedCommunityCallback} 
                            currentPage={communityPage} totalPages={totalCommunityPages} setCurrentPageCallback={setCommunityPageCallback}/> 
                        }
                    </div>
                </div>
            </div>
        </div>
    </div>
    )
}

export default AdmittedUsersPage;


function parseParam( n : string | undefined) : number{
  return parseInt(n as string)
}