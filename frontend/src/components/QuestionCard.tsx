import {Question} from "./../models/QuestionTypes"
import { useTranslation } from "react-i18next"


export default function QuestionCard(props: {question: Question}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()

    return(

        <div className="white-pill mt-5">
            <div className="card-body">
                <div className="d-flex justify-content-center">
                    <p className="h1 text-primary">
                        {props.question.title}
                    </p>
                </div>
            </div>

            <div className="row">
                <div className="col">
                    <div className="d-flex flex-column justify-content-center ml-3">
                        <div className="justify-content-center">
                            <p><span className="badge badge-primary badge-pill">{props.question.community.name}</span></p>
                        </div>
                        <div className="justify-content-center">
                            <p className="h6">{t("question.askedBy")} {props.question.owner.username}</p>
                        </div>

                    </div>
                    <div className="col-12 text-wrap-ellipsis justify-content-center">
                        <p className="h5">{props.question.body}</p>
                    </div>
                    <div className="d-flex ml-3 align-items-center ">
                        <div className="h4">
                            <i className="fas fa-calendar"></i>
                        </div>
                        <p className="ml-3 h6">{props.question.date}</p>
                    </div>

                </div>
            </div>
        </div>
    )
}