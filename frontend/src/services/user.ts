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

async function getUserFromApi(id: number) : Promise<User>{
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
async function getNotificationFromApi(id:number): Promise<Notification >{
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

async function getKarmaFromApi(id:number): Promise<Karma>{
    const response = await api.get(`/karma/${id}`);
    if(response.data != 200){
        throw new Error("Error fetching karma from API")
    }
    
    let karma: Karma = {
        karma: response.data.karma
    }
    return karma;
    
}



async function getUser(id:number): Promise<User>{
    let user : User = await getUser(id);
    user.karma = await getKarmaFromApi(id);
    return user;
}

export default {
    updateUserInfo,
    getUser
}
