import { cp } from "fs";
import { resolve } from "path";
import { isReturnStatement, updateFor } from "typescript";
import { api, apiURLfromApi, getPaginationInfo, PaginationInfo} from "./api";
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

   return await getCommunity(parseInt(path.split("/").pop() as string));
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

export async function searchCommunity(p :CommunitySearchParams) : Promise<{list: CommunityCard[] , pagination: PaginationInfo}>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof CommunitySearchParams]  ).toString()) }
    )
    let res = await api.get("/community-cards?" + searchParams.toString());
    // console.log(res);
    if(res.status != 200)
        throw new Error();
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}

export async function getAllowedCommunity(p :AskableCommunitySearchParams) : Promise<{list: CommunityCard[] , pagination: PaginationInfo}>{
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
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}

function idFromUrl( url: string){
    let path = new URL(url).pathname
    return parseInt(path.split("/").pop() as string)
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

export type ModeratedCommunitiesParams = {
    userId : number ,
    page? : number
}
export async function getModeratedCommunities(p : ModeratedCommunitiesParams) : Promise<{list: CommunityCard[] , pagination: PaginationInfo}>{

    let searchParams = new URLSearchParams();
    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof ModeratedCommunitiesParams]).toString()) }
    )
    let res = await api.get(`/community-card/moderated?` + searchParams.toString());
    if(res.status != 200)
       throw new Error();
       
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}





