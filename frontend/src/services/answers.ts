import { AnswerResponse } from "../models/AnswerTypes";
import { Question } from "../models/QuestionTypes";
import {
  api,
  PaginationInfo,
  getPaginationInfo,
  noContentPagination,
} from "./api";
import { getUserId } from "./auth";
import {
  HTTPStatusCodes,
  InternalServerError,
  apiErrors,
} from "../models/HttpTypes";


async function addVoteToAnswer( answer: AnswerResponse , userId: string): Promise<AnswerResponse>{
  let answerId = answer.id;
  let vote : boolean | undefined = undefined
  try {
    const response = await api.get(`/answers/${answerId}/votes/users/${userId}`);
    vote = response.data.vote;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;

    throw new errorClass("Error getting user vote");
  }


  answer.userVote = vote;
  return answer;
}

async function addVoteToAnswerList( list : AnswerResponse[] , userId : string | null){
  if(userId == null || list.length === 0)
    return list;
  const promises : Promise<AnswerResponse>[] = list.map( (x) => addVoteToAnswer(x,userId));
  return await Promise.all(promises);
}

export async function getAnswers(
  question: Question,
  page: number,
): Promise<{ list: AnswerResponse[]; pagination: PaginationInfo }> {
  try {
    const response = await api.get(`/answers`, {
      params: {
        page: page,
        questionId: question.id,
      },
    });

    return {
      list: await addVoteToAnswerList(response.data as AnswerResponse[] , getUserId()),
      pagination: getPaginationInfo(response.headers.link, page || 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting answers");
  }
}

export async function createAnswer(answer: any, questionId: number) {
  try {
    await api.post(`/answers/${questionId}`, {
      body: answer,
    });
  } catch (error: any) {
    // API returns CREATED (201) on success,
    // NOT FOUND (404) if question not found
    // BAD REQUEST (400) if answer is invalid
    // FORBIDDEN (403) if user is not allowed to answer
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error creating answer");
    
  }
}

export async function vote(userId: number, id: number, vote: boolean) {
  try {
    // API returns NO CONTENT (204) on success
    await api.put(`/answers/${id}/votes/users/${userId}?vote=${vote}`);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error voting on answer");
  }
}

export async function deleteVote(userId: number, id: number) {
  try {
    await api.delete(`/answers/${id}/votes/users/${userId}`);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error deleting vote");
  }
}

export async function verifyAnswer(id: number) {
  try {
    await api.post(`/answers/${id}/verify/`);
  } catch (error: any) {
    // API returns NO CONTENT (204) on success
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error verifying answer");
  }
}

export async function unVerifyAnswer(id: number) {
  try {
    await api.delete(`/answers/${id}/verify/`);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error unverifying answer");
  }
}

export type AnswersByOwnerParams = {
  userId: number;
  page?: number;
};

export async function getByOwner(p: AnswersByOwnerParams): Promise<{
  list: AnswerResponse[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof AnswersByOwnerParams];

    if (parameter) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    const res = await api.get("/answers/owner?" + searchParams.toString());
    // API Returns NO CONTENT (204) if there are no answers, and OK (200) if there are

    if (res.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    } else {
      return {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
      };
    }
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting answers by owner");
  }
}
