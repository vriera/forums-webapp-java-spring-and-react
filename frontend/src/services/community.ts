import { cp } from "fs";
import { resolve } from "path";
import { isReturnStatement, updateFor } from "typescript";
import { api, apiURLfromApi} from "./api";
import {Community, CommunityCard} from "../models/CommunityTypes"



export async function createCommunity( name : string , description: string){
    if(!window.localStorage.getItem("userId")){
        return;
    }
    let id = window.localStorage.getItem("userId")
    const resp = api.post(`/community/${id}` ,
     { name , description}
     );
    // console.log(resp); 
}

export async function getCommunityFromUrl(communityURL : string){
    let path = new URL(communityURL).pathname
    let resp;
    if(!window.localStorage.getItem("userId")){
        resp = await apiURLfromApi.get(path);
    }else{
        let id = window.localStorage.getItem("userId")
        resp = await apiURLfromApi.get(`${path}?userId=${id}`);
    }
    // console.log(resp); 

    if(resp.status != 200)
        return false
    
    return  {
        id: resp.data.id,
        name: resp.data.name,
        description: resp.data.description
    }

}

export async function getCommunity(communityId: number ): Promise<Community>{
    let resp;
    if(!window.localStorage.getItem("userId")){
        resp = await api.get(`/community/${communityId}`);
    }else{
        let id = window.localStorage.getItem("userId")
        resp = await api.get(`/community/${communityId}?userId=${id}`);
    }
    // console.log(resp); 

    if(resp.status != 200)
        return null as unknown as Community;

    return  {
        id: resp.data.id,
        name: resp.data.name,
        description: resp.data.description
    }
}







export type CommunitySearchParams = {
    query? :string , 
    page?:number 

}

export type AskableCommunitySearchParams = {
    page?:number , 
    requestorId?: number
}

export async function searchCommunity(p :CommunitySearchParams) : Promise<CommunityCard[]>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof CommunitySearchParams]  ).toString()) }
    )
    let res = await api.get("/community-cards?" + searchParams.toString());
    // console.log(res);
    if(res.status != 200)
        throw new Error();
    return res.data;
}

export async function getAllowedCommunity(p :AskableCommunitySearchParams) : Promise<CommunityCard[]>{
    //this functiion is for getting the comunities a specific user is allowed to ask to
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof AskableCommunitySearchParams]).toString()) }
    )
    let res = await api.get("/community-cards/askable?" + searchParams.toString());
    // console.log(res);
    if(res.status != 200)
        throw new Error();
    return res.data;
}



export enum ModerationListType {
    Invited = "invited",
    InviteRejected = "invite-rejected",
    Requested = "requested",
    Admitted = "admitted",
    Blocked = "blocked"
}

export function getModerationListType(name: string): ModerationListType  {
    switch(name){
        case "invited":
            return ModerationListType.Invited;
        case "invite-rejected":
            return ModerationListType.InviteRejected;
        case "blocked":
            return ModerationListType.Blocked;
        case "requested":
            return ModerationListType.Requested;
        default:
            return ModerationListType.Admitted;

    }
}

export type CommunityModerationSearchParams = {
    type : ModerationListType,
    communityId : number ,
    page? : number
}

export async function getCommunityModerationList( params : CommunityModerationSearchParams){
    if(!window.localStorage.getItem("userId"))
        return;
    
    let id = window.localStorage.getItem("userId")
    let url = new URL(`/community/${params.communityId}/user/${id}/${params.type}`)
    if(params.page)
        url.searchParams.append("page" , params.page.toString());

    let res = await api.get(url.toString());
    if( res.status != 200)
        return false;
        
    return res.data;
}

export async function getModeratedCommunities(userId: number, currentPage: number){
    let res = await api.get(`/users/${userId}/moderated?page=${currentPage}`);
    if( res.status != 200)
        return false;
    return  res.data;
}





