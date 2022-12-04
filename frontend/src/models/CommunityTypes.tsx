import {User, Notification, UserPreview} from "./UserTypes"

export type Community = {
    id: number,
    name: string,
    description: string,
    moderator?: User,
    userCount?: number,
    notifications?: Notification
}

export type CommunityPreview = {
    name:string,
    uri: string
}

export type CommunityCard = {
    id: number,
    name: string,
    moderator: UserPreview,
    userCount: number,
    uri: string
}

export type CommunitySearchParams = {
    page: number,
    size: number,
    query?: string
}
