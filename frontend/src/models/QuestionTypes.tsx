import {User} from "./UserTypes"
import {Community} from "./CommunityTypes"

export type Question = {
    id: number,
    title: string,
    body: string,
    owner: string,
    imageUrl?: string,
    date: string,
    community: string,
    votes?: number,
    voteTotal: number,
    myVote?: boolean,
    answers?:string[]
}

export type Vote = {
    id: number,
    vote: boolean,
    owner: User,
}