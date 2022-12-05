
export type User = {
    id: number
    username: string
    email: string
    karma?: Karma,
    notifications?: Notification
    
}


export type UserPreview = {
    username: string,
    uri: string
}

export type Karma = {
    karma: number
}

export type Notification = {
    requests: number
    invites: number
    total: number
}

