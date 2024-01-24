import { useNavigate, useParams } from "react-router-dom";
import Background from "../../components/Background";
import UserTabs from "../../components/UserTabs";
import { useEffect, useState } from "react";
import { CommunityResponse } from "../../models/CommunityTypes";
import {
  ModeratedCommunitiesParams,
  getModeratedCommunities,
} from "../../services/community";
import { createBrowserHistory } from "history";
import { useQuery } from "../../components/UseQuery";
import { useTranslation } from "react-i18next";
import Spinner from "../../components/Spinner";
import CommunityPreviewCard from "../../components/CommunityPreviewCard";
import Pagination from "../../components/Pagination";

const UserCommunitiesPane = () => {
  const { t } = useTranslation();

  const navigate = useNavigate();
  const query = useQuery();
  const history = createBrowserHistory();

  const [moderatedCommunities, setModeratedCommunities] = useState<
  CommunityResponse[]
  >(undefined as unknown as CommunityResponse[]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(-1);

  let { userId } = useParams();

  // Set initial page
  useEffect(() => {
    let pageFromQuery = query.get("page")
      ? parseInt(query.get("page") as string)
      : 1;
    setCurrentPage(pageFromQuery);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/user/${userId}/communities?page=${pageFromQuery}`,
    });
  }, [userId]);

  // Get user's moderated communities from API
  useEffect(() => {
    async function fetchModeratedCommunities() {
      let params: ModeratedCommunitiesParams = {
        moderatorId: parseInt(userId as string),
        page: currentPage,
      };
      try {
        let { list, pagination } = await getModeratedCommunities(params);
        setModeratedCommunities(list);
        setTotalPages(pagination.total);
      } catch (error) {
        // Show error page
        setModeratedCommunities([]);
      }
    }
    fetchModeratedCommunities();
  }, [currentPage, userId]);

  function setCurrentPageCallback(page: number) {
    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${page}`,
    });
    setModeratedCommunities(undefined as unknown as CommunityResponse[]);
  }

  return (
    <div className="white-pill mt-5">
      <div className="card-body overflow-hidden">
        <p className="h3 text-primary text-center">{t("user.communities")}</p>
        <UserTabs activeTab="communities" />
        {!moderatedCommunities && (
          <div className="my-3">
            <Spinner />
          </div>
        )}

        {/* no elements to show */}
        {moderatedCommunities && moderatedCommunities.length === 0 && (
          <div>
            <p className="row h1 text-gray">{t("community.noResults")}</p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../../images/empty.png")}
                alt="No hay nada para mostrar"
              />
            </div>
          </div>
        )}

        {moderatedCommunities &&
          moderatedCommunities.length > 0 &&
          moderatedCommunities.map((community) => (
            <CommunityPreviewCard community={community} />
          ))}
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          setCurrentPageCallback={setCurrentPageCallback}
        />
      </div>
    </div>
  );
};

const UserCommunitiesPage = () => {
  return (
    <div>
      {/* <Navbar changeToLogin={setOptionToLogin} changeToSignin={setOptionToSignin}/> */}
      <div className="wrapper">
        <div className="section section-hero section-shaped pt-3">
          <Background />

          <div className="row">
            <div className="col-3"></div>

            {/* CENTER PANE*/}
            <div className="col-6">
              <UserCommunitiesPane />
            </div>

            <div className="col-3"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserCommunitiesPage;
