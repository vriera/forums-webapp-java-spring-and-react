import { Question, QuestionCard } from '../models/QuestionTypes';
import parse from "parse-link-header";
import { api , getPaginationInfo , PaginationInfo} from "./api";
import Questions from "../pages/dashboard/questions/Questions";



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
    const question = response.data;
    question.id = questionId;
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

export type QuestionByUserParams = {
    requestorId?:number,
    page?:number
}


export async function getQuestionByUser(p : QuestionByUserParams) : 
   Promise<{list:QuestionCard[], pagination: PaginationInfo}>{
    let searchParams = new URLSearchParams();
    Object.keys(p).forEach(
        (key : string) =>  {searchParams.append(key , new String(p[key as keyof QuestionByUserParams]  ).toString()) }
    )
  
  
    // console.log(url.toString())
    let res = await api.get("/question-cards/owned?" + searchParams.toString());
    console.log(getPaginationInfo(res.headers.link , p.page || 1));
    if(res.status !== 200)
        throw new Error();
    return {
        list:res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    };
}

export type QuestionCreateParams = {
    title :string , 
    body:string , 
    community:number
}




export async function searchQuestions(p :QuestionSearchParams) :
    Promise<{list: QuestionCard[],pagination: PaginationInfo}>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof QuestionSearchParams]  ).toString()) }
    )


    // console.log(url.toString())
    let res = await api.get("/question-cards?" + searchParams.toString());
    console.log(res.headers.link);
    console.log(getPaginationInfo(res.headers.link , p.page || 1));
    if(res.status !== 200)
        throw new Error();
    return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}

export async function createQuestion(params : QuestionCreateParams , file : any){
    console.log("creating question");
    let res = await api.post("/questions" , params);
    console.log(res);
    console.log(res.headers);
    let location = res.headers.location;
    if(res.status !== 201)
        throw new Error();
    let id = parseInt(location.split('/').pop());
    console.log('got id:' + id);
    if(file)
        await addQuestionImage(id , file);
}

export async function addQuestionImage(id: number , file:any){
    console.log(`sending image`);
    let data = new FormData();
    data.append('file', file, file.name);

    let res = await api.post(`/questions/${id}/image` , data );
    console.log(res);
    
}


