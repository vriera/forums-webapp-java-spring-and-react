import {loginUser, logout, validateLogin,} from "../services/auth";
import {api} from "../services/api";
import MockAdapter from "axios-mock-adapter";


describe("AuthService", () => {
    const mockApiResponse = {
        data: {},

    };
    const mockHeader = {
        authorization: "mockToken",
    }

    let mockAxios = new MockAdapter(api);


    it("should successfully login user", async () => {
        mockAxios.onGet(`/users?email=test%40example.com`).reply(200, mockApiResponse, mockHeader);
        jest.spyOn(window.localStorage.__proto__, "getItem").mockReturnValue(null);
        const updateTokenSpy = jest.spyOn(window.localStorage.__proto__, "setItem");

        const email = "test@example.com";
        const password = "password";

        const response = await loginUser(email, password);

        expect(mockAxios.history.get.length).toBe(1); // Check that the API was called
        expect(updateTokenSpy).toHaveBeenCalledWith("token", "mockToken");
        // @ts-ignore
        expect(response.headers.authorization).toEqual("mockToken");
    });

    it("should logout user", () => {
        const mockLocationAssign = jest.fn();
        Object.defineProperty(window, "location", {
            value: {assign: mockLocationAssign},
            writable: true,
        });
        expect(mockLocationAssign).not.toHaveBeenCalled();
        const removeTokenSpy = jest.spyOn(window.localStorage.__proto__, "removeItem");
        logout();
        expect(removeTokenSpy).toHaveBeenCalledWith("token");
        expect(removeTokenSpy).toHaveBeenCalledWith("userId");
        expect(removeTokenSpy).toHaveBeenCalledWith("username");
        expect(removeTokenSpy).toHaveBeenCalledWith("email");

    });


    it("should validate login", () => {
        jest.spyOn(window.localStorage.__proto__, "getItem").mockReturnValue("mockToken");
        const result = validateLogin();
        expect(result).toBeTruthy();
    });

    it("should not validate login", () => {
        jest.spyOn(window.localStorage.__proto__, "getItem").mockReturnValue(null);
        const result = validateLogin();
        expect(result).toBeFalsy();
    });

});
