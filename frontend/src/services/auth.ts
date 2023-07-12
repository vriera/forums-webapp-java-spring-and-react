import { HTTPStatusCodes, InternalServerError, UnauthorizedError, apiErrors } from "../models/HttpTypes";
import { api, updateToken, removeToken } from "./api";
import { updateUserInfo } from "./user";

export async function loginUser(email: string, password: string) {

  const encodedUsername = encodeURIComponent(email);
  const encodedPassword = encodeURIComponent(password);
  const credentials = encodedUsername + ":" + encodedPassword;

  const encoder = new TextEncoder();
  const data = encoder.encode(credentials);
  const dataArray = Array.from(data);
  const encodedCredentials = btoa(String.fromCharCode(...dataArray));

  try {
    const response = await api.get(`/users?email=${encodedUsername}` , {
        headers: { 
          'Authorization': `Basic ${encodedCredentials}`
        }
  } );
    if (response.status === HTTPStatusCodes.OK) {
      updateToken(
        response.headers.Authorization || response.headers.authorization
      );
      if (response.data.length > 0)
        await updateUserInfo(new URL(response.data[0].url).pathname);
    }
    return response;
  } catch (error: any) { 
    if (error.response.status === HTTPStatusCodes.UNAUTHORIZED) {
      throw new UnauthorizedError("Invalid credentials");
    }
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error logging in");

  }
}

export function logout(): void {
  removeToken();
  window.localStorage.removeItem("userId");
}

export function validateLogin() {
  const token = window.localStorage.getItem("token");
  return token !== undefined && token !== null && token !== "";
}

export function getUserId() : string | null {
  return window.localStorage.getItem("userId");
}

const AuthService = {
  loginUser,
};

export default AuthService;
