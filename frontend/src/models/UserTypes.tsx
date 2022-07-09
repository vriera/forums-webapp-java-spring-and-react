
export type User = {
    id: number
    username: string
    email: string
    password: string
}

export type Karma = {
    user: User
    karma: number
}

export type Notification = {
    user: User
    requests: number
    invites: number
    total: number
}

