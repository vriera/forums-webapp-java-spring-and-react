import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import Background from "../../../components/Background";
import DashboardPane from "../../../components/DashboardPane";
import {CommunityResponse} from "../../../models/CommunityTypes";
import {User} from "../../../models/UserTypes";
import DashboardCommunitiesTabs from "../../../components/DashboardCommunityTabs";
import Pagination from "../../../components/Pagination";
import {useNavigate, useParams} from "react-router-dom";
import {createBrowserHistory} from "history";
import {
    getModeratedCommunities,
    ModeratedCommunitiesParams,
    setAccessType,
    SetAccessTypeParams,
} from "../../../services/community";
import ModeratedCommunitiesPane from "../../../components/DashboardModeratedCommunitiesPane";
import {getUserFromEmail, getUsersByAccessType, UsersByAcessTypeParams,} from "../../../services/user";
import {AccessType} from "../../../services/Access";
import {useQuery} from "../../../components/UseQuery";
import Spinner from "../../../components/Spinner";
import ModalPage from "../../../components/ModalPage";
import {t} from "i18next";
import Alert from '@mui/material/Alert';


type UserContentType = {
    userList: User[];
    selectedCommunity: CommunityResponse;
    currentPage: number;
    totalPages: number;
    currentCommunityPage: number;
    setCurrentPageCallback: (page: number) => void;
};

const MemberCard = (props: {
    user: User;
    kickUserCallback: () => void;
    banUserCallback: () => void;
}) => {
    return (
        <div className="card">
            <div className="d-flex flex-row justify-content-end">
                <p className="h4 card-title position-absolute start-0 ml-3 mt-2">
                    {props.user.username}
                </p>
                <button className="btn mb-0" onClick={props.kickUserCallback}>
                    <div className="h4 mb-0">
                        <i className="fas fa-user-minus"></i>
                    </div>
                </button>
                <button className="btn mb-0" onClick={props.banUserCallback}>
                    <div className="h4 mb-0">
                        <i className="fas fa-user-slash"></i>
                    </div>
                </button>
            </div>
        </div>
    );
};

