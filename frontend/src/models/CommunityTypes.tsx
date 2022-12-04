import {User, Notification} from "./UserTypes"
import { CommunitySearchParams } from '../services/community';

export type Community = {
    id: number,
    name: string,
    description: string,
    moderator?: User,
    userCount?: number,
    notifications?: Notification
}

export type CommunityPreview = {
    id:number,
    name:string,
    userCount:number,
    moderatorName:string,
    uri: string
}

export type CommunitySearchParams = {
    page: number,
    size: number,
    query?: string
}
