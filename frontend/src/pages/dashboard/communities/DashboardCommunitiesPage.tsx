import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import { CommunityResponse } from "../../../models/CommunityTypes";
import { User } from "../../../models/UserTypes";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import Pagination from "../../../components/Pagination";
import { useParams } from "react-router-dom";
import { createBrowserHistory } from "history";
import {
    ModeratedCommunitiesParams,
    SetAccessTypeParams,
    getModeratedCommunities,
    setAccessType,
    inviteUserByEmail,
} from "../../../services/community";
import ModeratedCommunitiesPane from "../../../components/DashboardModeratedCommunitiesPane";
import {
    GetUsersByAcessTypeParams,
    getUsersByAccessType,
} from "../../../services/user";
import { AccessType } from "../../../services/access";
import { useQuery } from "../../../components/UseQuery";
import Spinner from "../../../components/Spinner";
import ModalPage from "../../../components/ModalPage";
import AdmittedMembersContent from "../../../components/AdmittedMembersContent";
import BannedUsersContent from "../../../components/BannedUsersContent";
import InvitedMembersContent from "../../../components/InvitedMembersContent";
import RequestedUsersContent from "../../../components/RequestedUsersContent";

type UserContentType = {
    userList: User[];
    selectedCommunity: CommunityResponse;
    currentPage: number;
    totalPages: number;
    currentCommunityPage: number;
    setCurrentPageCallback: (page: number) => void;
};


// Follows endpoint /dashboard/communities/:communityId/admitted?communityPage={number}&userPage={number}
const DashboardCommunitiesPage = () => {
    const history = createBrowserHistory();
    const [tab, setTab] = useState<"admitted" | "invited" | "banned" | "requested">("admitted");

    let { communityId } = useParams();
    let pagesParam = parseParam(useParams().userPage);
    let communityPageParam = parseParam(useParams().communityPage);
    const { t } = useTranslation();

    const query = useQuery();

    const [moderatedCommunities, setModeratedCommunities] =
        useState<CommunityResponse[]>();
    const [selectedCommunity, setSelectedCommunity] =
        useState<CommunityResponse>();

    const [communityPage, setCommunityPage] = useState(communityPageParam);
    const [totalCommunityPages, setTotalCommunityPages] = useState(-1);

    const [userList, setUserList] = useState<User[]>();

    const [userPage, setUserPage] = useState(pagesParam);
    const [totalUserPages, setTotalUserPages] = useState(-1);

    const userId = parseInt(window.localStorage.getItem("userId") as string);

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
        setUserPage(userPageFromQuery);
        setCommunityPage(communityPageFromQuery);
    }, [query, communityId]);

    // Get user's moderated communities from API
    useEffect(() => {
        async function fetchModeratedCommunities() {
            let params: ModeratedCommunitiesParams = {
                userId: userId,
                page: communityPage,
            };
            try {
                let { list, pagination } = await getModeratedCommunities(params);
                setModeratedCommunities(list);
                let index = list.findIndex((x) => x.id === parseParam(communityId));
                if (index === -1) index = 0;
                setSelectedCommunity(list[index]);
                setUserPage(1);
                setTotalCommunityPages(pagination.total);
            } catch (error) {
                // Show error page
            }
        }
        fetchModeratedCommunities();
    }, [communityPage, userId]);

    // Get selected community's admitted users from API
    useEffect(() => {
        async function fetchAdmittedUsers() {
            if (selectedCommunity !== undefined) {
                let params: GetUsersByAcessTypeParams = {
                    accessType: AccessType.ADMITTED,
                    moderatorId: userId,
                    communityId: selectedCommunity?.id as number,
                    page: userPage,
                };
                try {
                    let { list, pagination } = await getUsersByAccessType(params);
                    setUserList(list);
                    setTotalUserPages(pagination.total);
                } catch (error) { }
            }
        }
        fetchAdmittedUsers();
    }, [selectedCommunity, userPage, userId]);

    function setCommunityPageCallback(page: number): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}?communityPage=${page}&userPage=${userPage}`,
        });
        setCommunityPage(page);

        setModeratedCommunities(undefined);
    }

    function setSelectedCommunityCallback(community: CommunityResponse): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}?communityPage=${communityPage}&userPage=${userPage}`,
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
                                            communityPage={communityPage}
                                        />
                                    </div>
                                )}

                                {!selectedCommunity && (
                                    <Spinner />)
                                }

                                {selectedCommunity && userList && (
                                    <div>
                                        {(tab === "admitted") && (
                                            <div className="card-body">
                                                <AdmittedMembersContent params={{
                                                    //userList: userList,
                                                    selectedCommunity: selectedCommunity,
                                                    //currentPage: userPage,
                                                    //totalPages: totalUserPages,
                                                    currentCommunityPage: communityPage,
                                                    //setCurrentPageCallback: setCommunityPageCallback,
                                                }} />
                                            </div>
                                        )}
                                        {(tab === "banned") && (
                                            <div className="card-body">
                                                <BannedUsersContent params={{
                                                    userList: userList,
                                                    selectedCommunity: selectedCommunity,
                                                    currentPage: userPage,
                                                    totalPages: totalUserPages,
                                                    currentCommunityPage: communityPage,
                                                    setCurrentPageCallback: setCommunityPageCallback,
                                                }} />
                                            </div>
                                        )}
                                        {(tab === "invited") && (
                                            <div className="card-body">
                                                <InvitedMembersContent params={{
                                                    userList: userList,
                                                    selectedCommunity: selectedCommunity,
                                                    currentPage: userPage,
                                                    totalPages: totalUserPages,
                                                    currentCommunityPage: communityPage,
                                                    setCurrentPageCallback: setCommunityPageCallback,
                                                }} />
                                            </div>
                                        )}

                                        {(tab === "requested") && (
                                            <div className="card-body">
                                                <RequestedUsersContent params={{
                                                    userList: userList,
                                                    selectedCommunity: selectedCommunity,
                                                    currentPage: userPage,
                                                    totalPages: totalUserPages,
                                                    currentCommunityPage: communityPage,
                                                    setCurrentPageCallback: setCommunityPageCallback,
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
