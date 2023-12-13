import {
  api,
  getPaginationInfo,
  noContentPagination,
  PaginationInfo,
} from "./api";
import { Community, CommunityResponse } from "../models/CommunityTypes";
import { AccessType, ACCESS_TYPE_ARRAY } from "./access";
import { findUserByEmail, getUserFromUri } from "./user";
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

export async function getCommunityFromUri(communityURL: string) {
  let path = new URL(communityURL).pathname;
  return await getCommunity(parseInt(path.split("/").pop() as string));
}

export async function getCommunityNotifications(id: number) {
  try {
    let res = await api.get(`/communities/${id}/notifications`);

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

  try {
    const response = await api.get(endpoint);
    return {
      id: response.data.id,
      name: response.data.name,
      description: response.data.description,
      userCount: response.data.userCount,
      moderator: await getUserFromUri(response.data.moderator),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting community");
  }
}

export type CommunitySearchParams = {
  query?: string;
  page?: number;
};

export async function searchCommunity(
  p: CommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof CommunitySearchParams];

    if (parameter !== undefined) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    let response = await api.get("/communities?" + searchParams.toString());
    return {
      list: response.data,
      pagination: getPaginationInfo(response.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error searching community");
  }
}

export type AskableCommunitySearchParams = {
  userId?: number;
  page?: number;
};

//this function is for getting the comunities a specific user is allowed to ask to
export async function getAskableCommunities(
  p: AskableCommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();
  let url = "";
  if (p.userId === undefined || p.userId < 0) {
    url = "/communities?moderatorId=0";
    if (p.page !== undefined) url = url + `&page=${p.page}`;
  } else {
    Object.keys(p).forEach((key: string) => {
      const parameter = p[key as keyof AskableCommunitySearchParams];

      if (parameter !== undefined) {
        searchParams.append(key, parameter.toString());
      }
    });
    searchParams.append("onlyAskable", "true");
    url = "/communities?" + searchParams.toString();
  }

  try {
    let res = await api.get(url);
    return {
      list: res.data,
      pagination: getPaginationInfo(res.headers.link, p.page ?? 1),
    };
  } catch (error: any) {
    // If the moderatorId is -1, this means that we are an admin user
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error getting allowed communities");
  }
}

export type ModeratedCommunitiesParams = {
  moderatorId: number;
  page?: number;
};

export async function getModeratedCommunities(
  p: ModeratedCommunitiesParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
  let searchParams = new URLSearchParams();

  Object.keys(p).forEach((key: string) => {
    const parameter = p[key as keyof ModeratedCommunitiesParams];

    if (parameter !== undefined) {
      searchParams.append(key, parameter.toString());
    }
  });

  try {
    const response = await api.get(`/communities?` + searchParams.toString());
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
  userId: number;
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
    if (parameter !== undefined) {
      if (key === "accessType") {
        searchParams.append(key, ACCESS_TYPE_ARRAY[parameter]);
      } else {
        searchParams.append(key, parameter.toString());
      }
    }
  });

  try {
    let response = await api.get(`/communities?` + searchParams.toString());

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

export async function canAccess(
  communityId: number,
  userId?: number
): Promise<boolean> {
  try {
    let community = await getCommunity(communityId);
    // Community is public
    if (community.moderator?.id === 0) return true;

    if (userId === undefined) return false;

    let res = await api.get(
      `/communities/${communityId}/access-type/${userId}`
    );

    return res.data.canAccess;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass(error.response.data.message);
  }
}

export async function canAccessWithType(
  communityId: number,
  userId: number
): Promise<{ canAcess: boolean , accessType: number}> {
  try {
   
    let res = await api.get(
      `/communities/${communityId}/access-type/${userId}`
    );

    return res.data;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass(error.response.data.message);
  }
}

export async function hasRequestedAccess(
  moderatorId: number,
  communityId: number
): Promise<boolean> {
  try {
    let res = await api.get(`/communities/${communityId}/access-type/${moderatorId}`);
    return res.data.accessType === AccessType.REQUESTED;
  } catch (error: any) {
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass(error.response.data.message);
  }
}

export type SetAccessTypeParams = {
  communityId: number;
  targetUserId: number;
  newAccessType: AccessType;
};

export async function setAccessType(p: SetAccessTypeParams): Promise<void> {
  let body = { accessType: ACCESS_TYPE_ARRAY[p.newAccessType] };
  try {
    await api.put(
      `/communities/${p.communityId}/access-type/${p.targetUserId}`,
      body
    );
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

export async function inviteUserByEmail(
  p: InviteCommunityParams
): Promise<void> {
  try {
    const user = await findUserByEmail(p.email);
    const params: SetAccessTypeParams = {
      communityId: p.communityId,
      targetUserId: user.id,
      newAccessType: AccessType.INVITED,
    };
    await setAccessType(params);
  } catch (e: any) {
    const errorClass = apiErrors.get(e.response.status) ?? InternalServerError;
    throw new errorClass("Error inviting user by email");
  }
}
