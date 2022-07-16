import React from "react";
import {Question} from "./../models/QuestionTypes"
import { useTranslation } from "react-i18next"
import {User} from "../models/UserTypes";
import {Community} from "../models/CommunityTypes";


export default function QuestionCard(props: {question: Question}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()

    function upVote() {
    
    }
    
    function downVote() {
        
    }
    
    function nullVote() {
        
    }


    return(

        <div className="card shadowOnHover">
            <div className="d-flex card-body m-0">
                <div className="row">
                    <div className="col-3">
                        {props.question.myVote == true &&
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={nullVote}>
                            <img src={process.env.PUBLIC_URL+'/resources/images/votes.png'} width="30" height="30"/>
                        </button>
                        }
                        {(props.question.myVote == null || props.question.myVote == false) &&
                        
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={upVote}>
                            <img src={process.env.PUBLIC_URL+'/resources/images/upvotep.png'} width="30" height="30"/>
                        </button>
                        }
                        <div className="d-flex ">
                            <p className="h5 ml-2">{props.question.voteTotal}</p>
                        </div>
                        
                        {props.question.myVote == false && 
                        <button className="clickable btn b-0 p-0" onClick={nullVote}>
                            <img src={process.env.PUBLIC_URL+'/resources/images/voted.png'} width="30" height="30"/>
                        </button>
                        }
                        {(props.question.myVote == true || props.question.myVote == null) &&
                            
                        <button className="clickable btn b-0 p-0" onClick={downVote}>
                            <img src={process.env.PUBLIC_URL+'/resources/images/downvotep.png'} width="30" height="30"/>
                        </button>
                            
                        }
                    </div>
                </div>
                <div className="row">
                    <div className="col mb-0">
                        <p className="h2 text-primary mb-0">
                            {props.question.title}
                        </p>
                        <div className="d-flex flex-column justify-content-center">
                            <div className="justify-content-center mb-0">
                                <p><span className="badge badge-primary badge-pill">{props.question.community.name}</span></p>
                            </div>
                            <div className="justify-content-center mb-0">
                                <p className="h6">{t("question.askedBy")} {props.question.owner.username}</p>
                            </div>
                        </div>
                        <div className="text-wrap-ellipsis justify-content-center">
                            <p className="h5">{props.question.body}</p>
                        </div>
                        <div className="d-flex align-items-center ">
                            <div className="h4">
                                <i className="fas fa-calendar"></i>
                            </div>
                            <p className="ml-3 h6">{props.question.date}</p>
                        </div>
                    </div>
                </div>

                
            </div>

        </div>
    )
}