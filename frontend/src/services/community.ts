import { api, getPaginationInfo, noContentPagination, PaginationInfo} from "./api";
import {Community, CommunityCard} from "../models/CommunityTypes"
import { AccessType , ACCESS_TYPE_ARRAY_ENUM , ACCESS_TYPE_ARRAY } from "./Access";
import { th } from "date-fns/locale";
import { ErrorResponse } from "@remix-run/router";
import { getUserFromURI } from "./user";
import { use } from "i18next";



export async function createCommunity( name : string , description: string){
    if(!window.localStorage.getItem("userId")){

        return false;
    }
    let resp;
    try{
        resp = await api.post(`/communities` , { name , description});
    }catch(error : any){
        resp = error.response;
    }
    if(resp.status == 400){
        if(resp.data.code == "community.name.taken"){

            return false;
        }
    }
    if(resp.status >= 300)
        throw new Error()
    let communityId = parseInt(resp.headers.location.split('/').pop());
    return communityId;
}

export async function getCommunityFromUrl(communityURL : string){
    let path = new URL(communityURL).pathname

   return await getCommunity(parseInt(path.split("/").pop() as string));
}

export async function getCommunityNotifications(id : number){
    let res;
    try{
    res = await api.get(`/notifications/communities/${id}`);
    }catch(e: any){
        return 0;
    }

    if(res.status === 204)
        return 0;

    return res.data.notifications;
}

export async function getCommunity(communityId: number ): Promise<Community>{
    let resp;
    if(!window.localStorage.getItem("userId")){
        resp = await api.get(`/communities/${communityId}`);
    }else{
        let id = window.localStorage.getItem("userId")
        resp = await api.get(`/communities/${communityId}?userId=${id}`);
    }


    if(resp.status !== 200)
        return null as unknown as Community;
    
    return  {
        id: resp.data.id,
        name: resp.data.name,
        description: resp.data.description,
        userCount: resp.data.userCount,
        moderator: await getUserFromURI(resp.data.moderator)
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

    if(res.status !== 200)
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

    if(res.status !== 200)
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
    let url = new URL(`/communities/${params.communityId}/user/${id}/${params.type}`)
    if(params.page)
        url.searchParams.append("page" , params.page.toString());

    let res = await api.get(url.toString());
    if( res.status !== 200)
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
    let res = await api.get(`/community-cards/moderated?` + searchParams.toString());

    if(res.status === 204)
        return {
            list: [],
            pagination: noContentPagination
        }

    if(res.status !== 200)
       throw new Error();
       
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}

export type CommunitiesByAcessTypeParams = {
    accessType: AccessType,
    requestorId: number,
    page?: number
}

export async function getCommunitiesByAccessType(p: CommunitiesByAcessTypeParams): Promise<{
    list: CommunityCard[],
    pagination: PaginationInfo
}> {
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
        (key: string) => { searchParams.append(key, new String(p[key as keyof CommunitiesByAcessTypeParams]).toString()) }
    )
    let res = await api.get(`/community-cards/${ACCESS_TYPE_ARRAY[p.accessType]}?` + searchParams.toString());

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


export type SetAccessTypeParams = {
    communityId: number,
    targetId: number,
    newAccess : AccessType
}


export async function canAccess(userId: number , communityId:number){
    try{
    let res = await api.get(`/communities/${communityId}/user/${userId}` );
    return res.data.canAccess;
    }catch(e : any){
        if(e.response.status === 403 || e.response.status === 401)
            throw new Error("unauthorized");

        if( e.response.status === 404)
            throw new Error("not.found");
        
        return false;
    }

}
export async function setAccessType(p:SetAccessTypeParams) {
    let body = { accessType: ACCESS_TYPE_ARRAY_ENUM[p.newAccess] }
    let res = await api.put(`/communities/${p.communityId}/user/${p.targetId}` , body );
    if(res.status >= 300)
       throw new Error();
    
}

export type InviteCommunityParams = {
    communityId: number,
    email: string
}

export async function inviteUserByEmail(p:InviteCommunityParams){
    try{
    let res = await api.put(`/communities/${p.communityId}/invite` , {email:p.email})
    return true;
    }catch(e){
        return false
    }
}