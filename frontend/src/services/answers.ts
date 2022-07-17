import {Answer} from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {baseUrl} from "./api";

async function getAnswer(answerId: number): Promise<Answer> {
    const response = await baseUrl.get(`/quesions/${answerId}`);
    return response.data;
}

async function getAnswers(question: Question| undefined): Promise<Answer[]> {
    var answers:Answer[] = [];
    if(question && question.id > 0){
        for (let i in question.answers) {
            const response = await baseUrl.get(i);
            answers.push(response.data);
        }
    }
    return answers;

}