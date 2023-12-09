import {
  BadRequestError,
  CommunityNameTakenError,
  ForbiddenError,
  HTTPStatusCodes,
  InternalServerError,
  NotFoundError,
  UnauthorizedError,
} from "../models/HttpTypes";
import { api, apiWithoutBaseUrl } from "../services/api";
import {
  AskableCommunitySearchParams,
  createCommunity,
  getAskableCommunities,
  getCommunity,
  getCommunityNotifications,
  searchCommunity,
} from "../services/community";
import MockAdapter from "axios-mock-adapter";

describe("CommunityService", () => {
  const mockAxios = new MockAdapter(api);
  const mockAxiosWithoutBaseUrl = new MockAdapter(apiWithoutBaseUrl);
  const loggedUserId = 1;
  beforeEach(() => {
    jest.spyOn(Storage.prototype, "getItem").mockImplementation((key) => {
      if (key === "userId") {
        return "1";
      }
      return null;
    });

    mockAxios.reset();
    mockAxiosWithoutBaseUrl.reset();
  });

  // Create community
  it("Should create community when given a valid user ID", async () => {
    const community = { name: "community", description: "description" };
    mockAxios.onPost(`/communities`).reply(HTTPStatusCodes.CREATED, undefined, {
      location: "http://localhost:8080/api/communities/1",
    });

    await createCommunity(community.name, community.description);

    expect(mockAxios.history.post).toHaveLength(1);
    expect(mockAxios.history.post[0].url).toBe("/communities");
    expect(mockAxios.history.post[0].data).toBe(
      JSON.stringify({
        name: community.name,
        description: community.description,
      })
    );
  });

  it("Should throw error when creating community with a taken name", async () => {
    const community = { name: "community", description: "description" };
    mockAxios
      .onPost(`/communities`)
      .reply(HTTPStatusCodes.CONFLICT, { message: "community.name.taken" });

    await expect(
      createCommunity(community.name, community.description)
    ).rejects.toThrow(CommunityNameTakenError);
  });

  it("Should throw error when failing to create a community", async () => {
    const community = { name: "community", description: "description" };
    mockAxios
      .onPost(`/communities`)
      .reply(HTTPStatusCodes.INTERNAL_SERVER_ERROR);

    await expect(
      createCommunity(community.name, community.description)
    ).rejects.toThrow(InternalServerError);
  });

  it("Should throw error when creating community without a user ID", async () => {
    window.localStorage.getItem = jest.fn().mockReturnValue(undefined);
    const community = { name: "community", description: "description" };

    await expect(
      createCommunity(community.name, community.description)
    ).rejects.toThrow(Error);
  });

  // Get notifications
  it("Should get community notifications", async () => {
    const id = 1;
    mockAxios
      .onGet(`/communities/${id}/notifications`)
      .reply(HTTPStatusCodes.OK, { notifications: 1 });

    await getCommunityNotifications(id);

    expect(mockAxios.history.get).toHaveLength(1);
    expect(mockAxios.history.get[0].url).toBe(
      `/communities/${id}/notifications`
    );
  });

  it("Should throw error when getting community notifications with invalid community id", async () => {
    const id = -1;
    mockAxios
      .onGet(`/communities/${id}/notifications`)
      .reply(HTTPStatusCodes.BAD_REQUEST);

    await expect(getCommunityNotifications(id)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when getting community notifications with non-existent community id", async () => {
    const id = 1;
    mockAxios
      .onGet(`/communities/${id}/notifications`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(getCommunityNotifications(id)).rejects.toThrow(NotFoundError);
  });

  it("Should get no content from request to empty notifications", async () => {
    const id = 1;
    mockAxios
      .onGet(`/communities/${id}/notifications`)
      .reply(HTTPStatusCodes.NO_CONTENT);

    let notifications = await getCommunityNotifications(id);

    expect(notifications).toBe(0);
  });

  // Get community
  it("Should get community", async () => {
    // Mock getUserFromURI from the user service to return a user object
    const communityId = 2;
    const moderatorId = 3;
    const moderatorUri = `http://localhost:8080/api/users/${moderatorId}`;
    mockAxios
      .onGet(`/communities/${communityId}?userId=${loggedUserId}`)
      .reply(HTTPStatusCodes.OK, {
        id: communityId,
        name: "Testing communities",
        description: "This is a mocked community",
        userCount: 1,
        moderator: moderatorUri,
      });

    mockAxiosWithoutBaseUrl.onGet(moderatorUri).reply(HTTPStatusCodes.OK, {
      id: moderatorId,
      })

    await getCommunity(communityId);

    expect(mockAxios.history.get[0].url).toBe(
      `/communities/${communityId}?userId=${loggedUserId}`
    );
    expect(mockAxiosWithoutBaseUrl.history.get[0].url).toBe(moderatorUri);
  });

  it("Should throw error when getting community with invalid community id", async () => {
    const id = -1;
    mockAxios
      .onGet(`/communities/${id}?userId=${loggedUserId}`)
      .reply(HTTPStatusCodes.BAD_REQUEST);

    mockAxios.onGet(`/users/${id}`).reply(HTTPStatusCodes.BAD_REQUEST);

    await expect(getCommunity(id)).rejects.toThrow(BadRequestError);
  });

  it("Should throw error when getting community with non-existent community id", async () => {
    const id = 1;
    mockAxios.onGet(`/communities/${id}`).reply(HTTPStatusCodes.NOT_FOUND);

    await expect(getCommunity(id)).rejects.toThrow(NotFoundError);
  });

  // Search communities
  it("Should search communities", async () => {
    let searchParams = {
      query: "testing",
      page: 1,
    };

    mockAxios
      .onGet(
        `/communities?query=${searchParams.query}&page=${searchParams.page}`
      )
      .reply(HTTPStatusCodes.OK, undefined, {});

    await searchCommunity(searchParams);

    expect(mockAxios.history.get[0].url).toBe(
      `/communities?query=${searchParams.query}&page=${searchParams.page}`
    );
  });

  it("Should throw error when searching communities with invalid community id", async () => {
    let searchParams = {
      query: "testing",
      page: 1,
    };
    mockAxios
      .onGet(
        `/communities?query=${searchParams.query}&page=${searchParams.page}`
      )
      .reply(HTTPStatusCodes.BAD_REQUEST);

    await expect(searchCommunity(searchParams)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when searching communities with non-existent community id", async () => {
    let searchParams = {
      query: "testing",
      page: 1,
    };
    mockAxios
      .onGet(`/communities?${searchParams.query}&page=${searchParams.page}`)
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(searchCommunity(searchParams)).rejects.toThrow(NotFoundError);
  });

  // Allowed communities
  it("Should get allowed communities", async () => {
    let params: AskableCommunitySearchParams = {
      page: 1,
      userId: 1,
    };
    mockAxios
      .onGet(
        `/communities/askable?page=${params.page}&userId=${params.userId}`
      )
      .reply(HTTPStatusCodes.OK, undefined, {});

    await getAskableCommunities(params);

    expect(mockAxios.history.get[0].url).toBe(
      `/communities/askable?page=${params.page}&userId=${params.userId}`
    );
  });

  it("Should throw error when getting allowed communities with invalid requestor id", async () => {
    let params: AskableCommunitySearchParams = {
      page: 1,
      userId: -7,
    };
    mockAxios
      .onGet(
        `/communities/askable?page=${params.page}&userId=${params.userId}`
      )
      .reply(HTTPStatusCodes.BAD_REQUEST);

    await expect(getAskableCommunities(params)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when getting allowed communities with non-existent requestor id", async () => {
    let params: AskableCommunitySearchParams = {
      page: 1,
      userId: 1,
    };
    mockAxios
      .onGet(
        `/communities/askable?page=${params.page}&userId=${params.userId}`
      )
      .reply(HTTPStatusCodes.NOT_FOUND);

    await expect(getAskableCommunities(params)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when getting allowed communities with unauthorized userId", async () => {
    let params: AskableCommunitySearchParams = {
      page: 1,
      userId: 1,
    };
    mockAxios
      .onGet(
        `/communities/askable?page=${params.page}&userId=${params.userId}`
      )
      .reply(HTTPStatusCodes.FORBIDDEN);

    await expect(getAskableCommunities(params)).rejects.toThrow(ForbiddenError);
  });

  it("Should throw error when getting allowed communities without a session", async () => {
    let params: AskableCommunitySearchParams = {
      page: 1,
    };
    mockAxios
      .onGet(`/communities/askable?page=${params.page}`)
      .reply(HTTPStatusCodes.UNAUTHORIZED);

    await expect(getAskableCommunities(params)).rejects.toThrow(
      UnauthorizedError
    );
  });
});