const AdmittedMembersContent = (props: { params: UserContentType }) => {
    const userId = parseInt(window.localStorage.getItem("userId") as string);
    const [showAlert, setShowAlert] = useState(false);
    const [successAlert, setSucessAlert] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [email, setEmail] = useState('');
    const {t} = useTranslation();

    //create your forceUpdate hook
    const [value, setValue] = useState(0); // integer state

    const [showModalForKick, setShowModalForKick] = useState(false);
    const handleCloseModalForKick = () => {
        setShowModalForKick(false);
    };
    const handleShowModalForKick = () => {
        setShowModalForKick(true);
    };

    async function handleKick(userIdKick: number) {
        let params: SetAccessTypeParams = {
            communityId: props.params.selectedCommunity.id,
            userId: userIdKick,
            accessType: AccessType.KICKED,
            moderatorId: userId
        };
        await setAccessType(params, (message) => {
            // Configurar el estado para mostrar la alerta y establecer el mensaje de error
            setShowAlert(true);
            setErrorMessage(message);
        }, t);
        setValue(value + 1); //To force update
        handleCloseModalForKick();
    }

    const [showModalForBan, setShowModalForBan] = useState(false);
    const handleCloseModalForBan = () => {
        setShowModalForBan(false);
    };
    const handleShowModalForBan = () => {
        setShowModalForBan(true);
    };

    async function handleBan(userId: number) {

        let params: SetAccessTypeParams = {
            communityId: props.params.selectedCommunity.id,
            userId: userId,
            accessType: AccessType.BANNED,
            moderatorId: userId
        };
        await setAccessType(params, (message) => {
            // Configurar el estado para mostrar la alerta y establecer el mensaje de error
            setShowAlert(true);
            setErrorMessage(message);
        }, t);
        setValue(value + 1); //To force update
        handleCloseModalForBan();
    }

    async function handleInvite() {
        let btn = document.getElementById("inviteBtn") as HTMLSelectElement;
        let input = document.getElementById("email") as HTMLSelectElement;
        btn.disabled = true;


        try {
            let user = await getUserFromEmail(input.value);
            if (user && user.data && user.data.length > 0) {
                let params: SetAccessTypeParams = {
                    communityId: parseInt(props.params.selectedCommunity.id as unknown as string),
                    userId: user.data[0].id,
                    accessType: AccessType.INVITED,
                    moderatorId: userId
                };
                let success = await setAccessType(params, (message) => {
                    // Configurar el estado para mostrar la alerta y establecer el mensaje de error
                    setEmail('');
                    setErrorMessage(message);
                    setShowAlert(true);

                }, t); // Pasar la función de traducción a setAccessType
                if (success) {
                    setEmail('');
                    setSucessAlert(true);

                }
            } else {
                setEmail('');
                setErrorMessage(t("dashboard.userNotExist"));
                setShowAlert(true);

            }
        } catch (e) {
            // Muestra la alerta en caso de error
            setShowAlert(true)
            setEmail('');
        }

        btn.disabled = false;
    }

    const handleCloseAlert = () => {
        setShowAlert(false);
        setErrorMessage('');
    };

    const handleCloseSuccess = () => {
        setSucessAlert(false);
    };

    return (
        <>
            {/* Different titles according to the corresponding tab */}
            <p className="h3 text-primary">{t("dashboard.members")}</p>

            {/* If members length is greater than 0  */}
            <div className="overflow-auto">
                {props.params.userList &&
                    props.params.userList.length > 0 &&
                    props.params.userList.map((user: User) => (
                        <>
                            <ModalPage
                                buttonName={t("dashboard.KickUser")}
                                show={showModalForKick}
                                onClose={handleCloseModalForKick}
                                onConfirm={() => handleKick(user.id)}
                            />
                            <ModalPage
                                buttonName={t("dashboard.BanUser")}
                                show={showModalForBan}
                                onClose={handleCloseModalForBan}
                                onConfirm={() => handleBan(user.id)}
                            />

                            <MemberCard
                                user={user}
                                key={user.id}
                                kickUserCallback={handleShowModalForKick}
                                banUserCallback={handleShowModalForBan}
                            />
                        </>
                    ))}
                {props.params.userList && props.params.userList.length === 0 && (
                    // Show no content image
                    <div className="ml-5">
                        <p className="row h1 text-gray">
                            {t("dashboard.noMembers")}
                        </p>
                        <div className="d-flex justify-content-center">
                            <img
                                className="row w-25 h-25"
                                src={require("../../../images/empty.png")}
                                alt="Nothing to show"
                            />
                        </div>
                    </div>
                )}
                {props.params.userList && props.params.userList.length > 0 && (
                    <Pagination
                        currentPage={props.params.currentPage}
                        setCurrentPageCallback={props.params.setCurrentPageCallback}
                        totalPages={props.params.totalPages}
                    />
                )}

                <div className="form-group mx-5">
                    <div className="input-group">
                        <input
                            className="form-control rounded"
                            type="input"
                            name="email"
                            id="email"
                            placeholder={t("email")}
                        />
                        <input
                            onClick={handleInvite}
                            className="btn btn-primary"
                            type="submit"
                            value={t("dashboard.invite")}
                            id="inviteBtn"
                        />
                    </div>
                    {/* Mostrar la alerta si showAlert es true */}
                    {showAlert && <Alert severity="error" onClose={handleCloseAlert}>{errorMessage}</Alert>}
                    {successAlert &&
                        <Alert severity="success"
                               onClose={handleCloseSuccess}>{t("dashboard.cantInvite")}</Alert>} //todo: cambiar nombre
                </div>
            </div>
        </>
    );
};

const AdmittedUsersPane = (props: { params: UserContentType }) => {
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

            <hr/>

            <DashboardCommunitiesTabs
                activeTab="admitted"
                communityId={props.params.selectedCommunity.id}
                communityPage={props.params.currentCommunityPage}
            />
            <div className="card-body">
                <AdmittedMembersContent params={props.params}/>
            </div>
        </div>
    );
};

