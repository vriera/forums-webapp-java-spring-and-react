
export type User = {
    id: number
    username: string
    email: string
    karma?: Karma,
    notifications?: Notification
    
}

export type Karma = {
    karma: number
}

export type Notification = {
    requests: number
    invites: number
    total: number
}

