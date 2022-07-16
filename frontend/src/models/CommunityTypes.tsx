import {User, Notification} from "./UserTypes"

export type Community = {
    id: number,
    name: string,
    description: string,
    moderator: User,
    userCount: number,
    notifications: Notification
}

