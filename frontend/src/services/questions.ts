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
    file: any,
    community:number,

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


export async function createQuestion(params : QuestionCreateParams){
/*    const question: any = {
        title: params.title,
        body: params.body,
        community: params.community,
    };*/
    const formData = new FormData();
    formData.append("title", JSON.stringify(params.title));
    formData.append("body", JSON.stringify(params.body));
    formData.append("community", JSON.stringify(params.community));
    let img = params.file;
    let blob = new Blob([img]);
    formData.append("file", blob);

    const config = {
        headers: {
            'content-type': 'multipart/form-data',
            "Accept": "application/json",
            "type": "formData"
        }
    }
    let res = await api.post("/questions" , formData,config);
}



export async function createQuestion2(params : QuestionCreateParams): Promise<number>{
    const formData = new FormData();
    formData.append("title", JSON.stringify(params.title));
    formData.append("body", JSON.stringify(params.body));
    formData.append("community", JSON.stringify(params.community));
    formData.append("file", params.file);

    const config = {
        headers: {
            'content-type': 'multipart/form-data',
            "Accept": "application/json",
            "type": "formData"
        }
    }
    let res = await api.post("/questions" , params,config);
    console.log(res);
    console.log(res.headers);
    let location = res.headers.location;
    if(res.status !== 201)
        throw new Error();
    let id = parseInt(location.split('/').pop());
    console.log('got id:' + id);
    return id;
}

export async function addQuestionImage(id: number , file:any){
    console.log(`sending image`);
    let data = new FormData();
    data.append('file', file, file.name);

    let res = await api.post(`/questions/${id}/image` , data );
    console.log(res);
    if(res.status !== 201)
        throw new Error();
    
}

export async function getQuestionUrl(questionUrl :string) : Promise<Question>{
    let path = new URL(questionUrl).pathname;
    console.log("getting: " +path);
    console.log("got the id: " +parseInt(path.split("/").pop() as string) );
    return await getQuestion(parseInt(path.split("/").pop() as string));
}

export async function vote(idUser:number,id:number,vote:Boolean){
    await api.put(`/questions/${id}/vote/user/${idUser}?vote=${vote}`,{
        vote: vote,
    })

}

export async function deleteVote(idUser:number,id:number) {
    await api.delete(`/questions/${id}/vote/user/${idUser}`);
}


