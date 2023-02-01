import {User} from "./UserTypes";
import {Community} from "./CommunityTypes";
import {Question} from "./QuestionTypes";

export type Answer = {
    id: number,
    title: string,
    body: string,
    owner: User,
    verify:boolean,
    question:Question,
    myVote:boolean,
    url:string,
    time:string,
    date: string,
    votes: number,
}

export type AnswerResponse = {
    id: number,
    title: string,
    body: string,
    owner: string,
    verify:boolean,
    question: string,
    community:string,
    myVote:boolean,
    url:string,
    time:string,
    date: string,
    votes: number,
}
