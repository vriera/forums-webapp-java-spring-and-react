import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import CreateCommunityPane from "../../../components/CreateCommunityPane";
import DashboardAccessTabs from "../../../components/DashboardAccessTabs";
import DashboardPane from "../../../components/DashboardPane";
import { User, } from "../../../models/UserTypes";
import { Community, CommunityCard } from "../../../models/CommunityTypes";
import { Link, useNavigate } from "react-router-dom";
import ModalPage from "../../../components/ModalPage";
import { useEffect, useState } from "react";
import { useQuery } from "../../../components/UseQuery";
import { createBrowserHistory } from "history";
import { AccessType } from "../../../services/Access";
import Pagination from "../../../components/Pagination";
import { CommunitiesByAcessTypeParams, getCommunitiesByAccessType } from "../../../services/community";
import Spinner from "../../../components/Spinner";

const AdmittedCommunities = () => {
    const {t} = useTranslation();

    const [showModalForAdmitted, setShowModalForAdmitted] = useState(false);  
    const handleCloseModalForAdmitted = () => {
        setShowModalForAdmitted(false);
    }

    const handleShowModalForAdmitted = (event: any) => {
        event.preventDefault();
        setShowModalForAdmitted(true);

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
            accessType: AccessType.ADMITTED,
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
        <div>
            {/* DON'T MOVE MODAL*/}
            <ModalPage buttonName="Hola" show={showModalForAdmitted} onClose={handleCloseModalForAdmitted} />
            {communities && communities.length === 0 &&
            <div>
                <p className="row h1 text-gray">{t("dashboard.noCommunities")}</p>
                <div className="d-flex justify-content-center">
                    <img className="row w-25 h-25" src={`${process.env.PUBLIC_URL}/resources/images/empty.png`} alt="No hay nada para mostrar"/>
                </div>
            </div>
            }
            <div className="overflow-auto">
            {!communities && 
                <div className="my-5"> 
                    <Spinner/>
                </div>
            }
            {communities && communities.length === 0 &&
                <p className="h3 text-gray mt-2">{t("dashboard.noPendingInvites")}</p>
            }
            {communities && communities.length > 0 && communities.map((community: CommunityCard) =>
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
            <Pagination totalPages={totalPages} currentPage={currentPage} setCurrentPageCallback={setCurrentPageCallback}/>
        </div>
    )
}

const AdmittedCommunitiesPane = () => {
    const { t } = useTranslation();
    return (
        <div className="white-pill mt-5">
            <div className="card-body">
            <p className="h2 text-primary text-center mt-3 text-uppercase">{t("dashboard.admittedCommunities")}</p>
                <DashboardAccessTabs activeTab={"admitted"}/>
                <AdmittedCommunities/>
            </div>
        </div>
    )
}

const AdmittedCommunitiesPage = () => {
    
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
                            <AdmittedCommunitiesPane/>
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