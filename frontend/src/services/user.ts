import {
  api,
  apiURLfromApi,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";
import { Notification, User, Karma } from "../models/UserTypes";
import {ACCESS_TYPE_ARRAY_ENUM, AccessType} from "./Access";
import {
  apiErrors,
  HTTPStatusCodes,
  IncorrectPasswordError,
  InternalServerError,
} from "../models/HttpTypes";
import {CommunityResponse} from "../models/CommunityTypes";
import {CommunitiesByAcessTypeParams} from "./community";
import {AxiosResponse} from "axios";


export async function getUserFromEmail(email: string){
  try {
    const response = await api.get(`/users?` + `email=${email}`)
    return response;

  }catch (error:any){

  }
}
export async function updateUserInfo(response: AxiosResponse<any>) {
  try {
    window.localStorage.setItem("userId", response.data[0].id);
    window.localStorage.setItem("username", response.data[0].username);
    window.localStorage.setItem("email", response.data[0].email);
  } catch (error: any) {
    /*const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error updating user info");*/
  }
}

export type UserUpdateParams = {
  userId: number;
  newUsername: string;
  newPassword: string;
  currentPassword: string;
};

export async function updateUser(p: UserUpdateParams) {
  try {
    await api.put(`/users/${p.userId}`, {
      newUsername: p.newUsername,
      newPassword: p.newPassword,
      currentPassword: p.currentPassword,
    });
  } catch (error: any) {
    if (error.response.status === HTTPStatusCodes.BAD_REQUEST) {
      if (error.response.data.code === "incorrect.current.password") {
        throw new IncorrectPasswordError();
      }
    }
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error updating user");
  }
}

/*export async function getUserFromURI(userURI: string): Promise<User> {
    let path = new URL(userURI).pathname;

    const response = await apiURLfromApi(userURI);
    if (response.status !== 200)
        throw new Error("Error fetching user from API")

    let user: User = {
        id: response.data.id,
        email: response.data.email,
        username: response.data.username
    }
    if(user.id == parseInt(window.localStorage.getItem("userId") as string) )
        user = {...user , notifications: await getNotificationFromApi(user.id)}
    return user;

}*/

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
    return user;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
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
      apiErrors.get(error.response.status) || InternalServerError;
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
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error fetching karma from API");
  }
}

export async function getUser(id: number): Promise<User> {
  let user: User = await getUserFromApi(id);
  user.karma = await getKarmaFromApi(id);
  return user;
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
  //forma galaxy brain
  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof UserSearchParams]).toString()
    );
  });

  try {
    let res = await api.get("/users?" + searchParams.toString());
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page || 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error searching users");
  }
}

export type UsersByAcessTypeParams = {
  accessType: AccessType;
  communityId: number;
  page?: number;
};

export async function getUsersByAccessType(p: UsersByAcessTypeParams): Promise<{
  list: User[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();

  searchParams.append('accessType', ACCESS_TYPE_ARRAY_ENUM[p.accessType]);
  searchParams.append('communityId', p.communityId.toString());
  if (p.page) {
    searchParams.append('page', p.page.toString());
  }

  try {
    let response = await api.get(
        `/users?` +
        searchParams.toString()
    );

    if (response.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    } else {
      return {
        list: response.data,
        pagination: getPaginationInfo(response.headers.link, p.page || 1),
      };
    }
  } catch (error: any) {
    const errorClass =
        apiErrors.get(error.response.status) || InternalServerError;
    throw new errorClass("Error getting communities by access type");
  }
}
