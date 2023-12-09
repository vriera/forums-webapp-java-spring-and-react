import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import { CommunityResponse } from "../../../models/CommunityTypes";
import { User } from "../../../models/UserTypes";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import Pagination from "../../../components/Pagination";
import { useNavigate, useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import {
    ModeratedCommunitiesParams,
    getModeratedCommunities,
} from "../../../services/community";
import ModeratedCommunitiesPane from "../../../components/DashboardModeratedCommunitiesPane";
import { useQuery } from "../../../components/UseQuery";
import Spinner from "../../../components/Spinner";
import AdmittedMembersContent from "../../../components/AdmittedMembersContent";
import BannedUsersContent from "../../../components/BannedUsersContent";
import InvitedMembersContent from "../../../components/InvitedMembersContent";
import RequestedUsersContent from "../../../components/RequestedUsersContent";


// Follows endpoint /dashboard/communities/:communityId/admitted?communityPage={number}&userPage={number}
const DashboardCommunitiesPage = () => {
    const navigate = useNavigate();
    const history = createBrowserHistory();
    const [tab, setTab] = useState<"admitted" | "invited" | "banned" | "requested">("admitted");

    const { t } = useTranslation();

    let { communityId } = useParams(); // Get communityId from URL
    const query = useQuery();

    const [moderatedCommunities, setModeratedCommunities] =
        useState<CommunityResponse[]>();
    const [selectedCommunity, setSelectedCommunity] =
        useState<CommunityResponse>();

    const [communityPage, setCommunityPage] = useState(1);
    const [totalCommunityPages, setTotalCommunityPages] = useState(-1);

    const currentUserId = parseInt(window.localStorage.getItem("userId") as string);
    
    // Set initial pages
    useEffect(() => {
        let communityPageFromQuery = query.get("communityPage")
            ? parseInt(query.get("communityPage") as string)
            : 1;

        let userPageFromQuery = query.get("userPage")
            ? parseInt(query.get("userPage") as string)
            : 1;

        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`,
        });
        setCommunityPage(communityPageFromQuery);
    }, [query]);

    // Get user's moderated communities from API
    useEffect(() => {

        
        async function fetchModeratedCommunities() {
            let params: ModeratedCommunitiesParams = {
                userId: currentUserId,
                page: communityPage,
            };

            try {
                let { list, pagination } = await getModeratedCommunities(params);
                setModeratedCommunities(list);

                let index = list.findIndex((x) => x.id === parseParam(communityId));
                console.log("index before filtering:", index);
                // If a community is selected but is not on the current list, don't select any community. If no community is selected, select the first one.
                if (index === -1 && !selectedCommunity) 
                    index = 0; 
                console.log("index", index);
                console.log("list", list);
                console.log("list at index", list[index]);
                //Agregué este if, chequear con salus si le parece bien
                if(!(index === -1 )) {
                    setSelectedCommunityCallback(list[index]);  
                }
                setTotalCommunityPages(pagination.total);
            } catch (error: any) {
                // Show error page
                console.log("Error fetching moderated communities", error);
                navigate(`/${error.code}`);
            }
        }
        fetchModeratedCommunities();
    }, [communityPage, currentUserId]);

    function setCommunityPageCallback(page: number): void {
        let userPageFromQuery = query.get("userPage")
            ? parseInt(query.get("userPage") as string)
            : 1;

        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${page}&userPage=${userPageFromQuery}`,
        });
        setCommunityPage(page);

        setModeratedCommunities(undefined);
    }

    function setSelectedCommunityCallback(community: CommunityResponse): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}?communityPage=${communityPage}&userPage=${1}`,
        });

        setSelectedCommunity(community);
    }


    return (
        <div>
            {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
            <div className="wrapper">
                <div className="section section-hero section-shaped pt-3">
                    <Background />

                    <div className="row">
                        {/* COMMUNITIES SIDE PANE*/}
                        <div className="col-3">
                            <DashboardPane option={"communities"} />
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">

                            <div className="white-pill mt-5">
                                <div className="align-items-start d-flex justify-content-center my-3">
                                    {selectedCommunity && (
                                        <p className="h1 text-primary bold">
                                            <strong>{t(selectedCommunity.name)}</strong>
                                        </p>
                                    )}
                                    {!selectedCommunity && (
                                        <Spinner />
                                    )}
                                </div>

                                {selectedCommunity && (
                                    <div>
                                        <div className="text-gray text-center mt--4 mb-2">
                                            {selectedCommunity.description}
                                        </div>
                                        <hr />
                                        <DashboardCommunitiesTabs
                                            activeTab={tab}
                                            setActiveTab={setTab}
                                            communityId={selectedCommunity.id}
                                        />
                                    </div>
                                )}

                                {/* TODO: Esto se abre en dos casos, si todavía no cargó la selected community y si no hay comunidades para este usuario (o si hay comm pero la categoría está vacia, cae en este caso?).  */}
                                {!selectedCommunity && (
                                    <Spinner />)
                                }

                                {selectedCommunity && (
                                    <div>
                                        {(tab === "admitted") && (
                                            <div className="card-body">
                                                <AdmittedMembersContent params={{
                                                    selectedCommunity: selectedCommunity,
                                                    currentCommunityPage: communityPage,
                                                }} />
                                            </div>
                                        )}
                                        {(tab === "banned") && (
                                            <div className="card-body">
                                                <BannedUsersContent params={{
                                                    selectedCommunity: selectedCommunity,
                                                    currentCommunityPage: communityPage,
                                                }} />
                                            </div>
                                        )}
                                        {(tab === "invited") && (
                                            <div className="card-body">
                                                <InvitedMembersContent params={{
                                                    selectedCommunity: selectedCommunity,
                                                    currentCommunityPage: communityPage,
                                                }} />
                                            </div>
                                        )}

                                        {(tab === "requested") && (
                                            <div className="card-body">
                                                <RequestedUsersContent params={{
                                                    selectedCommunity: selectedCommunity,
                                                    currentCommunityPage: communityPage,
                                                }} />
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* MODERATED COMMUNITIES SIDE PANE */}
                        <div className="col-3">
                            {(!moderatedCommunities || !selectedCommunity) && (
                                <>
                                    <div className="white-pill mt-5 mx-3">
                                        <div className="card-body">
                                            <Spinner />
                                        </div>
                                    </div>
                                </>
                            )}
                            {moderatedCommunities && selectedCommunity && (
                                <ModeratedCommunitiesPane
                                    communities={moderatedCommunities}
                                    selectedCommunity={selectedCommunity}
                                    setSelectedCommunityCallback={setSelectedCommunityCallback}
                                    currentPage={communityPage}
                                    totalPages={totalCommunityPages}
                                    setCurrentPageCallback={setCommunityPageCallback}
                                />
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DashboardCommunitiesPage;

function parseParam(n: string | undefined): number {
    return parseInt(n as string);
}
