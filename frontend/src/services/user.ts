import { api , apiBaseURL ,  apiURLfromApi,} from './api' 
import {Notification, User, Karma} from "../models/UserTypes";

export async function updateUserInfo(userURI : string){
    let response = await  apiURLfromApi.get(userURI);
    let user  = response.data;
    console.log(window.localStorage.getItem("token"));
    console.log(user);
    window.localStorage.setItem(
        "userId" , response.data.id
    )
}

export async function getUserFromURI(userURI: string){
    let response = await apiURLfromApi.get(userURI);

    if(response.status != 200)
        return false

        
    let user : User = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email
    }
    return user;
}

export async function getUserFromApi(id: number) : Promise<User>{
    const response = await api.get(`/users/${id}`);
    if(response.status != 200)
        throw new Error("Error fetching user from API")

    let user : User = {
        id: response.data.id,
        email: response.data.email,
        username: response.data.username
    }
    return user;   

    
}
export async function getNotificationFromApi(id:number): Promise<Notification >{
    const response = await api.get(`/notifications/${id}`);
    if(response.status != 200)
        throw new Error("Error fetching notification from API")
 
    let notification: Notification = {
        requests: response.data.requests,
        invites: response.data.invites,
        total: response.data.total
    }
    return notification;
}

export async function getKarmaFromApi(id:number): Promise<Karma>{
    const response = await api.get(`/karma/${id}`);
    if(response.data != 200){
        throw new Error("Error fetching karma from API")
    }
    
    let karma: Karma = {
        karma: response.data.karma
    }
    return karma;
    
}



export async function getUser(id:number): Promise<User>{
    let user : User = await getUser(id);
    user.karma = await getKarmaFromApi(id);
    return user;
}


export enum UserActionHasTarget {
    ADMITTED = 0,
    REQUESTED = 1,
    REQUEST_REJECTED = 2,
    INVITED = 3 ,
    INVITE_REJECTED = 4,
    LEFT = 5,
    BLOCKED_COMMUNITY = 6 ,
    KICKED = 7 ,
    BANNED = 8,

}

export type UserActionParams = {
    userId: number,
    communityId: number,
    targetId:number,
    action:number
}

export async function postUserAction( params : UserActionParams) {
    //TODO salus

}

