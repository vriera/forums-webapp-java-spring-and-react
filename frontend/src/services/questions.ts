import {
  HTTPStatusCodes,
  InternalServerError,
  apiErrors,
} from "../models/HttpTypes";
import { Question, QuestionResponse } from "../models/QuestionTypes";
import {
  api,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";

import { getUserFromURI } from "./user";

export type CommunitySearchParams = {
  query?: string;
  page?: number;
  size?: number;
  communityId?: number;
};

export type QuestionSearchParameters = {};

export async function getQuestion(questionId: number): Promise<Question> {
  try {
    const response = await api.get(`/questions/${questionId}`);
    const questionResponse = response.data;
    questionResponse.id = questionId;
    let _user = await getUserFromURI(questionResponse.owner);
    questionResponse.owner = _user;
    return response.data;
  } catch (error: any) {
    // The endpoint returns either a 200 or a 404 if there are no errors
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error getting question");
  }
}

export type QuestionSearchParams = {
  query?: string;
  filter?: number;
  order?: number;
  page?: number;
  size?: number;
  communityId?: number;
  requestorId?: number;
};

export type QuestionByUserParams = {
  requestorId?: number;
  page?: number;
};

export async function getQuestionByUser(
  p: QuestionByUserParams
): Promise<{ list: QuestionResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();
  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof QuestionByUserParams]).toString()
    );
  });
  try {
    let res = await api.get("/questions/owned?" + searchParams.toString());
    if (res.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    }
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page || 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error getting questions by user");
  }
}

export type QuestionCreateParams = {
  title: string;
  body: string;
  file?: any;
  community: number;
};

export async function searchQuestions(
  p: QuestionSearchParams
): Promise<{ list: QuestionResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof QuestionSearchParams]).toString()
    );
  });

  try {
    let response = await api.get("/questions?" + searchParams.toString());
    if (response.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    }
    return {
      list: response.data || [],
      pagination: getPaginationInfo(response.headers.link, p.page || 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error searching questions");
  }
}

export async function createQuestion(params: QuestionCreateParams) {
  /*    const question: any = {
        title: params.title,
        body: params.body,
        community: params.community,
    };*/
  const formData = new FormData();
  formData.append("title", params.title);
  formData.append("body", params.body);
  formData.append("community", params.community.toString());

  let img = params.file;
  if (img) {
    let blob = new Blob([img]);
    formData.append("file", blob);
  } else {
    formData.append("file", new Blob());
  }
  const config = {
    headers: {
      "content-type": "multipart/form-data",
      Accept: "application/json",
      type: "formData",
    },
  };
  try {
    let res = await api.post("/questions", formData, config);
    let location = res.headers.location;
    let id = parseInt(location.split("/").pop());
    return id;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error creating question");
  }
}

export async function addQuestionImage(id: number, file: any) {
  let data = new FormData();
  data.append("file", file, file.name);

  try {
    await api.post(`/questions/${id}/images`, data);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error adding question image");
  }
}

export async function getQuestionUrl(questionUrl: string): Promise<Question> {
  let path = new URL(questionUrl).pathname;
  return await getQuestion(parseInt(path.split("/").pop() as string));
}

/*
export async function getQuestionUrl(questionUrl :string) : Promise<Question>{
    let path = new URL(questionUrl).pathname;
    const response = await apiURLfromApi.get(questionUrl);
    const questionResponse = response.data;
    let _user = await getUserFromURI(questionResponse.owner);
    questionResponse.owner = _user;
    return response.data;
}

 */

export async function vote(idUser: number, id: number, vote: Boolean) {
  try {
    await api.put(`/questions/${id}/votes/users/${idUser}?vote=${vote}`, {
      vote: vote,
    });
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error voting on question");
  }
}

export async function deleteVote(idUser: number, id: number) {
  try {
    await api.delete(`/questions/${id}/votes/users/${idUser}`);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error deleting vote");
  }
}
