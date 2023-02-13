import {
  BadRequestError,
  CommunityNameTakenError,
  ForbiddenError,
  HTTPStatusCodes,
  InternalServerError,
  NotFoundError,
  UnauthorizedError,
} from "../models/HttpTypes";
import { api as mockApi } from "../services/api";
import {
  AskableCommunitySearchParams,
  createCommunity,
  getAskableCommunities,
  getCommunity,
  getCommunityNotifications,
  searchCommunity,
} from "../services/community";
import { getUserFromURI } from "../services/user";

describe("CommunityService", () => {
  beforeEach(() => {
    jest.spyOn(Storage.prototype, "getItem").mockImplementation((key) => {
      if (key === "userId") {
        return "1";
      }
      return null;
    });
  });

  // Create community
  it("Should create community when given a valid user ID", async () => {
    const mockCreateCommunity = jest.fn().mockReturnValue({
      status: HTTPStatusCodes.CREATED,
      headers: { location: "http://localhost:8080/api/communities/1" },
    });
    mockApi.post = mockCreateCommunity;
    const community = { name: "community", description: "description" };
    await createCommunity(community.name, community.description);

    expect(mockCreateCommunity).toHaveBeenCalledWith(`/communities`, {
      name: community.name,
      description: community.description,
    });
  });

  it("Should throw error when creating community with a taken name", async () => {
    mockApi.post = jest.fn().mockReturnValue({
      status: HTTPStatusCodes.CONFLICT,
      data: { message: "community.name.taken" },
    });
    const community = { name: "community", description: "description" };

    await expect(
      createCommunity(community.name, community.description)
    ).rejects.toThrow(CommunityNameTakenError);
  });

  it("Should throw error when failing to create a community", async () => {
    mockApi.post = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.INTERNAL_SERVER_ERROR });
    const community = { name: "community", description: "description" };

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
    mockApi.get = jest.fn().mockReturnValue({
      status: HTTPStatusCodes.OK,
      data: { notifications: 1 },
    });
    const id = 1;
    await getCommunityNotifications(id);

    expect(mockApi.get).toHaveBeenCalledWith(
      `/notifications/communities/${id}`
    );
  });

  it("Should throw error when getting community notifications with invalid community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.BAD_REQUEST });
    const id = -1;

    await expect(getCommunityNotifications(id)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when getting community notifications with non-existent community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.NOT_FOUND });
    const id = 1;

    await expect(getCommunityNotifications(id)).rejects.toThrow(NotFoundError);
  });

  it("Should get no content from request to empty notifications", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.NO_CONTENT });
    const id = 1;
    let notifications = await getCommunityNotifications(id);

    expect(notifications).toBe(0);
  });

  // Get community
  it("Should get community", async () => {
    // Mock getUserFromURI from the user service to return a user object
    jest.mock("../services/user", () => {
      jest.fn().mockReturnValue({
        id: 2,
        username: "testing",
        email: "testing@email.com",
      });
    });

    mockApi.get = jest.fn().mockReturnValue({
      status: HTTPStatusCodes.OK,
      data: {
        id: 1,
        name: "Testing communities",
        description: "This is a mocked community",
        userCount: 1,
        moderator: "http://localhost:8080/api/users/2",
      },
    });
    const id = 1;
    await getCommunity(id);

    expect(mockApi.get).toHaveBeenCalledWith(`/communities/${id}?userId=1`)
    expect(mockApi.get).toHaveBeenCalledWith(`/users/2`)
    expect(mockApi.get).toHaveBeenCalledWith(`/notifications/1`)
  });

  it("Should throw error when getting community with invalid community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.BAD_REQUEST });
    const id = -1;

    await expect(getCommunity(id)).rejects.toThrow(BadRequestError);
  });

  it("Should throw error when getting community with non-existent community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.NOT_FOUND });
    const id = 1;

    await expect(getCommunity(id)).rejects.toThrow(NotFoundError);
  });

  // Search communities
  it("Should search communities", async () => {
    mockApi.get = jest.fn().mockReturnValue({ status: HTTPStatusCodes.OK, headers: {}  });
    let searchParams = {
      query: "testing",
      page: 1,
    };
    await searchCommunity(searchParams);

    expect(mockApi.get).toHaveBeenCalledWith(
      `/communities?query=${searchParams.query}&page=${searchParams.page}`
    );
  });

  it("Should throw error when searching communities with invalid community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.BAD_REQUEST });
    let searchParams = {
      query: "testing",
      page: 1,
    };

    await expect(searchCommunity(searchParams)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when searching communities with non-existent community id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.NOT_FOUND });
    let searchParams = {
      query: "testing",
      page: 1,
    };

    await expect( searchCommunity(searchParams)).rejects.toThrow(NotFoundError);
  });

  // Allowed communities
  it("Should get allowed communities", async () => {
    mockApi.get = jest  
      .fn()
      .mockReturnValue( {status: HTTPStatusCodes.OK, headers: {} } )
    
      let params: AskableCommunitySearchParams = {
      page: 1,
      requestorId: 1,
    }

    await getAskableCommunities(params);

    expect(mockApi.get).toHaveBeenCalledWith(`/communities/askable?page=${params.page}&requestorId=${params.requestorId}`);
  });

  it("Should throw error when getting allowed communities with invalid requestor id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.BAD_REQUEST });
    let params: AskableCommunitySearchParams = {
      page: 1,
      requestorId: -7,
    }

    await expect(getAskableCommunities(params)).rejects.toThrow(
      BadRequestError
    );
  });

  it("Should throw error when getting allowed communities with non-existent requestor id", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.NOT_FOUND });
    let params: AskableCommunitySearchParams = {
      page: 1,
      requestorId: 1,
    }

    await expect(getAskableCommunities(params)).rejects.toThrow(NotFoundError);
  });

  it("Should throw error when getting allowed communities with unauthorized requestorId", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.FORBIDDEN });
    let params: AskableCommunitySearchParams = {
      page: 1,
      requestorId: 1,
    }

    await expect(getAskableCommunities(params)).rejects.toThrow(ForbiddenError);
  });

  it("Should throw error when getting allowed communities without a session", async () => {
    mockApi.get = jest
      .fn()
      .mockReturnValue({ status: HTTPStatusCodes.UNAUTHORIZED });
    let params: AskableCommunitySearchParams = {
      page: 1,
    }

    await expect(getAskableCommunities(params)).rejects.toThrow(UnauthorizedError);
  });
});
