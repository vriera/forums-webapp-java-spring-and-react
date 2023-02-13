import {
  api,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";
import { Community, CommunityResponse } from "../models/CommunityTypes";
import {
  AccessType,
  ACCESS_TYPE_ARRAY_ENUM,
  ACCESS_TYPE_ARRAY,
} from "./Access";
import { getUserFromURI } from "./user";
import { apiErrors, CommunityNameTakenError, HTTPStatusCodes, InternalServerError } from "../models/HttpTypes";

export async function createCommunity(name: string, description: string) {
  if (!window.localStorage.getItem("userId")) {
    throw new Error("User not logged in");
  }

  let res;
  try {
    res = await api.post(`/communities`, { name, description });
  } catch (error: any) {
    res = error.response;
  }

  // Returns 201 if successful
  if(res.status !== HTTPStatusCodes.CREATED){
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    
    if (res.status === HTTPStatusCodes.CONFLICT && res.data.message === 'community.name.taken') {
      throw new CommunityNameTakenError();
    }
    
    throw new errorClass("Error creating community");
  }

  let communityId = parseInt(res.headers.location.split("/").pop());
  return communityId;
}

export async function getCommunityFromUrl(communityURL: string) {
  let path = new URL(communityURL).pathname;
  return await getCommunity(parseInt(path.split("/").pop() as string));
}

/*export async function getCommunityFromUrl(communityURL : string){
    let path = new URL(communityURL).pathname
    let resp;
    if(!window.localStorage.getItem("userId")){
        resp = await apiURLfromApi.get(path)
    }else{
        let id = window.localStorage.getItem("userId")
        resp = await apiURLfromApi.get(path + `?userId=${id}`);
    }
    if(resp.status !== 200)
        return null as unknown as Community;
    return  {
        id: resp.data.id,
        name: resp.data.name,
        description: resp.data.description,
        userCount: resp.data.userCount,
        moderator: await getUserFromURI(resp.data.moderator)
   }
}

*/

export async function getCommunityNotifications(id: number) {

  let res = await api.get(`/notifications/communities/${id}`);  

  if (res.status === HTTPStatusCodes.NO_CONTENT) return 0;

  if(res.status !== HTTPStatusCodes.OK){
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error getting notifications");
  }

  // Notification count is returned as a java Long
  return res.data.notifications as number;
}

export async function getCommunity(communityId: number): Promise<Community> {
  let resp;
  if (!window.localStorage.getItem("userId")) {
    resp = await api.get(`/communities/${communityId}`);
  } else {
    let id = window.localStorage.getItem("userId");
    resp = await api.get(`/communities/${communityId}?userId=${id}`);
  }

  if (resp.status !== HTTPStatusCodes.OK){
    const errorClass = apiErrors.get(resp.status) || InternalServerError;
    throw new errorClass("Error getting community");
  }

  return {
    id: resp.data.id,
    name: resp.data.name,
    description: resp.data.description,
    userCount: resp.data.userCount,
    moderator: await getUserFromURI(resp.data.moderator),
  };
}

export type CommunitySearchParams = {
  query?: string;
  page?: number;
};

export type AskableCommunitySearchParams = {
  page?: number;
  requestorId?: number;
};

export async function searchCommunity(
  p: CommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();
  //forma galaxy brain

  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof CommunitySearchParams]).toString()
    );
  });
  let res = await api.get("/communities?" + searchParams.toString());

  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error searching community");
  }
  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}

export async function getAllowedCommunities(
  p: AskableCommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  //this functiion is for getting the comunities a specific user is allowed to ask to
  let searchParams = new URLSearchParams();
  //forma galaxy brain

  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof AskableCommunitySearchParams]).toString()
    );
  });
  let res = await api.get(
    "/communities/askable?" + searchParams.toString()
  );

  // If the requestorId is -1, this means that we are an admin user
  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error getting allowed communities");
  }

  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}

/*
function idFromUrl( url: string){
    let path = new URL(url).pathname
    return parseInt(path.split("/").pop() as string)
}
*/

