import {
  HTTPStatusCodes,
  InternalServerError,
  apiErrors,
} from "../models/HttpTypes";
import {
  Question,
  QuestionResponse,
  QuestionVoteResponse,
} from "../models/QuestionTypes";
import {
  api,
  apiWithoutBaseUrl,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";

import { getUserFromUri } from "./user";

import { getUserId } from "./auth";
export type CommunitySearchParams = {
  query?: string;
  page?: number;
  size?: number;
  communityId?: number;
};

export type QuestionSearchParameters = {};

async function getUserVote(
  questionId: number,
  userId: string
): Promise<boolean | undefined> {
  try {
    const response = await api.get(
      `/questions/${questionId}/votes?userId=${userId}`,
      {}
    );

    if (
      response.status === HTTPStatusCodes.NO_CONTENT ||
      response.data.length === 0
    )
      return undefined;

    const vote: QuestionVoteResponse = response.data[0];

    return vote.vote;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response?.status) ?? InternalServerError;
    throw new errorClass("Error getting question");
  }
}

export async function getQuestion(questionId: number): Promise<Question> {
  try {
    const response = await api.get(`/questions/${questionId}`);

    const questionResponse: QuestionResponse = response.data;
    questionResponse.id = questionId;

    let owner = await getUserFromUri(questionResponse.owner);

    let userId = getUserId();

    let questionAux: any = {
      ...questionResponse,
    };
    questionAux.owner = owner;
    let question: Question = questionAux;

    if (userId != null)
      question.userVote = await getUserVote(questionId, userId);

    return question;
  } catch (error: any) {
    // The endpoint returns either a 200 or a 404 if there are no errors
    const errorClass =
      apiErrors.get(error.response?.status) ?? InternalServerError;
    throw new errorClass("Error getting question");
  }
}

export type QuestionByUserParams = {
  ownerId: number;
  page?: number;
};

export async function getQuestionByUser(
  p: QuestionByUserParams
): Promise<{ list: QuestionResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof QuestionByUserParams];

    if (parameter !== undefined) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    let res = await api.get("/questions?" + searchParams.toString());
    if (res.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    }
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting questions by user");
  }
}

export type QuestionSearchParams = {
  query?: string;
  filter?: number;
  order?: number;
  page?: number;
  size?: number;
  communityId?: number;
  userId?: number;
};

const questionSearchOrder = [
  "MOST_RECENT",
  "LEAST_RECENT",
  "CLOSEST_MATCH",
  "BY_QUESTION_VOTES",
  "BY_ANSWER_VOTES",
];
const questionSearchFilter = [
  "NONE",
  "HAS_ANSWERS",
  "HAS_NO_ANSWERS",
  "HAS_VERIFIED",
];

export async function searchQuestions(
  p: QuestionSearchParams
): Promise<{ list: QuestionResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof QuestionSearchParams];

    if (parameter !== undefined) {
      if (key === "userId") {
        //check valid userId, if not then skip
        if ((parameter as number) >= 0)
          searchParams.append(key, parameter.toString());
      } else if (key === "order") {
        if (questionSearchOrder.length > (parameter as number))
          searchParams.append(key, questionSearchOrder[parameter as number]);
      } else if (key === "filter") {
        if (questionSearchFilter.length > (parameter as number))
          searchParams.append(key, questionSearchFilter[parameter as number]);
      } else {
        searchParams.append(key, parameter.toString());
      }
    }
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
      pagination: getPaginationInfo(response.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error searching questions");
  }
}

export type QuestionCreateParams = {
  title: string;
  body: string;
  file?: any;
  community: number;
};

export async function createQuestion(params: QuestionCreateParams) {
  const formData = new FormData();
  formData.append("title", params.title);
  formData.append("body", params.body);
  formData.append("communityId", params.community.toString());

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
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error creating question");
  }
}

export async function getQuestionFromUri(
  questionUrl: string
): Promise<Question> {
  try {
    const response = await apiWithoutBaseUrl.get(questionUrl);

    const questionResponse: QuestionResponse = response.data;

    let owner = await getUserFromUri(questionResponse.owner);

    let userId = getUserId();

    let questionAux: any = {
      ...questionResponse,
    };
    questionAux.owner = owner;
    let question: Question = questionAux;

    if (userId != null)
      question.userVote = await getUserVote(questionAux.id, userId);

    return question;
  } catch (error: any) {
    // The endpoint returns either a 200 or a 404 if there are no errors
    const errorClass =
      apiErrors.get(error.response?.status) ?? InternalServerError;
    throw new errorClass("Error getting question");
  }
}

export async function vote(userId: number, id: number, vote: boolean) {
  try {
    await api.put(`/questions/${id}/votes/${userId}`, {
      vote: vote,
    });
  } catch (error: any) {
    if (error.response.status === 304) return;
    if (error.response.status === HTTPStatusCodes.NOT_FOUND) {
      throw new Error("Answer not found");
    }
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error voting on question");
  }
}

export async function deleteVote(userId: number, id: number) {
  try {
    await api.delete(`/questions/${id}/votes/${userId}`);
  } catch (error: any) {
    if (error.response.status === 304) return;
    if (error.response.status === HTTPStatusCodes.NOT_FOUND) {
      throw new Error("Answer not found");
    }
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error deleting vote");
  }
}
