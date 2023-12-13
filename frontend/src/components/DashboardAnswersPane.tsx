import { AnswerResponse } from "../models/AnswerTypes";
import { useTranslation } from "react-i18next";
import Pagination from "./Pagination";
import AnswerCardURI from "./AnswerCardURI";
import { createBrowserHistory } from "history";
import { useQuery } from "./UseQuery";
import { AnswersByOwnerParams, getByOwner } from "../services/answers";
import { useState, useEffect } from "react";

const DashboardAnswersPane = () => {
  const { t } = useTranslation();

  const [totalPages, setTotalPages] = useState(-1);
  const [currentPage, setCurrentPage] = useState(1);
  const [answers, setAnswers] = useState<AnswerResponse[]>();

  const history = createBrowserHistory();
  const query = useQuery();

  // Set initial page
  useEffect(() => {
    const pageFromQuery = query.get("page");

    // If page is not in query, set it to 1 as a default
    let page = pageFromQuery ? parseInt(pageFromQuery) : 1;

    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/answers?page=${pageFromQuery}`,
    });
  }, [history, query]);

  // Fetch answers from API
  useEffect(() => {
    const userId = parseInt(window.localStorage.getItem("userId") as string);

    async function fetchUserAnswer() {
      let params: AnswersByOwnerParams = {
        ownerId: userId,
        page: currentPage,
      };
      try {
        let { list, pagination } = await getByOwner(params);
        
        setAnswers(list);
        setTotalPages(pagination.total);
      } catch {
        setAnswers([]);
      }
    }
    fetchUserAnswer();
  }, [currentPage]);

  function setCurrentPageCallback(page: number) {
    setCurrentPage(page);
    history.push({
      pathname: `${process.env.PUBLIC_URL}/dashboard/questions?page=${currentPage}`,
    });
    setAnswers([]);
  }

  return (
    <div className="white-pill mt-5">
      <div className="card-body overflow-hidden">
        <p className="h3 text-primary text-center">{t("title.answers")}</p>
        <hr />
        {answers && answers.length === 0 && (
          <div className="my-3">
            <p className="row h1 text-gray">{t("dashboard.noAnswers")}</p>
            <div className="d-flex justify-content-center">
              <img
                className="row w-25 h-25"
                src={require("../images/empty.png")}
                alt="No hay nada para mostrar"
              />
            </div>
          </div>
        )}
        {!answers && (
          // Show loading spinner
          <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        )}
        <div className="overflow-auto">
          {answers &&
            answers.length > 0 &&
            answers.map((answer: AnswerResponse) => (
              <div key={answer.id}>
                <AnswerCardURI answer={answer} />
              </div>
            ))}
        </div>
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          setCurrentPageCallback={setCurrentPageCallback}
        />
      </div>
    </div>
  );
};

export default DashboardAnswersPane;
