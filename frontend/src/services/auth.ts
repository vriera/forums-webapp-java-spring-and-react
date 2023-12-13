import { AxiosError, AxiosResponse } from "axios";
import {
  HTTPStatusCodes,
  IncorrectPasswordError,
  InternalServerError,
  UnauthorizedError,
  apiErrors,
} from "../models/HttpTypes";
import { User } from "../models/UserTypes";
import { api } from "./api";

// Gets user from API using Basic Auth and stores it in localStorage
export async function login(email: string, password: string): Promise<User> {
  const encodedUsername = encodeURIComponent(email);
  const encodedPassword = encodeURIComponent(password);
  const credentials = encodedUsername + ":" + encodedPassword;

  const encoder = new TextEncoder();
  const data = encoder.encode(credentials);
  const dataArray = Array.from(data);
  const encodedCredentials = btoa(String.fromCharCode(...dataArray));

  try {
    const response: AxiosResponse<User[]> = await api.get(
      `/users?email=${encodedUsername}`,
      {
        headers: {
          Authorization: `Basic ${encodedCredentials}`,
        },
      }
    );

    const sessionToken =
      response.headers.Authorization || response.headers.authorization;

    const userDoesNotExist = response.status === HTTPStatusCodes.NO_CONTENT;

    // If no token is returned, the input password is incorrect
    if (userDoesNotExist || !sessionToken) {
      throw new IncorrectPasswordError();
    }

    const user: User = {
      id: response.data[0].id,
      username: response.data[0].username,
      email: response.data[0].email,
    };

    updateSessionUserInfo(user, sessionToken);

    return user;
  } catch (error: any) {
    // If the error is an AxiosError, it is an error from the API
    if (!error.isAxiosError) {
      throw error;
    }

    // Otherwise, it is an error from the client
    const errorClass =
      apiErrors.get(error.response.status) ?? InternalServerError;
    throw new errorClass("Error logging in");
  }
}

function updateSessionUserInfo(user: User, sessionToken: string): void {
  window.localStorage.setItem("userId", user.id.toString());
  window.localStorage.setItem("username", user.username);
  window.localStorage.setItem("email", user.email);
  window.localStorage.setItem("token", sessionToken);
}

export function logout(): void {
  window.localStorage.removeItem("userId");
  window.localStorage.removeItem("username");
  window.localStorage.removeItem("email");
  window.localStorage.removeItem("token");
}

export function validateLogin() {
  const token = window.localStorage.getItem("token");
  return token !== undefined && token !== null && token !== "";
}

export function getUserId(): string | null {
  return window.localStorage.getItem("userId");
}

const AuthService = {
  loginUser: login,
};

export default AuthService;
