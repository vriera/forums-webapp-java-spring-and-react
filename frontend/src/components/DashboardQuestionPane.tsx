import { useTranslation } from "react-i18next";
import {Question} from "../models/QuestionTypes"
import QuestionCard from "./QuestionCard";

const DashboardQuestionPane = (props: {questions: Question[]}) => {
    const { t } = useTranslation()

    return (
        <div className="white-pill mt-5">
            <div className="card-body overflow-hidden">
                <p className="h3 text-primary text-center">{t("title.questions")}</p>
                <hr/>
                {props.questions.length == 0 &&
                <div>
                    <p className="row h1 text-gray">{t("dashboard.noQuestions")}</p>
                    <div className="d-flex justify-content-center">
                        <img className="row w-25 h-25" src="/resources/images/empty.png" alt="No hay nada para mostrar"/>
                    </div>
                </div>
                }
                <div className="overflow-auto">
                    {
                    props.questions.map((question: Question) =>
                    <div key={question.id}>
                        <QuestionCard question={question}/>
                    </div>
                    )                    
                    }                   
                    {/* <c:forEach items="${questions}" var="question">
                        <a className="d-block" href={"/question/view/" + question.id}>
                            <div className="card p-3 m-3 shadow-sm--hover ">
                                <div className="row">
                                    <div className="col-auto">
                                        <div className="d-flex align-items-center mt-2">
                                            {question.votes >=0 &&
                                                <div className="h4 mr-2 text-success">
                                                    <i className="fas fa-arrow-alt-circle-up"></i>
                                                </div>
                                                <p className="h5 text-success">${question.votes}</p>
                                            }
                                            {question.votes < 0 &&
                                                <div className="h4 mr-2 text-warning">
                                                    <i className="fas fa-arrow-alt-circle-up"></i>
                                                </div>
                                                <p className="h5 text-warning">${question.votes}</p>
                                            }

                                        </div>

                                    </div>

                                    <div className="col">
                                        <div className="d-flex flex-column justify-content-start ml-3">
                                            <div className="h2 text-primary">{question.title}</div>
                                            <p><span className="badge badge-primary badge-pill">{question.community.name}</span></p>
                                            <p className="h6">{t("question.askedBy")} {question.owner.username}</p>
                                        </div>
                                        <div className="col-12 text-wrap-ellipsis">
                                            <p className="h5">{question.body}</p>
                                        </div>
                                        <div className="d-flex ml-3 align-items-center ">
                                            <div className="h4">
                                                <i className="fas fa-calendar"></i>
                                            </div>
                                            <p className="ml-3 h6">{question.smartDate.date}</p>
                                        </div>
                                    </div>


                                </div>

                            </div>
                        </a>
                    </c:forEach> */}

                    {/* <!-- PAGINACION -->
                    <nav aria-label="Page navigation example" className="d-flex justify-content-center">
                        <ul className="pagination">

                            <!-- FLECHITA DE PREVIOUS; QUEDA DISABLED SI ESTOY EN = -->
                            {page != 0 &&
                                <li className="page-item">
                                    <a className="page-link" href="<c:url value="/dashboard/question/view?page=${page-1}">
                                        <i className="fa fa-angle-left"></i>
                                    </a>
                                </li>
                            }

                            {page == 0 &&
                                <li className="page-item disabled">
                                    <a className="page-link disabled" href="<c:url value="/dashboard/question/view?page=${page-1}">
                                        <i className="fa fa-angle-left"></i>
                                    </a>
                                </li>
                            }

                            <!-- NUMERICOS -->

                            <c:forEach var="num" begin="1" end="${totalPages}">
                                {num-1 == page &&
                                    <li className="page-item active"><a className="page-link" href="<c:url value="/dashboard/question/view?page=${num-1}">${num}</a></li>
                                }
                                {num-1 != page &&
                                    <li className="page-item"><a className="page-link" href="<c:url value="/dashboard/question/view?page=${num-1}">${num}</a></li>
                                }
                            </c:forEach>


                            <!-- FLECHITA DE NEXT -->
                            {page != totalPages-1 and totalPages != 0 &&
                                <li className="page-item">
                                    <a className="page-link" href="<c:url value="/dashboard/question/view?page=${page+1}" aria-label="Next">
                                        <i className="fa fa-angle-right"></i>
                                    </a>
                                </li>
                            }

                            {page == totalPages-1 || totalPages == 0 &&
                                <li className="page-item disabled">
                                    <a className="page-link " href="<c:url value="/dashboard/question/view?page=${page+1}" aria-label="Next">
                                        <i className="fa fa-angle-right"></i>
                                    </a>
                                </li>
                            }

                        </ul>
                    </nav> */}
                </div>
            </div>
        </div>
    );
}

export default DashboardQuestionPane