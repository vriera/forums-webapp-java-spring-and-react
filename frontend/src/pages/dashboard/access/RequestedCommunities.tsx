import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardPane from "../../../components/DashboardPane";
import { User, Notification } from "../../../models/UserTypes";
import { Community, CommunityCard } from "../../../models/CommunityTypes";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import { useEffect, useState } from "react";
import ModalPage from "../../../components/ModalPage";
import Pagination from "../../../components/Pagination";
import { createBrowserHistory } from "history";
import { useNavigate } from "react-router-dom";
import { useQuery } from "../../../components/UseQuery";
import { CommunitiesByAcessTypeParams, getCommunitiesByAccessType } from "../../../services/community";
import { AccessType } from "../../../services/Access";
import Spinner from "../../../components/Spinner";

const ManageRequests = () => {
    const {t} = useTranslation();

    const [showModalForRequests, setShowModalForRequests] = useState(false);
  
    const handleCloseModalForRequests = () => {
        setShowModalForRequests(false);
    }

    const handleShowModalForRequests = (event: any) => {
        event.preventDefault();
        setShowModalForRequests(true);
    } 
    const userId = parseInt(window.localStorage.getItem("userId") as string);
    const history = createBrowserHistory();
    const navigate = useNavigate();
    const query = useQuery();

    const [communities, setCommunities] = useState<CommunityCard[]>();
    
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    // Set initial page
    useEffect(() => {
        let pageFromQuery = query.get("page")? parseInt(query.get("page") as string) : 1;
        setCurrentPage( pageFromQuery);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/access/admitted?page=${pageFromQuery}`})

    }, [query])

    // Fetch communities from API
    useEffect(() => {
        async function fetchUserQuestions(){
            let params: CommunitiesByAcessTypeParams = {
            requestorId: userId,
            accessType: AccessType.REQUESTED,
            page: currentPage
            }; 
            try{
                let {list, pagination} = await getCommunitiesByAccessType(params);
                setCommunities(list);
                setTotalPages(pagination.total);
            }catch{
                //TODO: Route to error page
                navigate("/error")
            }            
        }
        fetchUserQuestions();
        
    }, [currentPage, navigate, userId])

    function setCurrentPageCallback(page: number){
        setCurrentPage(page);
        history.push({ pathname: `${process.env.PUBLIC_URL}/dashboard/access/admitted?page=${page}`})
        setCommunities(undefined);
    }


    return (
        <div >
            <ModalPage buttonName="Hola" show={showModalForRequests} onClose={handleCloseModalForRequests} />
            {!communities && 
                <div className="my-5"> 
                    <Spinner/>
                </div>
            }
            {communities && communities.length === 0 &&
                <p className=" my-3 h3 text-gray">{t("dashboard.noPendingRequests")}</p>
            }

            <div className="overflow-auto">
                {communities && communities.length > 0 && communities.map((community: CommunityCard) =>
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
            <Pagination totalPages={totalPages} currentPage={currentPage} setCurrentPageCallback={setCurrentPageCallback}/>
        </div>
    )
}

const RequestedCommunitiesPane = () => {

    const { t } = useTranslation();
    return (
        <div className="white-pill mt-5">
            <div className="card-body">
                <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.pendingRequests")}</p>
                <DashboardAccessTabs activeTab="requested"/>
                <ManageRequests/>
            </div>
        </div>
        )
}

const RequestedCommunitiesPage = () => {

   
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
                            <RequestedCommunitiesPane/>

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

export default RequestedCommunitiesPage;