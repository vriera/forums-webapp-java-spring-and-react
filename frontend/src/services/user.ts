import { api, apiURLfromApi, getPaginationInfo, noContentPagination, PaginationInfo } from './api'
import { Notification, User, Karma } from "../models/UserTypes";
import { AccessType, ACCESS_TYPE_ARRAY } from "./Access";

export async function updateUserInfo(userURI: string) {
    let response = await apiURLfromApi.get(userURI);
    let user = response.data;

    window.localStorage.setItem(
        "userId", response.data.id
    )
    window.localStorage.setItem("username", response.data.username);
    window.localStorage.setItem("email", response.data.email);

}


export type UserUpdateParams = {
    userId: number,
    newUsername: string,
    newPassword: string,
    currentPassword: string
}

export async function updateUser(p:UserUpdateParams) {
    let res;
    try{

        res = await api.put(`/users/${p.userId}` , {
            newUsername: p.newUsername,
            newPassword: p.newPassword,
            currentPassword: p.currentPassword
        });
    }catch(e: any){
        res = e.response;        
    }

    if(res.status === 400){

        if(res.data.code === "incorrect.current.password"){
            return false 
        }
    }

    if(res.status !== 200)
        throw new Error();



    return true 
}

export async function getUserFromURI(userURI: string): Promise<User> {
    let path = new URL(userURI).pathname;
    return await getUserFromApi(parseInt(path.split("/").pop() as string));
    
}

export async function getUserFromApi(id: number): Promise<User> {
    const response = await api.get(`/users/${id}`);
    if (response.status !== 200)
        throw new Error("Error fetching user from API")

    let user: User = {
        id: response.data.id,
        email: response.data.email,
        username: response.data.username
    }
    if(user.id == parseInt(window.localStorage.getItem("userId") as string) )
        user = {...user , notifications: await getNotificationFromApi(user.id)}
    return user;
}

export async function getNotificationFromApi(id: number): Promise<Notification> {
    const response = await api.get(`/notifications/${id}`);
    if (response.status !== 200)
        throw new Error("Error fetching notification from API")

    let notification: Notification = {
        requests: response.data.requests,
        invites: response.data.invites,
        total: response.data.total
    }
    return notification;
}

export async function getKarmaFromApi(id: number): Promise<Karma> {
    const response = await api.get(`/karma/${id}`);
    if (response.status !== 200) {
        throw new Error("Error fetching karma from API")
    }

    let karma: Karma = {
        karma: response.data.karma
    }
    return karma;

}



export async function getUser(id: number): Promise<User> {
    let user: User = await getUserFromApi(id);
    user.karma = await getKarmaFromApi(id);
    return user;
}


export enum UserActionHasTarget {
    ADMITTED = 0,
    REQUESTED = 1,
    REQUEST_REJECTED = 2,
    INVITED = 3,
    INVITE_REJECTED = 4,
    LEFT = 5,
    BLOCKED_COMMUNITY = 6,
    KICKED = 7,
    BANNED = 8,

}

export type UserSearchParams = {
    query?: string,
    page?: number,
    size?: number
}

export async function searchUser(p: UserSearchParams): Promise<{ list: User[], pagination: PaginationInfo }> {
    let searchParams = new URLSearchParams();
    //forma galaxy brain
    Object.keys(p).forEach(
        (key: string) => { searchParams.append(key, new String(p[key as keyof UserSearchParams]).toString()) }
    )

    let res = await api.get("/users?" + searchParams.toString());

    if (res.status !== 200)
        throw new Error();
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link, p.page || 1)
    }
}

export type UsersByAcessTypeParams = {
    accessType: AccessType,
    moderatorId: number,
    communityId: number,
    page?: number
}

export async function getUsersByAccessType(p: UsersByAcessTypeParams): Promise<{
    list: User[],
    pagination: PaginationInfo
}> {
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
        (key: string) => { searchParams.append(key, new String(p[key as keyof UsersByAcessTypeParams]).toString()) }
    )
    let res = await api.get(`/users/${ACCESS_TYPE_ARRAY[p.accessType]}?` + searchParams.toString());

    if (res.status === 204)
        return {
            list: [],
            pagination: noContentPagination
        }

    if (res.status !== 200)
        new Error();
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link, p.page || 1)
    }

}

