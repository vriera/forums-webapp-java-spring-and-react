import { Question } from "../models/QuestionTypes";
import parse from "parse-link-header";
import { baseUrl } from "./api";

export async function getQuestion(questionId: number): Promise<Question> {
    const response = await baseUrl.get(`/questions/${questionId}`);
    return response.data;
}

