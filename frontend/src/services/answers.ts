import { ListFormat } from "typescript";
import {Answer, AnswerResponse} from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {api , PaginationInfo  , getPaginationInfo} from "./api";

export async function getAnswer(answerId: number): Promise<Answer> {
    const response = await api.get(`/quesions/${answerId}`);
    return response.data;
}

export async function getAnswers(question: Question| undefined): Promise<AnswerResponse[]> {
    var answers: AnswerResponse[] = [];
    if (question && question.id > 0) {
        const response = await api.get(`/answers`, {
                params: {
                    idQuestion: question.id,
                }
            }
        );
        console.log(response.data)
        answers = response.data
    }
    return answers;
}

export async function setAnswer(answer: any, idQuestion: number){
    await api.post(`/answers/${idQuestion}`,
             {
                body: answer,
            }
    );
}

export async function vote(idUser:number,id:number,vote:Boolean){
    await api.put(`/answers/${id}/vote/user/${idUser}?vote=${vote}`,{
           vote: vote,
       })

}

export async function deleteVote(idUser:number,id:number) {
    await api.delete(`/answers/${id}/vote/user/${idUser}`);
}

export type AnswersByOwnerParams = {
    requestorId: number,
    page?:number
}

export async function getByOwner(p : AnswersByOwnerParams) : Promise<{
list: AnswerResponse[],
pagination: PaginationInfo}>
{
    
    let searchParams = new URLSearchParams();
    //forma galaxy brain

    Object.keys(p).forEach(
        (key : string) =>  {searchParams.append(key , new String(p[key as keyof AnswersByOwnerParams]  ).toString()) }
    )
    const res = await api.get("/answers/owner?" + searchParams.toString());
    if(res.status !== 200)
        new Error();

    return{
        list: res.data,
        pagination: getPaginationInfo(res.headers.link , p.page || 1)
    }
}


