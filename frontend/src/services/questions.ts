import { Question, QuestionCard } from '../models/QuestionTypes';
import parse from "parse-link-header";
import { api } from "./api";
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



export type QuestionCreateParams = {
    title :string , 
    body:string , 
    community:number
}




export async function searchQuestions(p :QuestionSearchParams) : Promise<QuestionCard[]>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof QuestionSearchParams]  ).toString()) }
    )


    // console.log(url.toString())
    let res = await api.get("/question-cards?" + searchParams.toString());
    console.log(res);
    if(res.status != 200)
        throw new Error();
    return res.data;
}

export async function createQuestion(params : QuestionCreateParams , file : any){
    console.log("creating question");
    let res = await api.post("/questions" , params);
    console.log(res);
    console.log(res.headers);
    let location = res.headers.location;
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