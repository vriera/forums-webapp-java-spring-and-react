import React, {useEffect, useState} from 'react';
import { useTranslation } from "react-i18next";
import {Question} from "../../models/QuestionTypes"
import {User} from "../../models/UserTypes"
import {Community} from "../../models/CommunityTypes"
import { getQuestion } from "../../services/questions";
//import { Pagination, Skeleton } from "@material-ui/lab";

import './ask.css'
import '../../components/CommunitiesCard'
import CommunitiesCard from "../../components/CommunitiesCard";
import QuestionCard from "../../components/QuestionCard";
import Background from "../../components/Background";


import { t } from "i18next";

const Header = () => {
    return (
        <div className="card-header">
            <span>Ask page</span>
        </div>
    );
}

const communities = [
    "Historia","matematica","logica"
]

/*
function questionApiCall(){
    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
        password: "hola"
    }
    let community: Community = {
        id: 1,
        name: "Filosofía",
        description: "Para filosofar",
        moderator: user,
        userCount: 2,
        notificationTotal: 0
    }
    let question: Question = {
        id: 1,
        title: "Hm?",
        body: "Hm",
        owner: user,
        date: "1/12/2021",
        community: "",
        voteTotal: 0
    }
    return question
}
*/

const Questions = () => {
    const { t } = useTranslation();
    const [question,setQuestion] = useState<Question>();
    useEffect(() => {
        const load = async () => {
          let  _question = await getQuestion(1);
          setQuestion(_question);
        };
        load();
    }, []);

    let user: User = {
        id: 1,
        username: "Horacio",
        email: "hor@ci.o",
    }
    let community: Community = {
        id: 1,
        name: "Matematica",
        description: "Para primer grado",
        moderator: user,
        userCount: 2,
        notifications: {
            requests: 1,
            invites: 2,
            total: 3
        }
    }

    return(
        <div className="wrapper">
            <div className="section section-hero section-shaped">
                <Background />
                <div className="float-parent-element">
                    <div className="float-child-element">
                        < CommunitiesCard title={t("landing.communities.message")} communities={communities} thisCommunity={"Matematica"}/>
                    </div>
                    <div className="float-child-element2">
                        {question && 
                        <QuestionCard question={question}/>
                        }
                        {/*{!question &&  <Skeleton width="80vw" height="50vh" animation="wave" />}*/}
                    </div>
                    <div className="float-child-element3">
                            <div className="white-pill mt-5">
                                <div className="card-body">
                                    <p className="h3 text-primary">respuesta</p>
                                    <hr></hr>
                                    <div className="form-group">
                                        <form>
                                            <input type="text">

                                            </input>
                                        </form>
                                    </div>
                                    <div className="d-flex justify-content-center mb-3 mt-3">
                                        <button type="submit" className="btn btn-primary">
                                            Enviar
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    )
}


const AskPage = () => {
    return (
        <React.Fragment>
            <Header />
            <Questions />
        </React.Fragment>

    );
};


export default AskPage;
