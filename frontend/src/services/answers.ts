import { AnswerResponse } from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {
  api,
  PaginationInfo,
  getPaginationInfo,
  noContentPagination,
} from "./api";
import { HTTPStatusCodes, InternalServerError, apiErrors } from "../models/HttpTypes";

export async function getAnswers(
  question: Question,
  page: number,
  limit: number
): Promise<{ list: AnswerResponse[]; pagination: PaginationInfo }> {
  var answers: AnswerResponse[] = [];
  var errorPagination = {
    current: -1,
    total: -1,
    uri: "Error, negativeId",
  };
  if (question && question.id > 0) {
    const response = await api.get(`/answers`, {
      params: {
        page: page,
        limit: limit,
        idQuestion: question.id,
      },
    });

    answers = response.data;
    return {
      list: answers,
      pagination: getPaginationInfo(response.headers.link, page || 1),
    };
  }

  return {
    list: answers,
    pagination: errorPagination,
  };
}

export async function createAnswer(answer: any, idQuestion: number) {
  const response = await api.post(`/answers/${idQuestion}`, {
    body: answer,
  });
  // API returns CREATED (201) on success, 
  // NOT FOUND (404) if question not found
  // BAD REQUEST (400) if answer is invalid
  // FORBIDDEN (403) if user is not allowed to answer
  if(response.status !== HTTPStatusCodes.CREATED){
    const errorClass = apiErrors.get(response.status) || InternalServerError;
    throw new errorClass("Error creating answer");    
  }
}

export async function vote(idUser: number, id: number, vote: Boolean) {
  const response = await api.put(`/answers/${id}/votes/users/${idUser}?vote=${vote}`);

  // API returns NO CONTENT (204) on success
  if(response.status !== HTTPStatusCodes.NO_CONTENT){
    const errorClass = apiErrors.get(response.status) || InternalServerError;
    throw new errorClass("Error voting");    
  }
}

export async function deleteVote(idUser: number, id: number) {
  const response = await api.delete(`/answers/${id}/votes/users/${idUser}`);

  // API returns NO CONTENT (204) on success
  if(response.status !== HTTPStatusCodes.NO_CONTENT){
    const errorClass = apiErrors.get(response.status) || InternalServerError;
    throw new errorClass("Error deleting vote");    
  }
}

export async function verifyAnswer(id: number) {
  const response = await api.post(`/answers/${id}/verify/`);

  // API returns NO CONTENT (204) on success
  if(response.status !== HTTPStatusCodes.NO_CONTENT){
    const errorClass = apiErrors.get(response.status) || InternalServerError;
    throw new errorClass("Error verifying answer");    
  }
}

export async function unVerifyAnswer(id: number) {
  const response = await api.delete(`/answers/${id}/verify/`);

  // API returns NO CONTENT (204) on success
  if(response.status !== HTTPStatusCodes.NO_CONTENT){
    const errorClass = apiErrors.get(response.status) || InternalServerError;
    throw new errorClass("Error unverifying answer");    
  }
}

export type AnswersByOwnerParams = {
  requestorId: number;
  page?: number;
};

export async function getByOwner(p: AnswersByOwnerParams): Promise<{
  list: AnswerResponse[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();
  //forma galaxy brain

  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof AnswersByOwnerParams]).toString()
    );
  });
  const res = await api.get("/answers/owner?" + searchParams.toString());

  // API Returns NO CONTENT (204) if there are no answers, and OK (200) if there are
  if (res.status === HTTPStatusCodes.NO_CONTENT)
    return {
      list: [],
      pagination: noContentPagination,
    };

  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error getting answers by owner");
  }

  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}

