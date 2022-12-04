import {User} from "./UserTypes"
import {Community} from "./CommunityTypes"
import { Answer } from "./AnswerTypes"

export type Question = {
    id: number,
    title: string,
    body: string,
    owner: User,
    imageUrl?: string,
    date: string,
    community: Community,
    votes?: Vote[],
    voteTotal: number,
    myVote?: boolean,
    answers?:Answer[]
}

export type QuestionPreview = {
    id: number,
    title: string,
    body: string,
    owneName: string,
    date: string,
    //community: Community,
    voteTotal: number,
}


export type QuestionSearchParameters = {

}

export type Vote = {
    id: number,
    vote: boolean,
    owner: User,
}