import { Question } from "../models/QuestionTypes";
import parse from "parse-link-header";
import { api } from "./api";

export async function getQuestion(questionId: number): Promise<Question> {
    const response = await api.get(`/questions/${questionId}`);
    return response.data;
}

