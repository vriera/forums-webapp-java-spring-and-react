import { Question, QuestionCard } from '../models/QuestionTypes';
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



export async function searchQuestions(p :QuestionSearchParams) : Promise<QuestionCard[]>{
    let url = new URL("/question-cards");
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {url.searchParams.append(key , new String(p[key as keyof QuestionSearchParams]  ).toString()) }
    )


    // console.log(url.toString())
    let res = await api.get(url.toString());
    console.log(res);
    if(res.status != 200)
        throw new Error();
    return res.data;
}