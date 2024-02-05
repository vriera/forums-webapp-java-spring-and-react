import {api, getPaginationInfo, noContentPagination, PaginationInfo,} from "./api";
import {Community, CommunityResponse} from "../models/CommunityTypes";
import {ACCESS_TYPE_ARRAY_ENUM, AccessType,} from "./Access";
import {apiErrors, HTTPStatusCodes, InternalServerError,} from "../models/HttpTypes";
import {IN_USE, SAME_ACCESS_INVITED} from "./apiErrorCodes";

export async function createCommunity(name: string, description: string, errorCallback: (message: string) => void, t: Function) {
    if (!window.localStorage.getItem("userId")) {
        throw new Error("User not logged in");
    }

    try {
        const response = await api.post(`/communities`, {name, description});
        // Returns 201 if successful
        let communityId = parseInt(response.headers.location.split("/").pop());
        return communityId;
    } catch (error: any) {
        switch (error.response.data.code) {
            case
            IN_USE:
                // Llamar a la función de devolución de llamada con el mensaje de error específico
                errorCallback(t("community.nameProblem") as string);
                break;
            // Agrega más casos según sea necesario para otros códigos de error
            default:
                // Llamar a la función de devolución de llamada con un mensaje de error genérico
                errorCallback("Error desconocido al establecer el tipo de acceso");
                break;
        }
    }
}


export async function getCommunityFromUrl(communityURL: string) {
    let path = new URL(communityURL).pathname;
    return await getCommunity(parseInt(path.split("/").pop() as string));
}


/*
export async function getCommunityFromUrl(communityURL: string) {
    let path = new URL(communityURL).pathname
    let resp;
    if (!window.localStorage.getItem("userId")) {
        resp = await apiURLfromApi.get(path)
    } else {
        let id = window.localStorage.getItem("userId")
        resp = await apiURLfromApi.get(path + `?userId=${id}`);
    }
    if (resp.status !== 200)
        return null as unknown as Community;
    return {
        id: resp.data.id,
        name: resp.data.name,
        description: resp.data.description,
        userCount: resp.data.userCount,
        moderator: await getUserFromURI(resp.data.moderator)
    }
}
*/

export async function getCommunityNotifications(id: number) {
    try {
        let res = await api.get(`/notifications/communities/${id}`);

        if (res.status === HTTPStatusCodes.NO_CONTENT) return 0;

        // Notification count is returned as a java Long
        return res.data.notifications as number;
    } catch (error: any) {
        const errorClass =
            apiErrors.get(error.response.status) || InternalServerError;
        throw new errorClass("Error getting notifications");
    }
}

export async function getCommunity(communityId: number): Promise<Community> {
    let endpoint = `/communities/${communityId}`;

    const id = window.localStorage.getItem("userId");
    if (id) endpoint += `?userId=${id}`

    try {
        const response = await api.get(endpoint);
        return {
            id: response.data.id,
            name: response.data.name,
            description: response.data.description,
            userCount: response.data.userCount
        };
    } catch (error: any) {
        const errorClass =
            apiErrors.get(error.response.status) || InternalServerError;
        console.log("Error getting community", error)
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
        searchParams.append(
            key,
            new String(p[key as keyof CommunitySearchParams]).toString()
        );
    });

    try {
        let response = await api.get("/communities?" + searchParams.toString());
        return {
            list: response.data,
            pagination: getPaginationInfo(response.headers.link, p.page || 1),
        };
    } catch (error: any) {
        console.log(error)
        const errorClass =
            apiErrors.get(error.response.status) || InternalServerError;
        throw new errorClass("Error searching community");
    }
}

export type UserCommunitySearchParams = {
    userId?: number;
    page: number;
    limit?: number;
};

//this function is for getting the comunities a specific user is allowed to ask to
export async function getUserCommunities(
    p: UserCommunitySearchParams
): Promise<{ list: CommunityResponse[]; pagination: PaginationInfo }> {
    let searchParams = new URLSearchParams();

    Object.keys(p).forEach((key: string) => {
        searchParams.append(
            key,
            new String(p[key as keyof UserCommunitySearchParams]).toString()
        );
    });

    try {
        let res = await api.get("/communities?" + searchParams.toString());
        return {
            list: res.data,
            pagination: getPaginationInfo(res.headers.link, p.page || 1),
        };
    } catch (error: any) {
        // If the requestorId is -1, this means that we are an admin user
        const errorClass =
            apiErrors.get(error.response.status) || InternalServerError;
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

    searchParams.append('moderatorId', p.moderatorId.toString());
    if (typeof p.page === 'number' && !isNaN(p.page)) {
        searchParams.append('page', p.page.toString());
    }

    try {
        const response = await api.get(`/communities?${searchParams.toString()}`);

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
    searchParams.append('accessType', ACCESS_TYPE_ARRAY_ENUM[p.accessType]);
    searchParams.append('userId', p.userId.toString());
    if (p.page) {
        searchParams.append('page', p.page.toString());
    }


    try {
        let response = await api.get(
            `/communities?` +
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

export type SetAccessTypeParams = {
    communityId: number;
    userId: number;
    accessType?: AccessType;
    moderatorId?: number;
};

//todo: cambiar can access
export async function canAccess(userId: number, communityId: number) {
    try {
        let res = await api.get(`/communities/${communityId}/access/${userId}`);
        return res.data.accessType === ACCESS_TYPE_ARRAY_ENUM[AccessType.ADMITTED];
    } catch (error: any) {
        const errorClass =
            apiErrors.get(error.response.status) || InternalServerError;
        throw new errorClass(error.response.data.message);
    }
}

export async function setAccessType(p: SetAccessTypeParams, errorCallback: (message: string) => void, t: Function) {
    const filteredParams = new URLSearchParams();
    if (p.accessType !== undefined && p.accessType !== null) filteredParams.set('accessType', ACCESS_TYPE_ARRAY_ENUM[p.accessType]);

    if (p.moderatorId !== null && p.moderatorId !== undefined) {
        filteredParams.set('moderatorId', p.moderatorId.toString());
    }

    try {
        let response = await api.put(`/communities/${p.communityId}/access/${p.userId}?${filteredParams.toString()}`);
        return response;
    } catch (error: any) {
        switch (error.response.data.code) {
            case SAME_ACCESS_INVITED:
                // Llamar a la función de devolución de llamada con el mensaje de error específico
                errorCallback(t("dashboard.alreadyInvited") as string);
                break;
            // Agrega más casos según sea necesario para otros códigos de error
            default:
                // Llamar a la función de devolución de llamada con un mensaje de error genérico
                errorCallback("Error desconocido al establecer el tipo de acceso");
                break;
        }
    }
}

/*

export type InviteCommunityParams = {
  communityId: number;
  email: string;
};
//todo: cambiar can access
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
*/
