import {Answer} from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {api} from "./api";

export async function getAnswer(answerId: number): Promise<Answer> {
    const response = await api.get(`/quesions/${answerId}`);
    return response.data;
}

export async function getAnswers(question: Question| undefined): Promise<Answer[]> {
    var answers: Answer[] = [];
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



