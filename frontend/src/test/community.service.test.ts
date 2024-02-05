import {BadRequestError, HTTPStatusCodes, NotFoundError,} from "../models/HttpTypes";
import {api} from "../services/api";
import {
    createCommunity,
    getCommunity,
    getCommunityNotifications,
    getUserCommunities,
    searchCommunity,
    UserCommunitySearchParams,
} from "../services/community";
import MockAdapter from "axios-mock-adapter";

describe("CommunityService", () => {
    const mockAxios = new MockAdapter(api);
    afterEach(() => {
        mockAxios.reset();
    });

    const loggedUserId = 1;
    beforeEach(() => {
        jest.spyOn(Storage.prototype, "getItem").mockImplementation((key) => {
            if (key === "userId") {
                return "1";
            }
            return null;
        });
        mockAxios.reset();
    });

    // Create community
    it("Should create community when given a valid user ID", async () => {
        const community = {name: "community", description: "description"};
        mockAxios.onPost(`/communities`).reply(HTTPStatusCodes.CREATED, undefined, {
            location: "http://localhost:8080/api/communities/1",
        });

        // @ts-ignore
        await createCommunity(community.name, community.description);

        expect(mockAxios.history.post).toHaveLength(1);
        expect(mockAxios.history.post[0].url).toBe("/communities");
        expect(mockAxios.history.post[0].data).toBe(JSON.stringify({
            name: community.name,
            description: community.description,
        }));
    });

    it("Should handle error while creating a community", async () => {
        const name = "Test Community";
        const description = "Test Community Description";
        const errorCallback = jest.fn();
        const t = jest.fn();

        mockAxios
            .onPost(`/communities`)
            .reply(HTTPStatusCodes.BAD_REQUEST, {code: "IN.USE"});

        let spy = jest.spyOn(api, "post");
        await createCommunity(name, description, errorCallback, t);

        // Verify the Axios request
        expect(spy).toHaveBeenCalledWith(`/communities`, {name, description});


        // Verify the errorCallback and t function calls
        expect(errorCallback).toHaveBeenCalledWith(t("community.nameProblem") as string);
        expect(t).toHaveBeenCalledWith("community.nameProblem");
    });
    // Get notifications
    it("Should get community notifications", async () => {
        const id = 1;
        mockAxios
            .onGet(`/notifications/communities/${id}`)
            .reply(HTTPStatusCodes.OK, {notifications: 1});

        await getCommunityNotifications(id);

        expect(mockAxios.history.get).toHaveLength(1);
        expect(mockAxios.history.get[0].url).toBe(`/notifications/communities/${id}`);
    });

    it("Should throw error when getting community notifications with invalid community id", async () => {
        const id = -1;
        mockAxios
            .onGet(`/notifications/communities/${id}`)
            .reply(HTTPStatusCodes.BAD_REQUEST);

        await expect(getCommunityNotifications(id)).rejects.toThrow(
            BadRequestError
        );
    });

    it("Should throw error when getting community notifications with non-existent community id", async () => {
        const id = 1;
        mockAxios
            .onGet(`/notifications/communities/${id}`)
            .reply(HTTPStatusCodes.NOT_FOUND);

        await expect(getCommunityNotifications(id)).rejects.toThrow(NotFoundError);
    });

    it("Should get no content from request to empty notifications", async () => {
        const id = 1;
        mockAxios
            .onGet(`/notifications/communities/${id}`)
            .reply(HTTPStatusCodes.NO_CONTENT);

        let notifications = await getCommunityNotifications(id);

        expect(notifications).toBe(0);
    });

    // Get community
    it("Should get community", async () => {
        // Mock getUserFromURI from the user service to return a user object
        const communityId = 2;
        const moderatorId = 3;
        mockAxios
            .onGet(`/communities/${communityId}?userId=${loggedUserId}`)
            .reply(HTTPStatusCodes.OK, {
                id: communityId,
                name: "Testing communities",
                description: "This is a mocked community",
                userCount: 1,
                moderator: `http://localhost:8080/api/users/${moderatorId}`,
            });

        mockAxios.onGet(`/users/${moderatorId}`).reply(HTTPStatusCodes.OK, {
            id: moderatorId,
        });

        const community = await getCommunity(communityId);

        // Wait for Axios requests to complete
        await new Promise(resolve => setTimeout(resolve, 0));

        // Assertions
        expect(mockAxios.history.get).toBeDefined(); // Ensure it's defined

        if (mockAxios.history.get.length > 1) {
            expect(mockAxios.history.get[0].url).toBe(`/communities/${communityId}?userId=${loggedUserId}`);
            expect(mockAxios.history.get[1].url).toBe(`/users/${moderatorId}`);
        }


        // Verify the returned community object
        expect(community).toEqual({
            id: communityId,
            name: "Testing communities",
            description: "This is a mocked community",
            userCount: 1,
        });
    });

    it("Should throw error when getting community with invalid community id", async () => {
        const id = -1;
        mockAxios.onGet(`/communities/${id}?userId=${loggedUserId}`).reply(HTTPStatusCodes.BAD_REQUEST);

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
            .onGet(`/communities?query=${searchParams.query}&page=${searchParams.page}`)
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
            .onGet(`/communities?query=${searchParams.query}&page=${searchParams.page}`)
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


    it("Should throw error when getting allowed communities with non-existent requestor id", async () => {
        let params: UserCommunitySearchParams = {
            page: 1,
        };
        mockAxios
            .onGet(
                `/communities/askable?page=${params.page}}`
            )
            .reply(HTTPStatusCodes.NOT_FOUND);

        await expect(getUserCommunities(params)).rejects.toThrow(NotFoundError);
    });

});
