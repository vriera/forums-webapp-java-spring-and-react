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
} from "./access";
import { getUserFromURI } from "./user";
import {
  apiErrors,
  CommunityNameTakenError,
  HTTPStatusCodes,
  InternalServerError,
} from "../models/HttpTypes";

export async function createCommunity(name: string, description: string) {
  if (!window.localStorage.getItem("userId")) {
    throw new Error("User not logged in");
  }

  try {
    const response = await api.post(`/communities`, { name, description });
    // Returns 201 if successful
    let communityId = parseInt(response.headers.location.split("/").pop());
    return communityId;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;

    if (
      error.response.status === HTTPStatusCodes.CONFLICT &&
      error.response.data.message === "community.name.taken"
    ) {
      throw new CommunityNameTakenError();
    }

    throw new errorClass("Error creating community");
  }
}

export async function getCommunityFromUrl(communityURL: string) {
  let path = new URL(communityURL).pathname;
  return await getCommunity(parseInt(path.split("/").pop() as string));
}

export async function getCommunityNotifications(id: number) {
  try {
    let res = await api.get(`/notifications/communities/${id}`);

    if (res.status === HTTPStatusCodes.NO_CONTENT) return 0;

    // Notification count is returned as a java Long
    return res.data.notifications as number;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting notifications");
  }
}

export async function getCommunity(communityId: number): Promise<Community> {
  let endpoint = `/communities/${communityId}`;

  const id = window.localStorage.getItem("userId");
  if (id) endpoint += `?userId=${id}`;
  
  try {
    const response = await api.get(endpoint);
    return {
      id: response.data.id,
      name: response.data.name,
      description: response.data.description,
      userCount: response.data.userCount,
      moderator: await getUserFromURI(response.data.moderator),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
      console.log("Error getting community", error)
    throw new errorClass("Error getting community");
  }
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

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof CommunitySearchParams];

    if (parameter){
      searchParams.append(
        key,
        parameter.toString()
      );
    }
  });

  try {
    let response = await api.get("/communities?" + searchParams.toString());
    return {
      list: response.data,
      pagination: getPaginationInfo(response.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    console.log(error)
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error searching community");
  }
}
//this function is for getting the comunities a specific user is allowed to ask to
export async function getAskableCommunities(
  p: AskableCommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof AskableCommunitySearchParams];
    
    if (parameter) {
      searchParams.append(
        key,
        parameter.toString()
      );
    }
  });

  try {
    let res = await api.get("/communities/askable?" + searchParams.toString());
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    // If the requestorId is -1, this means that we are an admin user
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting allowed communities");
  }
}

export enum ModerationListType {
  Invited = "invited",
  InviteRejected = "invite-rejected",
  Requested = "requested",
  Admitted = "admitted",
  Blocked = "blocked",
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
    const parameter = p[key as keyof ModeratedCommunitiesParams];

    if (parameter) {
      searchParams.append(
        key,
        parameter.toString()
      );
    }
  });

  try {
    const response = await api.get(
      `/communities/moderated?` + searchParams.toString()
    );
    if (response.status === HTTPStatusCodes.NO_CONTENT) {
      return {
        list: [],
        pagination: noContentPagination,
      };
    } else {
      return {
        list: response.data,
        pagination: getPaginationInfo(response.headers.link, p.page ?? 1),
      };
    }
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting moderated communities");
  }
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

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof CommunitiesByAcessTypeParams];

    if (parameter) {
      searchParams.append(
        key,
        parameter.toString()
      );
    }
  });
  
  try {
    let response = await api.get(
      `/communities/${ACCESS_TYPE_ARRAY[p.accessType]}?` +
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
        pagination: getPaginationInfo(response.headers.link, p.page ?? 1),
      };
    }
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting communities by access type");
  }
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
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass(error.response.data.message);
  }
}

export async function setAccessType(p: SetAccessTypeParams) {
  let body = { accessType: ACCESS_TYPE_ARRAY_ENUM[p.newAccess] };
  try {
    await api.put(`/communities/${p.communityId}/user/${p.targetId}`, body);
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
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
    const errorClass = apiErrors.get(e.response.status) ?? InternalServerError;
    throw new errorClass("Error inviting user by email");
  }
}
