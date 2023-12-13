import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { QuestionResponse } from "../models/QuestionTypes";
import Pagination from "./Pagination";
import { getQuestionByUser, QuestionByUserParams } from "../services/questions";
import QuestionPreviewCard from "./QuestionPreviewCard";
import { createBrowserHistory } from "history";
import { useQuery } from "./UseQuery";
import { useNavigate } from "react-router-dom";

const DashboardQuestionPane = () => {
  const { t } = useTranslation();
  const userId = parseInt(window.localStorage.getItem("userId") as string);

  const [totalPages, setTotalPages] = useState(-1);
  const [currentPage, setCurrentPage] = useState(1);
  const [questions, setQuestions] = useState<QuestionResponse[]>();

  const navigate = useNavigate();

  const history = createBrowserHistory();
  const query = useQuery();

  // Set initial page
  useEffect(() => {
    let pageFromQuery = query.get("page")
      ? parseInt(query.get("page") as string)
      : 1;
    setCurrentPage(pageFromQuery);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${pageFromQuery}`,
    });
  }, [query, history]);

  // Fetch questions from API
  useEffect(() => {
    async function fetchUserQuestions() {
      let params: QuestionByUserParams = {
        ownerId: userId,
        page: currentPage,
      };
      try {
        let { list, pagination } = await getQuestionByUser(params);
        setQuestions(list);
        setTotalPages(pagination.total);
      } catch {
        navigate("/error");
      }
    }
    fetchUserQuestions();
  }, [currentPage, navigate, userId]);

  function setPageAndQuery(page: number) {
    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${page}`,
    });
    setQuestions(undefined);
  }

  return (
    <div className="white-pill mt-5">
      <div className="card-body overflow-hidden">
        <p className="h3 text-primary text-center">{t("title.questions")}</p>
        <hr />
        {questions && questions.length === 0 && (
          <div>
            <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../images/empty.png")}
                alt="Nothing to show"
              />
            </div>
          </div>
        )}
        {!questions && (
          // Show loading spinner
          <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        )}
        <div className="overflow-auto">
          {questions?.map((question: QuestionResponse) => (
            <div key={question.id}>
              <QuestionPreviewCard question={question} />
            </div>
          ))}
        </div>
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          setCurrentPageCallback={setPageAndQuery}
        />
      </div>
    </div>
  );
};

export default DashboardQuestionPane;
