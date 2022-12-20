import { api , apiBaseURL ,  apiURLfromApi,  getPaginationInfo, noContentPagination, PaginationInfo} from './api' 
import {Notification, User, Karma} from "../models/UserTypes";
import { AccessType , ACCESS_TYPE_ARRAY } from "./Access";
export async function updateUserInfo(userURI : string){
    let response = await  apiURLfromApi.get(userURI);
    let user  = response.data;
    console.log(window.localStorage.getItem("token"));
    console.log(user);
    window.localStorage.setItem(
        "userId" , response.data.id
    )
    window.localStorage.setItem( "username" , response.data.username);
    window.localStorage.setItem( "email" , response.data.email);
    
}

export async function getUserFromURI(userURI: string){
    let response = await apiURLfromApi.get(userURI);

    if(response.status !== 200)
        return false

        
    let user : User = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email
    }
    return user;
}

export async function getUserFromApi(id: number) : Promise<User>{
    const response = await api.get(`/user/${id}`);
    if(response.status !== 200)
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
    if(response.status !== 200)
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
    if(response.data !== 200){
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


export type UserSearchParams = {
    query? :string ,
    page?:number , 
    size?:number
}

export async function searchUser(p :UserSearchParams) : Promise<{list: User[] , pagination: PaginationInfo}>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain
    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof UserSearchParams]  ).toString()) }
    )
    console.log(searchParams);
    let res = await api.get("/users?" + searchParams.toString);
    console.log(res);
    if(res.status !== 200)
        throw new Error();
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1);
    }
}

export type UsersByAcessTypeParams = {
    accessType: AccessType,
    moderatorId: number,
    communityId: number,
    page?: number
}

export async function getUsersByAccessType( p : UsersByAcessTypeParams) : Promise<{
    list:User[],
    pagination: PaginationInfo
}> {    
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof UsersByAcessTypeParams]).toString()) }
    )
    let res = await api.get(`/users/${ACCESS_TYPE_ARRAY[p.accessType]}?` + searchParams.toString());
    
    if(res.status == 204)
        return {
            list: [],
            pagination: noContentPagination
        }

    if( res.status != 200)
        new Error();
    return  {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
    
}

