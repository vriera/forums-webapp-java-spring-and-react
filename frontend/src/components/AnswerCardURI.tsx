import React, {useEffect, useState} from "react";
import { AnswerResponse} from "./../models/AnswerTypes"
import { useTranslation } from "react-i18next"
import { Community } from "../models/CommunityTypes";
import {deleteVote, vote} from "../services/answers";
import {Question} from "../models/QuestionTypes";
import { getQuestionUrl} from "../services/questions";
import {format} from "date-fns";
import {getCommunityFromUrl} from "../services/community";

export default function AnswerCardURI(props: {answer: AnswerResponse}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()
    const [community , setCommunity ] = useState<Community>();
    const [question , setQuestion] = useState<Question>();

    const userId = parseInt(window.localStorage.getItem("userId") as string);
    const username = window.localStorage.getItem("username") as string;

    useEffect( () => {
        async function fetchQuestion() {
          const question = await getQuestionUrl(props.answer.question);  
          setQuestion(question);
        } 
        fetchQuestion();
    }
    )

    useEffect(() => {
        if(!question) return
        const load = async () => {
            let _community = await getCommunityFromUrl(question.community);
            setCommunity(_community);
        };
        load();
    }, [question]);

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
                        {props.answer.myVote === true &&
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={nullVote}>
                            <img src={require('../images/votes.png')} alt="Votes icon" width="30" height="30"/>
                        </button>
                        }
                        {(props.answer.myVote == null || props.answer.myVote === false) &&
                        
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={upVote}>
                            <img src={require('../images/upvotep.png')} alt="Upvote icon" width="30" height="30"/>
                        </button>
                        }
                        <div className="d-flex ">
                            <p className="h5 ml-2">{props.answer.votes}</p>
                        </div>
                        
                        {props.answer.myVote === false && 
                        <button className="clickable btn b-0 p-0" onClick={nullVote}>
                            <img src={require('../images/voted.png')} alt="Voted icon" width="30" height="30"/>
                        </button>
                        }
                        {(props.answer.myVote === true || props.answer.myVote == null) &&
                            
                        <button className="clickable btn b-0 p-0" onClick={downVote}>
                            <img src={require('../images/downvotep.png')} alt="Downvote icon" width="30" height="30"/>
                        </button>
                            
                        }
                    </div>
                </div>
                <div className="row">
                    <div className="col mb-0">
                        <p className="h2 text-primary mb-0">
                            {props.answer.body}
                        </p>
                        {(!question || !community) && 
                            // Show spinner
                            <div className="spinner-border" role="status">
                                <span className="sr-only">Loading...</span>
                            </div>                            
                        }
                        <p className="h4 text-secondary-d mb-0">
                            {question && 
                            t("question.title") + ":" + question.title
                            }
                        </p> 
                        <div className="d-flex flex-column justify-content-center">
                            
                        {  
                            community &&
                            <div className="justify-content-center mb-0">
                                <p><span className="badge badge-primary badge-pill">{community.name}</span></p>
                            </div>
                        }
                        {
                            username &&
                            <div className="justify-content-center mb-0">
                                <p className="h6">{t("question.askedBy")} {username}</p>
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