export enum ModerationListType {
  Invited = "invited",
  InviteRejected = "invite-rejected",
  Requested = "requested",
  Admitted = "admitted",
  Blocked = "blocked",
}

export function getModerationListType(name: string): ModerationListType {
  switch (name) {
    case "invited":
      return ModerationListType.Invited;
    case "invite-rejected":
      return ModerationListType.InviteRejected;
    case "blocked":
      return ModerationListType.Blocked;
    case "requested":
      return ModerationListType.Requested;
    default:
      return ModerationListType.Admitted;
  }
}

export type CommunityModerationSearchParams = {
  type: ModerationListType;
  communityId: number;
  page?: number;
};

export async function getCommunityModerationList(
  params: CommunityModerationSearchParams
) {
  if (!window.localStorage.getItem("userId")) return;

  let id = window.localStorage.getItem("userId");
  let url = new URL(
    `/communities/${params.communityId}/user/${id}/${params.type}`
  );
  if (params.page) url.searchParams.append("page", params.page.toString());

  let res = await api.get(url.toString());
  if (res.status !== 200) return false;

  return res.data;
}

export type ModeratedCommunitiesParams = {
  userId: number;
  page?: number;
};
export async function getModeratedCommunities(
  p: ModeratedCommunitiesParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();
  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof ModeratedCommunitiesParams]).toString()
    );
  });
  let res = await api.get(
    `/communities/moderated?` + searchParams.toString()
  );

  if (res.status === HTTPStatusCodes.NO_CONTENT)
    return {
      list: [],
      pagination: noContentPagination,
    };

  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error getting moderated communities");
  }

  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}

export type CommunitiesByAcessTypeParams = {
  accessType: AccessType;
  requestorId: number;
  page?: number;
};

export async function getCommunitiesByAccessType(
  p: CommunitiesByAcessTypeParams
): Promise<{
  list: CommunityResponse[];
  pagination: PaginationInfo;
}> {
  let searchParams = new URLSearchParams();
  //forma galaxy brain

  Object.keys(p).forEach((key: string) => {
    searchParams.append(
      key,
      new String(p[key as keyof CommunitiesByAcessTypeParams]).toString()
    );
  });
  let res = await api.get(
    `/communities/${ACCESS_TYPE_ARRAY[p.accessType]}?` +
      searchParams.toString()
  );

  if (res.status === HTTPStatusCodes.NO_CONTENT)
    return {
      list: [],
      pagination: noContentPagination,
    };

  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error getting communities by access type");
  };
  return {
    list: res.data,
    pagination: getPaginationInfo(res.headers.link, p.page || 1),
  };
}

export type SetAccessTypeParams = {
  communityId: number;
  targetId: number;
  newAccess: AccessType;
};

export async function canAccess(userId: number, communityId: number) {
  try {
    let res = await api.get(`/communities/${communityId}/user/${userId}`);
    return res.data.canAccess;
  } catch (e: any) {
    const errorClass = apiErrors.get(e.response.status) || InternalServerError;
    throw new errorClass(e.response.data.message);
  }
}

export async function setAccessType(p: SetAccessTypeParams) {
  let body = { accessType: ACCESS_TYPE_ARRAY_ENUM[p.newAccess] };
  let res = await api.put(
    `/communities/${p.communityId}/user/${p.targetId}`,
    body
  );
  if (res.status !== HTTPStatusCodes.OK) {
    const errorClass = apiErrors.get(res.status) || InternalServerError;
    throw new errorClass("Error setting access type");
  }
}

export type InviteCommunityParams = {
  communityId: number;
  email: string;
};

export async function inviteUserByEmail(p: InviteCommunityParams) {
  try {
    await api.put(`/communities/${p.communityId}/invite`, {
      email: p.email,
    });
    return true;
  } catch (e: any) {
    const errorClass = apiErrors.get(e.response.status) || InternalServerError;
    throw new errorClass("Error inviting user by email");
  }
}
