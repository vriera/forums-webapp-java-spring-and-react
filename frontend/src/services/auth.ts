import { api, updateToken, removeToken } from "./api";
import { updateUserInfo } from "./user";
import axios from "axios";

export async function loginUser(email: string, password: string) {


  // Encode email and password in Base64
  const credentials = btoa(`${email}:${password}`)
  const headers = {
    Authorization: `Basic ${credentials}`,
  };
  const response = await api.get(`/users/user/${email}`, {headers})
  // Handle the API response
  if (response.status === 200) {
    updateToken(
        response.headers.Authorization || response.headers.authorization
    );
    await updateUserInfo(new URL(response.data[0].url).pathname); //TODO: CHEQUAR
  }
    return response;

}

export function logout(): void {
  removeToken();
  window.localStorage.removeItem("userId");
}

export async function registerUser(
  email: string,
  password: string,
  username: string,
  repeatPassword: string
) {
  const response = await api.post("/users", {
    email,
    password,
    username,
    repeatPassword,
  });
  return response;
}

export function validateLogin() {
  const token = window.localStorage.getItem("token");
  return token ? true : false;
}

const AuthService = {
  loginUser,
  registerUser,
};

export default AuthService;
