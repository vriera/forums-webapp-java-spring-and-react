import {User, UserPreview} from "./UserTypes"
import {Community, CommunityPreview} from "./CommunityTypes"
import { Answer } from "./AnswerTypes"

export type Question = {
    id: number,
    title: string,
    body: string,
    owner: User,
    imageUrl?: string,
    date: string,
    community: Community,
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