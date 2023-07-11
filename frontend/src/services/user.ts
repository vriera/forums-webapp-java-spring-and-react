import {
  api,
  apiURLfromApi,
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
  UsernameTakenError,
} from "../models/HttpTypes";

export async function updateUserInfo(userURI: string) {
  try {
    let response = await apiURLfromApi.get(userURI);

    window.localStorage.setItem("userId", response.data.id);
    window.localStorage.setItem("username", response.data.username);
    window.localStorage.setItem("email", response.data.email);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error updating user info");
  }
}

export type UpdateUserParams = {
  userId: number;
  newUsername: string;
  newPassword: string;
  currentPassword: string;
};

export async function updateUser(p: UpdateUserParams) {
  try {
    await api.put(`/users/${p.userId}`, {
      newUsername: p.newUsername,
      newPassword: p.newPassword,
      currentPassword: p.currentPassword,
    });
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

export async function createUser(
  params: CreateUserParams
) {
  try {
    const response = await api.post("/users", {
      email: params.email,
      username: params.username,
      password: params.password,
    });
    return response;
  } catch (error: any) {
    const responseIsConflictDueToUsernameAlreadyExists = 
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.code === ApiErrorCodes.USERNAME_ALREADY_EXISTS;

    const responseIsConflictDueToEmailAlreadyExists =
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.code === ApiErrorCodes.EMAIL_ALREADY_EXISTS;
      
    if (responseIsConflictDueToUsernameAlreadyExists) throw new UsernameTakenError();
    
    if (responseIsConflictDueToEmailAlreadyExists) throw new EmailTakenError();
    
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error registering user");
  }
}

export async function getUserFromURI(userURI: string): Promise<User> {
  let path = new URL(userURI).pathname;
  return await getUserFromApi(parseInt(path.split("/").pop() as string));
}

export async function getUserFromApi(id: number): Promise<User> {
  try {
    const response = await api.get(`/users/${id}`);
    let user: User = {
      id: response.data.id,
      email: response.data.email,
      username: response.data.username,
    };
    if (user.id === parseInt(window.localStorage.getItem("userId") as string))
      user = { ...user, notifications: await getNotificationFromApi(user.id) };
    return user;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error fetching user from API");
  }
}

export async function getNotificationFromApi(
  id: number
): Promise<Notification> {
  try {
    const response = await api.get(`/notifications/${id}`);
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

export async function getKarmaFromApi(id: number): Promise<Karma> {
  try {
    const response = await api.get(`/karma/${id}`);

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

export async function getUser(id: number): Promise<User> {
  let user: User = await getUserFromApi(id);
  user.karma = await getKarmaFromApi(id);
  return user;
}

export enum UserActionHasTarget {
  ADMITTED = 0,
  REQUESTED = 1,
  REQUEST_REJECTED = 2,
  INVITED = 3,
  INVITE_REJECTED = 4,
  LEFT = 5,
  BLOCKED_COMMUNITY = 6,
  KICKED = 7,
  BANNED = 8,
}

export type UserSearchParams = {
  query?: string;
  page?: number;
  size?: number;
};

export async function searchUser(
  p: UserSearchParams
): Promise<{ list: User[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof UserSearchParams];

    if (parameter) {
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

export type GetUsersByAcessTypeParams = {
  accessType: AccessType;
  moderatorId: number;
  communityId: number;
  page?: number;
};

export async function getUsersByAccessType(p: GetUsersByAcessTypeParams): Promise<{
  list: User[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();
  //forma galaxy brain

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof GetUsersByAcessTypeParams];

    if (parameter) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    let res = await api.get(
      `/users/${ACCESS_TYPE_ARRAY[p.accessType]}?` + searchParams.toString()
    );

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
