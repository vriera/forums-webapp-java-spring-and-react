import React from "react";
import {Answer} from "./../models/AnswerTypes"
import { useTranslation } from "react-i18next"
import { User } from "../models/UserTypes";
import { Community } from "../models/CommunityTypes";

export default function AnswerCard(props: {answer: Answer}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()

    function upVote() {
    
    }
    
    function downVote() {
        
    }
    
    function nullVote() {
        
    }


    return(

        <div className="white-pill mt-5">
            <div className="card-body">
                <div className="d-flex justify-content-center">
                    <p className="h1 text-primary">
                        {props.answer.title}
                    </p>
                </div>
            </div>

            <div className="row">
                <div className="col-auto">
                    {props.answer.myVote == true &&
                    <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={nullVote}>
                        <img src={process.env.PUBLIC_URL+'/resources/images/votes.png'} width="30" height="30"/>
                    </button>
                    }
                    {(props.answer.myVote == null || props.answer.myVote == false) &&
                    
                    <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={upVote}>
                        <img src={process.env.PUBLIC_URL+'/resources/images/upvotep.png'} width="30" height="30"/>
                    </button>
                    }

                    <p className="h5 ml-2">{props.answer.voteTotal}</p>
                    
                    {props.answer.myVote == false && 
                    <button className="clickable btn b-0 p-0" onClick={nullVote}>
                        <img src={process.env.PUBLIC_URL+'/resources/images/voted.png'} width="30" height="30"/>
                    </button>
                    }
                    {(props.answer.myVote == true || props.answer.myVote == null) &&
                        
                    <button className="clickable btn b-0 p-0" onClick={downVote}>
                        <img src={process.env.PUBLIC_URL+'/resources/images/downvotep.png'} width="30" height="30"/>
                    </button>
                        
                    }
                        
                </div>
                <div className="col">
                    <div className="d-flex flex-column justify-content-center ml-3">
                        <div className="justify-content-center">
                            <p><span className="badge badge-primary badge-pill">{props.answer.question.community.name}</span></p>
                        </div>
                        <div className="justify-content-center">
                            <p className="h6">{t("answer.owner")} {props.answer.owner.username}</p>
                        </div>

                    </div>
                    <div className="col-12 text-wrap-ellipsis justify-content-center">
                        <p className="h5">{props.answer.body}</p>
                    </div>
                    <div className="d-flex ml-3 align-items-center ">
                        <div className="h4">
                            <i className="fas fa-calendar"></i>
                        </div>
                        <p className="ml-3 h6">{props.answer.date}</p>
                    </div>
                </div>
            </div>
        </div>
    )
}