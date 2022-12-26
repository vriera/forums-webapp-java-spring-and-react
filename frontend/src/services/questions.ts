import {Question, QuestionCard, QuestionResponse} from '../models/QuestionTypes';
import parse from "parse-link-header";
import { api , getPaginationInfo , noContentPagination, PaginationInfo} from "./api";
import Questions from "../pages/dashboard/questions/Questions";
import {getCommunityFromUrl} from "./community";
import {User} from "../models/UserTypes";
import {SmartDate} from "../models/SmartDateTypes";
import {Answer} from "../models/AnswerTypes";
import {getUserFromURI} from "./user";



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
    const questionResponse = response.data;
    questionResponse.id = questionId;
    let _user = await getUserFromURI(questionResponse.owner);
    questionResponse.owner = _user;
    return response.data;
}


export type QuestionSearchParams = {
    query? :string , 
    filter?:number , 
    order?:number ,
    page?:number , 
    size?:number , 
    communityId?:number,
    requestorId?:number
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
  
  
    let res = await api.get("/question-cards/owned?" + searchParams.toString());

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
    file?: any,
    community:number,
}




export async function searchQuestions(p :QuestionSearchParams) :
    Promise<{list: QuestionCard[],pagination: PaginationInfo}>{
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
      (key : string) =>  {searchParams.append(key , new String(p[key as keyof QuestionSearchParams]  ).toString()) }
    )

    let res;

    try{
    res = await api.get("/question-cards?" + searchParams.toString());

    }catch(e : any){
        res = e.response;

    }
    if(res.status == 403)
        throw new Error("cannot.access");
    if(res.status == 204)
        return {
            list: [],
            pagination: noContentPagination
        }

    if(res.status !== 200 && res.status !== 204){

        throw new Error();
    }
        
    return {
        list: res.data || [],
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}


export async function createQuestion(params : QuestionCreateParams){
/*    const question: any = {
        title: params.title,
        body: params.body,
        community: params.community,
    };*/
    const formData = new FormData();
    formData.append("title", params.title);
    formData.append("body", params.body);
    formData.append("community", params.community.toString());
    
    let img = params.file;
    if(img){
        let blob = new Blob([img]);
        formData.append("file", blob);
    }else{
        formData.append("file", new Blob());
    }
    const config = {
        headers: {
            'content-type': 'multipart/form-data',
            "Accept": "application/json",
            "type": "formData"
        }
    }
    let res = await api.post("/questions" , formData,config);
    let location = res.headers.location;
    if(res.status !== 201)
        throw new Error();
    let id = parseInt(location.split('/').pop());

    return id;
}



export async function addQuestionImage(id: number , file:any){

    let data = new FormData();
    data.append('file', file, file.name);

    let res = await api.post(`/questions/${id}/images` , data );

    if(res.status !== 201)
        throw new Error();
    
}

export async function getQuestionUrl(questionUrl :string) : Promise<Question>{
    let path = new URL(questionUrl).pathname;
    return await getQuestion(parseInt(path.split("/").pop() as string));
}

export async function vote(idUser:number,id:number,vote:Boolean){
    await api.put(`/questions/${id}/votes/users/${idUser}?vote=${vote}`,{
        vote: vote,
    })

}

export async function deleteVote(idUser:number,id:number) {
    await api.delete(`/questions/${id}/votes/users/${idUser}`);
}


