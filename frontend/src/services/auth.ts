import { api, updateToken, removeToken } from "./api";
import { updateUserInfo } from "./user";
import jwtDecode from "jwt-decode";
import axios from "axios";

export async function loginUser(email: string, password: string) {


  // Encode email and password in Base64
  const credentials = btoa(`${email}:${password}`)
  const headers = {
    Authorization: `Basic ${credentials}`,
  };
  const response = await api.get(`/users?` + `email=${email}`, {headers})
  // Handle the API response
  if (response.status === 200) {
    updateToken(
        response.headers.Authorization || response.headers.authorization
    );
    await updateUserInfo(response); //TODO: CHEQUAR
  }
    return response;

}

export function logout(): void {
  removeToken();
  window.localStorage.removeItem("userId");
  window.localStorage.removeItem("username");
  window.localStorage.removeItem("email");
  window.location.href = process.env.PUBLIC_URL +   "/credentials/login";
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

export function validateToken() {
  const token = window.localStorage.getItem("token");
  if(token == null) return false
  try{
    const decodeToken: any = jwtDecode(token)
    if(decodeToken.exp > Date.now()/1000) return true;
  }catch (e) {
    return false
  }
}

const AuthService = {
  loginUser,
  registerUser,
};

export default AuthService;
