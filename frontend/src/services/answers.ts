import {Answer} from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {api} from "./api";

export async function getAnswer(answerId: number): Promise<Answer> {
    const response = await api.get(`/quesions/${answerId}`);
    return response.data;
}

export async function getAnswers(question: Question| undefined): Promise<Answer[]> {
    var answers:Answer[] = [];
    if(question && question.id > 0){
        for (let i in question.answers) {
            const response = await api.get(i);
            answers.push(response.data);
        }
    }
    return answers;

}



