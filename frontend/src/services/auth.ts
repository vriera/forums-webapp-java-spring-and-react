import { api, updateToken, removeToken } from "./api";
import { updateUserInfo } from "./user";

export async function loginUser(email: string, password: string) {


  console.log("enconding as URI component");

  const encodedUsername = encodeURIComponent(email);
  const encodedPassword = encodeURIComponent(password);
  const credentials = encodedUsername + ":" + encodedPassword;

  console.log("enconding to b64");

  const encoder = new TextEncoder();
  const data = encoder.encode(credentials);
  const dataArray = Array.from(data);
  const encodedCredentials = btoa(String.fromCharCode(...dataArray));;
  // try{
  //  encodedCredentials = Buffer.from(credentials, 'utf-8').toString('base64');
  // }catch(e){
  //   console.log(e)
  //   return;
  // }
  console.log("send api login");

  
  const response = await api.get(`/users?email=${encodedUsername}` , {
      headers: { 
        'Authorization': `Basic ${encodedCredentials}`
      }
 } );
 console.log("sent api login");
 console.log(response)
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
