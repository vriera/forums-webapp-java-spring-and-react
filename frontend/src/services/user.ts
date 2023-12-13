import {
  api,
  apiWithoutBaseUrl,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";
import { Notification, User, Karma } from "../models/UserTypes";
import { AccessType, ACCESS_TYPE_ARRAY } from "./access";
import {
  ApiErrorCodes,
  apiErrors,
  EmailTakenError,
  HTTPStatusCodes,
  IncorrectPasswordError,
  InternalServerError,
  InvalidEmailError,
  UsernameTakenError,
} from "../models/HttpTypes";
import { AxiosResponse } from "axios";

export type UpdateUserParams = {
  userId: number;
  newUsername?: string;
  newPassword?: string;
  currentPassword: string;
};

export async function updateUser(p: UpdateUserParams) {
  try { 
    let params : any= {
      currentPassword : p.currentPassword
    }
    if(p.newUsername !== undefined && p.newUsername !== "")
      params.newUsername = p.newUsername

    if(p.newPassword !== undefined && p.newPassword !== "")
      params.newPassword = p.newPassword

    await api.put(`/users/${p.userId}`, params);
  } catch (error: any) {
    const responseIsUnauthorizedDueToIncorrectPassword =
      error.response.status === HTTPStatusCodes.UNAUTHORIZED &&
      error.response.data.code === ApiErrorCodes.INCORRECT_CURRENT_PASSWORD;

    const responseIsConflictDueToUsernameAlreadyExists =
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.code === ApiErrorCodes.USERNAME_ALREADY_EXISTS;

    if (responseIsUnauthorizedDueToIncorrectPassword) {
      throw new IncorrectPasswordError();
    } else if (responseIsConflictDueToUsernameAlreadyExists) {
      throw new UsernameTakenError();
    }
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error updating user");
  }
}

export type CreateUserParams = {
  email: string;
  password: string;
  username: string;
};

export async function createUser(params: CreateUserParams) {
  try {
    const response = await api.post("/users", {
      email: params.email,
      username: params.username,
      password: params.password,
    });
    return response;
  } catch (error: any) {
    const usernameIsTaken =
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.code === ApiErrorCodes.USERNAME_ALREADY_EXISTS;

    const emailIsTaken =
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.code === ApiErrorCodes.EMAIL_ALREADY_EXISTS;

    const invalidEmail =
      error.response.status === HTTPStatusCodes.BAD_REQUEST &&
      error.response.data.code === ApiErrorCodes.INVALID_EMAIL;

    if (usernameIsTaken) throw new UsernameTakenError();

    if (emailIsTaken) throw new EmailTakenError();

    if (invalidEmail) throw new InvalidEmailError();

    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;

    throw new errorClass("Error registering user");
  }
}

// Processes common response when getting a user from a URI or an ID
async function processGetUserResponse(response: AxiosResponse<any>) {
  let user: User = {
    id: response.data.id,
    email: response.data.email,
    username: response.data.username,
  };
  if (user.id === parseInt(window.localStorage.getItem("userId") as string))
    user = { ...user, notifications: await getNotifications(user.id) };
  return user;
}

export async function getUserFromUri(userUri: string): Promise<User> {
  try {
    const response = await apiWithoutBaseUrl.get(userUri);
    return await processGetUserResponse(response);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error fetching user from API");
  }
}

export async function getUser(id: number): Promise<User> {
  try {
    const response = await api.get(`/users/${id}`);
    return await processGetUserResponse(response);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error fetching user from API");
  }
}

export async function getNotifications(userId: number): Promise<Notification> {
  try {
    const response = await api.get(`/users/${userId}/notifications`);
    let notification: Notification = {
      requests: response.data.requests,
      invites: response.data.invites,
      total: response.data.total,
    };
    return notification;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error fetching notifications from API");
  }
}

export async function getKarma(userId: number): Promise<Karma> {
  try {
    const response = await api.get(`/users/${userId}/karma`);

    let karma: Karma = {
      karma: response.data.karma,
    };
    return karma;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error fetching karma from API");
  }
}

export async function getUserAndKarma(userId: number): Promise<User> {
  let user: User = await getUser(userId);
  user.karma = await getKarma(userId);
  return user;
}

export enum UserActionHasTarget {
  ADMITTED = 0,
  REQUESTED = 1,
  REQUEST_REJECTED = 2,
  INVITED = 3,
  INVITE_REJECTED = 4,
  LEFT = 5,
  BLOCKED = 6,
  KICKED = 7,
  BANNED = 8,
}

export type UserSearchParams = {
  query?: string;
  page?: number;
};

export async function searchUser(
  p: UserSearchParams
): Promise<{ list: User[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof UserSearchParams];

    if (parameter !== undefined) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    let res = await api.get("/users?" + searchParams.toString());
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error searching users");
  }
}

export async function findUserByEmail(email: string): Promise<User> {
  try {
    const response = await api.get(`/users?email=${email}`);

    return response.data[0];
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error finding user by email");
  }
}

export type GetUsersByAcessTypeParams = {
  accessType: AccessType;
  communityId: number;
  page?: number;
};

export async function getUsersByAccessType(
  p: GetUsersByAcessTypeParams
): Promise<{
  list: User[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();
  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof GetUsersByAcessTypeParams];
    if (parameter !== undefined) {
      if (key === "accessType") {
        searchParams.append(key, ACCESS_TYPE_ARRAY[parameter]);
      } else {
        searchParams.append(key, parameter.toString());
      }
    }
  });

  try {
    let res = await api.get(`/users?${searchParams}`);
    let users;

    if (res.status === HTTPStatusCodes.NO_CONTENT) {
      users = {
        list: [],
        pagination: noContentPagination,
      };
    } else {
      users = {
        list: res.data,
        pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
      };
    }

    return users;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error searching users by access type");
  }
}
