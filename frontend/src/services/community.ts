import { cp } from "fs";
import { resolve } from "path";
import { isReturnStatement, updateFor } from "typescript";
import { api, apiURLfromApi} from "./api";
import {Community} from "../models/CommunityTypes"



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
    // filter?:number , 
    // order?:number ,
    page?:number , 
    size?:number , 
    communityId?:number
}

export async function searchCommunity(p :CommunitySearchParams){
    let url = new URL("/community/search/questions");
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {url.searchParams.append(key , new String(p[key as keyof CommunitySearchParams]  ).toString()) }
    )

    /* forma small brain
    if(p.query)
        url.searchParams.append("query" , p.query);
    if(p.filter)
        url.searchParams.append("filter" , p.filter.toString());
    if(p.order)
        url.searchParams.append("order" , p.order.toString());
    if(p.page)
        url.searchParams.append("page",p. page.toString() );
    if(p.size)
        url.searchParams.append("size" , p.size.toString());
    if(p.communityId)
        url.searchParams.append("communityId" , p.communityId.toString());
    */   
    if(window.localStorage.getItem("userId")){
       url.searchParams.append("userId" , new String(window.localStorage.getItem("userId")).toString());
    }
    // console.log(url.toString())
    let res = await api.get(url.toString());
    // console.log(res);
    if(res.status != 200)
        return false
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





