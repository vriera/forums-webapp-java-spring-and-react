import React, {useEffect, useState} from "react";
import {Answer, AnswerResponse} from "./../models/AnswerTypes"
import { useTranslation } from "react-i18next"
import { User } from "../models/UserTypes";
import { Community } from "../models/CommunityTypes";
import {deleteVote, vote} from "../services/answers";
import {Question} from "../models/QuestionTypes";
import {getQuestion, getQuestionUrl} from "../services/questions";
import { format } from 'date-fns'
import {getUserFromApi, getUserFromURI} from "../services/user";

export default function AnswerCard(props: {answer: AnswerResponse, question: Question}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()
    const [user , setUser] = useState<User>();

    const userId = parseInt(window.localStorage.getItem("userId") as string);
    const username = window.localStorage.getItem("username") as string;


    useEffect( () => {
        async function ownerLoad() {
            const _user = await getUserFromURI(props.answer.owner);
            setUser(_user);
        }
        ownerLoad()
    }, []);


    function upVote() {
        const load = async () => {
            let response = await vote(userId,props.answer.id,true)
            window.location.reload()
        };
        load();
    }
    
    function downVote() {
        const load = async () => {
            let response = await vote(userId,props.answer.id,false)
            window.location.reload()
        };
        load();
    }
    
    function nullVote() {
        const load = async () => {
            let response = await deleteVote(userId,props.answer.id)
            window.location.reload()
        };
        load();
    }

    return(

        <div className="card shadowOnHover">
            <div className="d-flex card-body m-0">
                <div className="row">
                    <div className="col-3">
                        {props.answer.myVote == true &&
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={nullVote}>
                            <img src={`${process.env.PUBLIC_URL}/resources/images/votes.png`} width="30" height="30"/>
                        </button>
                        }
                        {(props.answer.myVote == null || props.answer.myVote == false) &&
                        
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={upVote}>
                            <img src={`${process.env.PUBLIC_URL}/resources/images/upvotep.png`} width="30" height="30"/>
                        </button>
                        }
                        <div className="d-flex ">
                            <p className="h5 ml-2">{props.answer.votes}</p>
                        </div>
                        
                        {props.answer.myVote == false && 
                        <button className="clickable btn b-0 p-0" onClick={nullVote}>
                            <img src={`${process.env.PUBLIC_URL}/resources/images/voted.png`} width="30" height="30"/>
                        </button>
                        }
                        {(props.answer.myVote == true || props.answer.myVote == null) &&
                            
                        <button className="clickable btn b-0 p-0" onClick={downVote}>
                            <img src={`${process.env.PUBLIC_URL}/resources/images/downvotep.png`} width="30" height="30"/>
                        </button>
                            
                        }
                    </div>
                </div>
                <div className="row">
                    <div className="col mb-0">
                        <p className="h2 text-primary mb-0">
                            {props.answer.title}
                        </p>
                        <div className="d-flex flex-column justify-content-center">
                            <div className="justify-content-center mb-0">
                                    <p><span
                                        className="badge badge-primary badge-pill">{props.question.community.name}</span>
                                    </p>
                                </div>
                            { user &&
                                <div className="justify-content-center mb-0">
                                    <p className="h6">{t("question.answeredBy")} {user.username}</p>
                                </div>

                            }

                        </div>
                        <div className="text-wrap-ellipsis justify-content-center">
                            <p className="h5">{props.answer.body}</p>
                        </div>
                        <div className="d-flex align-items-center ">
                            <div className="h4">
                                <i className="fas fa-calendar"></i>
                            </div>
                            <p className="ml-3 h6">{format(Date.parse(props.answer.time), 'dd/MM/yyyy hh:mm:ss')}</p>
                        </div>
                    </div>
                </div>

                
            </div>

        </div>
    )
}