import { Question, QuestionPreview } from '../models/QuestionTypes';
import parse from "parse-link-header";
import { api } from "./api";


export type CommunitySearchParams = {
    query? :string , 
    // filter?:number , 
    // order?:number ,
    page?:number , 
    size?:number , 
    communityId?:number
}

export type QuestionSearchParameters = {

}

export async function getQuestion(questionId: number): Promise<Question> {
    const response = await api.get(`/questions/${questionId}`);
    return response.data;
}


export type QuestionSearchParams = {
    query? :string , 
    filter?:number , 
    order?:number ,
    page?:number , 
    size?:number , 
    communityId?:number
}

export async function searchQuestions(p :CommunitySearchParams){
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