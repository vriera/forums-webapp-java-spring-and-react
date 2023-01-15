import React, {useEffect, useState} from "react";
import {Question} from "./../models/QuestionTypes"
import { useTranslation } from "react-i18next"
import {User} from "../models/UserTypes";
import {Community} from "../models/CommunityTypes";
import {deleteVote, vote} from "../services/questions";
import {format, set} from "date-fns";
import {getCommunityFromUrl} from "../services/community";
import Spinner from "./Spinner";


export default function QuestionCard(props: {question: Question, user: User}){ //despues hay que pasarle todas las comunidades y en cual estoy
    const {t} = useTranslation()
    const[image,setImage] = useState<string>()
    useEffect(() => {
        const load = async () => {
            setImage(props.question.image)

        };
        load();
    }, [props.question]);
    const [community , setCommunity ] = useState<Community>();
    useEffect(() => {
        const load = async () => {
            let _community = await getCommunityFromUrl(props.question.community);
            setCommunity(_community);
        };
        load();
    }, [props.question]);

    function upVote() {
        const load = async () => {
            let response = await vote(props.user.id,props.question.id,true)
            window.location.reload()
        };
        load();
    }

    function downVote() {
        const load = async () => {
            let response = await vote(props.user.id,props.question.id,false)
            window.location.reload()
        };
        load();
    }

    function nullVote() {
        const load = async () => {
            let response = await deleteVote(props.user.id,props.question.id)
            window.location.reload()
        };
        load();
    }


    return(

        <div className="card shadowOnHover">
            <div className="d-flex card-body m-0">
                <div className="row">
                    <div className="col-3">
                        {props.question.myVote === true &&
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={nullVote}>
                            <img src={require('../images/votes.png')} width="30" height="30"/>
                        </button>
                        }
                        {(props.question.myVote == null || props.question.myVote === false) &&
                        
                        <button className="clickable btn b-0 p-0" aria-pressed="true" onClick={upVote}>
                            <img src={require('../images/upvotep.png')} width="30" height="30"/>
                        </button>
                        }
                        <div className="d-flex ">
                            <p className="h5 ml-2">{props.question.votes}</p>
                        </div>
                        
                        {props.question.myVote === false && 
                        <button className="clickable btn b-0 p-0" onClick={nullVote}>
                            <img src={require('../images/voted.png')} width="30" height="30"/>
                        </button>
                        }
                        {(props.question.myVote === true || props.question.myVote == null) &&
                            
                        <button className="clickable btn b-0 p-0" onClick={downVote}>
                            <img src={require('../images/downvotep.png')} width="30" height="30"/>
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
                                { community &&
                                <p><span className="badge badge-primary badge-pill">{community.name}</span></p>
                                }
                                {
                                    !community && <Spinner/>
                                }
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
                            <p className="ml-3 h6">{format(Date.parse(props.question.time), 'dd/MM/yyyy hh:mm:ss')}</p>
                        </div>
                        <div>
                            {
                                props.question.image &&  <img src={image} alt={props.question.title} />
                            }

                        </div>
                    </div>
                </div>

                
            </div>

        </div>
    )
}