// Follows endpoint /dashboard/communities/:communityId/admitted?communityPage={number}&userPage={number}
const AdmittedUsersPage = () => {
    const navigate = useNavigate();
    const history = createBrowserHistory();

    let {communityId} = useParams();
    let pagesParam = parseParam(useParams().userPage);
    let communityPageParam = parseParam(useParams().communityPage);

    const query = useQuery();

    const [moderatedCommunities, setModeratedCommunities] =
        useState<CommunityResponse[]>();
    const [selectedCommunity, setSelectedCommunity] = useState<CommunityResponse>();

    const [communityPage, setCommunityPage] = useState(communityPageParam);
    const [totalCommunityPages, setTotalCommunityPages] = useState(-1);

    const [userList, setUserList] = useState<User[]>();

    const [userPage, setUserPage] = useState(pagesParam);
    const [totalUserPages, setTotalUserPages] = useState(-1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${communityPageFromQuery}&userPage=${userPageFromQuery}`,
        });
        setUserPage(userPageFromQuery);
        setCommunityPage(communityPageFromQuery);
    }, [query, communityId]);

    // Get user's moderated communities from API
    useEffect(() => {
        async function fetchModeratedCommunities() {
            let params: ModeratedCommunitiesParams = {
                moderatorId: userId,
                page: communityPage,
            };
            try {
                let {list, pagination} = await getModeratedCommunities(params);
                if (list.length === 0) setLoading(false);
                setModeratedCommunities(list);
                let index = list.findIndex((x) => x.id === parseParam(communityId));
                if (index === -1) index = 0;
                setSelectedCommunity(list[index]);
                setUserPage(1);
                setTotalCommunityPages(pagination.total);
            } catch (error: any) {
                setError(error);
                setLoading(false)
                //navigate(`/${error.code}`);
            }
        }

        fetchModeratedCommunities();
    }, [communityPage, userId]);

    // Get selected community's admitted users from API
    useEffect(() => {
        async function fetchAdmittedUsers() {
            if (selectedCommunity !== undefined) {
                let params: UsersByAcessTypeParams = {
                    accessType: AccessType.ADMITTED,
                    communityId: selectedCommunity?.id as number,
                    page: userPage,
                };
                try {
                    let {list, pagination} = await getUsersByAccessType(params);
                    setUserList(list);
                    setTotalUserPages(pagination.total);
                } catch (error: any) {
                    setError(error);
                    setLoading(false)
                    //navigate(`/${error.code}`);
                }
            }
        }

        fetchAdmittedUsers();
    }, [selectedCommunity, userPage, userId]);

    function setCommunityPageCallback(page: number): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${page}&userPage=${userPage}`,
        });
        setCommunityPage(page);

        setModeratedCommunities(undefined);
    }

    function setSelectedCommunityCallback(community: CommunityResponse): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${community.id}/admitted?communityPage=${communityPage}&userPage=${userPage}`,
        });
        setSelectedCommunity(community);
    }

    function setUserPageCallback(page: number): void {
        history.push({
            pathname: `${process.env.PUBLIC_URL}/dashboard/communities/${communityId}/admitted?communityPage=${communityPage}&userPage=${page}`,
        });
        setUserPage(page);

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
                            <DashboardPane option={"communities"}/>
                        </div>

                        {/* CENTER PANE*/}
                        <div className="col-6">
                            {(!selectedCommunity || !userList) && (
                                <>
                                    <div className="white-pill mt-5">
                                        <div className="align-items-start d-flex justify-content-center my-3">
                                            {loading && (
                                                <p className="h1 text-primary bold">
                                                    <Spinner/>
                                                </p>
                                            )}
                                        </div>
                                        <hr/>
                                        <div className="card-body">
                                            {loading ? (
                                                <Spinner/>
                                            ) : (
                                                <div className="ml-5">
                                                    <p className="row h1 text-gray">
                                                        {t("dashboard.noCommunitiesModerator") as string}
                                                    </p>
                                                    <div className="d-flex justify-content-center">
                                                        <img
                                                            className="row w-25 h-25"
                                                            src={require("../../../images/empty.png")}
                                                            alt="Nothing to show"
                                                        />
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </>
                            )}
                            {selectedCommunity && userList && (
                                <AdmittedUsersPane
                                    params={{
                                        selectedCommunity: selectedCommunity,
                                        userList: userList,
                                        currentPage: userPage,
                                        totalPages: totalUserPages,
                                        currentCommunityPage: communityPage,
                                        setCurrentPageCallback: setUserPageCallback,
                                    }}
                                />
                            )}
                        </div>

                        {/* MODERATED COMMUNITIES SIDE PANE */}
                        <div className="col-3">
                            {(!moderatedCommunities || !selectedCommunity) && (
                                <>
                                    <div className="white-pill mt-5 mx-3">
                                        <div className="card-body">
                                            <div className="align-items-start d-flex justify-content-center my-3">
                                                {loading && (
                                                    <p className="h1 text-primary bold">
                                                        <Spinner/>
                                                    </p>
                                                )}
                                            </div>
                                            <hr/>
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

export default AdmittedUsersPage;

function parseParam(n: string | undefined): number {
    return parseInt(n as string);
}
