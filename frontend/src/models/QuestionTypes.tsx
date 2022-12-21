import {User, UserPreview} from "./UserTypes"
import {Community, CommunityPreview} from "./CommunityTypes"
import { Answer } from "./AnswerTypes"
import {SmartDate} from "./SmartDateTypes";

export type Question = {
    id: number,
    title: string,
    body: string,
    owner: User,
    image?: string,
    smartDate: SmartDate,
    community: string,
    votes: number,
    myVote?: boolean,
    answers?:Answer[]
}

export type QuestionCard= {
    id: number,
    title: string,
    body: string,
    owner: UserPreview,
    community: CommunityPreview,
    votes: number,
    date?: string,
    timestamp: string
}



export type Vote = {
    id: number,
    vote: boolean,
    owner: User,
}