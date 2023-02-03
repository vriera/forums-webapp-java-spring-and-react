import { ListFormat } from "typescript";
import { Answer, AnswerResponse } from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {
  api,
  PaginationInfo,
  getPaginationInfo,
  noContentPagination,
} from "./api";
import { Pagination } from "react-bootstrap";

export async function getAnswer(answerId: number): Promise<Answer> {
  const response = await api.get(`/quesions/${answerId}`);
  return response.data;
}

export async function getAnswers(
  question: Question,
  page: number,
  limit: number
): Promise<{ list: AnswerResponse[]; pagination: PaginationInfo }> {
  var answers: AnswerResponse[] = [];
  var pagination = {
    current: -1,
    total: -1,
    uri: "error, id negativo",
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
    pagination: pagination,
  };
}

export async function createAnswer(answer: any, idQuestion: number) {
  await api.post(`/answers/${idQuestion}`, {
    body: answer,
  });
}

export async function vote(idUser: number, id: number, vote: Boolean) {
  await api.put(`/answers/${id}/votes/users/${idUser}?vote=${vote}`, {
    vote: vote,
  });
}

export async function deleteVote(idUser: number, id: number) {
  await api.delete(`/answers/${id}/votes/users/${idUser}`);
}

export async function verifyAnswer(id: number) {
  await api.post(`/answers/${id}/verify/`);
}

export async function unVerifyAnswer(id: number) {
  await api.delete(`/answers/${id}/verify/`);
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

  if (res.status === 204)
    return {
      list: [],
      pagination: noContentPagination,
    };

  if (res.status !== 200) new Error();

  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}
