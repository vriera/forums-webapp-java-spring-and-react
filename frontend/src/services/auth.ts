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

  const response = await api.get(`/users?email=${encodedUsername}` , {
      headers: { 
        'Authorization': `Basic ${encodedCredentials}`
      }
 } );
  if (response.status === 200) {
    updateToken(
      response.headers.Authorization || response.headers.authorization
    );
    if (response.data.length > 0)
      await updateUserInfo(new URL(response.data[0].url).pathname);
  }
  return response;
}

export function logout(): void {
  removeToken();
  window.localStorage.removeItem("userId");
}

export function validateLogin() {
  const token = window.localStorage.getItem("token");
  return token ? true : false;
}

const AuthService = {
  loginUser,
};

export default AuthService;
